package com.spark.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class RekognitionMatchListDeserializer extends JsonDeserializer<List<RekognitionMatch>>{
	 @Override
	    public List<RekognitionMatch> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
	            JsonProcessingException {
	        List<RekognitionMatch> result = new ArrayList<RekognitionMatch>();
	        while (jp.nextToken() != JsonToken.END_ARRAY) {
	            if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
	                result.add(jp.readValueAs(RekognitionMatch.class));
	            }
	        }
	        return result;
	    }
}
