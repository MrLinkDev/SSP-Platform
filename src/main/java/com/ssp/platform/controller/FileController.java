package com.ssp.platform.controller;

import com.ssp.platform.entity.User;
import com.ssp.platform.exceptions.FileServiceException;
import com.ssp.platform.logging.Log;
import com.ssp.platform.response.ApiResponse;
import com.ssp.platform.response.FileResponse;
import com.ssp.platform.security.service.UserDetailsServiceImpl;
import com.ssp.platform.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

/**
 * Контроллер для действий с файлами
 * @author Горбунов Александр
 */
@RestController
public class FileController {
    private final UserDetailsServiceImpl userDetailsService;
    private final FileService fileService;
    private final Log log;

    @Autowired
    public FileController(UserDetailsServiceImpl userDetailsService, FileService fileService, Log log){
        this.userDetailsService = userDetailsService;
        this.fileService = fileService;
        this.log = log;
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<?> getFile(@RequestHeader("Authorization") String token, @PathVariable("fileId") UUID id) throws IOException, FileServiceException {
        if (id == null) {
            return new ResponseEntity<>(new ApiResponse(false, "Параметр id не предоставлен"), HttpStatus.NOT_ACCEPTABLE);
        }

        FileResponse fileResponse = fileService.getFile(id);
        if(!fileResponse.isSuccess()) {
            return new ResponseEntity<>(new ApiResponse(false, "Файл не найден"), HttpStatus.NOT_FOUND);
        } else {
            Resource resource = fileResponse.getResource();
            if (!Files.exists(resource.getFile().toPath())){
                fileService.delete(id);
                throw new FileServiceException(new ApiResponse(false, "Файл удалён"));
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + fileResponse.getFile().getType() + "\"")
                    .body(resource);
        }
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<?> deleteFile(@RequestHeader("Authorization") String token, @PathVariable("fileId") UUID id) throws IOException, FileServiceException {
        User user = userDetailsService.loadUserByToken(token);
        fileService.delete(id);

        log.info(user, Log.CONTROLLER_FILE, "Удаление файла", id);

        return new ResponseEntity<>(new ApiResponse(true, "Файл удалён"), HttpStatus.OK);
    }
}
