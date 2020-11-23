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

import com.jirvan.dates.Day;
import com.jirvan.dates.Hour;
import com.jirvan.dates.Millisecond;
import com.jirvan.dates.Minute;
import com.jirvan.dates.Month;
import com.jirvan.dates.Second;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.jirvan.util.Assertions.*;

public class Utl {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> T validate(T object) {
        return validate(null, object);
    }

    public static <T> T validate(String objectName, T object) {

        Set<ConstraintViolation<T>> constraintViolations = getConstraintViolations(object);

        if (constraintViolations.size() > 0) {

            // Sort the violations by property path
            SortedSet<ConstraintViolation<T>> sortedConstraintViolations = new TreeSet<ConstraintViolation<T>>(new Comparator<ConstraintViolation<T>>() {
                @Override public int compare(ConstraintViolation<T> o1, ConstraintViolation<T> o2) {
                    if (o1 == null || o1.getPropertyPath() == null || o2 == null || o2.getPropertyPath() == null) {
                        return 0;
                    } else {
                        return o1.getPropertyPath().toString().compareTo(o2.getPropertyPath().toString());
                    }
                }
            });
            sortedConstraintViolations.addAll(constraintViolations);

            // Construct message and throw exception
            String messagePrefix = objectName == null ? "Validation failure: " : "Invalid " + objectName + ": ";
            String indent = messagePrefix.replaceAll(".", " ");

            String message = "";
            for (ConstraintViolation<T> constraintViolation : sortedConstraintViolations) {
                if (message.equals("")) {
                    message += messagePrefix + constraintViolation.getPropertyPath().toString() + " " + constraintViolation.getMessage();
                } else {
                    message += "\n" + indent + constraintViolation.getPropertyPath().toString() + " " + constraintViolation.getMessage();
                }
            }
            throw new ConstraintViolationException(message, constraintViolations);

        }

        return object;

    }

    public static <T> boolean isValid(T object) {
        return getConstraintViolations(object).size() == 0;
    }

    public static <T> Set<ConstraintViolation<T>> getConstraintViolations(T object) {
        return validator.validate(object);
    }

    public static <T> ArrayList<T> newArrayList(T... items) {
        ArrayList<T> list = new ArrayList<T>();
        for (T item : items) {
            list.add(item);
        }
        return list;
    }

    public static <T> T[] merge(T firstItem, T[] theRest) {
        if (firstItem == null) {
            throw new RuntimeException("Jdbc.merge(T firstItem, T[] theRest): firstItem must not be null (if this is a possibility you should use merge(Class<?> arrayClass, T firstItem, T[] theRest) instead.");
        }
        return merge(firstItem.getClass(), firstItem, theRest);
    }

    public static <T> T[] merge(Class<?> arrayClass, T firstItem, T[] theRest) {
        @SuppressWarnings("unchecked")
        T[] mergedArray = (T[]) Array.newInstance(arrayClass, theRest.length + 1);
        mergedArray[0] = firstItem;
        for (int i = 0; i < theRest.length; i++) {
            mergedArray[i + 1] = theRest[i];
        }
        return mergedArray;
    }

    public static boolean exactlyOneNotNull(Object... objects) {
        boolean nullFound = false;
        for (Object object : objects) {
            if (object != null) {
                if (nullFound) return false;
                nullFound = true;
            }
        }
        return nullFound;
    }

    public static <T> T coalesce(T... objects) {
        for (T object : objects) {
            if (object != null) return object;
        }
        return null;
    }

    public static boolean areEqual(Integer value1, Integer value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Long value1, Long value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Float value1, Float value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Double value1, Double value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(BigInteger value1, BigInteger value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqualIgnoringScale(BigDecimal value1, BigDecimal value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.compareTo(value2) == 0;
        }
    }

    public static boolean areEqual(String value1, String value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Date value1, Date value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Month value1, Month value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Day value1, Day value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(LocalDate value1, LocalDate value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(LocalDateTime value1, LocalDateTime value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Hour value1, Hour value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Minute value1, Minute value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Second value1, Second value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static boolean areEqual(Millisecond value1, Millisecond value2) {
        if (value1 == null) {
            return value2 == null;
        } else {
            return value1.equals(value2);
        }
    }

    public static String trim(String value) {
        if (value == null) {
            return null;
        } else {
            return value.trim();
        }
    }

    public static String nullIfBlank(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value;
        }
    }

    public static String trimAndNullIfBlank(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return value.trim();
        }
    }


    public static Long toLong(Date value) {
        if (value == null) {
            return null;
        } else {
            return value.getTime();
        }
    }

    public static Long toLong(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return Long.parseLong(value);
        }
    }

    public static Integer toInteger(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Boolean toBoolean(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            if (Strings.isIn(value.toLowerCase(), "t", "true", "y", "yes", "1")) {
                return true;
            } else if (Strings.isIn(value.toLowerCase(), "f", "false", "n", "no", "0")) {
                return false;
            } else {
                throw new RuntimeException(String.format("Invalid boolean string \"%s\"\nvalid values are \"t\", \"true\", \"y\", \"yes\", \"1\",\"f\", \"false\", \"n\", \"no\", \"0\"", value));
            }
        }
    }

    public static BigDecimal toBigDecimal(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        } else {
            return new BigDecimal(value);
        }
    }

    public static String getStackTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try {
            t.printStackTrace(printWriter);
        } finally {
            printWriter.close();
        }
        return stringWriter.toString();
    }

    public static <T extends Comparable> T min(T... values) {
        assertTrue(values.length > 0, "At least one value must be provided");
        T min = null;
        for (T value : values) {
            assertTrue(value != null, "Value is null");
            if (min == null || value.compareTo(min) < 0) {
                min = value;
            }
        }
        return min;
    }

    public static <T extends Comparable> T max(T... values) {
        assertTrue(values.length > 0, "At least one value must be provided");
        T max = null;
        for (T value : values) {
            assertTrue(value != null, "Value is null");
            if (max == null || value.compareTo(max) > 0) {
                max = value;
            }
        }
        return max;
    }

}
