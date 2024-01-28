package com.tutorial.graphql.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
	private Status status;
	private int memberId;
	private String message;
}
