package com.ssp.platform.request;

import lombok.Data;

/**
 * Промежуточная сущность с данными запроса по получению списка пользователей
 * @author Василий Воробьев
 */
@Data
public class UsersPageRequest
{
    private Integer requestPage = 0;
    private Integer numberOfElements = 10;
    private String type;

    public UsersPageRequest(Integer requestPage, Integer numberOfElements, String type)
    {
        this.requestPage = requestPage;
        this.numberOfElements = numberOfElements;
        this.type = type;
    }
}
