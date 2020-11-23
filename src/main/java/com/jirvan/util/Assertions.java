/*

Copyright (c) 2013, Jirvan Pty Ltd
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Jirvan Pty Ltd nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package com.jirvan.util;

import java.io.*;

public class Assertions {

    /**
     * Asserts that a file does not exist.  If the file
     * exists, then an {@link AssertionError} with a default message is thrown.
     *
     * @param file the file to be checked
     * @throws AssertionError with a default message
     * @see #assertFileDoesNotExist(File, String)
     */
    public static void assertFileDoesNotExist(File file) {
        if (file.exists()) {
            throw new AssertionError(String.format("File \"%s\" exists", file.getAbsolutePath()));
        }
    }

    /**
     * Asserts that a file does not exist.  If the file
     * exists, then an {@link AssertionError} with the provided
     * message is thrown (or a default message if provided message is null).
     *
     * @param file    the file to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertFileDoesNotExist(File)
     */
    public static void assertFileDoesNotExist(File file, String message) {
        if (file.exists()) {
            throw message == null
                  ? new AssertionError(String.format("File \"%s\" exists", file.getAbsolutePath()))
                  : new AssertionError(message);
        }
    }

    /**
     * Asserts that a file exists.  If the file does not
     * exist, then an {@link AssertionError} with a default message
     * is thrown.
     *
     * @param file the file to be checked
     * @throws AssertionError with a default message
     * @see #assertFileDoesNotExist(File)
     */
    public static void assertFileExists(File file) {
        assertFileExists(file, null);
    }

    /**
     * Asserts that a file exists.  If the file does not
     * exist, then an {@link AssertionError} with the provided
     * message is thrown (or a default message if provided message is null).
     *
     * @param file    the file to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertFileDoesNotExist(File)
     */
    public static void assertFileExists(File file, String message) {
        if (!file.exists()) {
            throw message == null
                  ? new AssertionError(String.format("File \"%s\" does not exist", file.getAbsolutePath()))
                  : new AssertionError(message);
        }
    }

    /**
     * Asserts that a file exists and is a normal file (not for example
     * a directory).  If the file does not exist or is not a file, then
     * an {@link AssertionError} with a default message is thrown.
     *
     * @param file the file to be checked
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertIsDirectory(File)
     */
    public static void assertIsFile(File file) {
        assertIsFile(file, null);
    }

    /**
     * Asserts that a file exists and is a normal file (not for example
     * a directory).  If the file does not exist or is not a file, then
     * an {@link AssertionError} with the provided message is thrown
     * (or a default message if provided message is null).
     *
     * @param file    the file to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertIsDirectory(File, String)
     */
    public static void assertIsFile(File file, String message) {
        if (!file.isFile()) {
            throw message == null
                  ? new AssertionError(String.format("File \"%s\" does not exist or is not a file", file.getAbsolutePath()))
                  : new AssertionError(message);
        }
    }

    /**
     * Asserts that a file exists and is a directory (not for example
     * a normal file).  If the file does not exist or is not a directory,
     * then an {@link AssertionError} with a default message is thrown.
     *
     * @param file the file to be checked
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertIsFile(File, String)
     */
    public static void assertIsDirectory(File file) {
        assertIsDirectory(file, null);
    }

    /**
     * Asserts that a file exists and is a directory (not for example
     * a normal file).  If the file does not exist or is not a directory,
     * then an {@link AssertionError} with the provided message is thrown
     * (or a default message if provided message is null).
     *
     * @param file    the file to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails (or a default message if provided message is null)
     * @see #assertIsFile(File, String)
     */
    public static void assertIsDirectory(File file, String message) {
        if (!file.isDirectory()) {
            throw message == null
                  ? new AssertionError(String.format("Directory \"%s\" does not exist or is not a directory", file.getAbsolutePath()))
                  : new AssertionError(message);
        }
    }

    /**
     * Asserts that a condition is true.  If the assertion
     * fails, then an {@link AssertionError} with the provided
     * message is thrown.
     *
     * @param condition the condition to be checked
     * @param message   the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) throw message == null ? new AssertionError() : new AssertionError(message);
    }

    /**
     * Asserts that a condition is true.  If the assertion
     * fails, then an {@link AssertionError} is thrown.
     *
     * @param condition the condition to be checked
     * @throws AssertionError if the assertion fails
     */
    public static void assertTrue(boolean condition) {
        assertTrue(condition, null);
    }

    /**
     * Asserts that an object is an instance of the given class.  If the
     * assertion fails, then an {@link AssertionError} with the provided
     * message is thrown.
     *
     * @param object  the object to be checked
     * @param aClass  the class the object needs to be checked against
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertObjectIsInstanceof(Object object, Class aClass, String message) {
        if (!aClass.isInstance(object)) {
            throw message != null
                  ? new AssertionError(message)
                  : new AssertionError(String.format("Object was expected to be a %s (it was a %s)",
                                                     aClass.getName(),
                                                     object.getClass().getName()));
        }
    }

    /**
     * Asserts that an object is an instance of the given class.  If the
     * assertion fails, then an {@link AssertionError}  is thrown.
     *
     * @param object the object to be checked
     * @param aClass the class the object needs to be checked against
     * @throws AssertionError if the assertion fails
     */
    public static void assertObjectIsInstanceof(Object object, Class aClass) {
        assertObjectIsInstanceof(object, aClass, null);
    }

    /**
     * Asserts that an object is not null.  If the assertion fails, then
     * an {@link AssertionError} with the provided message is thrown.
     *
     * @param object  the object to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw message != null
                  ? new AssertionError(message)
                  : new AssertionError("Object was not expected to be null");
        }
    }

    /**
     * Asserts that an object is not null.  If the assertion fails, then
     * an {@link AssertionError} is thrown.
     *
     * @param object the object to be checked
     * @throws AssertionError if the assertion fails
     */
    public static void assertNotNull(Object object) {
        assertNotNull(object, null);
    }

    /**
     * Asserts that an object is null.  If the assertion fails, then
     * an {@link AssertionError} with the provided message is thrown.
     *
     * @param object  the object to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertNull(Object object, String message) {
        if (object != null) {
            throw message != null
                  ? new AssertionError(message)
                  : new AssertionError("Object was expected to be null");
        }
    }

    /**
     * Asserts that 2 objects are equal.  If the assertion fails, then
     * an {@link AssertionError} is thrown.
     *
     * @param object1 the first object to be checked
     * @param object2 the second object to be checked
     * @throws AssertionError if the assertion fails
     */
    public static void assertEquals(Object object1, Object object2) {
        assertEquals(object1, object2, null);
    }

    /**
     * Asserts that 2 objects are equal.  If the assertion fails, then
     * an {@link AssertionError} with the provided message is thrown.
     *
     * @param object1 the first object to be checked
     * @param object2 the second object to be checked
     * @param message the message associated with failure
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertEquals(Object object1, Object object2, String message) {
        if (object1 == null) {
            if (object2 != null) {
                throw new AssertionError(message != null ? message : String.format("Object1 was expected to be equal to Object2\n" +
                                                                                   "(Object 1 was null and object 2 was \"%s\")",
                                                                                   object2.toString()));
            }
        } else {
            if (object2 == null) {
                throw new AssertionError(message != null ? message : String.format("Object1 was expected to be equal to Object2\n" +
                                                                                   "(Object 2 was null and object 1 was \"%s\")",
                                                                                   object1.toString()));
            } else if (!object2.equals(object1)) {
                throw new AssertionError(message != null ? message : String.format("Object1 was expected to be equal to Object2\n" +
                                                                                   "(Object 1 was \"%s\"" +
                                                                                   " and Object 2 was \"%s\")",
                                                                                   object1.toString(),
                                                                                   object2.toString()));
            }
        }
    }

    /**
     * Asserts that an object is null.  If the assertion fails, then
     * an {@link AssertionError} is thrown.
     *
     * @param object the object to be checked
     * @throws AssertionError with the provided message if the assertion fails
     */
    public static void assertNull(Object object) {
        assertNull(object, null);
    }

}
