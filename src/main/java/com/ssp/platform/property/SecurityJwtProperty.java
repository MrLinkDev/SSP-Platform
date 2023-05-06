package com.ssp.platform.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки приложения связанные с конфигурацие с авторизацией
 * @author Василий Воробьев
 */
@Component
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityJwtProperty
{
    private String jwtSecret;
    private Long jwtTokenTime;
}
