package com.tutorial.graphql.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherSubjectResponse extends SubjectResponse implements SearchResult{
	
	private int experience;
}
