package com.ssp.platform.entity;

import com.fasterxml.jackson.annotation.*;
import com.ssp.platform.entity.enums.SupplyStatus;
import lombok.Data;
import org.hibernate.annotations.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;

/**
 * Сущность предложения к закупке
 * @author Горбунов Александр
 */
@Data
@Entity
@Table(name = "supply")
@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler", "created"})
public class SupplyEntity {

    public static final long DATE_DIVIDER = 1000L;
    public static final long THREE_THOUSAND_YEARS = 32503741200L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @JsonIgnore
    @ManyToOne(targetEntity = Purchase.class)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Column(name = "description")
    private String description;

    @Column(name = "create_date")
    private Long createDate;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "author")
    private User author;

    @Column(name = "budget")
    private Long budget;

    @Column(name = "comment")
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SupplyStatus status;

    @Column(name = "result_of_consideration")
    private String result;

    @Column(name = "result_date")
    private Long resultDate;

    @OneToMany(mappedBy = "supply", targetEntity = FileEntity.class)
    private List<FileEntity> files;
    
    public SupplyEntity(){
        createDate = System.currentTimeMillis() / DATE_DIVIDER;
    }

    public SupplyEntity(
            Purchase purchase, String description, User author,
            Long budget, String comment) {
        //id = UUID.randomUUID();
        this.purchase = purchase;
        this.description = description;
        this.author = author;
        this.budget = budget == null ? 0L : budget;
        this.comment = comment == null ? "" : comment;

        createDate = System.currentTimeMillis() / DATE_DIVIDER;
        status = SupplyStatus.UNDER_REVIEW;

        resultDate = THREE_THOUSAND_YEARS;
        result = "";
    }
}
