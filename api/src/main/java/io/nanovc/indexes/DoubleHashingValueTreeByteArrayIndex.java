/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.indexes;

import io.nanovc.ByteArrayIndex;

import java.util.HashMap;

/**
 * A time efficient index for byte arrays so that it's quick to look up existing byte arrays by value.
 * It uses a large amount of memory for the value tree.
 * This structure is suited for a medium number of medium size arrays.
 * This is useful in cases where we want to re-use references to bytes arrays that already exist for efficient memory lookups.
 */
public class DoubleHashingValueTreeByteArrayIndex implements ByteArrayIndex
{
    /**
     * A reference to a zero byte array if we have one.
     * This is a short circuiting optimization because we can only ever have one zero byte array in this index.
     */
    protected byte[] zeroByteArray;

    /**
     * The map of the xor hash and then sum hash to the hash node.
     * The first key is the xor hash for the array.
     * The second key is the sum hash for the array.
     * The hash node is the value.
     */
    protected HashMap<Integer, HashMap<Integer, HashNode>> xorHashNodeMap = new HashMap<>();

    /**
     * Checks whether the index already has bytes with the given values and returns the indexed bytes if it already exists.
     * If the bytes cannot be found then the bytes are indexed and returned.
     * @param bytes The bytes to search for by value.
     * @return The indexed bytes that match the value of the input bytes. This will be the same instance as the input if we have not previously indexed these values before.
     */
    public byte[] addOrLookup(byte[] bytes)
    {
        // Make sure we have an input:
        if (bytes == null) return null;
        // Now we know that the input is not null.

        // Check whether this is a zero byte array:
        if (bytes.length == 0)
        {
            // The input is a zero byte array.
            // Check whether we have seen a zero byte array before or not:
            if (this.zeroByteArray == null)
            {
                // We have never seen a zero byte array before.
                // Save the reference to the zero byte array:
                this.zeroByteArray = bytes;
            }
            return this.zeroByteArray;
        }
        // Now we know that it's not a zero byte array.

        // Compute cheap hashes of the data that we want to search for:
        // NOTE: Java does not throw exceptions on overflow so that makes our approach simpler:
        // https://stackoverflow.com/a/3001995/231860
        int xorHash = 0;
        int sumHash = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            int value = bytes[i];
            xorHash ^= value;
            sumHash += value;
        }
        // Now we have the hashes.

        // Get the boxed values:
        Integer xorHashBoxed = xorHash;
        Integer sumHashBoxed = sumHash;

        // Get the sum map for the xor hash:
        HashMap<Integer, HashNode> sumHashNodeMap = xorHashNodeMap.computeIfAbsent(xorHashBoxed, integer -> new HashMap<>());

        // Get the hash node for the sum hash:
        HashNode hashNode = sumHashNodeMap.computeIfAbsent(sumHashBoxed, integer -> new HashNode());
        // Now we have a hash node with a low probability of clashes for this array (because of the double hashing).

        // NOTE: We prefer to have a double hashing strategy for the chain of value nodes to maximise the chance that we only need the optimized fields for the value nodes
        //       thus avoiding the need to create hash maps at each step.
        //       This gives us a performance boost at the expense of creating lots of objects.
        //       However, this approach creates fewer objects for each step than an entire hash map at each step.

        // Start walking the value nodes:
        ValueNode currentNode = hashNode.valueNode;

