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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jirvan.dates.Dates;
import com.jirvan.json.JsonPrettyPrinter;
import com.jirvan.json.JsonShapeShifter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;

import static com.jirvan.util.Assertions.*;

public class Json {

    private static final ObjectMapper OBJECT_MAPPER = setUpObjectMapper(false);
    private static final ObjectMapper OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES = setUpObjectMapper(true);
    private static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writer(new JsonPrettyPrinter());

    public static String toPrettyJsonString(String rawJson) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(rawJson);
            return jsonNode == null ? null : Json.toJsonString(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return OBJECT_MAPPER.getTypeFactory().
                constructCollectionType(collectionClass, elementClass);
    }

    public static <T> T treeToValue(TreeNode n, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
        return OBJECT_MAPPER.treeToValue(n, valueType);
    }

    public static TreeNode readTree(String content) throws IOException, JsonProcessingException {
        return OBJECT_MAPPER.readTree(content);
    }

    public static String toJsonString(Object object) {
        try {
            return OBJECT_WRITER.writeValueAsString(object).replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void toJsonFile(Object object, String outputPathname) {
        toJsonFile(object, outputPathname, false);
    }

    public static void toJsonFile(Object object, String outputPathname, boolean overwriteExistingFileIfAny) {
        toJsonFile(object, new File(outputPathname), overwriteExistingFileIfAny);
    }

    public static void toJsonFile(Object object, File file) {
        toJsonFile(object, file, false);
    }

    public static void toJsonFile(Object object, File file, boolean overwriteExistingFileIfAny) {
        String jsonString = toJsonString(object);
        assertIsDirectory(file.getParentFile());
        if (!overwriteExistingFileIfAny) {
            assertFileDoesNotExist(file);
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(jsonString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String jsonString, Class<T> valueType) {
        return fromJsonString(jsonString, valueType, false);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, Class<T> valueType) {
        return fromJsonString(jsonString, returnNullForNullsNullStringsAndEmptyStrings, valueType, false);
    }

    public static <T> T fromJsonString(String jsonString, JavaType valueType) {
        return fromJsonString(jsonString, valueType, false);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, JavaType valueType) {
        return fromJsonString(jsonString, returnNullForNullsNullStringsAndEmptyStrings, valueType, false);
    }

    public static <T> T fromJsonString(String jsonString, TypeReference typeReference) {
        return fromJsonString(jsonString, typeReference, false);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, TypeReference typeReference) {
        return fromJsonString(jsonString, returnNullForNullsNullStringsAndEmptyStrings, typeReference, false);
    }

    public static <T> T fromJsonString(String jsonString, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(jsonString, false, valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, Class<T> valueType, boolean ignoreUnknownProperties) {
        try {

            if (returnNullForNullsNullStringsAndEmptyStrings && (jsonString == null || "\"null\"".equals(jsonString) || "".equals(jsonString.trim()))) {
                return null;
            }

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.readValue(jsonString, valueType)
                   : OBJECT_MAPPER.readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String jsonString, JavaType valueType, boolean ignoreUnknownProperties) {
        return fromJsonString( jsonString,  false,  valueType,  ignoreUnknownProperties);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, JavaType valueType, boolean ignoreUnknownProperties) {
        try {

            if (returnNullForNullsNullStringsAndEmptyStrings && (jsonString == null || "\"null\"".equals(jsonString) || "".equals(jsonString.trim()))) {
                return null;
            }

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(jsonString, valueType)
                   : OBJECT_MAPPER.<T>readValue(jsonString, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonString(String jsonString, TypeReference typeReference, boolean ignoreUnknownProperties) {
        return fromJsonString( jsonString,  false,  typeReference,  ignoreUnknownProperties);
    }

    public static <T> T fromJsonString(String jsonString, boolean returnNullForNullsNullStringsAndEmptyStrings, TypeReference typeReference, boolean ignoreUnknownProperties) {
        try {

            if (returnNullForNullsNullStringsAndEmptyStrings && (jsonString == null || "\"null\"".equals(jsonString) || "".equals(jsonString.trim()))) {
                return null;
            }

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(jsonString, typeReference)
                   : OBJECT_MAPPER.<T>readValue(jsonString, typeReference);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, Class<T> valueType) {
        return fromJsonInputStream(inputStream, valueType, false);
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, Class<T> valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.readValue(inputStream, valueType)
                   : OBJECT_MAPPER.readValue(inputStream, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, JavaType valueType) {
        return fromJsonInputStream(inputStream, valueType, false);
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, JavaType valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(inputStream, valueType)
                   : OBJECT_MAPPER.<T>readValue(inputStream, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, TypeReference typeReference) {
        return fromJsonInputStream(inputStream, typeReference, false);
    }

    public static <T> T fromJsonInputStream(InputStream inputStream, TypeReference typeReference, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(inputStream, typeReference)
                   : OBJECT_MAPPER.<T>readValue(inputStream, typeReference);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonReader(Reader reader, Class<T> valueType) {
        return fromJsonReader(reader, valueType, false);
    }

    public static <T> T fromJsonReader(Reader reader, Class<T> valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.readValue(reader, valueType)
                   : OBJECT_MAPPER.readValue(reader, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonReader(Reader reader, JavaType valueType) {
        return fromJsonReader(reader, valueType, false);
    }

    public static <T> T fromJsonReader(Reader reader, JavaType valueType, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(reader, valueType)
                   : OBJECT_MAPPER.<T>readValue(reader, valueType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonReader(Reader reader, TypeReference typeReference) {
        return fromJsonReader(reader, typeReference, false);
    }

    public static <T> T fromJsonReader(Reader reader, TypeReference typeReference, boolean ignoreUnknownProperties) {
        try {

            return ignoreUnknownProperties
                   ? OBJECT_MAPPER_ALLOW_UNKNOWN_PROPERTIES.<T>readValue(reader, typeReference)
                   : OBJECT_MAPPER.<T>readValue(reader, typeReference);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, JavaType valueType) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, TypeReference typeReference) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, typeReference, false);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, JavaType valueType, boolean ignoreUnknownProperties) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Object anchorObject, String filename, TypeReference typeReference, boolean ignoreUnknownProperties) {
        return fromJsonResourceFile((Class) anchorObject.getClass(), filename, typeReference, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType) {
        return fromJsonResourceFile(anchorClass, filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, JavaType valueType) {
        return fromJsonResourceFile(anchorClass, filename, valueType, false);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, TypeReference typeReference) {
        return fromJsonResourceFile(anchorClass, filename, typeReference, false);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getResourceFileString(anchorClass, filename), valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, JavaType valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getResourceFileString(anchorClass, filename), valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonResourceFile(Class anchorClass, String filename, TypeReference typeReference, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getResourceFileString(anchorClass, filename), typeReference, ignoreUnknownProperties);
    }

    public static <T> T fromJsonFile(File file, Class<T> valueType) {
        return fromJsonString(Io.getFileString(file), valueType, false);
    }

    public static <T> T fromJsonFile(File file, JavaType valueType) {
        return fromJsonString(Io.getFileString(file), valueType, false);
    }

    public static <T> T fromJsonFile(File file, TypeReference typeReference) {
        return fromJsonString(Io.getFileString(file), typeReference, false);
    }

    public static <T> T fromJsonFile(File file, Class<T> valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getFileString(file), valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonFile(File file, JavaType valueType, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getFileString(file), valueType, ignoreUnknownProperties);
    }

    public static <T> T fromJsonFile(File file, TypeReference typeReference, boolean ignoreUnknownProperties) {
        return fromJsonString(Io.getFileString(file), typeReference, ignoreUnknownProperties);
    }

    private static ObjectMapper setUpObjectMapper(boolean ignoreUnknownProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(Dates.getSerializerDeserializerModule());
        objectMapper.registerModule(getJsonShapeShifterSerializerModule());
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        if (ignoreUnknownProperties) objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private static SimpleModule getJsonShapeShifterSerializerModule() {
        SimpleModule module = new SimpleModule("JsonShapeShifterSerializerModule", new Version(1, 0, 0, null));

        module.addDeserializer(JsonShapeShifter.class, new JsonDeserializer<JsonShapeShifter>() {
            public JsonShapeShifter deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {

                ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

                // Get and tree node, and remove type field
                JsonNode jsonNode = mapper.readTree(jsonParser);
                if (!(jsonNode instanceof ObjectNode)) {
                    throw new RuntimeException("Expected JsonShapeShifter nodes to be ObjectNodes");
                }
                JsonNode typeNode = ((ObjectNode) jsonNode).remove("type");
                if (typeNode == null) {
                    throw new RuntimeException("Expected JsonShapeShifter node to have a type field");
                }
                if (!(typeNode instanceof TextNode)) {
                    throw new RuntimeException("Expected type to be a text node");
                }

                // Determine the object class based on the type field
                String className = typeNode.asText();
                Class configClass = null;
                try {
                    configClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(String.format("JsonShapeShifter type \"%s\" is not a recognized class", className));
                }

                // Extract and return the object
                return (JsonShapeShifter) mapper.treeToValue(jsonNode, configClass);
            }
        });

        return module;
    }

}
