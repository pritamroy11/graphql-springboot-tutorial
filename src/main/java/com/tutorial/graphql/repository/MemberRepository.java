package com.tutorial.graphql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tutorial.graphql.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>{
	
	List<Member> findByType(String type);
	
	@Query(value = "SELECT m FROM Member m WHERE LOWER(m.firstName) like %:name%")
	List<Member> fetchMembersByName (@Param("name") String text);
}
