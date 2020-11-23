/*

Copyright (c) 2013,2014 Jirvan Pty Ltd
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

import java.lang.reflect.*;
import java.util.*;

public class JiArrays {

    public static <T> T[] createFromNotNullItems(T... items) {
        List<T> list = new ArrayList<>();
        for (T item : items) {
            if (item != null) {
                list.add(item);
            }
        }
        T[] array = (T[]) Array.newInstance(items.getClass().getComponentType(), list.size());
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static void main(String[] args) {
        String[] zzz = concatArrays(new String[]{"cat", "dog"}, new String[]{"giraffe", "snake"});
        String[] sfd = concatArrays(new String[]{"budgie"});
        String[] sdf = concatArrays(new String[0]);
        int adf = 4;
    }

    public static <T> T[] concatArrays(T[] array1, T[]... arrays2ToN) {
        Objects.requireNonNull(array1, "array1 must not be null");

        // Determine resulting array length
        int resultLength = array1.length;
        for (T[] array : arrays2ToN) {
            resultLength += array.length;
        }

        // Create resulting array
        Class arrayComponentType = array1.getClass().getComponentType();
        T[] result = (T[]) Array.newInstance(arrayComponentType, resultLength);

        // Set resulting array values for items from first array
        int i = 0;
        for (T item : array1) {
            result[i++] = item;
        }

        // Set resulting array values for items from arrays 2 to n
        for (T[] array : arrays2ToN) {
            for (T item : array) {
                result[i++] = item;
            }
        }

        return result;

    }

}
