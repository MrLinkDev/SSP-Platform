package com.ssp.platform.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки приложения связанные с оповещением по email
 * @author Василий Воробьев
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConnectionProperty
{
    private String username;
}
