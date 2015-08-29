package com.spark.core;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long>{

	List<Image> findByHash(String hash);
	
	Image findById(Integer id);
}

