package com.royalties.marvel.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.royalties.marvel.APIClient.RestClient;
import com.royalties.marvel.model.CharacterInfo;
import com.royalties.marvel.model.CharactersResponse;
import com.royalties.marvel.model.ColaboratorsResponse;
import com.royalties.marvel.repository.CharacterInfoRepository;

@RestController
public class CharacterInfoController {
	
	@Autowired
	CharacterInfoRepository charRepo;
	
	@Autowired
	private Environment env;

	@GetMapping("/marvel/colaborators/{name}")	
	public Optional<ColaboratorsResponse> getColaborators(@PathVariable("name") String charName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		ColaboratorsResponse res=new ColaboratorsResponse();
		Optional<CharacterInfo> data;
		Duration tiempo;
		String characterId = env.getProperty("api.royalties.character."+charName);
		data=charRepo.findById(Integer.parseInt(characterId));
		if(data.isEmpty()) {
			if(syncupFromMarvel(characterId))
				data=charRepo.findById(Integer.parseInt(characterId));
		}else {
			tiempo=Duration.between(data.get().getLast_sync(),LocalDateTime.now());
			if(tiempo.getSeconds()>Integer.valueOf(env.getProperty("api.royalties.datarefresh.seconds"))) {
				if(syncupFromMarvel(characterId))
					data=charRepo.findById(Integer.parseInt(characterId));
			}
		}
		res.setLast_sync(data.get().getLast_sync().format(formatter));
		res.setEditors(data.get().getEditors());
		res.setWriters(data.get().getWriters());
		res.setColorists(data.get().getColorists());
		return Optional.of(res);
	}
	
	@GetMapping("/marvel/characters/{name}")	
	public Optional<CharactersResponse> getColleagues(@PathVariable("name") String charName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		CharactersResponse res=new CharactersResponse();
		Optional<CharacterInfo> data;
		Duration tiempo;	
		String characterId = env.getProperty("api.royalties.character."+charName);
		data=charRepo.findById(Integer.parseInt(characterId));
		if(data.isEmpty()) {
			if(syncupFromMarvel(characterId))
				data=charRepo.findById(Integer.parseInt(characterId));
		}else {
			tiempo=Duration.between(data.get().getLast_sync(),LocalDateTime.now());
			if(tiempo.getSeconds()>Integer.valueOf(env.getProperty("api.royalties.datarefresh.seconds"))) {
				if(syncupFromMarvel(characterId))
					data=charRepo.findById(Integer.parseInt(characterId));
			}
		}	
		res.setLast_sync(data.get().getLast_sync().format(formatter));
		res.setCharacters(data.get().getCharacters());
		return Optional.of(res);
	}
	
	
	private boolean syncupFromMarvel(String characterId) {
		CharacterInfo info=new CharacterInfo();
		RestClient cliente=new RestClient();
		try {
			info=cliente.getDataFromMarvel(Integer.parseInt(characterId));
			charRepo.save(info);
		} catch (RestClientException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
