package com.ssp.platform.telegram;

import lombok.*;
import org.hibernate.annotations.Proxy;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;

@Data
@Entity
@Table(name = "telegram_users")
@AllArgsConstructor
@NoArgsConstructor
@Proxy(lazy = false)
public class TelegramUsersEntity {

    @Id
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "temp_code")
    private Integer tempCode;
}
