package io.nanovc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper methods for unit testing.
 */
public class TestHelper
{
    private static int counter = 0;

    /**
     * This creates a new path for testing output.
     *
     * @return The path to the testing output.
     */
    public static Path createTestingOutputFolderPath()
    {
        return Paths.get(".", "..", "test-output", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + Integer.toString(counter++));
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It creates the directory at the path if it doesn't already exist.
     * @param testClassName The name of the test class that is being run.
     * @param testName The name of the test that is running.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPath(String testClassName, String testName)
    {
        return createTestOutputPath(testClassName, testName, true);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * @param testClassName The name of the test class that is being run.
     * @param testName The name of the test that is running.
     * @param createIfNecessary True to create the folder if it doesn't already exist. False to just get the path without creating the folder.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPath(String testClassName, String testName, boolean createIfNecessary)
    {
        // Create the path with the class name:
        Path testOutputPath = createTestOutputPath(testClassName, createIfNecessary).resolve(testName);

        // Create the path if necessary:
        createPathIfNecessary(testOutputPath, createIfNecessary);

        return testOutputPath;
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It creates the directory at the path if it doesn't already exist.
     * @param testClassName The name of the test class that is being run.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPath(String testClassName)
    {
        return createTestOutputPath(testClassName, true);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * @param testClassName The name of the test class that is being run.
     * @param createIfNecessary True to create the folder if it doesn't already exist. False to just get the path without creating the folder.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPath(String testClassName, boolean createIfNecessary)
    {
        // Split the class name so we just get the last part:
        String[] classParts = testClassName.split("\\.");

        // Use the last part of the class name:
        String className = classParts[classParts.length - 1];

        // Get a path for the test output:
        Path testOutputPath = createTestingOutputFolderPath().resolve(className);

        // Create the path if necessary:
        createPathIfNecessary(testOutputPath, createIfNecessary);

        return testOutputPath;
    }

    /**
     * Creates a directory at the given path if necessary, if it doesn't already exist.
     * @param pathToCheck The path to check.
     * @param createIfNecessary If this is true then we create the directory at the given path if it doesn't exist. If this is false then it does nothing.
     */
    public static void createPathIfNecessary(Path pathToCheck, boolean createIfNecessary)
    {
        // Check whether we must create the directory:
        if (createIfNecessary)
        {
            // Check whether the directory exists:
            if (!Files.exists(pathToCheck))
            {
                // The directory doesn't exist.
                try
                {
                    // Create the directory:
                    Files.createDirectories(pathToCheck);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a directory at the given path if necessary, if it doesn't already exist.
     * @param pathToCheck The path to check.
     */
    public static void createPathIfNecessary(Path pathToCheck)
    {
        createPathIfNecessary(pathToCheck, true);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It inspects the stack trace to get an appropriate test and class name.
     * This is useful for unit tests so that we don't have to remember to update a "testName" variable for each test, which is often a source of test errors.
     * @param skip The number of levels to skip in the stack trace before we expect to have the real test class and method that is running. Usually between 2 and 4 depending on how many method calls we are away from the actual test method.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPathFromStackTraceClassAndMethod(int skip)
    {
        return createTestOutputPathFromStackTraceClassAndMethod(skip, true);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It inspects the stack trace to get an appropriate test and class name.
     * This is useful for unit tests so that we don't have to remember to update a "testName" variable for each test, which is often a source of test errors.
     * @param skip The number of levels to skip in the stack trace before we expect to have the real test class and method that is running. Usually between 2 and 4 depending on how many method calls we are away from the actual test method.
     * @param createFolder True to create the folder if it doesn't already exist. False to just get the path without creating the folder.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPathFromStackTraceClassAndMethod(int skip, boolean createFolder)
    {
        // Get the name for the folder for these tests:
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Skip the appropriate number of levels:
        StackTraceElement element = stackTrace[skip];

        // Create the test output path:
        return createTestOutputPath(element.getClassName(), element.getMethodName(), createFolder);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It inspects the stack trace to get an appropriate class name.
     * This is useful for unit tests so that we don't have to remember to update a "testName" variable for each test, which is often a source of test errors.
     * @param skip The number of levels to skip in the stack trace before we expect to have the real test class and method that is running. Usually between 2 and 4 depending on how many method calls we are away from the actual test method.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPathFromStackTraceClass(int skip)
    {
        return createTestOutputPathFromStackTraceClass(skip, true);
    }

    /**
     * Creates a path where the given test can produce output for the test.
     * It inspects the stack trace to get an appropriate class name.
     * This is useful for unit tests so that we don't have to remember to update a "testName" variable for each test, which is often a source of test errors.
     * @param skip The number of levels to skip in the stack trace before we expect to have the real test class and method that is running. Usually between 2 and 4 depending on how many method calls we are away from the actual test method.
     * @param createFolder True to create the folder if it doesn't already exist. False to just get the path without creating the folder.
     * @return A path where the test can write its output.
     */
    public static Path createTestOutputPathFromStackTraceClass(int skip, boolean createFolder)
    {
        // Get the name for the folder for these tests:
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Skip the appropriate number of levels:
        StackTraceElement element = stackTrace[skip];

        // Create the test output path:
        return createTestOutputPath(element.getClassName(), createFolder);
    }

    /**
     * Gets the name of the test method by looking at the stack trace.
     * This is useful for unit tests so that we don't have to remember to update a "testName" variable for each test, which is often a source of test errors.
     * @param skip The number of levels to skip in the stack trace before we expect to have the real test class and method that is running. Usually between 2 and 4 depending on how many method calls we are away from the actual test method.
     * @return The name of the test method.
     */
    public static String getTestMethodName(int skip)
    {
        // Get the name for the folder for these tests:
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // Skip the appropriate number of levels:
        StackTraceElement element = stackTrace[skip];

        return element.getMethodName();
    }

}
