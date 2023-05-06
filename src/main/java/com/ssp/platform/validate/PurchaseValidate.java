package com.ssp.platform.validate;

import com.ssp.platform.entity.Purchase;
import com.ssp.platform.entity.enums.PurchaseStatus;
import com.ssp.platform.response.ValidateResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Валидация при действиях с закупкой
 * @author Василий Воробьев
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PurchaseValidate extends Validator
{
    public static final long ONE_HUNDRED_YEARS = 3155760000L;
    public static final long ONE_HOUR = 3600L;
    public static final long THREE_THOUSAND_YEARS = 32503741200L;
    private Purchase purchase;
    private String checkResult = "ok";
    private boolean foundInvalid = false;

    public PurchaseValidate(Purchase purchase)
    {
        this.purchase = purchase;
        this.checkResult = "ok";
        this.foundInvalid = false;
    }

    /**
     * Валидация при создании закупки
     */
    public ValidateResponse validatePurchaseCreate()
    {
        validateName();
        if (foundInvalid) return new ValidateResponse(false, "name", checkResult);

        validateDescription();
        if (foundInvalid) return new ValidateResponse(false, "description", checkResult);

        validateProposalDeadLine();
        if (foundInvalid) return new ValidateResponse(false, "proposalDeadLine", checkResult);

        validateFinishDeadLine();
        if (foundInvalid) return new ValidateResponse(false, "finishDeadLine", checkResult);

        validateBudget();
        if (foundInvalid) return new ValidateResponse(false, "budget", checkResult);

        validateDemands();
        if (foundInvalid) return new ValidateResponse(false, "demands", checkResult);

        validateTeam();
        if (foundInvalid) return new ValidateResponse(false, "team", checkResult);

        validateWorkCondition();
        if (foundInvalid) return new ValidateResponse(false, "workCondition", checkResult);

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Валидация при изменении закупки
     */
    public ValidateResponse validatePurchaseEdit(Purchase newPurchase)
    {
        String newStringParam = newPurchase.getName();
        String oldStringParam = purchase.getName();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setName(newStringParam);
            validateName();
            if (foundInvalid) return new ValidateResponse(false, "name", checkResult);
        }

        newStringParam = newPurchase.getDescription();
        oldStringParam = purchase.getDescription();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setDescription(newStringParam);
            validateDescription();
            if (foundInvalid) return new ValidateResponse(false, "description", checkResult);
        }

        Long newLongParam = newPurchase.getProposalDeadLine();
        Long oldLongParam = purchase.getProposalDeadLine();
        if (newLongParam != null && !oldLongParam.equals(newLongParam))
        {
            purchase.setProposalDeadLine(newLongParam);
            validateProposalDeadLine();
            if (foundInvalid) return new ValidateResponse(false, "proposalDeadLine", checkResult);
        }

        newLongParam = newPurchase.getFinishDeadLine();
        if (newLongParam != null) { purchase.setFinishDeadLine(newLongParam); }
        //несмотря на то что этот параметр не изменился, все еще надо выполнить проверку, т.к. дата предложений могла изменится
        validateFinishDeadLine();
        if (foundInvalid) return new ValidateResponse(false, "finishDeadLine", checkResult);

        newLongParam = newPurchase.getBudget();
        oldLongParam = purchase.getBudget();
        if (newLongParam != null && !oldLongParam.equals(newLongParam))
        {
            purchase.setBudget(newLongParam);
            validateBudget();
            if (foundInvalid) return new ValidateResponse(false, "budget", checkResult);
        }

        newStringParam = newPurchase.getDemands();
        oldStringParam = purchase.getDemands();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setDemands(newStringParam);
            validateDemands();
            if (foundInvalid) return new ValidateResponse(false, "demands", checkResult);
        }

        newStringParam = newPurchase.getTeam();
        oldStringParam = purchase.getTeam();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setTeam(newStringParam);
            validateTeam();
            if (foundInvalid) return new ValidateResponse(false, "team", checkResult);
        }

        newStringParam = newPurchase.getWorkCondition();
        oldStringParam = purchase.getWorkCondition();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setWorkCondition(newStringParam);
            validateWorkCondition();
            if (foundInvalid) return new ValidateResponse(false, "workCondition", checkResult);
        }

        PurchaseStatus newStatus = newPurchase.getStatus();
        PurchaseStatus oldStatus = purchase.getStatus();
        if (newStatus != null && !newStatus.equals(oldStatus))
        {
            purchase.setStatus(newStatus);
            validateStatus();
            if (foundInvalid) return new ValidateResponse(false, "status", checkResult);
        }

        newStringParam = newPurchase.getCancelReason();
        oldStringParam = purchase.getCancelReason();
        if (newStringParam != null && !newStringParam.equals(oldStringParam))
        {
            purchase.setCancelReason(newStringParam);
            validateCancelReason();
            if (foundInvalid) return new ValidateResponse(false, "cancelReason", checkResult);
        }

        return new ValidateResponse(true, "", checkResult);
    }

    private void validateName()
    {
        String checkString = purchase.getName();
        if (checkString == null)
        {
            setCheckResult("Поле наименование закупки должно быть заполнено");
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 100)
        {
            setCheckResult("Наименование закупки должно содержать от 1 до 100 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Наименование закупки не может состоять из одних пробелов");
            return;
        }
    }

    private void validateDescription()
    {
        String checkString = purchase.getDescription();
        if (checkString == null)
        {
            setCheckResult("Поле описание закупки должно быть заполнено");
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 1000)
        {
            setCheckResult("Описание закупки должно содержать от 1 до 1000 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Описание закупки не может состоять из одних пробелов");
            return;
        }
    }

    private void validateProposalDeadLine()
    {
        Long proposalSec = purchase.getProposalDeadLine();
        if (proposalSec == null)
        {
            setCheckResult("Дата окончания срока подачи предложений должна быть заполнена");
            return;
        }

        long createSec = purchase.getCreateDate();
        if (proposalSec > createSec + ONE_HUNDRED_YEARS)
        {
            setCheckResult("Дата окончания срока подачи предложений " +
                    "должна быть не позже чем через 100 лет от даты создания предложения");
            return;
        }

        if (proposalSec < createSec + ONE_HOUR)
        {
            setCheckResult("Время окончания срока подачи предложений " +
                    "не может быть раньше чем через час после создания предложения");
            return;
        }
    }

    private void validateFinishDeadLine()
    {
        Long finishSec = purchase.getFinishDeadLine();

        if (finishSec == null)
        {
            purchase.setFinishDeadLine(THREE_THOUSAND_YEARS);
            return;
        }
        if (finishSec < purchase.getProposalDeadLine())
        {
            setCheckResult("Дата окончания выполнения работ " +
                    "не может быть перед датой окончания срока подачи предложений");
            return;
        }
    }

    private void validateBudget()
    {
        Long budget = purchase.getBudget();
        if (budget == null)
        {
            purchase.setBudget(0L);
            return;
        }

        if (budget < 0)
        {
            setCheckResult("Бюджет закупки не может быть отрицательный");
            return;
        }

        if (budget > 99999999)
        {
            setCheckResult("Бюджет закупки должен быть не более 8 цифр");
            return;
        }

    }


    private void validateDemands()
    {
        String checkString = purchase.getDemands();
        if (checkString == null)
        {
            purchase.setDemands("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 1000)
        {
            setCheckResult("Общие требования должны содержать не более 1000 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Общие требования не могут состоять из одних пробелов");
            return;
        }
    }

    private void validateTeam()
    {
        String checkString = purchase.getTeam();
        if (checkString == null)
        {
            purchase.setTeam("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 1000)
        {
            setCheckResult("Состав команды должен содержать не более 1000 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Состав команды не может состоять из одних пробелов");
            return;
        }
    }

    private void validateWorkCondition()
    {
        String checkString = purchase.getWorkCondition();
        if (checkString == null)
        {
            purchase.setWorkCondition("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 1000)
        {
            setCheckResult("Условия работы должны содержать не более 1000 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Условия работы не может состоять из одних пробелов");
            return;
        }
    }

    private void validateStatus()
    {
        if (purchase.getStatus() == PurchaseStatus.bidAccepting)
        {
            long nowSec = System.currentTimeMillis() / 1000;
            if (nowSec >= purchase.getProposalDeadLine())
            {
                setCheckResult("Срок приема предложений закончен, измените статус или срок");
                return;
            }
        }
        else if (purchase.getStatus() == PurchaseStatus.bidReview)
        {
            long nowSec = System.currentTimeMillis() / 1000;
            if (nowSec < purchase.getProposalDeadLine())
            {
                setCheckResult("Срок приема предложений еще не окончен, измените статус или срок");
                return;
            }
            else if (nowSec > purchase.getFinishDeadLine())
            {
                setCheckResult("Срок рассмотрения предложений закончен, измените статус или срок");
                return;
            }
        }
    }

    private void validateCancelReason()
    {
        String checkString = purchase.getCancelReason();
        if (checkString == null)
        {
            purchase.setCancelReason("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 1000)
        {
            setCheckResult("Причина отмены должна содержать не более 1000 символов");
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult("Причина отмены не может состоять из одних пробелов");
            return;
        }
    }

    private void setCheckResult(String result)
    {
        foundInvalid = true;
        checkResult = result;
    }

}
