/*

Copyright (c) 2012, Jirvan Pty Ltd
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

package com.jirvan.dates;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jirvan.dates.jackson.DayToJavascriptStringJsonSerializer;

import java.io.IOException;

public class Dates {

    public static String formatElapsedTime(int minutes) {
        int hours = minutes / 60;
        int minuteInHour = minutes - (hours * 60);
        return String.format("%d:%02d", hours, minuteInHour);
    }

    public static String formatTimeOfDay(int minutes) {
        int hours = minutes / 60;
        int minuteInHour = minutes - (hours * 60);
        return String.format("%02d:%02d", hours, minuteInHour);
    }

    public static Integer hourPart(Integer totalMinutes) {
        return totalMinutes == null
               ? null
               : totalMinutes / 60;
    }

    public static Integer minutePart(Integer totalMinutes) {
        return totalMinutes == null
               ? null
               : totalMinutes - ((totalMinutes / 60) * 60);
    }

    public static SimpleModule getSerializerDeserializerModule() {
        SimpleModule module = new SimpleModule("JiDatesSerializerModule", new Version(1, 0, 0, null));

        module.addSerializer(Month.class, new JsonSerializer<Month>() {
            public void serialize(Month value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Month.class, new JsonDeserializer<Month>() {
            public Month deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Month.fromString((jsonNode.textValue()));
            }
        });

        module.addSerializer(Day.class, new DayToJavascriptStringJsonSerializer());
        module.addDeserializer(Day.class, new JsonDeserializer<Day>() {
            public Day deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Day.fromString((jsonNode.textValue()));
            }
        });

        module.addSerializer(Hour.class, new JsonSerializer<Hour>() {
            public void serialize(Hour value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Hour.class, new JsonDeserializer<Hour>() {
            public Hour deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Hour.fromString((jsonNode.textValue()));
            }
        });

        module.addSerializer(Minute.class, new JsonSerializer<Minute>() {
            public void serialize(Minute value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Minute.class, new JsonDeserializer<Minute>() {
            public Minute deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Minute.fromString((jsonNode.textValue()));
            }
        });

        module.addSerializer(Second.class, new JsonSerializer<Second>() {
            public void serialize(Second value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Second.class, new JsonDeserializer<Second>() {
            public Second deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Second.fromString((jsonNode.textValue()));
            }
        });

        module.addSerializer(Millisecond.class, new JsonSerializer<Millisecond>() {
            public void serialize(Millisecond value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.toString());
            }
        });
        module.addDeserializer(Millisecond.class, new JsonDeserializer<Millisecond>() {
            public Millisecond deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                ObjectCodec oc = jsonParser.getCodec();
                JsonNode jsonNode = oc.readTree(jsonParser);
                if (!(jsonNode instanceof TextNode)) throw new RuntimeException("Expected a text node");
                return Millisecond.fromString((jsonNode.textValue()));
            }
        });

        return module;
    }

}
