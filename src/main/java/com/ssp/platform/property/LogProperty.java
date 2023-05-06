package com.ssp.platform.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "log")
public class LogProperty {
    private boolean enabled;
    private String directory;
}
