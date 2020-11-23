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

import javax.servlet.http.*;
import java.util.*;

/**
 * This needs to have functionality added for minifying everything into one file for production use *
 */
public class JsAndCssOLD {

    private HttpServletRequest request;
    private String contextPath;
    private boolean suppressMinification;
    private String minifiedHtml;
    private String unminifiedHtml;

    private JsAndCssOLD(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
        this.contextPath = httpServletRequest.getContextPath();
    }

    public static JsAndCssOLD forRequest(HttpServletRequest httpRequest) {
        return new JsAndCssOLD(httpRequest);
    }

    public JsAndCssOLD addJsPathsFoundIn(String rootPath) {
        return addJsPathsFoundIn(rootPath, ".js");
    }

    public JsAndCssOLD addJsPathsFoundIn(String rootPath, String jsFileSuffix) {
        return addJsPaths(getSortedFilePathsFoundIn(rootPath, jsFileSuffix));
    }

    public JsAndCssOLD addCssPathsFoundIn(String rootPath) {
        return addCssPathsFoundIn(rootPath, ".css");
    }

    public JsAndCssOLD addCssPathsFoundIn(String rootPath, String cssFileSuffix) {
        return addCssPaths(getSortedFilePathsFoundIn(rootPath, cssFileSuffix));
    }

    public JsAndCssOLD addJsPaths(String... jsPaths) {
        if (jsPaths != null && jsPaths.length > 0) {
            StringBuilder stringBuilder = unminifiedHtml != null ? new StringBuilder(unminifiedHtml) : new StringBuilder();
            for (String jsPath : jsPaths) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
                stringBuilder.append(String.format("<script src=\"%s%s%s\"></script>",
                                                   contextPath,
                                                   jsPath.startsWith("/") ? "" : "/",
                                                   jsPath));
            }
            unminifiedHtml = stringBuilder.toString();
        }
        return this;
    }

    public JsAndCssOLD addCssPaths(String... cssPaths) {
        if (cssPaths != null && cssPaths.length > 0) {
            StringBuilder stringBuilder = unminifiedHtml != null ? new StringBuilder(unminifiedHtml) : new StringBuilder();
            for (String cssPath : cssPaths) {
                if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
                stringBuilder.append(String.format("<link rel=\"stylesheet\" href=\"%s%s%s\" type=\"text/css\"/>",
                                                   contextPath,
                                                   cssPath.startsWith("/") ? "" : "/",
                                                   cssPath));
            }
            unminifiedHtml = stringBuilder.toString();
        }
        return this;
    }

    public JsAndCssOLD addIeOnlyCssPaths(String... ieOnlyCssPaths) {
        if (ieOnlyCssPaths != null && ieOnlyCssPaths.length > 0) {
            StringBuilder stringBuilder = unminifiedHtml != null ? new StringBuilder(unminifiedHtml) : new StringBuilder();
            if (stringBuilder.length() != 0) stringBuilder.append("\n    ");
            stringBuilder.append("<!--[if IE]>");
            for (String ieOnlyCssPath : ieOnlyCssPaths) {
                stringBuilder.append(String.format("\n    <link rel=\"stylesheet\" href=\"%s/%s\" type=\"text/css\"/>", contextPath, ieOnlyCssPath));
            }
            stringBuilder.append("\n    <![endif]-->");
            unminifiedHtml = stringBuilder.toString();
        }
        return this;
    }

    public JsAndCssOLD suppressMinification(boolean suppressMinification) {
        this.suppressMinification = suppressMinification;
        return this;
    }

    public JsAndCssOLD setMinifiedCssFile(String filePath) {
        if (this.minifiedHtml != null) {
            this.minifiedHtml += "\n    ";
        } else {
            this.minifiedHtml = "";
        }
        this.minifiedHtml += String.format("<link rel=\"stylesheet\" href=\"%s/%s\" type=\"text/css\"/>", contextPath, filePath);
        return this;
    }

    public JsAndCssOLD setMinifiedIeOnlyCssFile(String filePath) {
        if (this.minifiedHtml != null) {
            this.minifiedHtml += "\n    ";
        } else {
            this.minifiedHtml = "";
        }
        this.minifiedHtml += String.format("<!--[if IE]>\n" +
                                           "    <link rel=\"stylesheet\" href=\"%s/%s\" type=\"text/css\"/>\n" +
                                           "    <![endif]-->", contextPath, filePath);
        return this;
    }

    public JsAndCssOLD setMinifiedJsFile(String filePath) {
        if (this.minifiedHtml != null) {
            this.minifiedHtml += "\n    ";
        } else {
            this.minifiedHtml = "";
        }
        this.minifiedHtml += String.format("<script src=\"%s/%s\"></script>", contextPath, filePath);
        return this;
    }

    public String getHtml() {
        if (suppressMinification) {
            return unminifiedHtml == null ? "" : unminifiedHtml;
        } else {
            return minifiedHtml == null ? "" : minifiedHtml;
        }
    }

    private String[] getSortedFilePathsFoundIn(String rootPath, String jsFileSuffix) {
        ArrayList<String> jsPaths = new ArrayList<String>();
        addUnsortedFilePathsFoundIn(rootPath, jsFileSuffix, jsPaths);
        Collections.sort(jsPaths, new Comparator<String>() {
            @Override public int compare(String path1, String path2) {
                return path1.replaceAll(".*/", "").compareTo(path2.replaceAll(".*/", ""));
            }
        });
        return jsPaths.toArray(new String[jsPaths.size()]);
    }

    private void addUnsortedFilePathsFoundIn(String rootPath, String fileSuffix, List<String> jsPaths) {
        Set resourcePaths = request.getSession().getServletContext().getResourcePaths(rootPath.startsWith("/")
                                                                                      ? rootPath
                                                                                      : "/" + rootPath);
        for (String path : (Set<String>) resourcePaths) {
            if (path.endsWith(fileSuffix)) {   // path is the right kind of file - add
                jsPaths.add(path);
            } else if (path.endsWith("/")) {     // path is a directory - recurse
                addUnsortedFilePathsFoundIn(path, fileSuffix, jsPaths);
            } else {                             // paths is another type of file - ignore
            }
        }
    }

}