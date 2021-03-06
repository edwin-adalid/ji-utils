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

import org.apache.velocity.*;
import org.apache.velocity.app.*;

import java.io.*;

public class Vel {

    public static String merge(String template, Object... keysAndValues) {
        VelocityContext velocityContext = new VelocityContext();
        String currentKey = null;
        for (Object keyOrValue : keysAndValues) {
            if (currentKey == null) {
                if (!(keyOrValue instanceof String)) throw new RuntimeException("keys must be of type String");
                currentKey = (String) keyOrValue;
            } else {
                velocityContext.put(currentKey, keyOrValue);
                currentKey = null;
            }
        }
        if (currentKey != null) throw new RuntimeException("There must be an equal amount of keys and values");
        return merge(template, velocityContext);
    }

    public static String merge(String template, VelocityContext velocityContext) {
        Velocity.init();
        StringWriter stringWriter = new StringWriter();
        Velocity.evaluate(velocityContext, stringWriter, "com.jirvan.util.Vel.merge()", template);
        return stringWriter.toString();
    }

}
