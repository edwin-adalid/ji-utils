/*

Copyright (c) 2012,2013,2014 Jirvan Pty Ltd
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static com.jirvan.util.Assertions.*;

public class Strings {

    public static String constrainLengthWithDotDotDot(String string, int maxLength) {
        assertTrue(maxLength >= 3, "You cannot constrain length to less than 3");
        if (string == null) {
            return null;
        } else if (maxLength == 3) {
            return "...";
        } else if (string.length() <= maxLength) {
            return string;
        } else {
            return string.substring(0, maxLength - 4) + "...";
        }
    }

    public static String nullIfBlank(String string) {
        return string == null || string.trim().length() == 0 ? null : string;
    }

    public static boolean notBlank(String string) {
        return string != null && string.trim().length() > 0;
    }

    public static boolean isBlank(String string) {
        return !notBlank(string);
    }

    public static String[] appendToAll(String stringToAppend, String... strings) {
        if (stringToAppend == null) {
            return strings;
        } else if (strings == null) {
            return strings;
        } else {
            String[] returnArray = new String[strings.length];
            for (int i = 0; i < returnArray.length; i++) {
                returnArray[i] = strings[i] == null ? null : strings[i] + stringToAppend;
            }
            return returnArray;
        }
    }

    public static String[] prependToAll(String stringToAppend, String... strings) {
        if (stringToAppend == null) {
            return strings;
        } else if (strings == null) {
            return strings;
        } else {
            String[] returnArray = new String[strings.length];
            for (int i = 0; i < returnArray.length; i++) {
                returnArray[i] = strings[i] == null ? null : stringToAppend + strings[i];
            }
            return returnArray;
        }
    }

    /**
     * Returns a wrapped string for use in method chaining.
     * e.g. if (String.string(aString).isIn("some string", "another string", "yet another string")
     *
     * @param stringToWrap the string to wrap.
     * @return the wrapped string.
     */
    public static StringWrapper string(String stringToWrap) {
        return new StringWrapper(stringToWrap);
    }

    /**
     * Returns a boolean indicating whether or not a string is one of
     * a number of other strings.
     *
     * @param string  the string to look for.
     * @param strings the group of strings to look in
     * @return whether or not a string is a member of a group of strings
     */
    public static boolean isIn(String string, String... strings) {
        for (String thisString : strings) {
            if (string == null) {
                if (thisString == null) {
                    return true;
                }
            } else {
                if (string.equals(thisString)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a boolean indicating whether or not a string is one of
     * a number of other strings whilst ignoring the case.
     *
     * @param string  the string to look for.
     * @param strings the group of strings to look in
     * @return whether or not a string is a member of a group of strings.
     */
    public static boolean isIn(String string, Collection<String> strings) {
        for (String thisString : strings) {
            if (string == null) {
                if (thisString == null) {
                    return true;
                }
            } else {
                if (string.equals(thisString)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a boolean indicating whether or not a string is one of
     * a number of other strings whilst ignoring the case.
     *
     * @param string  the string to look for.
     * @param strings the group of strings to look in
     * @return whether or not a string is a member of a group of
     * strings (ignoring case).
     */
    public static boolean isInIgnoreCase(String string, Collection<String> strings) {
        for (String thisString : strings) {
            if (string == null) {
                if (thisString == null) {
                    return true;
                }
            } else {
                if (string.equalsIgnoreCase(thisString)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Returns a boolean indicating whether or not a string is one of
     * a number of other strings whilst ignoring the case.
     *
     * @param string  the string to look for.
     * @param strings the group of strings to look in
     * @return whether or not a string is a member of a group of
     * strings (ignoring case).
     */
    public static boolean isInIgnoreCase(String string, String... strings) {
        for (String thisString : strings) {
            if (string == null) {
                if (thisString == null) {
                    return true;
                }
            } else {
                if (string.equalsIgnoreCase(thisString)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String commaList(Object[] objects) {
        return join(objects, ',');
    }

    public static String doubleQuotesCommaList(Object[] objects) {
        return "\"" + join(objects, "\",\"") + "\"";
    }

    public static String singleQuotesCommaList(Object[] objects) {
        return "'" + join(objects, "','") + "'";
    }

    public static String doubleQuotesCommaSpaceList(Object[] objects) {
        return "\"" + join(objects, "\", \"") + "\"";
    }

    public static String singleQuotesCommaSpaceList(Object[] objects) {
        return "'" + join(objects, "', '") + "'";
    }

    public static String commaEllipsesList(Object[] objects, int maxItems) {
        if (objects.length <= maxItems) {
            return join(objects, ',');
        } else {
            List list = new ArrayList<String>();
            Iterator iterator = Arrays.asList(objects).iterator();
            int count = 0;
            while (iterator.hasNext() && count < maxItems) {
                count++;
                list.add(iterator.next());
            }
            list.add("...");
            return join(list, ',');
        }
    }

    public static String commaEllipsesListToString(Objects[] objects, int maxItems) {
        List<String> list = new ArrayList<String>();
        Iterator iterator = Arrays.asList(objects).iterator();
        int count = 0;
        while (iterator.hasNext() && count < maxItems) {
            count++;
            list.add(iterator.next().toString());
        }
        if (objects.length > maxItems) {
            list.add("...");
        }
        return join(list, ',');
    }

    public static String commaSpaceList(Object[] objects) {
        return join(objects, ", ");
    }

    public static String join(Object[] objects, String joinString) {
        StringBuffer buf = new StringBuffer();
        for (Object object : objects) {
            if (buf.length() != 0) buf.append(joinString);
            buf.append(object);
        }
        return buf.toString();
    }

    public static String join(Object[] objects, char joinChar) {
        StringBuffer buf = new StringBuffer();
        for (Object object : objects) {
            if (buf.length() != 0) buf.append(joinChar);
            buf.append(object);
        }
        return buf.toString();
    }

    public static String commaList(Collection objects) {
        return join(objects, ',');
    }

    public static String doubleQuotesCommaList(Collection objects) {
        return "\"" + join(objects, "\",\"") + "\"";
    }

    public static String singleQuotesCommaList(Collection objects) {
        return "'" + join(objects, "','") + "'";
    }

    public static String doubleQuotesCommaSpaceList(Collection objects) {
        return "\"" + join(objects, "\", \"") + "\"";
    }

    public static String singleQuotesCommaSpaceList(Collection objects) {
        return "'" + join(objects, "', '") + "'";
    }

    public static String commaEllipsesList(Collection objects, int maxItems) {
        if (objects.size() <= maxItems) {
            return join(objects, ',');
        } else {
            List<String> list = new ArrayList<String>();
            Iterator<String> iterator = objects.iterator();
            int count = 0;
            while (iterator.hasNext() && count < maxItems) {
                count++;
                list.add(iterator.next());
            }
            list.add("...");
            return join(list, ',');
        }
    }

    public static String commaEllipsesListToString(Collection objects, int maxItems) {
        List<String> list = new ArrayList<String>();
        Iterator iterator = objects.iterator();
        int count = 0;
        while (iterator.hasNext() && count < maxItems) {
            count++;
            list.add(iterator.next().toString());
        }
        if (objects.size() > maxItems) {
            list.add("...");
        }
        return join(list, ',');
    }

    public static String commaSpaceList(Collection objects) {
        return join(objects, ", ");
    }

    public static String join(Collection objects, String joinString) {
        StringBuffer buf = new StringBuffer();
        for (Object object : objects) {
            if (buf.length() != 0) buf.append(joinString);
            buf.append(object);
        }
        return buf.toString();
    }

    public static String join(Collection objects, char joinChar) {
        StringBuffer buf = new StringBuffer();
        for (Object object : objects) {
            if (buf.length() != 0) buf.append(joinChar);
            buf.append(object);
        }
        return buf.toString();
    }

    public static class StringWrapper {

        private java.lang.String wrappedString;

        public StringWrapper(java.lang.String stringToWrap) {
            this.wrappedString = stringToWrap;
        }

        /**
         * Returns a boolean indicating whether or not the string is one of
         * a number of other strings.
         *
         * @param strings the group of strings to look in
         * @return whether or not the string is a member of a group of strings
         */
        public boolean isIn(String... strings) {
            return Strings.isIn(wrappedString, strings);
        }

        /**
         * Returns a boolean indicating whether or not the string is one of
         * a number of other strings whilst ignoring the case.
         *
         * @param strings the group of strings to look in
         * @return whether or not the string is a member of a group of strings.
         */
        public boolean isIn(Collection<String> strings) {
            return Strings.isIn(wrappedString, strings);
        }

        /**
         * Returns a boolean indicating whether or not the string is one of
         * a number of other strings whilst ignoring the case.
         *
         * @param strings the group of strings to look in
         * @return whether or not the string is a member of a group of
         * strings (ignoring case).
         */
        public boolean isInIgnoreCase(Collection<String> strings) {
            return Strings.isInIgnoreCase(wrappedString, strings);
        }


        /**
         * Returns a boolean indicating whether or not the string is one of
         * a number of other strings whilst ignoring the case.
         *
         * @param strings the group of strings to look in
         * @return whether or not the string is a member of a group of
         * strings (ignoring case).
         */
        public boolean isInIgnoreCase(String... strings) {
            return Strings.isInIgnoreCase(wrappedString, strings);
        }

    }

}
