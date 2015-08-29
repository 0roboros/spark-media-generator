package com.spark.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RekognitionMatch {
	private String tag;
	private Double score;
	
	public void setTag(String tag){
		this.tag = tag;
	}
	
	public void setScore(Double score){
		this.score = score;
	}
	
	public String getTag(){
		return tag;
	}
	
	public Double getScore(){
		return score;
	}
}
