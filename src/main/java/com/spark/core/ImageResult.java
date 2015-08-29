package com.spark.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResult {
	private String mediaUrl;
	
	public void setMediaUrl(String mediaUrl){
		this.mediaUrl = mediaUrl;
	}
	
	public String getMediaUrl(){
		return mediaUrl;
	}
}
