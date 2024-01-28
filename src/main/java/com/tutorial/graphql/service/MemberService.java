package com.tutorial.graphql.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.stereotype.Service;

import com.tutorial.graphql.entity.Member;
import com.tutorial.graphql.entity.MemberType;
import com.tutorial.graphql.repository.MemberRepository;
import com.tutorial.graphql.request.MemberInput;
import com.tutorial.graphql.request.PageInput;
import com.tutorial.graphql.response.MemberResponse;
import com.tutorial.graphql.response.MemberSearchResult;
import com.tutorial.graphql.response.Response;
import com.tutorial.graphql.response.Status;

@Service
public class MemberService {
	
	private final MemberRepository repository;

	@Autowired
	public MemberService(MemberRepository repository) {
		this.repository = repository;
	}
	
	public Response addMember (ArgumentValue<MemberInput> memberInputArgValue) {
		Member member = new Member();
		Member savedMember = new Member();
		//System.out.println(memberInputArgValue.isOmitted() + " " +memberInputArgValue.isPresent());
		if (!memberInputArgValue.isOmitted() && memberInputArgValue.isPresent()) {
			MemberInput memberInput = memberInputArgValue.value();
			member.setFirstName(memberInput.getFirstName());
			member.setLastName(memberInput.getLastName());
			member.setContact(memberInput.getContact());
			member.setType(memberInput.getType().toString());
			
			savedMember = repository.saveAndFlush(member);
		}
		String createdString = String.format("Member Created: %s %s", member.getFirstName(),
				member.getLastName());
		return new Response(Status.SUCCESS, savedMember.getId(), createdString);
	}
	
	public List<MemberResponse> getMembers(MemberType type) {
		List<Member> members = null;
		if (null == type) {
			members = repository.findAll();
		} else {
			members = repository.findByType(type.toString());
		}
		List<MemberResponse> responses = new ArrayList<>();
		for(Member member : members) {
			MemberResponse response = new MemberResponse();
			response.setId(member.getId());
			response.setName(member.getFirstName()+" "+member.getLastName());
			response.setContact(member.getContact());
			response.setType(MemberType.valueOf(member.getType()));
			responses.add(response);
		}
		return responses;
	}
	
	public List<MemberSearchResult> getSearchResult(String text) {
		List<Member> members = repository.fetchMembersByName(text);
		if(members.isEmpty()) {
			return new ArrayList<MemberSearchResult>();
		}
		
		List<MemberSearchResult> responses = new ArrayList<>();
		for(Member mem : members) {
			MemberSearchResult res = new MemberSearchResult();
			res.setId(mem.getId());
			res.setName(mem.getFirstName()+" "+mem.getLastName());
			res.setContact(mem.getContact());
			res.setType(MemberType.valueOf(mem.getType()));
			responses.add(res);
		}
		return responses;
	}
	
	public List<MemberResponse> getPaginatedMembers(PageInput pageInput) {
		Page<Member> members = repository.findAll(PageRequest.of(pageInput.getOffset(), pageInput.getLimit()));
		
		List<MemberResponse> responses = new ArrayList<>();
		for(Member member : members) {
			MemberResponse response = new MemberResponse();
			response.setId(member.getId());
			response.setName(member.getFirstName()+" "+member.getLastName());
			response.setContact(member.getContact());
			response.setType(MemberType.valueOf(member.getType()));
			responses.add(response);
		}
		return responses;
	}
	
	public MemberResponse fetchMember(int memberId) {
		Member member = repository.getReferenceById(memberId);
		return new MemberResponse(memberId, member.getFirstName() + " " + member.getLastName(), member.getContact(),
				MemberType.valueOf(member.getType()));
	}
	
	public Response removeMember(int memberId) {
		Member member = repository.getReferenceById(memberId);
		repository.delete(member);
		String removedString = String.format("Member Removed: %s %s", member.getFirstName(),
				member.getLastName());
		return new Response(Status.SUCCESS, member.getId(), removedString);
	}
	
	
	
	
}
