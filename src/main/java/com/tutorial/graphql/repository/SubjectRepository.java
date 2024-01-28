package com.tutorial.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tutorial.graphql.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer>{

}
