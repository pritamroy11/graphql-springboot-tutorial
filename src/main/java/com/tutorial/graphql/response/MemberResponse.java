package com.tutorial.graphql.response;

import com.tutorial.graphql.entity.MemberType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
	private int id;
	
	private String name;
	
	private String contact;
	
	private MemberType type;
}
