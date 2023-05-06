package com.ssp.platform.email;

import com.ssp.platform.entity.*;
import com.ssp.platform.property.EmailAnnouncementProperty;
import com.ssp.platform.property.EmailConnectionProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * Класс для отправки писем на email
 * Формирует письма, добавляет в очередь и переодически отправляет первое в очереди письмо
 * Формирование и добавление в очередь работает в нескольких потоках
 * Отправление работает в 1 потоке
 * все работает асинхронно
 * @author Василий Воробьев
 */
@Component
@EnableAsync
public class EmailSender
{
    private final JavaMailSender emailSender;
    private final InternetAddress internetAddress;

    Queue<EmailParams> EmailSendQueue;
    private volatile boolean busyQueue = false;
    private volatile boolean busySending = false;
    int SleepTime = 100;

    private final int purchaseCreate;
    private final int supplyEdit;
    private final int answerCreate;
    private final int answerEdit;
    private final int sendCoolDown;
    private final int queMax;
    private final String host;

    private final int purchaseCPriority;
    private final String purchaseCSubject;
    private final String purchaseCFirstLine;
    private final int purchaseCDescription;
    private final int purchaseCBudget;

    private final int supplyEPriority;
    private final String supplyESubject;
    private final String supplyEFirstLine;

    private final int answerPriority;
    private final String answerSubject;
    private final String answerCFirstLine;
    private final String answerEFirstLine;


    @Autowired
    EmailSender(JavaMailSender emailSender, EmailConnectionProperty emailConnectionProperty, EmailAnnouncementProperty emailAProperty) throws MessagingException
    {
        this.emailSender = emailSender;
        this.internetAddress = new InternetAddress(emailConnectionProperty.getUsername(), false);

        this.sendCoolDown = emailAProperty.getSendCoolDown()*1000;
        this.host = emailAProperty.getHost();
        this.queMax = emailAProperty.getQueMax();

        this.purchaseCreate = emailAProperty.getPurchaseCreate();
        this.purchaseCPriority = emailAProperty.getPurchaseCPriority();
        this.purchaseCSubject = emailAProperty.getPurchaseCSubject();
        this.purchaseCFirstLine = emailAProperty.getPurchaseCFirstLine();
        this.purchaseCDescription = emailAProperty.getPurchaseCDescription();
        this.purchaseCBudget = emailAProperty.getPurchaseCBudget();

        this.supplyEdit = emailAProperty.getSupplyEdit();
        this.supplyEPriority = emailAProperty.getSupplyEPriority();
        this.supplyESubject = emailAProperty.getSupplyESubject();
        this.supplyEFirstLine = emailAProperty.getSupplyEFirstLine();

        this.answerCreate = emailAProperty.getAnswerCreate();
        this.answerEdit = emailAProperty.getAnswerEdit();
        this.answerPriority = emailAProperty.getAnswerPriority();
        this.answerSubject = emailAProperty.getAnswerSubject();
        this.answerCFirstLine = emailAProperty.getAnswerCFirstLine();
        this.answerEFirstLine = emailAProperty.getAnswerEFirstLine();

        this.EmailSendQueue = new PriorityQueue<>(queMax, EmailSendComparator);
    }

    /**
     * Формирование оповещения аккредитованых поставщиков при создании закупки
     * @param purchase закупка о которой оповестить
     * @param users список поставщиков
     */
    @Async
    public void sendMailPurchaseCreate(Purchase purchase, List<User> users)
    {
        if (purchaseCreate == 0 || users.size() == 0) return;

        MimeMessage purchaseCreateMessage = emailSender.createMimeMessage();

        String content = purchaseCFirstLine + "<br>";
        content = content + "<b>Название закупки:</b> " + purchase.getName() + "<br>";
        if (purchaseCDescription == 1)
        {
            content = content + "<b>Описание закупки:</b> " + purchase.getDescription() + "<br>";
        }
        if (purchaseCBudget == 1)
        {
            Long budget = purchase.getBudget();
            if (budget > 0) content = content + "<b>Бюджет закупки:</b> " + budget + "<br>";
        }
        content = content + "<b>Закупка доступна по адресу:</b> " + host + "/purchase/" + purchase.getId();
        Date nowDate = new Date();

        try
        {
            purchaseCreateMessage.setFrom(internetAddress);
            purchaseCreateMessage.setSubject(purchaseCSubject, "UTF-8");
            purchaseCreateMessage.setContent(content, "text/html; charset=UTF-8");
            purchaseCreateMessage.setSentDate(nowDate);
        }
        catch (MessagingException e)
        {
            /*TODO логирование warning*/
            return;
        }

        List<EmailParams> emailParamsList = new ArrayList<>(users.size());

        for (User user : users)
        {
            emailParamsList.add(new EmailParams(purchaseCPriority, purchaseCreateMessage, user.getEmail(), nowDate));
        }

        try { addToQueue(emailParamsList); }
        catch (MessagingException | InterruptedException e)
        {
            /*TODO логирование warning*/
        }
    }

