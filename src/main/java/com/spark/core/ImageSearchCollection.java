package com.spark.core;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
//@XmlRootElement(name = "feed", namespace="https://api.datamarket.azure.com/Data.ashx/Bing/Search/Image")
public class ImageSearchCollection {
	private List<ImageResult> listOfImage;

	@JsonDeserialize(using = BingImageSearchDeserializer.class)
	public void setListOfImage(List<ImageResult> listOfImage){
		this.listOfImage = listOfImage;
	}
	
	public List<ImageResult> getListOfImage(){
		return this.listOfImage;
	}
	
}
