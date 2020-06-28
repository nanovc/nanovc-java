/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc;

import io.nanovc.areas.StringAreaAPI;
import io.nanovc.areas.StringLinkedHashMapArea;
import io.nanovc.content.StringContent;

import java.time.Instant;

/**
 * This is a helper method for creating common commit tags.
 * This acts as a factory and builder for {@link StringAreaAPI}'s that are used during commits.
 * You don't have to use this class because you can pass in your own {@link StringAreaAPI}'s when committing
 * but it does help to use this code to make commits neat and tidy.
 * These commit tags can be used in a fluent manner to chain calls together.
 */
public class CommitTags
    extends StringLinkedHashMapArea
{
    /**
     * This holds common paths for commit tags.
     */
    public static class CommonPaths
    {
        /**
         * This is the relative path to use for timestamps.
         */
        public static final String TIMESTAMP_PATH = "/timestamp";

        /**
         * The path to the author commit tag.
         * Author is usually the same as committer but in certain rare cases (like in Git),
         * the author is the person that created the content but the committer is the one
         * that actually commits it to version control on the authors behalf.
         */
        public static final String AUTHOR_PATH = "/author";

        /**
         * The path to the timestamp for the author tag.
         */
        public static final String AUTHOR_TIMESTAMP_PATH = AUTHOR_PATH + TIMESTAMP_PATH;

        /**
         * The path to the committer commit tag.
         * Committer is usually the same as author but in certain rare cases (like in Git),
         * the author is the person that created the content but the committer is the one
         * that actually commits it to version control on the authors behalf.
         */
        public static final String COMMITTER_PATH = "/committer";

        /**
         * The path to the timestamp for the committer tag.
         */
        public static final String COMMITTER_TIMESTAMP_PATH = COMMITTER_PATH + TIMESTAMP_PATH;

        /**
         * This is the path to use for a lengthier description compared to the message for the commit.
         */
        public static final String DESCRIPTION_PATH = "/description";
    }

    /**
     * Creates empty commit tags that can be used to chain further calls.
     *
     * @return Empty commit tags that can be chained in a fluent manner.
     */
    public static CommitTags with()
    {
        return new CommitTags();
    }

    /**
     * Creates empty commit tags.
     *
     * @return Empty commit tags that can be chained in a fluent manner.
     */
    public static CommitTags none()
    {
        return new CommitTags();
    }

    /**
     * Creates commit tags with the given value.
     * Use this to define your own purpose specific tags.
     *
     * @param pathToTag The path to the tag. If this doesn't have the / prefix then it is automatically added and the path is understood as being relative to the root.
     * @param value     The value for the tag.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags with(String pathToTag, String value)
    {
        return new CommitTags()
            .and(pathToTag, value);
    }

    /**
     * Creates commit tags with the given value.
     * Use this to define your own purpose specific tags.
     *
     * @param pathToTag The path to the tag. The absolute path is used.
     * @param value     The value for the tag.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags with(RepoPath pathToTag, String value)
    {
        return new CommitTags()
            .and(pathToTag, value);
    }

    /**
     * Creates new commit tags and adds all the other tags without modifying them.
     * Use this to mixin other standard tags into the new tags.
     *
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags with(StringAreaAPI otherTags)
    {
        CommitTags commitTags = new CommitTags();
        for (AreaEntry<StringContent> otherTag : otherTags)
        {
            commitTags.putString(otherTag.path, otherTag.content.getValue());
        }
        return commitTags;
    }

    /**
     * Defines the author for the content of the commit.
     * Use the author tag by default instead of committer because that is more common.
     * Author is usually the same as committer but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author Defines the author for the content of the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthor(String author)
    {
        return
            new CommitTags()
                .author(author);
    }

    /**
     * Defines the author for the content of the commit and the timestamp when the content was created.
     * Use the author tag by default instead of committer because that is more common.
     * Author is usually the same as committer but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author    Defines the author for the content of the commit.
     * @param timestamp The timestamp for the author of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorAndTimestamp(String author, TimestampAPI timestamp)
    {
        return
            new CommitTags()
                .author(author)
                .authorTimestamp(timestamp);
    }

    /**
     * Defines the author for the content of the commit and the timestamp when the content was created.
     * Use the author tag by default instead of committer because that is more common.
     * Author is usually the same as committer but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author    Defines the author for the content of the commit.
     * @param timestamp The timestamp for the author of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorAndTimestamp(String author, Instant timestamp)
    {
        return
            new CommitTags()
                .author(author)
                .authorTimestamp(timestamp);
    }

    /**
     * Defines the person creating the commit.
     * Use the author tag by default instead of committer because that is more common.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param committer Defines the person creating the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withCommitter(String committer)
    {
        return
            new CommitTags()
                .committer(committer);
    }

    /**
     * Defines the person creating the commit and the timestamp when the commit was created.
     * Use the author tag by default instead of committer because that is more common.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param committer Defines the person creating the commit.
     * @param timestamp The timestamp for the committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withCommitterAndTimestamp(String committer, TimestampAPI timestamp)
    {
        return
            new CommitTags()
                .committer(committer)
                .committerTimestamp(timestamp);
    }

    /**
     * Defines the person creating the commit and the timestamp when the commit was created.
     * Use the author tag by default instead of committer because that is more common.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param committer Defines the person creating the commit.
     * @param timestamp The timestamp for the committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withCommitterAndTimestamp(String committer, Instant timestamp)
    {
        return
            new CommitTags()
                .committer(committer)
                .committerTimestamp(timestamp);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param authorAndCommitter Defines the person that authored the content and created the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorAndCommitter(String authorAndCommitter)
    {
        return
            new CommitTags()
                .author(authorAndCommitter)
                .committer(authorAndCommitter);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * It also sets the timestamp for both of them.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param authorAndCommitter Defines the person that authored the content and created the commit.
     * @param timestamp          The timestamp for the author and committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorCommitterAndTimestamp(String authorAndCommitter, TimestampAPI timestamp)
    {
        return
            new CommitTags()
                .author(authorAndCommitter)
                .authorTimestamp(timestamp)
                .committer(authorAndCommitter)
                .committerTimestamp(timestamp);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * It also sets the timestamp for both of them.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param authorAndCommitter Defines the person that authored the content and created the commit.
     * @param timestamp          The timestamp for the author and committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorCommitterAndTimestamp(String authorAndCommitter, Instant timestamp)
    {
        return
            new CommitTags()
                .author(authorAndCommitter)
                .authorTimestamp(timestamp)
                .committer(authorAndCommitter)
                .committerTimestamp(timestamp);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author    Defines the author for the content of the commit.
     * @param committer Defines the person creating the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorAndCommitter(String author, String committer)
    {
        return
            new CommitTags()
                .author(author)
                .committer(committer);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * It also sets the timestamp for both of them.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author    Defines the author for the content of the commit.
     * @param committer Defines the person creating the commit.
     * @param timestamp The timestamp for the author and committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorCommitterAndTimestamp(String author, String committer, TimestampAPI timestamp)
    {
        return
            new CommitTags()
                .author(author)
                .authorTimestamp(timestamp)
                .committer(committer)
                .committerTimestamp(timestamp);
    }

    /**
     * Defines the author of the content and the person creating the commit.
     * It also sets the timestamp for both of them.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author    Defines the author for the content of the commit.
     * @param committer Defines the person creating the commit.
     * @param timestamp The timestamp for the author and committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withAuthorCommitterAndTimestamp(String author, String committer, Instant timestamp)
    {
        return
            new CommitTags()
                .author(author)
                .authorTimestamp(timestamp)
                .committer(committer)
                .committerTimestamp(timestamp);
    }

    /**
     * A lengthier description of the commit.
     * Use this to put more information that isn't appropriate in the commit message.
     *
     * @param description A lengthier description of the commit. Use this to put more information that isn't appropriate in the commit message.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public static CommitTags withDescription(String description)
    {
        return
            new CommitTags()
                .description(description);
    }

    /**
     * Defines the author for the content of the commit.
     * Use the author tag by default instead of committer because that is more common.
     * Author is usually the same as committer but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param author Defines the author for the content of the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags author(String author)
    {
        putString(CommonPaths.AUTHOR_PATH, author);
        return this;
    }

    /**
     * Defines the timestamp for the author of the content.
     * The format for the timestamp is ISO-8601 representation which is the same as
     * {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME}.
     *
     * @param timestamp The timestamp for the author of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags authorTimestamp(TimestampAPI timestamp)
    {
        putString(CommonPaths.AUTHOR_TIMESTAMP_PATH, timestamp.getInstant().toString());
        return this;
    }

    /**
     * Defines the timestamp for the author of the content.
     * The format for the timestamp is ISO-8601 representation which is the same as
     * {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME}.
     *
     * @param timestamp The timestamp for the author of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags authorTimestamp(Instant timestamp)
    {
        putString(CommonPaths.AUTHOR_TIMESTAMP_PATH, timestamp.toString());
        return this;
    }

    /**
     * Defines the person creating the commit.
     * Use the author tag by default instead of committer because that is more common.
     * Committer is usually the same as author but in certain rare cases (like in Git),
     * the author is the person that created the content but the committer is the one
     * that actually commits it to version control on the authors behalf.
     *
     * @param committer Defines the person creating the commit.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags committer(String committer)
    {
        putString(CommonPaths.COMMITTER_PATH, committer);
        return this;
    }

    /**
     * Defines the timestamp for the committer of the content.
     * The format for the timestamp is ISO-8601 representation which is the same as
     * {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME}.
     *
     * @param timestamp The timestamp for the committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags committerTimestamp(TimestampAPI timestamp)
    {
        putString(CommonPaths.COMMITTER_TIMESTAMP_PATH, timestamp.getInstant().toString());
        return this;
    }

    /**
     * Defines the timestamp for the committer of the content.
     * The format for the timestamp is ISO-8601 representation which is the same as
     * {@link java.time.format.DateTimeFormatter#ISO_DATE_TIME}.
     *
     * @param timestamp The timestamp for the committer of this content.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags committerTimestamp(Instant timestamp)
    {
        putString(CommonPaths.COMMITTER_TIMESTAMP_PATH, timestamp.toString());
        return this;
    }

    /**
     * A lengthier description of the commit.
     * Use this to put more information that isn't appropriate in the commit message.
     *
     * @param description A lengthier description of the commit. Use this to put more information that isn't appropriate in the commit message.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags description(String description)
    {
        putString(CommonPaths.DESCRIPTION_PATH, description);
        return this;
    }


    /**
     * Adds an arbitrary tag with the given value.
     * Use this to define your own purpose specific tags.
     *
     * @param pathToTag The path to the tag. If this doesn't have the / prefix then it is automatically added and the path is understood as being relative to the root.
     * @param value     The value for the tag.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags and(String pathToTag, String value)
    {
        this.putString(pathToTag, value);
        return this;
    }

    /**
     * Adds an arbitrary tag with the given value.
     * Use this to define your own purpose specific tags.
     *
     * @param pathToTag The path to the tag. The absolute path is used.
     * @param value     The value for the tag.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags and(RepoPath pathToTag, String value)
    {
        this.putString(pathToTag, value);
        return this;
    }

    /**
     * Adds all the given tags to the current tags without modifying the other tags.
     * Use this to mixin other standard tags into these tags.
     *
     * @param otherTags The other tags to mix in to these tags.
     * @return The modified tags so that they can be chained in a fluent manner.
     */
    public CommitTags and(StringAreaAPI otherTags)
    {
        for (AreaEntry<StringContent> otherTag : otherTags)
        {
            this.putString(otherTag.path, otherTag.content.getValue());
        }
        return this;
    }
}
