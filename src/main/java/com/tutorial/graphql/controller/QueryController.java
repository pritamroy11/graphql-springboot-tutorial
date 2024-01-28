package com.tutorial.graphql.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.tutorial.graphql.entity.MemberType;
import com.tutorial.graphql.request.PageInput;
import com.tutorial.graphql.response.MemberResponse;
import com.tutorial.graphql.response.MemberSearchResult;
import com.tutorial.graphql.service.MemberService;
import com.tutorial.graphql.service.ResultService;
import com.tutorial.graphql.service.SubjectService;
import com.tutorial.graphql.utils.EncoderDecoder;

import graphql.GraphQLContext;
import graphql.relay.Connection;
import graphql.relay.ConnectionCursor;
import graphql.relay.DefaultConnection;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import graphql.schema.DataFetchingEnvironment;

@Controller
public class QueryController {
	
	private final MemberService memberService;
	private final ResultService resultService;
	private final SubjectService subjectService;
	
	@Autowired
	QueryController(SubjectService subjectService, ResultService resultService, MemberService memberService) {
		this.memberService = memberService;
		this.resultService = resultService;
		this.subjectService = subjectService;
	}
	
	@QueryMapping
	public String firstQuery() {
		return "First GQL Query successful";
	}
	
	@QueryMapping
	public String queryWithInterceptor(DataFetchingEnvironment dfe, @ContextValue String testHeader, GraphQLContext context) {
		//String value = dfe.getGraphQlContext().get("testHeader");
		context.put("setHeader1", "test1");
		dfe.getGraphQlContext().put("setHeader2", "test2");
		return "Hello "+testHeader;
	}
	
	@QueryMapping
	public String secondQuery(@Argument String firstName, @Argument String lastName) {
		return firstName + " " + lastName;
	}
	
	@QueryMapping(name = "getMembers")
	public List<MemberResponse> getMembers(@Argument("filter") MemberType type) {
		// fetch all students records
		System.out.println(":: fetching all members ::");
		return memberService.getMembers(type);
	}
	
	@BatchMapping(typeName = "MemberResponse", field = "subjectData")
	public Map<MemberResponse, List<?>> getSubjectsData (List<MemberResponse> members) {
		System.out.println(":: fetching all members subject data ::");
		List<MemberResponse> students = new ArrayList<MemberResponse>();
		List<MemberResponse> teachers = new ArrayList<MemberResponse>();
		Map<MemberResponse, List<?>> outputMap = new HashMap<MemberResponse, List<?>>();
		
		members.forEach(member -> {
			if(member.getType() == MemberType.TEACHER) {
				teachers.add(member);
			} else {
				students.add(member);
			}
		});
		
		if (!teachers.isEmpty()) {
			System.out.println(":: fetching teachers subject data ::");
			outputMap.putAll(subjectService.getSubjectsInfoForTeachers(teachers));
		}
		if (!students.isEmpty()) {
			System.out.println(":: fetching students subject data ::");
			outputMap.putAll(resultService.getResultsForStudents(students));
		}		
		
		return outputMap;
		
	}
	
	@QueryMapping("searchByName")
	public List<MemberSearchResult> getSearchResult (@Argument String text) {
		return memberService.getSearchResult(text);
		
	}
	
	@BatchMapping(typeName = "MemberSearchResult", field = "subjectData")
	public Map<MemberSearchResult, List<?>> getSearchData (List<MemberSearchResult> members) {
		System.out.println(":: searching and fetching all members subject data ::");
		List<MemberSearchResult> students = new ArrayList<MemberSearchResult>();
		List<MemberSearchResult> teachers = new ArrayList<MemberSearchResult>();
		Map<MemberSearchResult, List<?>> outputMap = new HashMap<MemberSearchResult, List<?>>();
		
		members.forEach(member -> {
			if(member.getType() == MemberType.TEACHER) {
				teachers.add(member);
			} else {
				students.add(member);
			}
		});
		
		if (!teachers.isEmpty()) {
			outputMap.putAll(subjectService.getSubjectSearchResults(teachers));
		}
		if (!students.isEmpty()) {
			outputMap.putAll(resultService.getResultsForSearch(students));
		}		
		
		return outputMap;
		
	}
	
	@QueryMapping(name = "getPaginatedMembers")
	public Connection<MemberResponse> getMembers2(@Argument PageInput page) {
		// fetch all students records
		System.out.println(":: fetching all members ::");
		if (page.getAfter() != null) {
			String decodedValue = EncoderDecoder.decode(page.getAfter());
			int offset = Integer.valueOf(decodedValue)/page.getLimit();
			page.setOffset(offset);
		}
		List<MemberResponse> response = memberService.getPaginatedMembers(page);
		
		List<Edge<MemberResponse>> edges = response.stream()
				.map(member -> (Edge<MemberResponse>) new DefaultEdge<MemberResponse>(member, 
						new DefaultConnectionCursor(EncoderDecoder.encode(String.valueOf(member.getId())))))
				.toList();
		
		ConnectionCursor startCursor = edges.isEmpty() ? null : edges.get(0).getCursor();
		ConnectionCursor endCursor = edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor();
		boolean hasPrev = page.getOffset() > 0;
		boolean hasNext = page.getLimit() == response.size();
		
		PageInfo pageInfo = new DefaultPageInfo(startCursor, endCursor, hasPrev, hasNext);
		return new DefaultConnection<MemberResponse>(edges, pageInfo);
		
	}

}
