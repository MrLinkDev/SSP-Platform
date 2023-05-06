package com.ssp.platform.repository;

import com.ssp.platform.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID>, JpaSpecificationExecutor<Answer> {

}