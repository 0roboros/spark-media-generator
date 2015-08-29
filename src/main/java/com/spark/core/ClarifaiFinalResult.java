package com.spark.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarifaiFinalResult {
	
	@JsonProperty("tag")
	private ClarifaiTag clarifaiTag;
	
	public void setClarifaiTag(ClarifaiTag clarifaiTag){
		this.clarifaiTag = clarifaiTag;
	}
	
	public ClarifaiTag getClarifaiTag(){
		return this.clarifaiTag;
	}
	

}
