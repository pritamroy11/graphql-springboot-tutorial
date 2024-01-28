package com.tutorial.graphql.interceptor;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ResponseInterceptor implements WebGraphQlInterceptor {

	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
		return chain.next(request).doOnNext(response -> {
			String value1 = response.getExecutionInput().getGraphQLContext().get("setHeader1");
			String value2 = response.getExecutionInput().getGraphQLContext().get("setHeader2");
			
			response.getResponseHeaders().set("resHeader1", value1);
			response.getResponseHeaders().set("resHeader2", value2);
		});
	}

}
