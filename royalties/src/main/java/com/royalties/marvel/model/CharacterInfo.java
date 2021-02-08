package com.royalties.marvel.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Neftaly Espino Viveros
 *
 */
@Data
@ToString
@Document(collection = "CharacterInfo")
public class CharacterInfo {
	@Id
	private int id;	
	
	@DateTimeFormat(pattern = "dd/mm/yyyy hh:mm:ss")
	private LocalDateTime last_sync;
		
	private List<String> editors= new ArrayList<String>();
	private List<String> writers= new ArrayList<String>();
	private List<String> colorists= new ArrayList<String>();
	private List<CharacterComics> characters= new ArrayList<CharacterComics>();
	
	public CharacterInfo(int id) {
		this.id=id;
		this.last_sync=LocalDateTime.now();
	}
	
	public CharacterInfo() {}
	
}
