package com.ssp.platform.request;

import com.ssp.platform.entity.enums.SupplyStatus;
import lombok.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SupplyUpdateRequest {

    private String description;

    private Long budget;

    private String comment;

    private SupplyStatus status;

    private String result;

    private MultipartFile[] files;

}
