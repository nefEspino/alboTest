package com.royalties.marvel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

/**
 * 
 * @author Neftaly Espino Viveros
 *
 */
@Data
public class MarvelResponseFromCharComic{
	
	private Integer code;
	private String status;
	private String copyright;
	private String attibutionText;
	private String attributionHTML;
	private String etag;
	@JsonProperty("data")
	private JsonNode data;
}
