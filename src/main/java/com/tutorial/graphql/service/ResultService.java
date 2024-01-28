package com.tutorial.graphql.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tutorial.graphql.entity.Result;
import com.tutorial.graphql.repository.ResultRepository;
import com.tutorial.graphql.response.MemberResponse;
import com.tutorial.graphql.response.MemberSearchResult;
import com.tutorial.graphql.response.StudentSubjectResponse;

@Service
public class ResultService {
	
	@Autowired
	private ResultRepository repository;
	
	public Map<MemberResponse, List<?>> getResultsForStudents(List<MemberResponse> responses) {
		List<Result> results = repository.findAll();
		
		Map<MemberResponse, List<?>> batchingMap = new HashMap<>();
		for (MemberResponse response: responses) {
			List<StudentSubjectResponse> ssResponse = new ArrayList<StudentSubjectResponse>();
			for(Result result: results) {
				if(response.getId() == result.getStudent().getId()) {
					StudentSubjectResponse res = new StudentSubjectResponse();
					res.setMarks(result.getMarks());
					res.setSubjectName(result.getSubject().getSubjectName());
					ssResponse.add(res);
				}
			}
			batchingMap.put(response, ssResponse);
		}
		return batchingMap;
	}
	
	public Map<MemberSearchResult, List<?>> getResultsForSearch(List<MemberSearchResult> responses) {
		List<Result> results = repository.findAll();
		
		Map<MemberSearchResult, List<?>> batchingMap = new HashMap<>();
		for (MemberSearchResult response: responses) {
			List<StudentSubjectResponse> ssResponse = new ArrayList<StudentSubjectResponse>();
			for(Result result: results) {
				if(response.getId() == result.getStudent().getId()) {
					StudentSubjectResponse res = new StudentSubjectResponse();
					res.setMarks(result.getMarks());
					res.setSubjectName(result.getSubject().getSubjectName());
					ssResponse.add(res);
				}
			}
			batchingMap.put(response, ssResponse);
		}
		return batchingMap;
	}
	
}