    /**
     * Формирование оповещения поставщика при изменении статуса его предложения к закупке
     * @param purchase закупка к которой было сделано предложение
     * @param supplyEntity предложение, статус которого изменилось
     * @param user поставщик которого надо оповестить
     */
    @Async
    public void sendMailSupplyEdit(Purchase purchase, SupplyEntity supplyEntity, User user)
    {
        if (supplyEdit == 0 || !user.getStatus().equals("Approved")) return;

        MimeMessage supplyEditMessage = emailSender.createMimeMessage();

        String content = supplyEFirstLine + " “" + supplyEntity.getStatus()  + "“<br>";
        content = content + "<b>Название закупки:</b> " + purchase.getName() + "<br>";
        String result = supplyEntity.getResult();
        if(result.length() > 0) content = content + "<b>Результат рассмотрения:</b> " + result + "<br>";
        content = content + "<b>Закупка доступна по адресу:</b> " + host + "/purchase/" + purchase.getId();
        Date nowDate = new Date();

        try
        {
            supplyEditMessage.setFrom(internetAddress);
            supplyEditMessage.setSubject(supplyESubject, "UTF-8");
            supplyEditMessage.setContent(content, "text/html; charset=UTF-8");
            supplyEditMessage.setSentDate(nowDate);
        }
        catch (MessagingException e)
        {
            /*TODO логирование warning*/
            return;
        }

        EmailParams emailParams = new EmailParams(supplyEPriority, supplyEditMessage, user.getEmail(), nowDate);


        try { addToQueue(emailParams); }
        catch (MessagingException | InterruptedException e)
        {
            /*TODO логирование warning*/
        }
    }

    /**
     * Формирование оповещения поставщика при ответе на его вопрос
     * @param purchase закупка к которой был дан вопрос
     * @param question вопрос который был дан
     * @param answer ответ сотрудника
     * @param user поставщик которого надо оповестить
     */
    @Async
    public void sendMailAnswerCreate(Purchase purchase, Question question, Answer answer, User user)
    {
        if (answerCreate == 0 || !user.getStatus().equals("Approved")) return;

        MimeMessage answerCEMessage = emailSender.createMimeMessage();

        String content = answerCFirstLine + "<br>";
        content = content + "<b>Название закупки:</b> " + purchase.getName() + "<br>";
        content = content + "<b>Тема вопроса:</b> " + question.getName() + "<br>";
        content = content + "<b>Текст вопроса:</b> " + question.getDescription() + "<br>";
        content = content + "<b>Текст ответа:</b> " + answer.getDescription() + "<br>";
        content = content + "<b>Закупка доступна по адресу:</b> " + host + "/purchase/" + purchase.getId();
        Date nowDate = new Date();

        try
        {
            answerCEMessage.setFrom(internetAddress);
            answerCEMessage.setSubject(answerSubject, "UTF-8");
            answerCEMessage.setContent(content, "text/html; charset=UTF-8");
            answerCEMessage.setSentDate(nowDate);
        }
        catch (MessagingException e)
        {
            /*TODO логирование warning*/
            return;
        }

        EmailParams emailParams = new EmailParams(answerPriority, answerCEMessage, user.getEmail(), nowDate);

        try { addToQueue(emailParams); }
        catch (MessagingException | InterruptedException e)
        {
            /*TODO логирование warning*/
        }
    }

