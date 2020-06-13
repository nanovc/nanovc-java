# nanovc-java
![NanoVC Java Implementation](https://github.com/nanovc/nanovc-java/workflows/NanoVC%20Java%20Implementation/badge.svg)

Java implementation of Nano Version Control.



The web page describing Nano Version Control is here:
http://nanovc.io

To understand the basic idea, look at this blog post:
http://nanovc.io/2020/05/25/the-basic-idea/


### Getting Started Example

```@Test
 public void testHelloWorld()
 {
     // Create the repo:
     StringNanoRepo repo = new StringNanoRepo();

     // Create an area for us to put content:
     // NOTE: Think of this as a mini-filesystem.
     StringHashMapArea contentArea = repo.createArea();

     contentArea.putString("Hello", "World");
     contentArea.putString("Static", "Content");
     contentArea.putString("Mistake", "Honest");

     // Commit the content:
     MemoryCommit commit1 = repo.commit(contentArea, "First commit!");

     // Modify content:
     contentArea.putString("Hello", "Nano World");

     // Remove unwanted content:
     contentArea.removeContent("Mistake");

     // The content area supports paths:
     contentArea.putString(RepoPath.at("Hello").resolve("Info"), "Details");

     // And even emoji's:
     contentArea.putString(RepoPath.at("🔧").resolve("👍"), "I ❤ NanoVC‼");

     // Commit again, but this time to a branch:
     MemoryCommit commit2 = repo.commitToBranch(contentArea, "master", "Second commit.");

     // Get the difference between the two commits:
     Comparison comparison = repo.computeComparisonBetweenCommits(commit1, commit2);
     assertEquals(
         "/Hello : Changed\n" +
         "/Hello/Info : Added\n" +
         "/Mistake : Deleted\n" +
         "/Static : Unchanged\n" +
         "/🔧/👍 : Added",
         comparison.asListString()
     );
 }
```
