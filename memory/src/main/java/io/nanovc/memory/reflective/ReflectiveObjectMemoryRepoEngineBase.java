/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.*;
import io.nanovc.indexes.ByteArrayIndex;
import io.nanovc.memory.MemoryCommitBase;
import io.nanovc.memory.MemoryRepoEngineBase;
import io.nanovc.memory.MemorySearchQueryBase;
import io.nanovc.memory.MemorySearchResultsBase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * The base class for the engine for working with a nano version control repository in memory.
 * A Repo Engine does not contain any state. Just the logic of how to manipulate a repo.
 * Therefore you need to pass the repo into all the calls.
 * This is good where one Repo Engine is going to be reused across many Repos.
 * A repo engine is thread safe because it is stateless.
 *
 * @param <TContent> The specific type of content that is stored in area for each commit in the repo.
 * @param <TArea>    The specific type of area that is stored for each commit in the repo.
 * @param <TCommit>  The specific type of commit that is created in the repo.
 * @param <TRepo>    The specific type of repo that this engine is for.
 */
public abstract class ReflectiveObjectMemoryRepoEngineBase<
    TContent extends Content,
    TArea extends Area<TContent>,
    TCommit extends MemoryCommitBase<TCommit>,
    TSearchQuery extends MemorySearchQueryBase<TCommit>,
    TSearchResults extends MemorySearchResultsBase<TCommit, TSearchQuery>,
    TRepo extends ReflectiveObjectMemoryRepoBase<TContent, TArea, TCommit>
    >
    extends MemoryRepoEngineBase<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
    implements ReflectiveObjectMemoryRepoEngineAPI<
    TContent,
    TArea,
    TCommit,
    TSearchQuery,
    TSearchResults,
    TRepo
    >
{
    /**
     * This is the value to signify a null object when serializing.
     */
    public static final String NULL_OBJECT_TYPE = "NULL";

    /**
     * This is the value to signify a null when serializing.
     */
    public static final String NULL_VALUE = "\u0000"; // NULL

    /**
     * Serializes the given object into the content area.
     *
     * @param object         The object to serialize.
     * @param area           The content area to serialize into.
     * @param contentFactory The content factory to use when populating the content area.
     * @param repo           The repo that contains settings for serialization.
     */
    public void serializeObjectToContentArea(Object object, TArea area, ContentFactory<TContent> contentFactory, TRepo repo)
    {
        // Get the repo path where we save the type of object we are serializing:
        RepoPath objectTypeRepoPath = repo.getObjectTypeRepoPath();

        // Check whether we have an object:
        if (object == null)
        {
            // We have a null object to serialize.

            // Write the null object type:
            area.putContent(objectTypeRepoPath, contentFactory.createContent(NULL_OBJECT_TYPE.getBytes(StandardCharsets.UTF_8)));
        }
        else
        {
            // We have an actual object to serialize.
            // Get the class of the object:
            Class<?> objectClass = object.getClass();

            // Write the object type:
            area.putContent(objectTypeRepoPath, contentFactory.createContent(objectClass.getName().getBytes()));

            // Get the path under which we want to serialize all the fields:
            RepoPath objectContentRepoPath = repo.getObjectContentRepoPath();

            // Start reflecting for all fields on the class:
            Field[] fields = objectClass.getFields();
            for (Field field : fields)
            {
                // Make sure that this field is one of our supported types:
                if (field.getType() != String.class) continue;
                // Now we know that the field type is one of the ones we recognise.

                try
                {
                    // Get the value for the field:
                    Object fieldValue = field.get(object);

                    // Get the string value for the field:
                    String fieldStringValue = Objects.toString(fieldValue, NULL_VALUE);

                    // Get the path for this field:
                    RepoPath fieldPath = objectContentRepoPath.resolve(field.getName());

                    // Create the content at the path:
                    area.putContent(fieldPath, contentFactory.createContent(fieldStringValue.getBytes(StandardCharsets.UTF_8)));
                }
                catch (IllegalAccessException e)
                {
                }
            }
        }
    }

    /**
     * Deserializes a new object from the given content area.
     *
     * @param area The content area to deserialize from.
     * @param repo The repo that contains settings for deserialization.
     * @return The object that was deserialized from the content area.
     */
    public Object deserializeObjectFromContentArea(TArea area, TRepo repo)
    {
        // Get the repo path where we save the type of object we are deserializing:
        RepoPath objectTypeRepoPath = repo.getObjectTypeRepoPath();

        // Get the content for the object type:
        TContent objectTypeContent = area.getContent(objectTypeRepoPath);

        // Check whether we have the content type:
        if (objectTypeContent == null)
        {
            // There was no object type in the area.
            return null;
        }
        else
        {
            // We have the object type content.

            // Get the object type:
            String objectType = new String(objectTypeContent.asByteArray(), StandardCharsets.UTF_8);

            try
            {
                // Check whether we have this class:
                Class<?> objectClass = Class.forName(objectType);

                // Create an instance of the object:
                Object object = objectClass.getDeclaredConstructor().newInstance();

                // Get the path to where all the fields are stored:
                RepoPath objectContentRepoPath = repo.getObjectContentRepoPath();

                // Get the absolute root path for fields:
                String fieldRootAbsolutePath = objectContentRepoPath.toAbsolutePath().path;

                // Make sure we have a path for the fields:
                if (objectContentRepoPath != null)
                {
                    // We have a path to where all the fields are stored.

                    // Go through all the paths in the area:
                    for (AreaEntry<TContent> entry : area)
                    {
                        // Make sure that the path is a field:
                        if (entry.path.toAbsolutePath().path.startsWith(fieldRootAbsolutePath))
                        {
                            // This entry represents a field.

                            // Get the field path:
                            RepoPath fieldPath = entry.path;

                            // Get the name of the field:
                            String fieldName = fieldPath.toAbsolutePath().path.substring(fieldRootAbsolutePath.length() + 1);

                            try
                            {
                                // Get the field for the object class:
                                Field field = objectClass.getField(fieldName);

                                // Get the field value:
                                String fieldValueString = new String(entry.content.asByteArray(), StandardCharsets.UTF_8);

                                // Check if the value matches our null string:
                                if (NULL_VALUE.equals(fieldValueString))
                                {
                                    // This is a null value.
                                    fieldValueString = null;
                                }

                                // Set the field value:
                                field.set(object, fieldValueString);
                            }
                            catch (NoSuchFieldException | IllegalAccessException e)
                            {
                            }
                        }
                    }
                }
                return object;
            }
            catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                return null;
            }
        }
    }

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     *
     * @param object         The object to commit to the repo.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Create a new content area for the destination of the checkout:
        TArea area = createArea(areaFactory);

        // Serialize the object into the area:
        serializeObjectToContentArea(object, area, contentFactory, repo);

        // Commit the area:
        return commit(area, message, repo, byteArrayIndex, clock);
    }

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param object         The object to commit to the repo.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @param parentCommit   The parent commit that we want to make this commit from.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, TCommit parentCommit)
    {
        // Create a new content area for the destination of the checkout:
        TArea area = createArea(areaFactory);

        // Serialize the object into the area:
        serializeObjectToContentArea(object, area, contentFactory, repo);

        // Commit the area:
        return commit(area, message, repo, byteArrayIndex, clock, parentCommit);
    }

    /**
     * Commit the given content to the repo.
     * The commit is registered as a new commit root because it has no parents and there is no branch pointing at it.
     * It tracks the given commit as the parent.
     *
     * @param object             The object to commit to the repo.
     * @param message            The commit message.
     * @param repo               The repo to commit the content area to.
     * @param byteArrayIndex     The byte array index to use when creating snap-shots for the content.
     * @param clock              The clock to use for generating the timestamp for the commit.
     * @param areaFactory        The user specified factory method for the specific type of content area to create.
     * @param contentFactory     The content factory to use when populating the content area.
     * @param firstParentCommit  The parent commit that we want to make this commit from.
     * @param otherParentCommits The other parents to have in addition to the first parent commit.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commitObject(Object object, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory, TCommit firstParentCommit, List<TCommit> otherParentCommits)
    {
        // Create a new content area for the destination of the checkout:
        TArea area = createArea(areaFactory);

        // Serialize the object into the area:
        serializeObjectToContentArea(object, area, contentFactory, repo);

        // Commit the area:
        return commit(area, message, repo, byteArrayIndex, clock, firstParentCommit, otherParentCommits);
    }

    /**
     * Commit the given content to the repo.
     *
     * @param object         The object to commit to the repo.
     * @param branchName     The name of the branch to commit to. The branch is created if it doesn't already exist.
     * @param message        The commit message.
     * @param repo           The repo to commit the content area to.
     * @param byteArrayIndex The byte array index to use when creating snap-shots for the content.
     * @param clock          The clock to use for generating the timestamp for the commit.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return The commit for this content area.
     */
    @Override
    public TCommit commitObjectToBranch(Object object, String branchName, String message, TRepo repo, ByteArrayIndex byteArrayIndex, Clock<? extends Timestamp> clock, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Create a new content area for the destination of the checkout:
        TArea area = createArea(areaFactory);

        // Serialize the object into the area:
        serializeObjectToContentArea(object, area, contentFactory, repo);

        // Commit the area:
        return commitToBranch(area, branchName, message, repo, byteArrayIndex, clock);
    }

    /**
     * Checks out the object for the given commit.
     *
     * @param commit         The commit to check out.
     * @param repo           The repo to check out from.
     * @param areaFactory    The user specified factory method for the specific type of content area to create.
     * @param contentFactory The content factory to use when populating the content area.
     * @return A new object with the content from the checkout.
     */
    @Override
    public Object checkoutObject(TCommit commit, TRepo repo, AreaFactory<TContent, TArea> areaFactory, ContentFactory<TContent> contentFactory)
    {
        // Checkout the area for the commit:
        TArea area = checkout(commit, repo, areaFactory, contentFactory);

        // Deserialize the content area into an object:
        return deserializeObjectFromContentArea(area, repo);
    }
}
