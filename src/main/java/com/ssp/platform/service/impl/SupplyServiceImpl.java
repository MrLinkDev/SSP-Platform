package com.ssp.platform.service.impl;

import com.ssp.platform.email.EmailSender;
import com.ssp.platform.entity.*;
import com.ssp.platform.exceptions.*;
import com.ssp.platform.repository.*;
import com.ssp.platform.request.SupplyUpdateRequest;
import com.ssp.platform.response.*;
import com.ssp.platform.service.*;
import com.ssp.platform.telegram.SSPPlatformBot;
import com.ssp.platform.validate.*;
import com.ssp.platform.validate.ValidatorMessages.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.ssp.platform.validate.ValidatorMessages.SupplyMessages.*;

/**
 * Сервис для работы с предложениями к закупке
 * @author Горбунов Александр
 */
@Service
public class SupplyServiceImpl implements SupplyService {

    public static final long DATE_DIVIDER = 1000L;
    public static final int MAX_FILES = 20;

    private final SupplyRepository supplyRepository;
    private final PurchaseRepository purchaseRepository;
    private final FileServiceImpl fileService;
    private final SupplyValidator supplyValidator;
    private final EmailSender emailSender;

    private final SSPPlatformBot sspPlatformBot;

    @Autowired
    public SupplyServiceImpl(
            SupplyRepository supplyRepository, FileServiceImpl fileService, SupplyValidator supplyValidator, FileValidator fileValidator,
            PurchaseRepository purchaseRepository, EmailSender emailSender, SSPPlatformBot sspPlatformBot
    ) {
        this.supplyRepository = supplyRepository;
        this.fileService = fileService;
        this.supplyValidator = supplyValidator;
        this.purchaseRepository = purchaseRepository;
        this.emailSender = emailSender;
        this.sspPlatformBot = sspPlatformBot;
    }

    @Override
    public void create(UUID purchaseId, String description, User author, Long budget, String comment, MultipartFile[] files)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, FileValidationException, SupplyServiceException {

        if (supplyRepository.existsByAuthorAndPurchaseId(author, purchaseId)) {
            throw new SupplyServiceException(new ApiResponse(false, SupplyMessages.SUPPLY_ALREADY_EXIST_BY_USER_ERROR));
        }

        SupplyEntity supplyEntity = new SupplyEntity(purchaseRepository.getOne(purchaseId), description, author, budget, comment);

        supplyValidator.validateSupplyCreating(supplyEntity);
        fileService.validateFiles(files);

        supplyRepository.save(supplyEntity);
        fileService.addFiles(files, supplyEntity.getId(), FileServiceImpl.LOCATION_SUPPLY);
    }

