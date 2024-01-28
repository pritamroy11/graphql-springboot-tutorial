package com.tutorial.graphql.response;

import com.tutorial.graphql.entity.MemberType;

import lombok.Data;

@Data
public class MemberSearchResult {
	
	private int id;
	
	private String name;
	
	private String contact;
	
	private MemberType type;

}
