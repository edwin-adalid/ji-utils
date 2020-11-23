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

package com.jirvan.json;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jirvan.lang.*;
import com.jirvan.util.*;

import static com.jirvan.util.Assertions.assertNotNull;

public abstract class JsonConfig {

    private String filename;

    public static <T> JsonConfig getFromHomeDirectoryFile(String filename) {
        try {
            JsonConfig config;
            try {

                TreeNode tree = Json.readTree(Io.getFileString(Io.getHomeDirectoryFile(filename)));
                if (!(tree instanceof ObjectNode)) {
                    throw new RuntimeException(String.format("Error in %s.\nFile should look something like this:\n{\n  \"type\" : \"com.acme.ThingyConfig\",\n.\n.\n.\n}\n",
                                                             filename));
                }
                JsonNode typeNode = ((ObjectNode) tree).remove("type");
                if (typeNode == null) {
                    throw new RuntimeException(String.format("Error in %s.\nFile should look something like this:\n{\n  \"type\" : \"com.acme.ThingyConfig\",\n.\n.\n.\n}\n",
                                                             filename));
                }
                Class configClass = Class.forName(typeNode.asText());

                config = Json.<JsonConfig>treeToValue(tree, configClass);
                JsonConfig.class.getDeclaredField("filename").set(config, filename);


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            config.validate();

            return config;
        } catch (FileNotFoundRuntimeException e) {
            throw new FileNotFoundRuntimeException(String.format("%s.\nIt should look something like this:\n{\n  \"type\" : \"com.acme.ThingyConfig\",\n.\n.\n.\n}\n", e.getMessage()));
        }
    }

    //    public static <T extends JsonConfig> T getFromHomeDirectoryFile(Class<T> clazz, String filename) {
//        try {
//            T config;
//            try {
//
//                JsonNode tree = Json.readTree(Io.getFileString(Io.getHomeDirectoryFile(filename)));
//                if (!(tree instanceof ObjectNode)) {
//                    throw new RuntimeException(String.format("Error in %s.\nFile should look something like this:\n%s\n",
//                                                             filename,
//                                                             exampleJsonString(clazz)));
//                }
//                JsonNode typeNode = ((ObjectNode) tree).remove("type");
//                if (typeNode == null) {
//                    throw new RuntimeException(String.format("Error in %s, type must be provided.\nFile should look something like this:\n%s\n",
//                                                             filename,
//                                                             exampleJsonString(clazz)));
//                }
//                Class configClass = Class.forName(typeNode.asText());
//
//                config = (T)Json.treeToValue(tree, configClass);
//
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//            return config;
//        } catch (FileNotFoundRuntimeException e) {
//            throw new FileNotFoundRuntimeException(String.format("%s.\nIt should look something like this:\n%s\n", e.getMessage(), exampleJsonString(clazz)));
//        }
//    }
//
    private static <T extends JsonConfig> String exampleJsonString(Class<T> clazz) {
        T dummyConfig = null;
        try {
            dummyConfig = clazz.newInstance();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return dummyConfig.exampleJsonString();
    }

    protected abstract JsonConfig createExample();

    protected abstract void validate();

    public void assertPropertyNotNull(String propertyName, Object propertyValue) {
        assertNotNull(propertyValue, String.format("Error in %s, %s must be provided.\nFile should look something like this:\n%s\n",
                                                   filename == null ? "config file"
                                                                    : filename,
                                                   propertyName,
                                                   exampleJsonString()));
    }

    protected String exampleJsonString() {
        return Json.toJsonString(createExample()).replaceFirst("\\{", "{\n  \"type\" : \"" + this.getClass().getName() + "\",");
    }
}
