package com.royalties.marvel.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.royalties.marvel.model.CharacterInfo;

public interface CharacterInfoRepository extends MongoRepository<CharacterInfo, Integer>{
	
}
