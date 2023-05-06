package com.ssp.platform.response;

import com.ssp.platform.entity.FileEntity;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class FileResponse
{
    private boolean success;
    Resource resource;
    FileEntity file;

    public FileResponse(boolean success, Resource resource, FileEntity file)
    {
        this.success = success;
        this.resource = resource;
        this.file = file;
    }
}
