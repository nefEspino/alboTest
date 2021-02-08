package com.royalties.marvel.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

/**
 * 
 * @author Neftaly Espino Viveros
 *
 */
@Data
@ToString
public class CharactersResponse extends ApiResponse{
	private List<CharacterComics> characters= new ArrayList<CharacterComics>();
}
