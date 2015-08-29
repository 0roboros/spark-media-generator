package com.spark.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ClarifaiResultListDeserializer extends JsonDeserializer<List<ClarifaiResult>>{
	 @Override
	    public List<ClarifaiResult> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
	            JsonProcessingException {
	        List<ClarifaiResult> result = new ArrayList<ClarifaiResult>();
	        while (jp.nextToken() != JsonToken.END_ARRAY) {
	            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
	                result.add(jp.readValueAs(ClarifaiResult.class));
	            }
	        }
	        return result;
	    }
}
