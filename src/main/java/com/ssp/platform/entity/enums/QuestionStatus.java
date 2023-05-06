package com.ssp.platform.entity.enums;

public enum QuestionStatus {

    PRIVATE("private"),
    PUBLIC("public");

    public String message;


    QuestionStatus(String status)
    {
        this.message = status;
    }

    public static QuestionStatus fromString(String status) throws IllegalArgumentException {

        for(QuestionStatus questionStatus : QuestionStatus.values()){
            if(questionStatus.getMessage().equals(status)){
                return questionStatus;
            }
        }
        throw new IllegalArgumentException();
    }

    public String getMessage()
    {
        return this.message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
