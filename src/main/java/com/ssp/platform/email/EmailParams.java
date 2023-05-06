package com.ssp.platform.email;

import lombok.Data;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * сущность для параметров почты, из таких сущностей формируется очередь
 * для отправки на email
 * @author Василий Воробьев
 */
@Data
public class EmailParams
{
    /**
     * приоритет отправки
     */
    private int priority;

    /**
     * Сообщение которое будет отправлено
     */
    private MimeMessage message;

    /**
     * Адресс куда будет отправлено
     */
    private String email;

    /**
     * Дата формирования сущности
     */
    private Date date;

    public EmailParams(int priority, MimeMessage message, String email, Date date)
    {
        this.priority = priority;
        this.message = message;
        this.email = email;
        this.date = date;
    }
}
