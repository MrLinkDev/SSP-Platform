package com.ssp.platform.entity;

import com.fasterxml.jackson.annotation.*;
import com.ssp.platform.entity.enums.QuestionStatus;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Сущность вопроса
 * @author Изначальный автор Рыжков Дмитрий, доработал Иван Медведев
 */
@Data
@Entity
@Table(name = "questions")
public class Question {

    /**
     * UUID вопроса
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Тема вопроса
     */
    @NotNull
    private String name;

    /**
     * Текст сообщения
     */
    @NotNull
    private String description;

    /**
     * Автор сообщения
     */
    @ManyToOne
    @JoinColumn(name = "author_username", nullable = false)
    @NotNull
    private User author;

    /**
     * Закупка, к которой прикреплён вопрос
     */
    @ManyToOne
    @JoinColumn(name = "original_purchase", nullable = false)
    @NotNull
    @JsonIgnore
    private Purchase purchase;

    /**
     * Статус вопроса (публичный/приватный)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private QuestionStatus publicity;

    /**
     * Дата создания вопроса
     */
    @NotNull
    private Long createDate;

    /**
     * Ответ на вопрос
     */
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "question")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Answer answer;

    public Question(){
        this.createDate = System.currentTimeMillis()/1000;
    }

    public Question(String name, String description, User author, Purchase purchase){
        this.name = name;
        this.description = description;
        this.author = author;
        this.purchase = purchase;
        this.createDate = System.currentTimeMillis()/1000;
        this.publicity = QuestionStatus.PRIVATE;
    }

    //Нужно для LinkedHashSet - удаления повторов
    //Если не сработает выборка spring data jpa

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question)o;
        return id.equals(question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
