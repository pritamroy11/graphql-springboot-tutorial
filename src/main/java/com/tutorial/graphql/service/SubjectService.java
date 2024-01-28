package com.tutorial.graphql.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tutorial.graphql.entity.Subject;
import com.tutorial.graphql.repository.SubjectRepository;
import com.tutorial.graphql.response.MemberResponse;
import com.tutorial.graphql.response.MemberSearchResult;
import com.tutorial.graphql.response.TeacherSubjectResponse;

@Service
public class SubjectService {

	@Autowired
	SubjectRepository repository;
	
	public Map<MemberResponse, List<?>> getSubjectsInfoForTeachers(List<MemberResponse> responses) {
		List<Subject> subjects = repository.findAll();
		
		Map<MemberResponse, List<?>> batchingMap = new HashMap<>();
		for (MemberResponse response: responses) {
			List<TeacherSubjectResponse> ssResponse = new ArrayList<>();
			for(Subject sub : subjects) {
				if(response.getId() == sub.getTeacher().getId()) {
					TeacherSubjectResponse res = new TeacherSubjectResponse();
					res.setSubjectName(sub.getSubjectName());
					res.setExperience(sub.getExperience());
					ssResponse.add(res);
				}
			}
			batchingMap.put(response, ssResponse);
		}
		return batchingMap;
	}
	
	public Map<MemberSearchResult, List<?>> getSubjectSearchResults(List<MemberSearchResult> responses) {
		List<Subject> subjects = repository.findAll();
		
		Map<MemberSearchResult, List<?>> batchingMap = new HashMap<>();
		for (MemberSearchResult response: responses) {
			List<TeacherSubjectResponse> ssResponse = new ArrayList<>();
			for(Subject sub : subjects) {
				if(response.getId() == sub.getTeacher().getId()) {
					TeacherSubjectResponse res = new TeacherSubjectResponse();
					res.setSubjectName(sub.getSubjectName());
					res.setExperience(sub.getExperience());
					ssResponse.add(res);
				}
			}
			batchingMap.put(response, ssResponse);
		}
		return batchingMap;
	}
}
