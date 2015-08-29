package com.spark.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="images")
public class Image implements Serializable{
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Id
	@Column(nullable = false)
	private String hash;
	
	@Column
	private String query;
	
	@Column
	private String tags;
	
	@Column
	private String categories;
	
	protected Image (){}

	
	public Image(String hash, String query, String tags, String categories){
		this.hash = hash;
		this.query = query;
		this.tags = tags;
		this.categories = categories;
	}
	
	
	public String getHash(){
		return hash;
	}
	
	public void setHash(String hash){
		this.hash = hash;
	}
	
	public String getCategories(){
		return categories;
	}
	
	public void setCategories(String categories){
		this.categories = categories;
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String query){
		this.query = query;
	}
	public String getTags(){
		return tags;
	}
	
	public void setTags(String tags){
		this.tags = tags;
	}
}

	

