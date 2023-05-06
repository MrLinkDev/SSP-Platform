package com.ssp.platform.validate;

import com.ssp.platform.entity.SupplyEntity;
import com.ssp.platform.entity.enums.SupplyStatus;
import com.ssp.platform.exceptions.SupplyValidationException;
import com.ssp.platform.repository.PurchaseRepository;
import com.ssp.platform.request.SupplyUpdateRequest;
import com.ssp.platform.response.ValidateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.ssp.platform.validate.ValidatorMessages.SupplyMessages.*;
import static com.ssp.platform.validate.ValidatorMessages.SupplyMessages.WRONG_SUPPLY_RESULT_BOUNDS_ERROR;

@Component
public class SupplyValidator extends Validator {
    public static final int ROLE_EMPLOYEE = 0;
    public static final int ROLE_FIRM = 1;

    private static final int MAX_SUPPLY_DESCRIPTION_SYMBOLS = 1000;
    private static final int MAX_SUPPLY_COMMENT_SYMBOLS = 1000;
    private static final int MAX_SUPPLY_RESULT_SYMBOLS = 1000;
    private static final long MAX_SUPPLY_BUDGET = 99999999L;

    private static final String PURCHASE_ID_FIELD_NAME = "purchaseId";
    private static final String DESCRIPTION_FIELD_NAME = "description";
    private static final String BUDGET_FIELD_NAME = "budget";
    private static final String COMMENT_FIELD_NAME = "comment";
    private static final String STATUS_FIELD_NAME = "status";
    private static final String RESULT_FIELD_NAME = "review_result";

    private final PurchaseRepository purchaseRepository;