        // Now start walking the hash node for each value of the array:
        int remainingBytes = bytes.length;
        int arrayIndex = 0;
        while (remainingBytes > 0)
        {
            // Check whether we are at the end of the array and get the integer value of the next 4 bytes if possible:
            // NOTE: We expect that it will only read up to 4 bytes of the array.
            int nextValue = Bits.byteArrayToInt(bytes, arrayIndex, remainingBytes);
            int stepSize;
            switch (remainingBytes)
            {
                case 1:
                case 2:
                case 3:
                case 4:
                    // There are between 1 and 4 remaining bytes.

                    // Update the array index:
                    arrayIndex += remainingBytes;

                    // Keep track of the step size that was made:
                    stepSize = remainingBytes;

                    // Flag that we have finished walking the incoming array:
                    remainingBytes = 0;

                    break;

                default:
                    // There are more than 4 remaining bytes.

                    // Update the array index:
                    arrayIndex += 4;

                    // Decrement the remaining bytes:
                    remainingBytes -= 4;

                    // Keep track of the step size that was made:
                    stepSize = 4;

                    break;
            }
            // Now we have the next 4-byte value from the array and we know the size of the step that was taken.

            // Create the next value node along this walk:
            ValueNode nextNode;
            switch (stepSize)
            {
                case 1:
                    if (currentNode.size1ValueNode == null)
                    {
                        // This is the first node we are adding.
                        // Create the next node:
                        nextNode = new ValueNode();

                        // Link it to the current node:
                        currentNode.size1ValueNode = nextNode;
                        currentNode.size1Value = nextValue;
                    }
                    else
                    {
                        // We already have a step from this node.

                        // Check whether our step is the same as the first step that is already there:
                        if (currentNode.size1Value == nextValue)
                        {
                            // Our next step is the same as the first step already on the current node.

                            // Walk to the next step:
                            nextNode = currentNode.size1ValueNode;
                        }
                        else
                        {
                            // Our step is different to the first step that is already there.

                            // This is not the first node we are adding.

                            // Make sure we have a map for subsequent nodes:
                            if (currentNode.size1ValueNodes == null) currentNode.size1ValueNodes = new HashMap<>();

                            // Create the next node and add it into the map for the next step:
                            nextNode = currentNode.size1ValueNodes.computeIfAbsent(nextValue, integer -> new ValueNode());
                        }
                    }
                    break;

                case 2:
                    if (currentNode.size2ValueNode == null)
                    {
                        // This is the first node we are adding.

                        // Create the next node:
                        nextNode = new ValueNode();

                        // Link it to the current node:
                        currentNode.size2ValueNode = nextNode;
                        currentNode.size2Value = nextValue;
                    }
                    else
                    {
                        // We already have a step from this node.

                        // Check whether our step is the same as the first step that is already there:
                        if (currentNode.size2Value == nextValue)
                        {
                            // Our next step is the same as the first step already on the current node.

                            // Walk to the next step:
                            nextNode = currentNode.size2ValueNode;
                        }
                        else
                        {
                            // Our step is different to the first step that is already there.

                            // This is not the first node we are adding.

                            // Make sure we have a map for subsequent nodes:
                            if (currentNode.size2ValueNodes == null) currentNode.size2ValueNodes = new HashMap<>();

                            // Create the next node and add it into the map for the next step:
                            nextNode = currentNode.size2ValueNodes.computeIfAbsent(nextValue, integer -> new ValueNode());
                        }
                    }
                    break;

                case 3:
                    if (currentNode.size3ValueNode == null)
                    {
                        // This is the first node we are adding.

                        // Create the next node:
                        nextNode = new ValueNode();

                        // Link it to the current node:
                        currentNode.size3ValueNode = nextNode;
                        currentNode.size3Value = nextValue;
                    }
                    else
                    {
                        // We already have a step from this node.

                        // Check whether our step is the same as the first step that is already there:
                        if (currentNode.size3Value == nextValue)
                        {
                            // Our next step is the same as the first step already on the current node.

                            // Walk to the next step:
                            nextNode = currentNode.size3ValueNode;
                        }
                        else
                        {
                            // Our step is different to the first step that is already there.

                            // This is not the first node we are adding.

                            // Make sure we have a map for subsequent nodes:
                            if (currentNode.size3ValueNodes == null) currentNode.size3ValueNodes = new HashMap<>();

                            // Create the next node and add it into the map for the next step:
                            nextNode = currentNode.size3ValueNodes.computeIfAbsent(nextValue, integer -> new ValueNode());
                        }
                    }
                    break;

                case 4:
                    if (currentNode.size4ValueNode == null)
                    {
                        // This is the first node we are adding.
                        // Create the next node:
                        nextNode = new ValueNode();

                        // Link it to the current node:
                        currentNode.size4ValueNode = nextNode;
                        currentNode.size4Value = nextValue;
                    }
                    else
                    {
                        // We already have a step from this node.

                        // Check whether our step is the same as the first step that is already there:
                        if (currentNode.size4Value == nextValue)
                        {
                            // Our next step is the same as the first step already on the current node.

                            // Walk to the next step:
                            nextNode = currentNode.size4ValueNode;
                        }
                        else
                        {
                            // Our step is different to the first step that is already there.

                            // This is not the first node we are adding.

                            // Make sure we have a map for subsequent nodes:
                            if (currentNode.size4ValueNodes == null) currentNode.size4ValueNodes = new HashMap<>();

                            // Create the next node and add it into the map for the next step:
                            nextNode = currentNode.size4ValueNodes.computeIfAbsent(nextValue, integer -> new ValueNode());
                        }
                    }
                    break;

                default:
                    nextNode = null;
                    break;
            }
            // Now we have the map of possible next steps.

            // Walk the next step:
            currentNode = nextNode;
        }
        // Now we have walked the entire input array.

