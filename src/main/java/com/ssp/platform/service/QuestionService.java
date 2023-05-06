package com.ssp.platform.service;

import com.ssp.platform.entity.*;

import java.util.*;

public interface QuestionService {

	Question save(Question question);

	Optional<Question> update(Question question);

	boolean delete(UUID id);

	List<Question> getQuestionsOfPurchase(Purchase purchase);

	Optional<Question> findById(UUID id);

	List<Question> getQuestionsOfPurchaseByAuthor(Purchase purchase, User author);

}