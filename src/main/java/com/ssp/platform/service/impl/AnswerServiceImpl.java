package com.ssp.platform.service.impl;

import com.ssp.platform.entity.Answer;
import com.ssp.platform.entity.Question;
import com.ssp.platform.repository.AnswerRepository;
import com.ssp.platform.repository.QuestionRepository;
import com.ssp.platform.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с вопросами
 * @author Изначальный автор Рыжков Дмитрий, доработал Иван Медведев
 */
@Service
public class AnswerServiceImpl implements AnswerService {

	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;

	@Autowired
	AnswerServiceImpl(AnswerRepository answerRepository, QuestionRepository questionRepository){
		this.answerRepository = answerRepository;
		this.questionRepository = questionRepository;
	}

	@Override
	public Answer save(Answer answer) {
		return answerRepository.saveAndFlush(answer);
	}

	@Override
	public Optional<Answer> update(Answer answer) {
		Optional<Answer> optionalAnswer = answerRepository.findById(answer.getId());
		if (optionalAnswer.isPresent()){
			return Optional.of(answerRepository.saveAndFlush(answer));
		}
		return Optional.empty();
	}

	@Override
	public boolean delete(UUID id) {
		Optional<Answer> optionalAnswer = answerRepository.findById(id);
		if (optionalAnswer.isPresent()){
			answerRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Optional<Answer> findByQuestID(UUID id) {
		Optional<Question> optionalQuestion = questionRepository.findById(id);
		if (optionalQuestion.isPresent()){
			Question question = optionalQuestion.get();
			return Optional.of(question.getAnswer());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Answer> findById(UUID id) {
		return answerRepository.findById(id);
	}
}
