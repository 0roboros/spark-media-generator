package com.spark.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarifaiResp {

	@JsonProperty("results")
	private List<ClarifaiResult> results;
	
	public void setResults(List<ClarifaiResult> results){
		this.results = results;
	}
	
	public List<ClarifaiResult> getResults(){
		return results;
	}
}
