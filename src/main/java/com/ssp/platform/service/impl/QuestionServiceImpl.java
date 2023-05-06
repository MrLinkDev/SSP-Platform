package com.ssp.platform.service.impl;

import com.ssp.platform.entity.*;
import com.ssp.platform.entity.enums.QuestionStatus;
import com.ssp.platform.repository.QuestionRepository;
import com.ssp.platform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис для работы с вопросами
 * @author Изначальный автор Рыжков Дмитрий, доработал Иван Медведев
 */
@Service
public class QuestionServiceImpl implements QuestionService {

	private final QuestionRepository questionRepository;

	@Autowired
	QuestionServiceImpl(QuestionRepository questionRepository){
		this.questionRepository = questionRepository;
	}


	@Override
	public Question save(Question question) {
		return questionRepository.saveAndFlush(question);
	}

	@Override
	public Optional<Question> update(Question question) {
		Optional<Question> optionalQuestion = questionRepository.findById(question.getId());
		if (optionalQuestion.isPresent()){
			return Optional.of(questionRepository.saveAndFlush(question));
		}
		return Optional.empty();
	}

	@Override
	public boolean delete(UUID id) {
		Optional<Question> optionalQuestion = questionRepository.findById(id);
		if (optionalQuestion.isPresent()){
			questionRepository.deleteById(id);
			return true;
		}
		return false;
	}

    @Override
    public List<Question> getQuestionsOfPurchase(Purchase purchase) {
	    List<Question> questions = questionRepository.findByPurchase(purchase);
        //сортировка по дате
        questions.sort(Comparator.comparing(Question::getCreateDate).reversed());
	    return questions;
    }

    @Override
	public Optional<Question> findById(UUID id) {
		return questionRepository.findById(id);
	}

    @Override
    public List<Question> getQuestionsOfPurchaseByAuthor(Purchase purchase, User author) {

	    //план Б если будут проблемы с чудовищем ниже

	    /*List<Question> questions = questionRepository.findByPurchaseAndAuthor(purchase, author);
	    questions.addAll(questionRepository.findByPurchaseAndPublicity(purchase, QuestionStatus.PUBLIC));

	    //удаляем повторы
	    Set<Question> set = new LinkedHashSet<>(questions);

	    //возвращаем обратно в List
	    questions = new ArrayList<>(set);

	    return questions;*/

        List<Question> questions = questionRepository.findByPurchaseAndAuthorOrPurchaseAndPublicity(purchase, author, purchase, QuestionStatus.PUBLIC);

        //сортировка по дате
        questions.sort(Comparator.comparing(Question::getCreateDate).reversed());

	    return questions;
    }
}
