/*
 MIT License
 https://opensource.org/licenses/MIT
 Copyright 2020 Lukasz Machowski

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.nanovc.memory.reflective;

import io.nanovc.Record;
import io.nanovc.areas.ByteArrayHashMapArea;
import io.nanovc.indexes.ByteArrayIndex;
import io.nanovc.indexes.HashWrapperByteArrayIndex;
import io.nanovc.memory.MemoryCommit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the creation of {@link ReflectiveObjectNanoRepo}'s.
 */
public class ReflectiveObjectNanoRepoTests
{
    @Test
    public void testCreation()
    {
        new ReflectiveObjectNanoRepo();
        new ReflectiveObjectNanoRepo(new HashWrapperByteArrayIndex());
        new ReflectiveObjectNanoRepo(
            new HashWrapperByteArrayIndex(),
            ReflectiveObjectNanoRepo.COMMON_ENGINE,
            ReflectiveObjectNanoRepo.COMMON_CLOCK,
            ReflectiveObjectNanoRepo.COMMON_DIFFERENCE_HANDLER,
            ReflectiveObjectNanoRepo.COMMON_COMPARISON_HANDLER,
            ReflectiveObjectNanoRepo.COMMON_MERGE_HANDLER
        );
    }

    @Test
    public void testUsage1()
    {
        // Create the repo:
        ReflectiveObjectNanoRepo repo = new ReflectiveObjectNanoRepo();

        // Create an employee to commit:
        Employee employee = new Employee();
        employee.firstName = "Lukasz";
        employee.lastName = "Machowski";

        // Commit the content:
        MemoryCommit first_commit = repo.commitObjectToBranch(employee, "master", "First commit");

        // Change the content:
        employee.firstName = "Luke";

        // Commit the changed content:
        MemoryCommit second_commit = repo.commitObjectToBranch(employee, "master", "Second Commit");

        // Create another branch:
        repo.createBranchAtCommit(first_commit, "alternate");

        // Make a change to the content:
        employee.lastName = "Sky Walker";

        // Commit the change:
        MemoryCommit memoryCommit = repo.commitObjectToBranch(employee, "alternate", "Alternate commit");

        // Merge the changes:
        MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master");

        // Get the merged content:
        ByteArrayHashMapArea mergedContent = repo.checkout(merge_commit);

        // Make sure the content is as expected:
        assertEquals("/fields/firstName : byte[4] ➡ 'Luke'\n" +
                     "/fields/lastName : byte[10] ➡ 'Sky Walker'\n" +
                     "/type : byte[66] ➡ 'io.nanovc.memory.reflective.ReflectiveObjectNanoRepoTests$Employee'", mergedContent.asListString());

        // Checkout the merged content:
        Employee mergedEmployee = (Employee) repo.checkoutObject(merge_commit);
        assertEquals("Employee{firstName='Luke', lastName='Sky Walker'}", mergedEmployee.toString());
    }

    @Test
    public void performanceTest1()
    {
        final int MAX = 1_000_000;

        // Create a shared byte array index so that we can reuse memory for common content.
        ByteArrayIndex sharedByteArrayIndex = new HashWrapperByteArrayIndex();

        // Keep track of the repo's:
        int count = 0;
        int employeeToStringCharacterCount = 0;

        // Start timing:
        long startTime = System.nanoTime();

        for (int i = 0; i < MAX; i++)
        {
            // Create the repo:
            ReflectiveObjectNanoRepo repo = new ReflectiveObjectNanoRepo(sharedByteArrayIndex);

            // Create the employee:
            Employee employee = new Employee();
            employee.firstName = "John";
            employee.lastName = "Smith";

            // Commit the content:
            MemoryCommit first_commit = repo.commitObjectToBranch(employee, "master", "First commit");

            // Change the content:
            employee.lastName = "Smith " + (i + 1);

            // Commit the changed content:
            MemoryCommit second_commit = repo.commitObjectToBranch(employee, "master", "Second Commit");

            // Create another branch:
            repo.createBranchAtCommit(first_commit, "alternate");

            // Make a change to the content:
            employee.firstName = "John" + (i + 1);

            // Commit the change:
            MemoryCommit memoryCommit = repo.commitObjectToBranch(employee, "alternate", "Alternate commit");

            // Merge the changes:
            MemoryCommit merge_commit = repo.mergeIntoBranchFromAnotherBranch("master", "alternate", "Merging Alternate Branch into Master");

            // Get the merged content:
            Employee mergedEmployee = (Employee) repo.checkoutObject(merge_commit);

            // Increase our counters:
            count++;
            employeeToStringCharacterCount += mergedEmployee.toString().length();
        }

        // End timing:
        long endTime = System.nanoTime();
        long durationNanos = endTime - startTime;
        System.out.printf("Duration = %,d ns%n", durationNanos);
        System.out.printf("Duration = %,d ms%n", durationNanos / 1_000_000);
        System.out.printf("Count = %,d repos%n", MAX);
        System.out.printf("Employee String Characters = %,d repos%n", employeeToStringCharacterCount);
        System.out.printf("Rate = %,d repos/sec%n", MAX * 1_000_000_000L / durationNanos);
        System.out.printf("Employee Character Rate = %,d chars/sec%n", employeeToStringCharacterCount * 1_000_000_000L / durationNanos);


        // Make sure that the count has a value:
        assertTrue(count > 0);
    }

    @Record
    public static class Employee
    {
        public String firstName;
        public String lastName;

        @Override
        public String toString()
        {
            return "Employee{" +
                   "firstName='" + firstName + '\'' +
                   ", lastName='" + lastName + '\'' +
                   '}';
        }
    }
}
