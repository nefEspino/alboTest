package com.royalties.marvel.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalties.marvel.model.CharacterInfo;

/**
 * 
 * @author Neftaly Espino Viveros
 * @implNote Interfaz para interactuar con MongoDB
 *
 */
public interface CharacterInfoRepository extends MongoRepository<CharacterInfo, Integer>{
	
}
