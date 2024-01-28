package com.tutorial.graphql;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.graphql.test.tester.WebSocketGraphQlTester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.tutorial.graphql.entity.MemberType;
import com.tutorial.graphql.response.Response;
import com.tutorial.graphql.response.Status;

import graphql.Assert;
import graphql.ErrorType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = GraphqlSpringbootTutorialApplication.class)
class GraphqlSpringbootTutorialApplicationTests {

	private static HttpGraphQlTester httpTester;
	private static WebSocketGraphQlTester wsTester;
	
	@BeforeAll
	public static void init(@Autowired WebApplicationContext context) {
		WebTestClient webClient = MockMvcWebTestClient.bindToApplicationContext(context)
				.configureClient()
				.baseUrl("/apis")
				.build();
		httpTester = HttpGraphQlTester.create(webClient);
		
		WebSocketClient wsClient = new ReactorNettyWebSocketClient();
		String url = "ws://localhost:8080/subscription";
		wsTester = WebSocketGraphQlTester.builder(url, wsClient).build();
	}
	
	@Test
	void testGraphqlQuery() {
//		String query = ""
//				+ "{"
//				+ "  secondQuery(firstName: \"test_first\", lastName: \"test_last\")"
//				+ "}";
		String response = httpTester.documentName("secondQuery")
			.execute()
			.path("secondQuery")
			.entity(String.class)
			.matches(p -> p.contains("test_first test_last"))
			.get();
		
		Assert.assertNotNull(response).contains("test_first test_last");
			
	}
	
	@Test
	void testGraphqlQueryWithInterceptor() {
		String query = "{"
				+ "  queryWithInterceptor"
				+ "}";
		
		HttpGraphQlTester modifiedTester = httpTester.mutate()
				.headers(header -> header.set("testHeader", "context value"))
				.build();
		
		modifiedTester.document(query)
			.execute()
			.path("queryWithInterceptor")
			.entity(String.class)
			.matches(p -> p.contains("Hello context value"));
		
	}
	
	@Test
	void testMutation() {
		Response addMemberResponse = httpTester.documentName("addMember")
			.variable("fn", "test_fn")
			.variable("ln", "test_ln")
			.variable("type", MemberType.TEACHER)
			.variable("contact", "test@example.com")
			.execute()
			.path("addMember")
			.entity(Response.class)
			.get();
		
		Assert.assertNotNull(addMemberResponse);
		Assert.assertNotNull(addMemberResponse.getStatus()).equals(Status.SUCCESS);
		Assert.assertTrue(addMemberResponse.getMemberId() > 0);
		
		httpTester.documentName("removeMember")
			.variable("id", addMemberResponse.getMemberId())
			.execute()
			.path("removeMember", np -> 
					np
					.path("status").entity(Status.class).isEqualTo(Status.SUCCESS)
					.path("memberId").entity(Integer.class).isEqualTo(addMemberResponse.getMemberId())
					.path("message").entity(String.class).matches(p -> p.contains("Member Removed"))
			);
		
	}
	
	@Test
	void testMemberSubscription() {
		wsTester.start();
		
		Flux<String> stream = wsTester.documentName("memberSubs")
			.executeSubscription()
			.toFlux("memberSubscription.message", String.class);
		
		// event -> addMember /remove
		
		new java.util.Timer().schedule(
				new java.util.TimerTask() {
					@Override
					public void run() {
						Response addMemberResponse = httpTester.documentName("addMember")
								.variable("fn", "test_fn_subs")
								.variable("ln", "test_ln_subs")
								.variable("type", MemberType.TEACHER)
								.variable("contact", "test@example.com")
								.execute()
								.path("addMember")
								.entity(Response.class)
								.get();
							
						Assert.assertNotNull(addMemberResponse);
						Assert.assertNotNull(addMemberResponse.getStatus()).equals(Status.SUCCESS);
						Assert.assertTrue(addMemberResponse.getMemberId() > 0);
						
						httpTester.documentName("removeMember")
							.variable("id", addMemberResponse.getMemberId())
							.execute()
							.path("removeMember", np -> 
									np
									.path("status").entity(Status.class).isEqualTo(Status.SUCCESS)
									.path("memberId").entity(Integer.class).isEqualTo(addMemberResponse.getMemberId())
									.path("message").entity(String.class).matches(p -> p.contains("Member Removed"))
							);
					}
					
				}, 2000);
		
		// wait for data to come in stream and verify
		StepVerifier.create(stream)
			.expectNext("Member Created: test_fn_subs test_ln_subs")
			.expectNext("Member Removed: test_fn_subs test_ln_subs")
			.thenCancel()
			.verify(Duration.ofSeconds(5));
		
		wsTester.stop();
		
	}
	
	@Test
	void testGraphQLErrorForContextValue() {
		
		String query = "{"
				+ "  queryWithInterceptor"
				+ "}";
		
		httpTester.document(query)
			.execute()
			.errors()
			.expect(error -> error.getMessage().contains("Missing required context value"));
	}
	
	@Test
	void testGraphQLValidationError() {
		httpTester.documentName("addMember")
				.variable("fn", "test_fn")
				.variable("ln", "test_ln")
				.variable("type", MemberType.TEACHER)
				.variable("contact", "testexample.com")
				.execute()
				.errors()
				.expect(error -> error.getErrorType().equals(ErrorType.ValidationError));
	}
	
}
