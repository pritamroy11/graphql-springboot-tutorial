package com.tutorial.graphql.interceptor;

import java.util.Collections;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class RequestInterceptor implements WebGraphQlInterceptor {

	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
		String headerValue = request.getHeaders().getFirst("testHeader");
		if (headerValue != null) {
			request.configureExecutionInput(
					(executionInput, builder) -> builder.graphQLContext(Collections.singletonMap("testHeader", headerValue)).build());
		}
		return chain.next(request);
	}

}
