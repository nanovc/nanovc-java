/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.Difference;
import io.nanovc.Record;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.clocks.ClockWithVMNanos;
import io.nanovc.comparisons.HashMapComparisonHandler;
import io.nanovc.content.ByteArrayContent;
import io.nanovc.differences.HashMapDifferenceHandler;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.MemoryCommit;
import io.nanovc.memory.MemoryRepoHandlerTestBase;
import io.nanovc.memory.MemorySearchQuery;
import io.nanovc.memory.MemorySearchResults;
import io.nanovc.merges.LastWinsMergeHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link ReflectiveObjectMemoryRepoHandler}.
 */
public class ReflectiveObjectMemoryRepoHandlerTests extends MemoryRepoHandlerTestBase<
    ByteArrayContent,
    ByteArrayHashMapArea,
    MemoryCommit,
    MemorySearchQuery,
    MemorySearchResults,
    ReflectiveObjectMemoryRepo,
    ReflectiveObjectMemoryRepoEngine,
    ReflectiveObjectMemoryRepoHandler
    >
{

    /**
     * Creates the specific type of handler under test.
     *
     * @return A new instance of the handler under test.
     */
    @Override protected ReflectiveObjectMemoryRepoHandler createNewRepoHandler()
    {
        return new ReflectiveObjectMemoryRepoHandler();
    }

    @Test
    public void creationTest()
    {
        new ReflectiveObjectMemoryRepoHandler();
        new ReflectiveObjectMemoryRepoHandler(new ReflectiveObjectMemoryRepo());
        new ReflectiveObjectMemoryRepoHandler(
            new ReflectiveObjectMemoryRepo(),
            new HashWrapperByteArrayIndex(),
            new ClockWithVMNanos(),
            new ReflectiveObjectMemoryRepoEngine(),
            new HashMapDifferenceHandler(),
            new HashMapComparisonHandler(),
            new LastWinsMergeHandler()
            );
    }

    @Test
    public void testAPI()
    {
        ReflectiveObjectMemoryRepoHandler handler = new ReflectiveObjectMemoryRepoHandler();

        // Create the employee to commit:
        Employee employee = new Employee();
        employee.firstName = "Lukasz";
        employee.lastName = "Machowski";

        // Commit the employee:
        MemoryCommit commit1 = handler.commitObject(employee, "First Commit");

        // Modify the employee:
        employee.firstName = "Luke";

        // Commit the employee again:
        MemoryCommit commit2 = handler.commitObject(employee, "Second Commit");

        // Checkout the employee:
        Employee employeeAt1 = (Employee) handler.checkoutObject(commit1);

        // Make sure that we got a new employee instance from the checkout:
        assertNotSame(employee, employeeAt1);

        // Make sure the values for the checked out employee are as expected:
        assertEquals("Lukasz", employeeAt1.firstName);
        assertEquals("Machowski", employeeAt1.lastName);


        // Checkout the employee:
        Employee employeeAt2 = (Employee) handler.checkoutObject(commit2);

        // Make sure that we got a new employee instance from the checkout:
        assertNotSame(employee, employeeAt2);
        assertNotSame(employeeAt1, employeeAt2);

        // Make sure the values for the checked out employee are as expected:
        assertEquals("Luke", employeeAt2.firstName);
        assertEquals("Machowski", employeeAt2.lastName);

        // Do a diff between the two commits:
        Difference differences = handler.computeDifferenceBetweenCommits(commit1, commit2);
        String diffs = differences.asListString();
        String expectedDiffs = "/fields/firstName : Changed";
        assertEquals(expectedDiffs, diffs);
    }

    /**
     * An employee class for testing repo's with reflective objects.
     */
    @Record
    public static class Employee
    {
        /**
         * The first name of the employee.
         */
        public String firstName;

        /**
         * The last name of the employee.
         */
        public String lastName;
    }
}
