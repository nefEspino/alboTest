package com.royalties.marvel.APIClient;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.royalties.marvel.config.MyEnviroment;
import com.royalties.marvel.controller.CharacterInfoController;
import com.royalties.marvel.model.CharacterComics;
import com.royalties.marvel.model.CharacterInfo;
import com.royalties.marvel.model.MarvelResponseFromCharComic;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Neftaly Espino Viveros
 *
 */
@Slf4j
public class RestClient {
	
	@Autowired
	private MyEnviroment env;
	private String privatekey;
	private String publickey;
	private String ts;
	private String url;
	private String resultspage;
	
	
	public RestClient() {
		privatekey=env.getProperty("api.marvel.credentials.privatekey");
		publickey=env.getProperty("api.marvel.credentials.publickey");
		ts=env.getProperty("api.marvel.ts");
		url=env.getProperty("api.marvel.url");
		resultspage=env.getProperty("api.marvel.resultspage");
	}
	
	
	/**
	 * 
	 * @return String 
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @implNote Genera el hash a utilizar al consumir el servicio rest de Marvel
	 */
	private String getHash() throws NoSuchAlgorithmException, UnsupportedEncodingException {
			String toHash=this.ts+this.privatekey+this.publickey;
		
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(toHash.getBytes("UTF-8"));
	       
			StringBuilder sb = new StringBuilder(2*hash.length);
			for(byte b : hash){
				sb.append(String.format("%02x", b&0xff));
			}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param idCharacter
	 * @param page
	 * @return String
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @implNote Genera la url para obtener información del personaje por su id y la página de información a obtener
	 */
	public String getConnectionUrl(int idCharacter,Integer page) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return this.url+idCharacter+"/comics?"+"ts="+this.ts+"&apikey="+this.publickey+"&hash="+this.getHash()+"&limit="+this.resultspage+"&offset="+((page)*Integer.parseInt(this.resultspage));
	}
	

	/**
	 * 
	 * @param idCharacter
	 * @return CharacterInfo
	 * @throws RestClientException
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @implNote Consume el servicio "Comics" de Marvel 
	 */
	public CharacterInfo getDataFromMarvel(int idCharacter) throws RestClientException, NoSuchAlgorithmException, UnsupportedEncodingException {
		int page=0;
		int totalPages=1;
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        ResponseEntity<MarvelResponseFromCharComic> entity;
        JsonNode node;     
    	CharacterInfo charInfo=new CharacterInfo(idCharacter);
    	String[] title = {""};
    	
    	log.info("Sincronizando información con Marvel API para el personaje "+idCharacter);
        
        for(page=0;page<=totalPages;page++) {    
	        entity = new RestTemplate().exchange(this.getConnectionUrl(idCharacter,page), HttpMethod.GET, new HttpEntity<Object>(headers), MarvelResponseFromCharComic.class);
	        node=entity.getBody().getData();
	        if(totalPages==1)
	        	totalPages=(Integer)(node.get("total").intValue()/Integer.parseInt(this.resultspage));
	        
	        log.info("Procesando la página "+page+" de "+totalPages+" ---- Registros totales: "+node.get("total").intValue());
	     
	        node=node.get("results");
	        node.forEach(cre -> {
	        	title[0]=new String(cre.get("title").toString().replaceAll("\"",""));
	        	cre.get("creators").get("items").forEach(item->{
	        		if(item.get("role").toString().equalsIgnoreCase("\"editor\""))
	        			if(!charInfo.getEditors().contains(item.get("name").toString().replaceAll("\"","")))
	        				charInfo.getEditors().add(item.get("name").toString().replaceAll("\"",""));
	        		if(item.get("role").toString().equalsIgnoreCase("\"writer\""))
	        			if(!charInfo.getWriters().contains(item.get("name").toString().replaceAll("\"","")))
	        				charInfo.getWriters().add(item.get("name").toString().replaceAll("\"",""));
	        		if(item.get("role").toString().equalsIgnoreCase("\"colorist\""))
	        			if(!charInfo.getColorists().contains(item.get("name").toString().replaceAll("\"","")))
	        				charInfo.getColorists().add(item.get("name").toString().replaceAll("\"",""));
	        	});
	        	
	        	cre.get("characters").get("items").forEach(item->{
	        		int index = IntStream.range(0, charInfo.getCharacters().size())
	        			     .filter(i -> charInfo.getCharacters().get(i).getCharacter().equals(item.get("name").toString().replaceAll("\"","")))
	        			     .findFirst().orElse(-1);
	        		if(index<0) {
	        			if(!item.get("resourceURI").toString().contains(String.valueOf(idCharacter))) {
	        				CharacterComics chara=new CharacterComics();
	        				chara.setCharacter(item.get("name").toString().replaceAll("\"",""));
	        				chara.getComics().add(title[0]);
	        				charInfo.getCharacters().add(chara);
	        			}
	        				
	        		}else {
	        			if(!charInfo.getCharacters().get(index).getComics().contains(title[0]))
	        				charInfo.getCharacters().get(index).getComics().add(title[0]);
	        		}
	        	});
	        });
	        
	        node=null;
	       
	      
	        
        }
	    
        log.info("Fin de sincronización con Marvel API...");
		
		
		return charInfo;		
	}
	
}