    /**
     * Формирование оповещения поставщика при изменении ответа на его вопрос
     * @param purchase закупка к которой был дан вопрос
     * @param question вопрос который был дан
     * @param answer ответ сотрудника
     * @param user поставщик которого надо оповестить
     */
    @Async
    public void sendMailAnswerEdit(Purchase purchase, Question question, Answer answer, User user)
    {
        if (answerEdit == 0 || !user.getStatus().equals("Approved")) return;

        MimeMessage answerCEMessage = emailSender.createMimeMessage();

        String content = answerEFirstLine + "<br>";
        content = content + "Название закупки: " + purchase.getName() + "<br>";
        content = content + "Тема вопроса: " + question.getName() + "<br>";
        content = content + "Текст вопроса: " + question.getDescription() + "<br>";
        content = content + "Текст ответа: " + answer.getDescription() + "<br>";
        content = content + "Закупка доступна по адресу: " + host + "/purchase/" + purchase.getId();
        Date nowDate = new Date();

        try
        {
            answerCEMessage.setFrom(internetAddress);
            answerCEMessage.setSubject(answerSubject, "UTF-8");
            answerCEMessage.setContent(content, "text/html; charset=UTF-8");
            answerCEMessage.setSentDate(nowDate);
        }
        catch (MessagingException e)
        {
            /*TODO логирование warning*/
        }

        EmailParams emailParams = new EmailParams(answerPriority, answerCEMessage, user.getEmail(), nowDate);

        try { addToQueue(emailParams); }
        catch (MessagingException | InterruptedException e)
        {
            /*TODO логирование warning*/
        }
    }

    /**
     * Добавление писем в очередь на отправку
     * @param emailParams параметры для отправки и очереди, такие как приоритет отправки, письмо и адресс куда будет
     *                   совершена отправки
     */
    @Async
    private void addToQueue(EmailParams emailParams) throws InterruptedException, MessagingException
    {
        while(busyQueue)
        {
            Thread.sleep(SleepTime);
            //TODO: wait notify в будущем сделать
        }
        busyQueue = true;

        if(EmailSendQueue.size() + 1 > queMax)
        {
            busyQueue = false;
            /*TODO логирование превышение очереди*/
            return;
        }

        EmailSendQueue.add(emailParams);
        busyQueue = false;
        if(!busySending) beginSendMail();
    }

    /**
     * @param emailParamsList параметры для отправки и очереди, такие как приоритет отправки, письмо и адресс куда будет
     *                       совершена отправки
     */
    @Async
    private void addToQueue(List<EmailParams> emailParamsList) throws InterruptedException, MessagingException
    {
        while(busyQueue)
        {
            Thread.sleep(SleepTime);
        }

        busyQueue = true;

        if(EmailSendQueue.size() + emailParamsList.size() > queMax)
        {
            busyQueue = false;
            /*TODO логирование превышение очереди*/
            return;
        }

        EmailSendQueue.addAll(emailParamsList);

        busyQueue = false;

        if(!busySending)
        {
            beginSendMail();
        }
    }

    /**
     * Отправление писем на почту
     * Отправляет первое в очереди
     */
    @Async
    private void beginSendMail() throws InterruptedException, MessagingException
    {
        busySending = true;

        while(busyQueue)
        {
            Thread.sleep(SleepTime);
        }

        busyQueue = true;
        EmailParams params = EmailSendQueue.poll();

        if(params == null)
        {
            busySending = false;
            busyQueue = false;
            return;
        }
        busyQueue = false;

        MimeMessage message = params.getMessage();

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(params.getEmail()));

        emailSender.send(message);
        if(sendCoolDown > 0) Thread.sleep(sendCoolDown);
        beginSendMail();
    }

    /**
     * Письма выстраиваются в очередь сначала по приоритету, если приоритет одинаков - по дате
     */
    private static final Comparator<EmailParams> EmailSendComparator = new Comparator<EmailParams>()
    {
        @Override
        public int compare(EmailParams c1, EmailParams c2)
        {
            int priority1 = c1.getPriority();
            int priority2 = c2.getPriority();

            if (priority1 != priority2) return priority1 - priority2;
            else return c1.getDate().compareTo(c2.getDate());
        }
    };

}