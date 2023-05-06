package com.ssp.platform.validate;

import com.ssp.platform.request.UsersPageRequest;
import com.ssp.platform.response.ValidateResponse;
import lombok.Data;

@Data
public class UsersPageValidate
{
    private UsersPageRequest usersPageRequest;

    public UsersPageValidate(UsersPageRequest usersPageRequest)
    {
        this.usersPageRequest = usersPageRequest;
    }

    public ValidateResponse validateUsersPage()
    {
        Integer checkInt = usersPageRequest.getRequestPage();
        if (checkInt == null)
        {
            usersPageRequest.setRequestPage(0);
        }
        else if (checkInt < 0 || checkInt > 100000)
        {
            return new ValidateResponse(false, "requestPage", "Параметр requestPage может быть только 0-100000");
        }


        checkInt = usersPageRequest.getNumberOfElements();
        if (checkInt == null)
        {
            usersPageRequest.setNumberOfElements(10);
        }
        else if (checkInt < 1 || checkInt > 100)
        {
            usersPageRequest.setNumberOfElements(10);
        }

        String checkString = usersPageRequest.getType();
        if (checkString == null)
        {
            return new ValidateResponse(false, "type", "Параметр type не предоставлен");
        }
        if(!checkString.equals("firm") && !checkString.equals("employee"))
        {
            return new ValidateResponse(false, "type", "Параметр type может быть только firm|employee");
        }

        return new ValidateResponse(true, "", "ok");
    }
}