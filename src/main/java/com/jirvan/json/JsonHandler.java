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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jirvan.dates.*;
import com.jirvan.util.*;

import java.io.*;
import java.util.*;

public abstract class JsonHandler {

    private final ObjectMapper OBJECT_MAPPER = setUpObjectMapper(false);
    private final ObjectMapper OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES = setUpObjectMapper(true);
    private final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer()
                                                            .withDefaultPrettyPrinter();

    public <T> T treeToValue(TreeNode n, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return OBJECT_MAPPER.treeToValue(n, valueType);
    }

    public JsonNode readTree(String content) throws IOException, JsonProcessingException {
        return OBJECT_MAPPER.readTree(content);
    }

    public String toJsonString(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object).replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJsonString(String jsonString, Class<T> valueType) {
        return fromJsonString(jsonString, valueType, false);
    }

    public <T> T fromJsonString(String jsonString, Class<T> valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.readValue(jsonString, valueType)
                   : OBJECT_MAPPER.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, false);
    }

    public <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, ignoreUnknownProperties);
    }

    public <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType) {
        return fromJsonResourceFile(anchorClass, filename, valueType, false);
    }

    public <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getResourceFileString(anchorClass, filename), valueType, ignoreUnknownProperties);
    }

    public <T> T fromJsonFile(File file, Class<T> valueType) {
        return fromJsonString(Io.getFileString(file), valueType, false);
    }

    public <T> T fromJsonFile(File file, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getFileString(file), valueType, ignoreUnknownProperties);
    }

    private ObjectMapper setUpObjectMapper(boolean ignoreUnknownProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(Dates.getSerializerDeserializerModule());
        List<Module> additionalModules = new ArrayList<Module>();
        addModules(additionalModules);
        for (Module additionalModule : additionalModules) {
            objectMapper.registerModule(additionalModule);
        }
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        if (ignoreUnknownProperties) objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    protected abstract void addModules(List<Module> modules);

}
