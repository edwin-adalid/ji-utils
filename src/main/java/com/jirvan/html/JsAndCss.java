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

package com.jirvan.html;

import com.jirvan.util.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * This needs to have functionality added actually minifying (everything is currently just concatenated into one file)
 */
public class JsAndCss {

    public static String getHtmlFileRefs(HttpServletRequest request, String projectVersion, String minifyConfigFile, boolean referToOriginalSourceFiles) {

        // Get config
        MinifyConfig minifyConfig;
        try {
            String configFilePath = minifyConfigFile.startsWith("/")
                                  ? minifyConfigFile
                                  : "/" + minifyConfigFile;
            InputStream configFileInputStream = request.getSession().getServletContext().getResourceAsStream(configFilePath);
            if (configFileInputStream != null) {
                try {
                    minifyConfig = Json.fromJsonString(Io.readStreamIntoString(configFileInputStream), MinifyConfig.class);
                } finally {
                    configFileInputStream.close();
                }
            } else {
                throw new RuntimeException(String.format("Config file \"%s\" not found", configFilePath));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (minifyConfig.cssFiles != null) {
            if (referToOriginalSourceFiles) {
                addPaths(minifyConfigFile, stringBuilder, false, "<link rel=\"stylesheet\" href=\"%s%s%s\" type=\"text/css\"/>", request, minifyConfig.cssFiles.sourceFiles);
            } else {
                addPath(stringBuilder, "<link rel=\"stylesheet\" href=\"%s%s%s\" type=\"text/css\"/>", request.getContextPath(), minifyConfig.cssFiles.minFile.replaceAll("\\$\\{version\\}", projectVersion));
            }
        }
        if (minifyConfig.ieOnlyCssFiles != null) {
            if (referToOriginalSourceFiles) {
                addPaths(minifyConfigFile, stringBuilder, true, "<link rel=\"stylesheet\" href=\"%s%s%s\" type=\"text/css\"/>", request, minifyConfig.ieOnlyCssFiles.sourceFiles);
            } else {
                if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
                stringBuilder.append("<!--[if IE]>");
                addPath(stringBuilder, "<link rel=\"stylesheet\" href=\"%s%s%s\" type=\"text/css\"/>", request.getContextPath(), minifyConfig.ieOnlyCssFiles.minFile.replaceAll("\\$\\{version\\}", projectVersion));
                stringBuilder.append("\n    <![endif]-->");
            }
        }
        if (minifyConfig.jsFiles != null) {
            if (referToOriginalSourceFiles) {
                addPaths(minifyConfigFile, stringBuilder, false, "<script src=\"%s%s%s\"></script>", request, minifyConfig.jsFiles.sourceFiles);
            } else {
                addPath(stringBuilder, "<script src=\"%s%s%s\"></script>", request.getContextPath(), minifyConfig.jsFiles.minFile.replaceAll("\\$\\{version\\}", projectVersion));
            }
        }

        return stringBuilder.toString();

    }


    private static void addPaths(String minifyConfigFile, StringBuilder stringBuilder, boolean ieOnly, String tagTemplate, HttpServletRequest request, String... paths) {
        if (paths != null && paths.length > 0) {
            if (ieOnly) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
                stringBuilder.append("<!--[if IE]>");
            }
            for (String path : paths) {
                if (path.indexOf('*') != -1) {
                    addPathsFoundIn(minifyConfigFile, stringBuilder, ieOnly, tagTemplate, request, path);
                } else {
                    addPath(stringBuilder, tagTemplate, request.getContextPath(), path);
                }
            }
            if (ieOnly) stringBuilder.append("\n    <![endif]-->");
        }
    }

    private static void addPathsFoundIn(String minifyConfigFile, StringBuilder stringBuilder, boolean ieOnly, String tagTemplate, HttpServletRequest request, String path) {

        // Decompose wildcard path
        String root;
        String suffix;
        Matcher m = Pattern.compile("^(.*)\\*\\*/\\*(\\..+)$").matcher(path);
        if (m.matches()) {
            root = m.group(1);
            suffix = m.group(2);
        } else {
            throw new RuntimeException(String.format("Path \"%s\" in %s is malformed (\"somewhere/**/*.whatever\" is the only wildcard path format currently accepted)",
                                                     path, minifyConfigFile));
        }

        addPaths(minifyConfigFile, stringBuilder, ieOnly, tagTemplate, request, getSortedFilePathsFoundIn(request, root, suffix));

    }

    private static String[] getSortedFilePathsFoundIn(HttpServletRequest request, String rootPath, String fileSuffix) {
        ArrayList<String> jsPaths = new ArrayList<String>();
        addUnsortedFilePathsFoundIn(request, rootPath, fileSuffix, jsPaths);
        Collections.sort(jsPaths, new Comparator<String>() {
            @Override public int compare(String path1, String path2) {
                return path1.replaceAll(".*/", "").compareTo(path2.replaceAll(".*/", ""));
            }
        });
        return jsPaths.toArray(new String[jsPaths.size()]);
    }

    private static void addUnsortedFilePathsFoundIn(HttpServletRequest request, String rootPath, String fileSuffix, List<String> jsPaths) {
        Set resourcePaths = request.getSession().getServletContext().getResourcePaths(rootPath.startsWith("/")
                                                                                      ? rootPath
                                                                                      : "/" + rootPath);
        for (String path : (Set<String>) resourcePaths) {
            if (path.endsWith(fileSuffix)) {   // path is the right kind of file - add
                jsPaths.add(path);
            } else if (path.endsWith("/")) {     // path is a directory - recurse
                addUnsortedFilePathsFoundIn(request, path, fileSuffix, jsPaths);
            } else {                             // paths is another type of file - ignore
            }
        }
    }

    private static void addPath(StringBuilder stringBuilder, String tagTemplate, String contextPath, String path) {
        if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
        stringBuilder.append(String.format(tagTemplate,
                                           contextPath,
                                           path.startsWith("/") ? "" : "/",
                                           path));
    }


    public static class MinifyConfig {

        public FileSet cssFiles;
        public FileSet ieOnlyCssFiles;
        public FileSet jsFiles;

        public static MinifyConfig fromJsonString(String configFile, String configFileContents) {
            MinifyConfig minifyConfig = Json.fromJsonString(configFileContents, MinifyConfig.class);
            verifyFileSet(configFile, minifyConfig.cssFiles, "cssFiles");
            verifyFileSet(configFile, minifyConfig.ieOnlyCssFiles, "ieOnlyCssFiles");
            verifyFileSet(configFile, minifyConfig.jsFiles, "jsFiles");
            return minifyConfig;
        }

        private static void verifyFileSet(String configFile, FileSet fileSet, String setName) {
            if (fileSet != null) {
                if (fileSet.minFile == null) {
                    throw new RuntimeException(String.format("%s is invalid, %s.minFile must be specified (if %s is)", configFile, setName, setName));
                }
                if (fileSet.sourceFiles == null) {
                    throw new RuntimeException(String.format("%s is invalid, %s.sourceFiles must be specified (if %s is)", configFile, setName, setName));
                }
                if (fileSet.sourceFiles.length == 0) {
                    throw new RuntimeException(String.format("%s is invalid, %s.sourceFiles must have at least one item", configFile, setName));
                }
            }
        }

        public static class FileSet {

            public String minFile;
            public String[] sourceFiles;

        }

    }

}