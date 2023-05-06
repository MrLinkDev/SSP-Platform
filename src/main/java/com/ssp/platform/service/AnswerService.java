package com.ssp.platform.service;

import com.ssp.platform.entity.Answer;

import java.util.Optional;
import java.util.UUID;

public interface AnswerService {

	Answer save(Answer answer);

	Optional<Answer> update(Answer answer);

	boolean delete(UUID id);

	Optional<Answer> findByQuestID(UUID id);

	Optional<Answer> findById(UUID id);

}