        // Check whether this is the first time we have an array at the final value node or not:
        if (currentNode.array == null)
        {
            // We have never seen this input array in our index yet.
            // Index it:
            currentNode.array = bytes;

            // Return the input array:
            return bytes;
        }
        else
        {
            // We have already seen a previous array with the same values.
            // Return the indexed array:
            return currentNode.array;
        }
    }

    /**
     * Clears the byte array index of all the arrays that have been indexed so far.
     */
    @Override
    public void clear()
    {
        this.xorHashNodeMap.clear();
        this.zeroByteArray = null;
    }

    /**
     * A node for the result of hashing an array of bytes.
     */
    private static class HashNode
    {
        /**
         * The root value node for this hash node.
         */
        public ValueNode valueNode = new ValueNode();
    }

    /**
     * A node for a specific walk of the array.
     */
    private static class ValueNode
    {
        //region Size 1 Steps: This means that the value was only for a 1 byte step in the array.

        /**
         * This is the first value for the next 1 byte step in the array.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size1ValueNodes}.
         */
        public int size1Value;

        /**
         * This is the first value node for the next 1 byte step in the array.
         * If this is null then there has been no step yet.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size1ValueNodes}.
         */
        public ValueNode size1ValueNode;

        /**
         * The map of value nodes for the next 1 byte step in the array.
         * This map is only created if there are more than 1 value from this node, for performance reasons.
         * Use {@link #size1Value} and {@link #size1ValueNode} for the first step if there is on.
         * The key is the value of the byte array for the step size of 1.
         * If this is null then there is no entry in the index with one more byte.
         */
        public HashMap<Integer, ValueNode> size1ValueNodes;

        //endregion



        //region Size 2 Steps: This means that the value was only for a 2 byte step in the array.

        /**
         * This is the first value for the next 2 byte step in the array.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size2ValueNodes}.
         */
        public int size2Value;

        /**
         * This is the first value node for the next 2 byte step in the array.
         * If this is null then there has been no step yet.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size2ValueNodes}.
         */
        public ValueNode size2ValueNode;

        /**
         * The map of value nodes for the next 2 byte step in the array.
         * This map is only created if there are more than 1 value from this node, for performance reasons.
         * Use {@link #size2Value} and {@link #size2ValueNode} for the first step if there is on.
         * The key is the value of the byte array for the step size of 2.
         * If this is null then there is no entry in the index with two more bytes.
         */
        public HashMap<Integer, ValueNode> size2ValueNodes;

        //endregion


        //region Size 3 Steps: This means that the value was only for a 3 byte step in the array.

        /**
         * This is the first value for the next 3 byte step in the array.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size3ValueNodes}.
         */
        public int size3Value;

        /**
         * This is the first value node for the next 3 byte step in the array.
         * If this is null then there has been no step yet.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size3ValueNodes}.
         */
        public ValueNode size3ValueNode;

        /**
         * The map of value nodes for the next 3 byte step in the array.
         * This map is only created if there are more than 1 value from this node, for performance reasons.
         * Use {@link #size3Value} and {@link #size3ValueNode} for the first step if there is on.
         * The key is the value of the byte array for the step size of 3.
         * If this is null then there is no entry in the index with three more bytes.
         */
        public HashMap<Integer, ValueNode> size3ValueNodes;

        //endregion


        //region Size 4 Steps: This means that the value was only for a 4 byte step in the array.

        /**
         * This is the first value for the next 4 byte step in the array.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size4ValueNodes}.
         */
        public int size4Value;

        /**
         * This is the first value node for the next 4 byte step in the array.
         * If this is null then there has been no step yet.
         * This field is explicit for performance reasons.
         * If there are multiple values from this node then they are stored in {@link #size4ValueNodes}.
         */
        public ValueNode size4ValueNode;

        /**
         * The map of value nodes for the next 4 byte step in the array.
         * This map is only created if there are more than 1 value from this node, for performance reasons.
         * Use {@link #size4Value} and {@link #size4ValueNode} for the first step if there is on.
         * The key is the value of the byte array for the step size of 4.
         * If this is null then there is no entry in the index with four more bytes.
         */
        public HashMap<Integer, ValueNode> size4ValueNodes;

        //endregion


        /**
         * The array at this value node.
         * If this is null then it means we haven't indexed an array with this value yet.
         * If it is pointing at an array then it means we have an array already indexed.
         */
        public byte[] array;
    }


}
