package com.royalties.marvel.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class CharacterComics {
	@Id
	private String character;
	private List<String> comics=new ArrayList<String>();
	

	@Override
	public String toString(){
		return character;
	}
	
	@Override
	public boolean equals(Object o) { 
		  
        if (o == this) { 
            return true; 
        } 
  
        if (!(o instanceof CharacterComics)) { 
            return false; 
        } 
          
        CharacterComics c = (CharacterComics) o; 
          
        return this.getCharacter().equals(c.getCharacter());
    } 
}
