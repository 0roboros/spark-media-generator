package com.spark.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RekognitionSceneUnderstanding {
	
	
	@JsonProperty("matches")
	@JsonDeserialize(using = RekognitionMatchListDeserializer.class)
	private List<RekognitionMatch> rekognitionMatchList;
	
	public void setRekognitionMatchList(List<RekognitionMatch> rekognitionMatchList){
		this.rekognitionMatchList = rekognitionMatchList;
	}
	
	public List<RekognitionMatch> getRekognitionMatchList(){
		return rekognitionMatchList;
	}
}
