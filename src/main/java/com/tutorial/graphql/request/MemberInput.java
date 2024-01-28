package com.tutorial.graphql.request;

import com.tutorial.graphql.entity.MemberType;

import lombok.Data;

@Data
public class MemberInput {
	private String firstName;
	private String lastName;
	private MemberType type;
	private String contact;
}
