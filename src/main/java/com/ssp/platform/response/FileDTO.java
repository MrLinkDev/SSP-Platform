package com.ssp.platform.response;

import lombok.*;

@Data
public class FileDTO {

    @NonNull
    private String name;

    @NonNull
    private String mimeType;

    @NonNull
    private Long size;

    @NonNull
    private String hash;
}
