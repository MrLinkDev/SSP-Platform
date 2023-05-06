package com.ssp.platform.validate.ValidatorMessages;

public class SupplyMessages {

    public static final String OK = "OK";

    public static final String ONLY_SPACES_ERROR = "Поле не может состоять только из пробелов";

    public static final String SUPPLY_ALREADY_EXIST_BY_USER_ERROR = "Можно создавать только одно предложение к закупке от одного автора";

    public static final String WRONG_PURCHASE_ID_ERROR = "Закупки с данным ID не существует";

    public static final String WRONG_SUPPLY_ID_ERROR = "Предложения с данным ID не существует";

    public static final String SUPPLY_WRONG_DATE_ERROR = "Подача предложений по данной закупке уже завершена";

    public static final String EMPTY_SUPPLY_DESCRIPTION_ERROR = "Поле 'Описание предложения' не может быть пустым";

    public static final String WRONG_SUPPLY_DESCRIPTION_BOUNDS_ERROR = "Поле 'Описание предложения' должно содержать до 1000 символов";

    public static final String WRONG_SUPPLY_BUDGET_BOUNDS_ERROR = "Поле 'Бюджет' должно содержать до 8 символов";

    public static final String NEGATIVE_SUPPLY_BUDGET_ERROR = "Содержимое поля 'Бюджет' не должно быть меньше нуля!";

    public static final String WRONG_SUPPLY_COMMENT_BOUNDS_ERROR = "Поле 'Комментарии' должно содержать до 1000 символов";

    public static final String EMPTY_SUPPLY_STATUS_ERROR = "Поле 'Статус предложения' не может быть пустым";

    public static final String WRONG_SUPPLY_STATUS_ERROR = "Данного статуса для предложения не существует";

    public static final String EMPTY_SUPPLY_RESULT_ERROR = "Поле 'Результат рассмотрения' не может быть пустым";

    public static final String WRONG_SUPPLY_RESULT_BOUNDS_ERROR = "Поле 'Результат рассмотрения' должно содержать до 1000 символов";

    public static final String WRONG_ROLE_FOR_UPDATING = "У Вас нет прав для изменения данного поля";

    public static final String WRONG_ROLE_FOR_DELETING = "У Вас нет прав для удаления данной закупки";

    public static final String TIME_IS_OVER = "На данном этапе нельзя изменить предложение";

}
