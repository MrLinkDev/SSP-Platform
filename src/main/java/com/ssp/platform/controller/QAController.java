package com.ssp.platform.controller;

import com.ssp.platform.entity.*;
import com.ssp.platform.entity.enums.QuestionStatus;
import com.ssp.platform.logging.Log;
import com.ssp.platform.request.QuestionUpdateRequest;
import com.ssp.platform.response.*;
import com.ssp.platform.security.service.UserDetailsServiceImpl;
import com.ssp.platform.service.*;
import com.ssp.platform.telegram.SSPPlatformBot;
import com.ssp.platform.validate.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Контроллер для действий с вопросами и ответами
 * @author Изначальный автор Рыжков Дмитрий, доработал Иван Медведев
 */
@RestController
public class QAController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserDetailsServiceImpl userDetailsService;
	private final PurchaseService purchaseService;
	private final QuestionValidate questionValidate;
	private final Log log;
	private final SSPPlatformBot sspPlatformBot;

	Logger logger = LoggerFactory.getLogger(QAController.class);

	@Autowired
    public QAController(
            QuestionService questionService, AnswerService answerService, UserDetailsServiceImpl userDetailsService,
            PurchaseService purchaseService,
            QuestionValidate questionValidate,
            Log log,
            SSPPlatformBot sspPlatformBot
    ) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.userDetailsService = userDetailsService;
        this.purchaseService = purchaseService;
        this.questionValidate = questionValidate;
        this.log = log;
        this.sspPlatformBot = sspPlatformBot;
    }

    /**
	 * Метод создания вопроса. Доступен сотрудникам и поставщикам
	 * @param token Токен пользователя для выставления автора
	 * @param name тема вопроса
	 * @param description текст вороса
	 * @param purchaseId id закупки вопроса
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping(value = "/question", produces = "application/json")
	@PreAuthorize("hasAuthority('firm')")
	public ResponseEntity<Object> addQuestion
			(
			@RequestHeader("Authorization") String token,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "description") String description,
			@RequestParam(value = "purchaseId") UUID purchaseId
 	        )throws IOException, NoSuchAlgorithmException {

	    if (purchaseId == null){
	        return new ResponseEntity<>(new ValidateResponse(false, "id", "Поле ID не может быть пустым"), HttpStatus.NOT_ACCEPTABLE);
        }
		User author = userDetailsService.loadUserByToken(token);
		Optional<Purchase> optionalPurchase = purchaseService.findById(purchaseId);
		if (!optionalPurchase.isPresent()){
			return new ResponseEntity<>("Закупки по данному id не существует!", HttpStatus.NOT_ACCEPTABLE);
		}

		//устанавливаем приватный статус в конструкторе
		Question question = new Question(name, description, author, optionalPurchase.get());

		//валидация
        ValidateResponse validateResponse = questionValidate.validateQuestion(question);
        if(!validateResponse.isSuccess()){
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

		try
		{
		    log.info(author, Log.CONTROLLER_QA, "Вопрос создан", name, description, purchaseId);
			return new ResponseEntity<>(questionService.save(question), HttpStatus.CREATED);
		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Метод изменения вопроса. Доступен только сотруднику: т.к. может поменять статус вопроса
	 * и все поля (даже дату создания можно было без #97 стоки)
	 * @param questionUpdateRequest запрос на изменение вопроса
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/question", produces = "application/json", consumes = "application/json")
	@PreAuthorize("hasAuthority('employee')")
	public ResponseEntity<Object> changeQuestion(@RequestHeader("Authorization") String token, @RequestBody QuestionUpdateRequest questionUpdateRequest)
			throws IOException, NoSuchAlgorithmException {

        //валидация
        ValidateResponse validateResponse = questionValidate.validateQuestionUpdate(questionUpdateRequest);
        if(!validateResponse.isSuccess()){
            return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

		try{
			Question question = questionService.findById(questionUpdateRequest.getId()).orElse(null);
			if(question == null){
                return new ResponseEntity<>(new ApiResponse(false, "Вопрос не найден"), HttpStatus.NOT_FOUND);
			}

			Object[] was = {
			        question.getId(),
                    question.getName(),
                    question.getDescription(),
                    question.getPublicity()};

			question.setName(questionUpdateRequest.getName());
			question.setDescription(questionUpdateRequest.getDescription());

			QuestionStatus questionStatus = QuestionStatus.fromString(questionUpdateRequest.getPublicity());
			question.setPublicity(questionStatus);

			Optional<Question> optionalQuestion = questionService.update(question);
			if (optionalQuestion.isPresent())
			{
			    Object[] became = {
			            optionalQuestion.get().getId(),
                        optionalQuestion.get().getName(),
                        optionalQuestion.get().getDescription(),
                        optionalQuestion.get().getPublicity()};

			    log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_QA, "Вопрос изменён", was, became);

				return new ResponseEntity<>(optionalQuestion.get(), HttpStatus.OK);
			}

            return new ResponseEntity<>(new ApiResponse(false, "Вопрос не найден"), HttpStatus.NOT_FOUND);

		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Метод получения списка публичных вопросов (и заданных самим пользователем) по закупке (нужно указать id нужной закупки).
	 * Метод предназначен как для сотрудников, так и для поставщиков.
	 * @param purchaseId id закупки вопроса
	 * @return
	 */
	@GetMapping(value = "/questions", produces = "application/json")
	@PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
	public ResponseEntity<Object> getQuestion(  @RequestHeader("Authorization") String token,
												@RequestParam(value = "purchaseId") String purchaseId) {
		if (purchaseId.isEmpty()) {
			return new ResponseEntity<>(new ApiResponse(false, "Пустое поле purchaseId"), HttpStatus.NOT_ACCEPTABLE);
		}

		Purchase purchase = purchaseService.findById(UUID.fromString(purchaseId)).orElse(null);
		if (purchase == null) {
			return new ResponseEntity<>(new ApiResponse(false, "Закупки не существует"), HttpStatus.INTERNAL_SERVER_ERROR);
		}


		//Разделение исполняемого кода по ролям (employee, firm).
		//Сотрудникам видны все вопросы. Поставщикам публичные и свои
		User user = userDetailsService.loadUserByToken(token);
		if (user.getRole().equals("employee")) {
			try {
				return new ResponseEntity<>(questionService.getQuestionsOfPurchase(purchase), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if (user.getRole().equals("firm")) {

			try {
				return new ResponseEntity<>(questionService.getQuestionsOfPurchaseByAuthor(purchase,user), HttpStatus.OK);

			} catch (Exception e) {
				return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>(new ApiResponse(false, "Unreal mistake!"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Метод получения вопроса по id. Метод предназначен для сотрудников и поставщиков
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/question/{id}", produces = "application/json")
	@PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
	public ResponseEntity<Object> getQuestionForEmployee(   @RequestHeader("Authorization") String token,
															@PathVariable(value = "id") UUID id)
	{
	    //заменил id.isEmpty()
		if (id == null)
		{
			return new ResponseEntity<>(new ApiResponse(false, "Пустое поле id"), HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Question> optionalQuestion = questionService.findById(id);
		if (!optionalQuestion.isPresent()){
			return new ResponseEntity<>(new ApiResponse(false, "Вопроса не существует"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Question question = optionalQuestion.get();

		User user = userDetailsService.loadUserByToken(token);
		if (user.getRole().equals("employee")){
			try {
				return new ResponseEntity<>(question, HttpStatus.OK);
			}
			catch (Exception e){
				return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if (user.getRole().equals("firm")){
			try {
				if (question.getAuthor().equals(user) ||
						question.getPublicity().equals(QuestionStatus.PUBLIC)){
					return new ResponseEntity<>(optionalQuestion.get(), HttpStatus.OK);
				}
				return new ResponseEntity<>(new ApiResponse(false,
						"Доступ к чужим приватным вопросам запрещён"), HttpStatus.FORBIDDEN);
			}
			catch (Exception e){
				return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<>(new ApiResponse(false, "Unreal mistake!"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Метод удаления вопроса.
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/question/{id}", produces = "application/json")
	@PreAuthorize("hasAuthority('employee') or hasAuthority('firm')")
	public ResponseEntity<Object> deleteQuestion(@RequestHeader("Authorization") String token, @PathVariable(name = "id") UUID id)
	{

		if (id == null)
		{
			return new ResponseEntity<>(new ApiResponse(false, "Пустое поле id"), HttpStatus.NOT_ACCEPTABLE);
		}

        User user = userDetailsService.loadUserByToken(token);

		Question question = questionService.findById(id).orElse(null);

		if(question == null){
		    return new ResponseEntity<>(new ApiResponse(false, "Вопрос не найден"), HttpStatus.NOT_FOUND);
        }

		//Если пользователь - не является сотрудником или автором вопроса - ошибка
		if( (!question.getAuthor().equals(user)) && (!user.getRole().equals("employee")) ){
		    return new ResponseEntity<>(new ApiResponse(false, "Вопрос может удалить только его автор или сотрудник ресурса"), HttpStatus.NOT_ACCEPTABLE);
        }


		try
		{
			if (questionService.delete(id))
			{
			    log.info(user, Log.CONTROLLER_QA, "Вопрос удалён", id);
				return new ResponseEntity<>(new ApiResponse(true, "Вопрос удалён"), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiResponse(false, "Вопрос не найден"), HttpStatus.NOT_FOUND);

		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/////////////////////////////////////
	//Методы по работе с вопросами ниже//
	/////////////////////////////////////

	/**
	 * Меотод добавления ответа на вопрос. Доступен только сотрудникам
	 * @param description
	 * @param questionId
	 * @param publicity
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping(value = "/answer", produces = "application/json")
	@PreAuthorize("hasAuthority('employee')")
	public ResponseEntity<Object> addAnswer
			(
                    @RequestHeader("Authorization") String token,
                    @RequestParam(value = "description") String description,
					@RequestParam(value = "questionId") String questionId,
					@RequestParam(value = "publicity") String publicity
			)throws IOException, NoSuchAlgorithmException {

		Optional<Question> optionalQuestion = questionService.findById(UUID.fromString(questionId));
		if (!optionalQuestion.isPresent()){
			return new ResponseEntity<>("Вопрос по данному id не существует!", HttpStatus.NOT_ACCEPTABLE);
		}
		if (publicity.isEmpty()){
			return new ResponseEntity<>("Пустой статус", HttpStatus.NOT_ACCEPTABLE);
		}

        Question question = optionalQuestion.get();

        if(question.getAnswer() != null){
            return new ResponseEntity<>(new ApiResponse(false, "Ответ уже существует, добавить новый невозможно"), HttpStatus.NOT_ACCEPTABLE);
        }

        question.setPublicity(QuestionStatus.valueOf(publicity));
		Answer answer = new Answer(description, question);

		AnswerValidate answerValidate = new AnswerValidate();
		ValidateResponse validateResponse = answerValidate.validateAnswer(answer);
		if(!validateResponse.isSuccess()){
		    return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

		question.setAnswer(answer);

		try
		{
		    //сначала сохраняем ответ, потом обновляем вопрос иначе ошибка
		    answer = answerService.save(answer);
			questionService.update(question);

			sspPlatformBot.notifyAboutAnswer(question);

			log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_QA, "Ответ создан", description, questionId, publicity);

			return new ResponseEntity<>(answer, HttpStatus.CREATED);
		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Метод изменения ответа. Доступен только сотрудникам
	 * @param id
     * @param description
     * @param publicity
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	@PutMapping(value = "/answer", produces = "application/json")
	@PreAuthorize("hasAuthority('employee')")
	public ResponseEntity<Object> changeAnswer(
	                @RequestHeader("Authorization") String token,
                    @RequestParam(value = "id") UUID id,
                    @RequestParam(value = "description") String description,
                    @RequestParam(value = "publicity", defaultValue = "false") Boolean publicity)
			throws IOException, NoSuchAlgorithmException {

        if (id == null)
        {
            return new ResponseEntity<>(new ApiResponse(false, "Пустое поле id"), HttpStatus.NOT_ACCEPTABLE);
        }

	    Answer answer = answerService.findById(id).orElse(null);
        if (answer == null){
            return new ResponseEntity<>(new ApiResponse(false, "Ответ, который вы хотите изменить, не найден"), HttpStatus.NOT_ACCEPTABLE);
        }

        Object[] was = {answer.getDescription()};

        answer.setDescription(description);

        AnswerValidate answerValidate = new AnswerValidate();
	    ValidateResponse validateResponse = answerValidate.validateAnswer(answer);
	    if (!validateResponse.isSuccess()){
	        return new ResponseEntity<>(validateResponse, HttpStatus.NOT_ACCEPTABLE);
        }

	    Question question = answer.getQuestion();

	    if (publicity){
	        question.setPublicity(QuestionStatus.PUBLIC);
        }
	    if(!publicity){
	        question.setPublicity(QuestionStatus.PRIVATE);
        }

		try
		{
		    Optional<Question> optionalQuestion = questionService.update(question);
			Optional<Answer> optionalAnswer = answerService.update(answer);
			if (optionalAnswer.isPresent() && optionalQuestion.isPresent())
			{
                Object[] became = {optionalAnswer.get().getDescription()};
                log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_QA, "Ответ изменён", was, became);

				return new ResponseEntity<>(optionalAnswer.get(), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiResponse(false, "Ответ не найден"), HttpStatus.NOT_FOUND);
		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Метод получения ответа по id. Доступен только сотрудникам
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/answer/{id}", produces = "application/json")
	@PreAuthorize("hasAuthority('employee')")
	public ResponseEntity<Object> getAnswer(  @PathVariable(value = "id") UUID id)
	{
		if (id == null)
		{
			return new ResponseEntity<>(new ApiResponse(false, "Пустое поле id"), HttpStatus.NOT_ACCEPTABLE);
		}

		try {
			Optional<Answer> optionalAnswer = answerService.findById(id);
			if (optionalAnswer.isPresent()){
				return new ResponseEntity<>(optionalAnswer.get(), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiResponse(false, "Ответа не существует"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception e){
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Метод удаления ответа на вопрос. Доступен только сотрудникам
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/answer/{id}", produces = "application/json")
	@PreAuthorize("hasAuthority('employee')")
	public ResponseEntity<Object> deleteAnswer(@RequestHeader("Authorization") String token, @PathVariable(name = "id") UUID id)
	{
		if (id == null)
		{
			return new ResponseEntity<>(new ApiResponse(false, "Пустое поле id"), HttpStatus.NOT_ACCEPTABLE);
		}

		try
		{
		    Answer answer = answerService.findById(id).orElse(null);
		    if (answer == null){
                return new ResponseEntity<>(new ApiResponse(false, "Ответ не найден"), HttpStatus.NOT_FOUND);
            }

		    //важно порвать связь с вопросом для корректного удаления
		    Question question = answer.getQuestion();
		    question.setAnswer(null);
		    questionService.update(question);

			if (answerService.delete(id))
			{
			    log.info(userDetailsService.loadUserByToken(token), Log.CONTROLLER_QA, "Ответ удалён", id);
				return new ResponseEntity<>(new ApiResponse(true, "Ответ удалён"), HttpStatus.OK);
			}
			return new ResponseEntity<>(new ApiResponse(false, "Ответ не найден"), HttpStatus.NOT_FOUND);

		}
		catch (Exception e)
		{
			return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}