    @Autowired
    public SupplyValidator(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    public void validateSupplyCreating(SupplyEntity supplyEntity) throws SupplyValidationException {
        validatePurchaseId(supplyEntity.getPurchase().getId());
        validateCreateDate(supplyEntity.getPurchase().getProposalDeadLine(), supplyEntity.getCreateDate());
        validateDescription(supplyEntity.getDescription());
        validateBudget(supplyEntity.getBudget());
        validateComment(supplyEntity.getComment());
    }

    public void validateSupplyUpdating(SupplyUpdateRequest updateRequest, SupplyEntity oldSupply, int role) throws SupplyValidationException {
        validateStatusForChanges(oldSupply.getStatus());
        validatePurchaseDeadLine(oldSupply.getPurchase().getProposalDeadLine());

        if (role == ROLE_FIRM){
            if (updateRequest.getStatus() != null && !oldSupply.getStatus().equals(updateRequest.getStatus())){
                throw new SupplyValidationException(new ValidateResponse(false, STATUS_FIELD_NAME, WRONG_ROLE_FOR_UPDATING));
            }

            if (updateRequest.getResult() != null && !updateRequest.getResult().isEmpty() && !oldSupply.getResult().equals(updateRequest.getResult())){
                throw new SupplyValidationException(new ValidateResponse(false, RESULT_FIELD_NAME, WRONG_ROLE_FOR_UPDATING));
            }

            if (updateRequest.getDescription() != null && !updateRequest.getDescription().isEmpty() && !oldSupply.getDescription().equals(updateRequest.getDescription())){
                validateDescription(updateRequest.getDescription());
            }

            if (updateRequest.getBudget() != null && !updateRequest.getBudget().equals(oldSupply.getBudget())){
                validateBudget(updateRequest.getBudget());
            }

            if (updateRequest.getComment() != null && !updateRequest.getComment().isEmpty() && !oldSupply.getComment().equals(updateRequest.getComment())){
                validateComment(updateRequest.getComment());
            }
        }

        if (role == ROLE_EMPLOYEE){
            if (updateRequest.getDescription() != null && !updateRequest.getDescription().isEmpty() && !oldSupply.getDescription().equals(updateRequest.getDescription())){
                throw new SupplyValidationException(new ValidateResponse(false, DESCRIPTION_FIELD_NAME, WRONG_ROLE_FOR_UPDATING));
            }

            if (updateRequest.getBudget() != null && !updateRequest.getBudget().equals(oldSupply.getBudget())){
                throw new SupplyValidationException(new ValidateResponse(false, BUDGET_FIELD_NAME, WRONG_ROLE_FOR_UPDATING));
            }

            if (updateRequest.getComment() != null && !updateRequest.getComment().isEmpty() && !oldSupply.getComment().equals(updateRequest.getComment())){
                throw new SupplyValidationException(new ValidateResponse(false, COMMENT_FIELD_NAME, WRONG_ROLE_FOR_UPDATING));
            }

            if (updateRequest.getStatus() != null && !oldSupply.getStatus().equals(updateRequest.getStatus())){
                validateStatus(updateRequest.getStatus());
            }

            if (updateRequest.getResult() != null && !updateRequest.getResult().isEmpty() && !oldSupply.getResult().equals(updateRequest.getResult())){
                validateResult(updateRequest.getResult());
            }
        }
    }

    private void validateStatusForChanges(SupplyStatus status) throws SupplyValidationException {
        if (status != SupplyStatus.UNDER_REVIEW){
            throw new SupplyValidationException(new ValidateResponse(false, TIME_IS_OVER));
        }
    }

    private void validatePurchaseDeadLine(long purchaseDeadLine) throws SupplyValidationException {
        if (System.currentTimeMillis() / 1000L > purchaseDeadLine) {
            throw new SupplyValidationException(new ValidateResponse(false, TIME_IS_OVER));
        }
    }

    private void validatePurchaseId(UUID id) throws SupplyValidationException {
        if (!purchaseRepository.existsById(id)){
            throw new SupplyValidationException(new ValidateResponse(false, PURCHASE_ID_FIELD_NAME, WRONG_PURCHASE_ID_ERROR));
        }
    }

    private void validateCreateDate(long purchaseDeadLine, long supplyCreateDate) throws SupplyValidationException {
        if (supplyCreateDate > purchaseDeadLine) {
            throw new SupplyValidationException(new ValidateResponse(false, SUPPLY_WRONG_DATE_ERROR));
        }
    }

    private void validateDescription(String description) throws SupplyValidationException {
        if (description == null) {
            throw new SupplyValidationException(new ValidateResponse(false, DESCRIPTION_FIELD_NAME, EMPTY_SUPPLY_DESCRIPTION_ERROR));
        }

        if (description.isEmpty()) {
            throw new SupplyValidationException(new ValidateResponse(false, DESCRIPTION_FIELD_NAME, EMPTY_SUPPLY_DESCRIPTION_ERROR));
        }

        if (onlySpaces(description)) {
            throw new SupplyValidationException(new ValidateResponse(false, DESCRIPTION_FIELD_NAME, ONLY_SPACES_ERROR));
        }

        if (description.length() > MAX_SUPPLY_DESCRIPTION_SYMBOLS) {
            throw new SupplyValidationException(new ValidateResponse(false, DESCRIPTION_FIELD_NAME, WRONG_SUPPLY_DESCRIPTION_BOUNDS_ERROR));
        }
    }

    private void validateBudget(Long budget) throws SupplyValidationException {
        if (budget == null) return;

        if (budget > MAX_SUPPLY_BUDGET) {
            throw new SupplyValidationException(new ValidateResponse(false, BUDGET_FIELD_NAME, WRONG_SUPPLY_BUDGET_BOUNDS_ERROR));
        }

        if (budget < 0L) {
            throw new SupplyValidationException(new ValidateResponse(false, BUDGET_FIELD_NAME, NEGATIVE_SUPPLY_BUDGET_ERROR));
        }
    }

    private void validateComment(String comment) throws SupplyValidationException {
        if (comment == null) return;
        if (comment.isEmpty()) return;

        if (onlySpaces(comment)) {
            throw new SupplyValidationException(new ValidateResponse(false, COMMENT_FIELD_NAME, ONLY_SPACES_ERROR));
        }

        if (comment.length() > MAX_SUPPLY_COMMENT_SYMBOLS) {
            throw new SupplyValidationException(new ValidateResponse(false, COMMENT_FIELD_NAME, WRONG_SUPPLY_COMMENT_BOUNDS_ERROR));
        }
    }

    private void validateStatus(SupplyStatus status) throws SupplyValidationException {
        if (status == null) {
            throw new SupplyValidationException(new ValidateResponse(false, STATUS_FIELD_NAME, EMPTY_SUPPLY_STATUS_ERROR));
        }
    }

    private void validateResult(String result) throws SupplyValidationException {
        if (result == null) {
            throw new SupplyValidationException(new ValidateResponse(false, RESULT_FIELD_NAME, EMPTY_SUPPLY_RESULT_ERROR));
        }

        if (result.isEmpty()) {
            throw new SupplyValidationException(new ValidateResponse(false, RESULT_FIELD_NAME, EMPTY_SUPPLY_RESULT_ERROR));
        }

        if (onlySpaces(result)) {
            throw new SupplyValidationException(new ValidateResponse(false, RESULT_FIELD_NAME, ONLY_SPACES_ERROR));
        }

        if (result.length() > MAX_SUPPLY_RESULT_SYMBOLS) {
            throw new SupplyValidationException(new ValidateResponse(false, RESULT_FIELD_NAME, WRONG_SUPPLY_RESULT_BOUNDS_ERROR));
        }
    }

}