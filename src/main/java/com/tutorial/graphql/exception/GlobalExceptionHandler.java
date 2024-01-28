package com.tutorial.graphql.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.tutorial.graphql.response.Response;
import com.tutorial.graphql.response.Status;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@GraphQlExceptionHandler
	public GraphQLError handle (ArithmeticException ex, DataFetchingEnvironment dfe) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("details", new Response(Status.FAILURE, -1, "detailed exception reason"));
		return GraphQLError.newError()
				.message("arithmetic exception caught!")
				.errorType(ErrorType.INTERNAL_ERROR)
				.location(dfe.getField().getSourceLocation())
				.path(dfe.getExecutionStepInfo().getPath())
				.extensions(map)
				.build();
	}
	
	@GraphQlExceptionHandler
	public GraphQLError handle (NullPointerException ex, DataFetchingEnvironment dfe) {
		return GraphQLError.newError()
				.message("arithmetic exception caught!")
				.errorType(ErrorType.INTERNAL_ERROR)
				.location(dfe.getField().getSourceLocation())
				.path(dfe.getExecutionStepInfo().getPath())
				.build();
	}
	
	@GraphQlExceptionHandler
	public GraphQLError handle (Throwable ex, DataFetchingEnvironment dfe) {
		return GraphQLError.newError()
				.message(ex.getLocalizedMessage())
				.errorType(ErrorType.INTERNAL_ERROR)
				.location(dfe.getField().getSourceLocation())
				.path(dfe.getExecutionStepInfo().getPath())
				.build();
	}
	
}
