package com.ssp.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Сущность ответа
 * @author Изначальный автор Рыжков Дмитрий, доработал Иван Медведев
 */
@Data
@Entity
@Table(name = "answers")
@AllArgsConstructor
public class Answer {

    /**
     * UUID ответа
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Текст ответа
     */
    @NotNull
    private String description;

    /**
     * Дата отправки ответа
     */
    @NotNull
    private Long createDate;

    /**
     * Вопрос
     */
    @OneToOne
    //@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="question")
    @JsonIgnore
    private Question question;

    public Answer(){
        this.createDate = System.currentTimeMillis()/1000;
    }

    public Answer(String description, Question question){
        this.createDate = System.currentTimeMillis()/1000;
        this.description = description;
        this.question = question;
    }
}