    @Override
    public void update(User user, UUID id, SupplyUpdateRequest updateRequest)
            throws IOException, NoSuchAlgorithmException, SupplyValidationException, SupplyServiceException, FileValidationException {
        SupplyEntity supplyEntity = supplyRepository.getOne(id);
        boolean statusChanged = false;

        switch (user.getRole()){
            case "firm":
                if (user.equals(supplyEntity.getAuthor())){
                    supplyValidator.validateSupplyUpdating(updateRequest, supplyEntity, SupplyValidator.ROLE_FIRM);
                }

                if (updateRequest.getDescription() != null && !updateRequest.getDescription().isEmpty()){
                    supplyEntity.setDescription(updateRequest.getDescription());
                }

                if (updateRequest.getBudget() != null){
                    supplyEntity.setBudget(updateRequest.getBudget());
                }

                if (updateRequest.getComment() != null && !updateRequest.getComment().isEmpty()){
                    supplyEntity.setComment(updateRequest.getComment());
                }

                if (updateRequest.getFiles() != null && updateRequest.getFiles().length > MAX_FILES){
                    throw new FileValidationException(new ValidateResponse(false, "files", FileMessages.TOO_MUCH_FILES));
                }

                if (updateRequest.getFiles() != null && updateRequest.getFiles().length > 0){
                    if (supplyEntity.getFiles().size() + updateRequest.getFiles().length > MAX_FILES){
                        throw new FileValidationException(new ValidateResponse(false, "files", FileMessages.TOO_MUCH_FILES));
                    }

                    fileService.addFiles(updateRequest.getFiles(), supplyEntity.getId(), FileServiceImpl.LOCATION_SUPPLY);
                }

                break;

            case "employee":
                if (updateRequest.getFiles() != null && updateRequest.getFiles().length > 0){
                    throw new SupplyValidationException(new ValidateResponse(false, "files", WRONG_ROLE_FOR_UPDATING));
                }

                supplyValidator.validateSupplyUpdating(updateRequest, supplyEntity, SupplyValidator.ROLE_EMPLOYEE);

                if (updateRequest.getStatus() != null){
                    if(!updateRequest.getStatus().equals(supplyEntity.getStatus())) statusChanged = true;
                    supplyEntity.setStatus(updateRequest.getStatus());
                }

                if (updateRequest.getResult() != null && !updateRequest.getResult().isEmpty()){
                    supplyEntity.setResult(updateRequest.getResult());
                }

                supplyEntity.setResultDate(System.currentTimeMillis() / DATE_DIVIDER);
                break;
        }
        if(statusChanged) {
            emailSender.sendMailSupplyEdit(supplyEntity.getPurchase(), supplyEntity, supplyEntity.getAuthor());
            sspPlatformBot.notifyAboutSupplyChange(supplyEntity.getAuthor(), supplyEntity.getPurchase().getName(), supplyEntity);
        }
        supplyRepository.saveAndFlush(supplyEntity);
    }

    @Override
    public void delete(User user, UUID id) throws IOException, FileServiceException, SupplyServiceException {
        if (!supplyRepository.existsById(id)) throw new SupplyServiceException(new ApiResponse(false, WRONG_SUPPLY_ID_ERROR));
        SupplyEntity supplyEntity = supplyRepository.getOne(id);

        switch (user.getRole()) {
            case "firm":
                if (user.equals(supplyEntity.getAuthor())){
                    delete(supplyEntity);
                    break;
                }
                
                throw new SupplyServiceException(new ApiResponse(false, WRONG_ROLE_FOR_DELETING));

            case "employee":
                delete(supplyEntity);
                break;
            default:
                throw new SupplyServiceException(new ApiResponse(false, WRONG_ROLE_FOR_DELETING));
        }
    }
    
    private void delete(SupplyEntity supplyEntity) throws IOException, FileServiceException {
        List<FileEntity> files = supplyEntity.getFiles();
        
        for (FileEntity file : files) fileService.delete(file.getId());
        supplyRepository.delete(supplyEntity);
    }

    @Override
    public SupplyEntity get(User user, UUID id) throws SupplyServiceException {
        if (!supplyRepository.existsById(id)) throw new SupplyServiceException(new ApiResponse(false, WRONG_SUPPLY_ID_ERROR));
        SupplyEntity supplyEntity = supplyRepository.getOne(id);

        switch (user.getRole()){
            case "firm":
                if (user.equals(supplyEntity.getAuthor())){
                    return supplyEntity;
                }
                break;

            case "employee":
                return supplyEntity;
        }

        throw new SupplyServiceException(new ApiResponse(false, WRONG_ROLE_FOR_UPDATING));
    }

    @Override
    public List<SupplyEntity> getList(UUID purchaseId) throws SupplyServiceException {
        if (!purchaseRepository.existsById(purchaseId)) throw new SupplyServiceException(new ApiResponse(false, WRONG_PURCHASE_ID_ERROR));
        List<SupplyEntity> list = supplyRepository.findAllByPurchase(purchaseRepository.getOne(purchaseId));
        list.sort((o1, o2) -> {
            if (o2.getStatus() == o1.getStatus()) {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            }
            return o1.getStatus().compareTo(o2.getStatus());
        });
        return list;
    }
}
