package com.ssp.platform.request;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
public class QuestionUpdateRequest {

    UUID id;

    String name;

    String description;

    String publicity;

}
