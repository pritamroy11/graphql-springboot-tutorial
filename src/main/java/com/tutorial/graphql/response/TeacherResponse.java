package com.tutorial.graphql.response;

import java.util.List;

import lombok.Data;

@Data
public class TeacherResponse {
	
	private int id;
	
	private String name;
	
	private String contact;
	
	private List<TeacherSubjectResponse> result;
}
