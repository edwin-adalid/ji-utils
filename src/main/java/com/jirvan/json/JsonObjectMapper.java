package com.jirvan.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jirvan.dates.*;

public class JsonObjectMapper extends ObjectMapper {

    public JsonObjectMapper() {
        this.registerModule(Dates.getSerializerDeserializerModule());
    }

}
