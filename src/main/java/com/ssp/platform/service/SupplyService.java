package com.ssp.platform.service;

import com.ssp.platform.exceptions.*;
import com.ssp.platform.request.SupplyUpdateRequest;
import com.ssp.platform.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public interface SupplyService {

    void create(UUID purchaseId, String description, User authorId, Long budget, String comment, MultipartFile[] files)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, FileValidationException, SupplyServiceException;

    void update(User user, UUID id, SupplyUpdateRequest updateRequest)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, SupplyServiceException, FileValidationException;

    void delete(User user, UUID id) throws IOException, FileServiceException, SupplyServiceException;

    SupplyEntity get(User user, UUID id) throws SupplyServiceException;

    List<SupplyEntity> getList(UUID purchaseId) throws SupplyServiceException;
}