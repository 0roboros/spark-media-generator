package com.spark.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RekognitionResp {
	
	@JsonProperty("scene_understanding")
	private RekognitionSceneUnderstanding rekognitionSceneUnderstanding;
	
	public void setRekognitionSceneUnderstanding(RekognitionSceneUnderstanding rekognitionSceneUnderstanding){
		this.rekognitionSceneUnderstanding = rekognitionSceneUnderstanding;
	}
	public RekognitionSceneUnderstanding getRekognitionSceneUnderstanding(){
		return rekognitionSceneUnderstanding;
	}
}
