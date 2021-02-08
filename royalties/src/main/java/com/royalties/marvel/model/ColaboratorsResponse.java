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
public class ColaboratorsResponse extends ApiResponse{
	private List<String> editors= new ArrayList<String>();
	private List<String> writers= new ArrayList<String>();
	private List<String> colorists= new ArrayList<String>();	
}
