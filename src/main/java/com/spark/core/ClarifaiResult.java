package com.spark.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarifaiResult {
	
	@JsonProperty("result")
	private ClarifaiFinalResult clarifaiFinalResult;
	
	public void setClarifaiFinalResult(ClarifaiFinalResult clarifaiFinalResult){
		this.clarifaiFinalResult = clarifaiFinalResult;
	}
	
	public ClarifaiFinalResult getClarifaiFinalResult(){
		return this.clarifaiFinalResult;
	}
	
}
