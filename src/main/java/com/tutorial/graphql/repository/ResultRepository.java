package com.tutorial.graphql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorial.graphql.entity.Result;
import com.tutorial.graphql.entity.ResultID;

@Repository
public interface ResultRepository extends JpaRepository<Result, ResultID>{

	List<Result> findByStudentId(int studentId);
}
