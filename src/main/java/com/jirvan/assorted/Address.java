/*

Copyright (c) 2014, Jirvan Pty Ltd
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

package com.jirvan.assorted;

public interface Address {

    String getPoBox();

    void setPoBox(String poBox);

    String getUnitOrLevel();

    void setUnitOrLevel(String unitOrLevel);

    String getPropertyName();

    void setPropertyName(String propertyName);

    String getAddressLine1();

    void setAddressLine1(String addressLine1);

    String getAddressLine2();

    void setAddressLine2(String addressLine2);

    String getSuburb();

    void setSuburb(String suburb);

    String getState();

    void setState(String state);

    String getPostCode();

    void setPostCode(String postCode);

    String getCountry();

    void setCountry(String country);

    static class Utl {

        public static String toString(Address address) {
            StringBuilder stringBuilder = new StringBuilder();

            if (address.getPoBox() != null) addLine(stringBuilder, "PO Box: " + address.getPoBox());
            addLineIfNotNull(stringBuilder, address.getPropertyName());
            addLineIfNotNull(stringBuilder, address.getUnitOrLevel());
            addLineIfNotNull(stringBuilder, address.getAddressLine1());
            addLineIfNotNull(stringBuilder, address.getAddressLine2());
            String stateAndPostCode;
            if (address.getState() != null) {
                if (address.getPostCode() != null) {
                    stateAndPostCode = address.getState() + " " + address.getPostCode();
                } else {
                    stateAndPostCode = address.getState();
                }
            } else {
                if (address.getPostCode() != null) {
                    stateAndPostCode = address.getPostCode();
                } else {
                    stateAndPostCode = null;
                }
            }
            if (address.getSuburb() != null) {
                if (stateAndPostCode != null) {
                    addLine(stringBuilder, address.getSuburb() + ", " + stateAndPostCode);
                } else {
                    addLine(stringBuilder, address.getSuburb());
                }
            } else {
                if (stateAndPostCode != null) {
                    addLine(stringBuilder, stateAndPostCode);
                }
            }
            addLineIfNotNull(stringBuilder, address.getCountry());

            return stringBuilder.toString();
        }

        private static void addLineIfNotNull(StringBuilder stringBuilder, String line) {
            if (line != null) {
                addLine(stringBuilder, line);
            }
        }

        private static void addLine(StringBuilder stringBuilder, String line) {
            if (stringBuilder.length() > 0) stringBuilder.append("\n");
            stringBuilder.append(line);
        }

    }

}
