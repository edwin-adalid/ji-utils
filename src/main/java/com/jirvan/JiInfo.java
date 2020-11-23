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

package com.jirvan;

import com.jirvan.util.*;

public class JiInfo {

    private static final ArtifactInfo jiInfo = new ArtifactInfo(Io.getResourcePropertyValue(JiInfo.class, "ji.build.properties", "project.name"),
                                                                Io.getResourcePropertyValue(JiInfo.class, "ji.build.properties", "project.version"));

    public static final String USAGE = "\nUsage:\n\n   java -jar <jar file> [-j]";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.printf("\n%s\n", getDetails());
        } else if (args.length == 1 && "-j".equals(args[0])) {
            System.out.printf("\n%s\n", getDetailsJson());
        } else {
            System.err.println(USAGE);
        }
    }

    public static ArtifactInfo getInfo() {
        return jiInfo;
    }

    public static String getName() {
        return jiInfo.getName();
    }

    public static String getVersion() {
        return jiInfo.getVersion();
    }

    public static String getDetails() {
        return String.format("%s: %s", jiInfo.getName(), jiInfo.getVersion());
    }

    public static String getDetailsJson() {
        return String.format("{\n" +
                             "    \"name\": \"%s\",\n" +
                             "    \"version\": \"%s\"\n" +
                             "}", jiInfo.getName(), jiInfo.getVersion());
    }

}
