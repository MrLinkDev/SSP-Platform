package com.ssp.platform.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "file")
public class FileProperty {
    public String uploadDirectory;
    public long maxOneFileSize;
    private String[] restrictedTypes;
}
