package com.ssp.platform.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Настройки приложения связанные с созданием пользователя сотрудника в системе
 * @author Василий Воробьев
 */
@Component
@Data
@ConfigurationProperties(prefix = "user-create")
public class UserCreateProperty
{
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;

    public String getFirstName()
    {
        return new String(firstName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public String getLastName()
    {
        return new String(lastName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    public String getPatronymic()
    {
        return new String(patronymic.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

}
