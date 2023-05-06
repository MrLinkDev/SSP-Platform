package com.ssp.platform.validate;

import com.ssp.platform.entity.Answer;
import com.ssp.platform.response.ValidateResponse;

public class AnswerValidate extends Validator{

    private final int MIN_SIZE_DESCRIPTION = 1;
    private final int MAX_SIZE_DESCRIPTION = 1000;

    public  ValidateResponse validateAnswer(Answer answer){

        String description = answer.getDescription();

        if (description == null || onlySpaces(description)){
            return new ValidateResponse(false, "description", "Поле Текст ответа не может быть пустым");
        }

        if (description.length()< MIN_SIZE_DESCRIPTION || description.length()>MAX_SIZE_DESCRIPTION) {
            String message = String.format("Поле Текст ответа должно содержать от %d до %d символов", MIN_SIZE_DESCRIPTION, MAX_SIZE_DESCRIPTION);
            return new ValidateResponse(false, "description", message);
        }


        return new ValidateResponse(true, "", "ok");
    }


}
