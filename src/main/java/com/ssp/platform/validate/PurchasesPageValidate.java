package com.ssp.platform.validate;

import com.ssp.platform.request.PurchasesPageRequest;
import com.ssp.platform.response.ValidateResponse;
import com.ssp.platform.validate.ValidatorMessages.UserMessages;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PurchasesPageValidate extends Validator
{
    private PurchasesPageRequest purchasePageRequest;
    private String checkResult = "ok";
    private boolean foundInvalid = false;

    public PurchasesPageValidate(PurchasesPageRequest purchasePageRequest)
    {
        this.purchasePageRequest = purchasePageRequest;
        this.checkResult = "ok";
        this.foundInvalid = false;
    }

    public ValidateResponse validatePurchasePage()
    {
        validateNumberOfElements();
        validateRequestPage();
        if (foundInvalid) return new ValidateResponse(false, "requestPage", checkResult);
        validateFilterName();
        if (foundInvalid) return new ValidateResponse(false, "filterName", checkResult);

        return new ValidateResponse(true, "", checkResult);
    }

    private void validateRequestPage()
    {
        Integer checkInt = purchasePageRequest.getRequestPage();
        if (checkInt == null)
        {
            purchasePageRequest.setRequestPage(0);
        }
        else if (checkInt < 0 || checkInt > 100000)
        {
            setCheckResult("Параметр requestPage может быть только 0-100000");
        }
    }

    private void validateNumberOfElements()
    {
        Integer checkInt = purchasePageRequest.getNumberOfElements();
        if (checkInt == null)
        {
            purchasePageRequest.setNumberOfElements(10);
        }
        else if (checkInt < 1 || checkInt > 100)
        {
            purchasePageRequest.setNumberOfElements(10);
        }
    }

    private void validateFilterName()
    {
        String checkString = purchasePageRequest.getFilterName();

        if (checkString == null)
        {
            purchasePageRequest.setFilterName("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 100)
        {
            setCheckResult("Поиск по имени может быть не более 100 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            purchasePageRequest.setFilterName("");
            return;
        }
    }

    private void setCheckResult(String result)
    {
        foundInvalid = true;
        checkResult = result;
    }
}