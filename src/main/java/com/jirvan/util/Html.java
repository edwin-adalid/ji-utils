/*

Copyright (c) 2018, Jirvan Pty Ltd
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Html {

    public static void writeErrorPageHtmlToResponse(HttpServletResponse response,
                                                    String errorMessagePrefix,
                                                    Throwable t) {
        response.setContentType("text/html");
        response.setHeader("Content-Disposition", "inline");
        try {
            response.getOutputStream().print("<!DOCTYPE html>\n" +
                                             "<html lang=\"en\">\n" +
                                             "<head>\n" +
                                             "    <meta charset=\"UTF-8\">\n" +
                                             "    <title>Error</title>\n" +
                                             "</head>\n" +
                                             "<body>\n" +
                                             "<h3>" + errorMessagePrefix + t.getMessage() + "</h3>\n" +
                                             "<div>\n" +
                                             "    <pre>\n" +
                                             Utl.getStackTrace(t) +
                                             "    </pre>\n" +
                                             "</div>\n" +
                                             "</body>\n" +
                                             "</html>");
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
