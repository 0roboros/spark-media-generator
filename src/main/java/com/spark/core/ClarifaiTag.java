package com.spark.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ClarifaiTag {
	
	@JsonProperty("classes")
	private List<String> classes;
	
	@JsonProperty("probs")
	private List<Double> probs;
	
	public void setClasses(List<String> classes){
		this.classes = classes;
	}
	
	public List<String> getClasses(){
		return classes;
	}
	
	public void setProbs(List<Double> probs){
		this.probs = probs;
	}
	
	public List<Double> getProbs(){
		return probs;
	}
}
