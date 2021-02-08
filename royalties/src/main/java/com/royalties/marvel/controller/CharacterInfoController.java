package com.royalties.marvel.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import com.royalties.marvel.APIClient.RestClient;
import com.royalties.marvel.model.CharacterInfo;
import com.royalties.marvel.model.CharactersResponse;
import com.royalties.marvel.model.ColaboratorsResponse;
import com.royalties.marvel.repository.CharacterInfoRepository;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * @author Neftaly Espino Viveros
 *
 */
@Slf4j
@RestController
public class CharacterInfoController {
	
	@Autowired
	CharacterInfoRepository charRepo;
	
	@Autowired
	private Environment env;

	/**
	 * 
	 * @param charName
	 * @return Optional<ColaboratorsResponse>
	 * @implNote servicio que regresa los colaboradores de un personaje, 
	 * 			el nombre proporcionado puede parametrizarse en el archivo application.properties junto con su id en Marvel api
	 * 			Este método obtiene la información en la bd local, si dicha información tiene más del tiempo permitido desde su última
	 * 			sincronización, se actualiza y regresa esa informacion.
	 */
	@GetMapping("/marvel/colaborators/{name}")	
	public Optional<ColaboratorsResponse> getColaborators(@PathVariable("name") String charName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		ColaboratorsResponse res=new ColaboratorsResponse();
		Optional<CharacterInfo> data;
		Duration tiempo;
		try {
			String characterId = env.getProperty("api.royalties.character."+charName);
			if(characterId==null) {
				log.error("Exception: Personaje no configurado en properties: "+characterId);
				throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de personaje no permitido");
			}
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
		}catch (Exception e) {
			log.error("Exception: "+e);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ocurrió un error inesperado, repórtelo al administrador del sistema");
		}
		
		return Optional.of(res);
	}
	
	/**
	 * 
	 * @param charName
	 * @return Optional<CharactersResponse>
	 * @implNote servicio que regresa los caracteres y los comics con los que ha interactuado un personaje, 
	 * 			el nombre proporcionado puede parametrizarse en el archivo application.properties junto con su id en Marvel api
	 * 			Este método obtiene la información en la bd local, si dicha información tiene más del tiempo permitido desde su última
	 * 			sincronización, se actualiza y regresa esa informacion.
	 */
	@GetMapping("/marvel/characters/{name}")	
	public Optional<CharactersResponse> getColleagues(@PathVariable("name") String charName) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		CharactersResponse res=new CharactersResponse();
		Optional<CharacterInfo> data;
		Duration tiempo;	
		try {
			String characterId = env.getProperty("api.royalties.character."+charName);
			if(characterId==null) {
				log.error("Exception: Personaje no configurado en properties: "+characterId);
				throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Nombre de personaje no permitido");
			}
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
		}catch (Exception e) {
			log.error("Exception: "+e);
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Ocurrió un error inesperado, revisar LOG");
		}		
		return Optional.of(res);
	}
	
	/**
	 * 
	 * @param characterId
	 * @return boolean
	 * @implNote método que actualiza la información en la bd local cuando el tiempo transcurrido desde la ultima sincronizacion supera
	 * 			el límite configurado, este límite se configura en segundos en el archivo application.properties
	 */
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
