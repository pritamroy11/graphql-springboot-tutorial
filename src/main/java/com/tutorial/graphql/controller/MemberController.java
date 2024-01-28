package com.tutorial.graphql.controller;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.tutorial.graphql.request.MemberInput;
import com.tutorial.graphql.response.MemberResponse;
import com.tutorial.graphql.response.Response;
import com.tutorial.graphql.service.MemberService;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Controller
public class MemberController {
	
	private final MemberService service;
	
	private FluxSink<Response> memberStream;
	private ConnectableFlux<Response> memberPublisher;
	
	@Autowired
	public MemberController(MemberService service) {
		this.service = service;
	}
	
	@PostConstruct
	public void initSubscription() {
		Flux<Response> publisher = Flux.create(emitter -> memberStream = emitter);
		memberPublisher = publisher.publish();
		memberPublisher.connect();
	}
	
	@QueryMapping("getMember")
	public MemberResponse getMember(@Argument int memberId) {
		return service.fetchMember(memberId);
	}
	
	@MutationMapping("addMember")
	public Response addMember(ArgumentValue<MemberInput> memberInput) {
		Response addMemberResponse = service.addMember(memberInput);
		memberStream.next(addMemberResponse);
		return addMemberResponse;
	}
	
	@MutationMapping("removeMember")
	public Response removeMember(@Argument int memberId) {
		Response removeMemberResponse = service.removeMember(memberId);
		memberStream.next(removeMemberResponse);
		return removeMemberResponse;
	}
	
	@SubscriptionMapping("memberSubscription")
	public Publisher<Response> addMemberSubscription() {
		return memberPublisher;
	}
	
}
