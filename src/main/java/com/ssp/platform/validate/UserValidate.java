package com.ssp.platform.validate;

import com.ssp.platform.entity.User;
import com.ssp.platform.repository.UserRepository;
import com.ssp.platform.response.ValidateResponse;
import com.ssp.platform.validate.ValidatorMessages.UserMessages;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Валидация при действиях с пользователем
 * @author Изначальный автор Горбунов Александр, доработал Василий Воробьев
 */
//TODO:В будущем если будет время сделать так что бы название полей ValidateResponse совпадало с названием поля user
@Getter
@Component
public class UserValidate extends com.ssp.platform.validate.Validator
{
    private User user;
    private String checkResult = "ok";
    private boolean foundInvalid = false;

    private final UserRepository userRepository;

    private final int MIN_LOGIN_SIZE = 2;
    private final int MAX_LOGIN_SIZE = 30;


    public void UserValidateBegin(User user)
    {
        this.checkResult = "ok";
        this.foundInvalid = false;
        this.user = user;
    }

    @Autowired
    public UserValidate(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    /**
     * Валидация при создании пользователя сотрудника
     */
    public ValidateResponse validateEmployeeUser()
    {
        checkUsername();
        if(foundInvalid) return new ValidateResponse(false, "login", checkResult);

        checkPassword();
        if(foundInvalid) return new ValidateResponse(false, "password", checkResult);

        checkFirstName();
        if(foundInvalid) return new ValidateResponse(false, "firstName", checkResult);

        checkLastName();
        if(foundInvalid) return new ValidateResponse(false, "lastName", checkResult);

        checkPatronymic();
        if(foundInvalid) return new ValidateResponse(false, "patronymic", checkResult);

        user.setFirmName("");
        user.setDescription("");
        user.setAddress("");
        user.setActivity("");
        user.setTechnology("");
        user.setInn("");
        user.setTelephone("");
        user.setEmail("");
        user.setRole("employee");
        user.setStatus("Approved");

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Валидация при регистрации пользователя поставщика
     */
    public ValidateResponse validateFirmUser()
    {
        checkUsername();
        if(foundInvalid) return new ValidateResponse(false, "login", checkResult);

        checkPassword();
        if(foundInvalid) return new ValidateResponse(false, "password", checkResult);

        checkFirstName();
        if(foundInvalid) return new ValidateResponse(false, "firstName", checkResult);

        checkLastName();
        if(foundInvalid) return new ValidateResponse(false, "lastName", checkResult);

        checkPatronymic();
        if(foundInvalid) return new ValidateResponse(false, "patronymic", checkResult);

        checkFirmName();
        if(foundInvalid) return new ValidateResponse(false, "companyName", checkResult);

        checkDescription();
        if(foundInvalid) return new ValidateResponse(false, "companyDescription", checkResult);

        checkAddress();
        if(foundInvalid) return new ValidateResponse(false, "companyAddress", checkResult);

        checkActivity();
        if(foundInvalid) return new ValidateResponse(false, "companyKindOfActivity", checkResult);

        checkTechnology();
        if(foundInvalid) return new ValidateResponse(false, "companyTechnologyStack", checkResult);

        checkInn();
        if(foundInvalid) return new ValidateResponse(false, "TIN", checkResult);

        checkTelephone();
        if(foundInvalid) return new ValidateResponse(false, "phoneNumber", checkResult);

        checkEmail();
        if(foundInvalid) return new ValidateResponse(false, "email", checkResult);

        user.setRole("firm");
        user.setStatus("NotApproved");

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Валидация при авторизации
     */
    public ValidateResponse validateLogin()
    {
        validateUsernameNoUnique();
        if(foundInvalid) return new ValidateResponse(false, "login", checkResult);

        checkPassword();
        if(foundInvalid) return new ValidateResponse(false, "password", checkResult);

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Валидация при изменении пользователя поставщика
     */
    public ValidateResponse validateEditFirmUser(User newUser)
    {
        /*
          Валидация параметров только если они предоставлены и отличны от прошлых
          username, role изменить нельзя
         */

        String checkParameter = newUser.getPassword();
        String oldParameter = user.getPassword();
        /*
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setPassword(checkParameter);
            checkPassword();
            if(foundInvalid) return new ValidateResponse(false, "password", checkResult);
            //encodePassword
        }
        */

        checkParameter = newUser.getFirstName();
        oldParameter = user.getFirstName();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setFirstName(checkParameter);
            checkFirstName();
            if(foundInvalid) return new ValidateResponse(false, "firstName", checkResult);
        }

        checkParameter = newUser.getLastName();
        oldParameter = user.getLastName();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setLastName(checkParameter);
            checkLastName();
            if(foundInvalid) return new ValidateResponse(false, "lastName", checkResult);
        }

        checkParameter = newUser.getPatronymic();
        oldParameter = user.getPatronymic();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setPatronymic(checkParameter);
            checkPatronymic();
            if(foundInvalid) return new ValidateResponse(false, "patronymic", checkResult);
        }

        checkParameter = newUser.getFirmName();
        oldParameter = user.getFirmName();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setFirmName(checkParameter);
            checkFirmName();
            if(foundInvalid) return new ValidateResponse(false, "companyName", checkResult);
        }

        checkParameter = newUser.getDescription();
        oldParameter = user.getDescription();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setDescription(checkParameter);
            checkDescription();
            if(foundInvalid) return new ValidateResponse(false, "companyDescription", checkResult);
        }

        checkParameter = newUser.getAddress();
        oldParameter = user.getAddress();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setAddress(checkParameter);
            checkAddress();
            if(foundInvalid) return new ValidateResponse(false, "companyAddress", checkResult);
        }

        checkParameter = newUser.getActivity();
        oldParameter = user.getActivity();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setActivity(checkParameter);
            checkActivity();
            if(foundInvalid) return new ValidateResponse(false, "companyKindOfActivity", checkResult);
        }

        checkParameter = newUser.getTechnology();
        oldParameter = user.getTechnology();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setTechnology(checkParameter);
            checkTechnology();
            if(foundInvalid) return new ValidateResponse(false, "companyTechnologyStack", checkResult);
        }

        checkParameter = newUser.getInn();
        oldParameter = user.getInn();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setInn(checkParameter);
            checkInn();
            if(foundInvalid) return new ValidateResponse(false, "TIN", checkResult);
        }

        checkParameter = newUser.getTelephone();
        oldParameter = user.getTelephone();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setTelephone(checkParameter);
            checkTelephone();
            if(foundInvalid) return new ValidateResponse(false, "phoneNumber", checkResult);
        }

        checkParameter = newUser.getEmail();
        oldParameter = user.getEmail();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setEmail(checkParameter);
            checkEmail();
            if(foundInvalid) return new ValidateResponse(false, "email", checkResult);
        }

        checkParameter = newUser.getStatus();
        oldParameter = user.getStatus();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setStatus(checkParameter);
            checkStatus();
            if(foundInvalid) return new ValidateResponse(false, "status", checkResult);
        }

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Валидация при изменении пользователя сотрудника
     */
    public ValidateResponse validateEditEmployeeUser(User newUser)
    {
        /*
          Валидация параметров только если они предоставлены и отличны от прошлых
          username, role, status изменить нельзя
         */


        String checkParameter = newUser.getPassword();
        String oldParameter = user.getPassword();
        /*
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setPassword(checkParameter);
            checkPassword();
            if(foundInvalid) return new ValidateResponse(false, "password", checkResult);
            //encodePassword
        }
        */

        checkParameter = newUser.getFirstName();
        oldParameter = user.getFirstName();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setFirstName(checkParameter);
            checkFirstName();
            if(foundInvalid) return new ValidateResponse(false, "firstName", checkResult);
        }

        checkParameter = newUser.getLastName();
        oldParameter = user.getLastName();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setLastName(checkParameter);
            checkLastName();
            if(foundInvalid) return new ValidateResponse(false, "lastName", checkResult);
        }

        checkParameter = newUser.getPatronymic();
        oldParameter = user.getPatronymic();
        if(checkParameter != null && !checkParameter.equals(oldParameter))
        {
            user.setPatronymic(checkParameter);
            checkPatronymic();
            if(foundInvalid) return new ValidateResponse(false, "patronymic", checkResult);
        }

        return new ValidateResponse(true, "", checkResult);
    }

    /**
     * Приватный метод проверки username
     * Необходимо вынести так как будет использоваться при validateFirmUser() и при validateEmployeeUser()
     * Те при проверки регистрации нового поставщика и при создании сотрудника
     */
    private void checkUsername()
    {
        String checkString = user.getUsername();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_LOGIN_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < MIN_LOGIN_SIZE || checkLength > MAX_LOGIN_SIZE)
        {
            setCheckResult(UserMessages.WRONG_LOGIN_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "(?!\\d|[ ])\\w+", Pattern.CASE_INSENSITIVE))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_LOGIN_ERROR);
            return;
        }

        if (userRepository.existsByUsername(checkString))
        {
            setCheckResult(UserMessages.LOGIN_ALREADY_EXIST_ERROR);
            return;
        }
    }

    /**
     * Приватный метод проверки пароля
     */
    private void checkPassword()
    {
        String checkString = user.getPassword();

        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_PASSWORD_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 8 || checkLength > 20)
        {
            setCheckResult(UserMessages.WRONG_PASSWORD_SIZE_ERROR);
            return;
        }

        char currentCharacter;
        boolean numberPresent = false;
        boolean upperCasePresent = false;
        boolean lowerCasePresent = false;
        boolean specialCharacterPresent = false;

        String specialCharactersString = "!@#$%&*()'+,-.\\/:;<=>?[]^_`{|}";

        for (int i = 0; i < checkLength; i++)
        {
            currentCharacter = checkString.charAt(i);
            if (Character.isDigit(currentCharacter))
            {
                numberPresent = true;
            }
            else if (Character.isUpperCase(currentCharacter))
            {
                upperCasePresent = true;
            }
            else if (Character.isLowerCase(currentCharacter))
            {
                lowerCasePresent = true;
            }
            else if (specialCharactersString.contains(Character.toString(currentCharacter)))
            {
                specialCharacterPresent = true;
            }
        }

        if (!((numberPresent || specialCharacterPresent) && upperCasePresent && lowerCasePresent))
        {
            setCheckResult(UserMessages.WRONG_PASSWORD_SYMBOLS_ERROR);
            return;
        }

        if (!isMatch(checkString, "[A-Za-z0-9^!@#$%&\\*()'\\+,\\-\\./:;<=\\>\\?\\[\\]^_`{\\|}\\\\]*"))
        {
            setCheckResult(UserMessages.WRONG_PASSWORD_SYMBOLS_REGEX);
            return;
        }
    }

    private void checkFirstName()
    {
        String checkString = user.getFirstName();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_FIRST_NAME_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 30)
        {
            setCheckResult(UserMessages.WRONG_FIRST_NAME_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "[А-ЯёЁа-яA-Za-z]+([ -]?[А-ЯёЁа-яA-Za-z]+)?"))
        {
            setCheckResult(UserMessages.WRONG_FIRST_NAME_SYMBOLS_ERROR);
            return;
        }
    }

    private void checkLastName()
    {
        String checkString = user.getLastName();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_LAST_NAME_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 30)
        {
            setCheckResult(UserMessages.WRONG_LAST_NAME_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "[А-ЯёЁа-яA-Za-z]+([ -]?[А-ЯёЁа-яA-Za-z]+)?"))
        {
            setCheckResult(UserMessages.WRONG_LAST_NAME_SYMBOLS_ERROR);
            return;
        }
    }

    private void checkPatronymic()
    {
        String checkString = user.getPatronymic();
        if (checkString == null)
        {
            user.setPatronymic("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 30)
        {
            setCheckResult(UserMessages.WRONG_PATRONYMIC_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "[А-ЯёЁа-яA-Za-z]+([ -]?[А-ЯёЁа-яA-Za-z]+)?"))
        {
            setCheckResult(UserMessages.WRONG_PATRONYMIC_SYMBOLS_ERROR);
            return;
        }
    }

    private void checkFirmName()
    {
        String checkString = user.getFirmName();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_COMPANY_NAME_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 30)
        {
            setCheckResult(UserMessages.WRONG_COMPANY_NAME_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

        if (checkString.charAt(checkLength - 1) == ' ' && checkString.charAt(0) == ' ')
        {
            setCheckResult(UserMessages.WRONG_COMPANY_NAME_SYMBOLS_ERROR);
            return;
        }

    }

    private void checkDescription()
    {
        String checkString = user.getDescription();
        if (checkString == null)
        {
            user.setDescription("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 1000)
        {
            setCheckResult(UserMessages.WRONG_COMPANY_DESCRIPTION_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

    }

    private void checkAddress()
    {
        String checkString = user.getAddress();
        if (checkString == null)
        {
            user.setAddress("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 50)
        {
            setCheckResult(UserMessages.WRONG_COMPANY_ADDRESS_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

        if (!isMatch(checkString, "[A-Za-zа-яёЁA-Я0-9 .,!@#№$;%:^?&*()_/\\-+={}]+", Pattern.CASE_INSENSITIVE))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_ADDRESS_ERROR);
            return;
        }

        if (checkString.charAt(checkLength - 1) == ' ' && checkString.charAt(0) == ' ')
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_ADDRESS_ERROR);
            return;
        }
    }

    private void checkActivity()
    {
        String checkString = user.getActivity();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_COMPANY_KIND_OF_ACTIVITY_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 1 || checkLength > 100)
        {
            setCheckResult(UserMessages.WRONG_COMPANY_KIND_OF_ACTIVITY_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

    }

    private void checkTechnology()
    {
        String checkString = user.getTechnology();
        if (checkString == null)
        {
            user.setTechnology("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength > 100)
        {
            setCheckResult(UserMessages.WRONG_COMPANY_TECHNOLOGY_STACK_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

    }

    private void checkInn()
    {
        String checkString = user.getInn();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_TIN_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 9 || checkLength > 12)
        {
            setCheckResult(UserMessages.WRONG_TIN_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

        if (!isMatch(checkString, "[A-Z0-9]*"))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_TIN_ERROR);
            return;
        }

        if (!isMatch(checkString, "[A-Z0-9]*[0-9]+[A-Z0-9]*"))
        {
            setCheckResult(UserMessages.NO_NUMBER_IN_TIN_ERROR);
            return;
        }

        if (userRepository.existsByInn(checkString))
        {
            setCheckResult(UserMessages.TIN_ALREADY_EXIST_ERROR);
            return;
        }
    }

    private void checkTelephone()
    {
        String checkString = user.getTelephone();
        if (checkString == null)
        {
            user.setTelephone("");
            return;
        }

        int checkLength = checkString.length();

        if (checkLength == 0) return;

        if (checkLength < 11 || checkLength > 17)
        {
            setCheckResult(UserMessages.WRONG_PHONE_NUMBER_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "[0-9]*"))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_PHONE_NUMBER_ERROR);
            return;
        }

        if (userRepository.existsByTelephone(checkString))
        {
            setCheckResult(UserMessages.PHONE_NUMBER_ALREADY_EXIST_ERROR);
            return;
        }

    }

    private void checkEmail()
    {
        String checkString = user.getEmail();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_EMAIL_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < 7 || checkLength > 192)
        {
            setCheckResult(UserMessages.WRONG_EMAIL_SIZE_ERROR);
            return;
        }

        if (onlySpaces(checkString))
        {
            setCheckResult(UserMessages.ONLY_SPACES_ERROR);
            return;
        }

        if (!isMatch(checkString, ".*\\@.+\\..+"))
        {
            setCheckResult(UserMessages.WRONG_EMAIL_MASK_TYPE_ERROR);
            return;
        }

        if (!isMatch(checkString, ".{1,64}\\@.*"))
        {
            setCheckResult(UserMessages.WRONG_EMAIL_MASK_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, ".{1,64}\\@.{2,63}\\..*"))
        {
            setCheckResult(UserMessages.WRONG_EMAIL_MASK_SIZE_ERROR2);
            return;
        }

        if (!isMatch(checkString, ".{1,64}\\@.{2,63}\\..{2,63}"))
        {
            setCheckResult(UserMessages.WRONG_EMAIL_MASK_SIZE_ERROR3);
            return;
        }

        if (!isMatch(checkString, "[a-zA-Z0-9.^!@#$%&~_‘`/=\\?\\{\\}\\|\\-\\+\\*]+\\@[a-z0-9._\\-]+\\.[a-z0-9._\\-]+", Pattern.CASE_INSENSITIVE))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_EMAIL_ERROR);
            return;
        }

        if (userRepository.existsByEmail(checkString))
        {
            setCheckResult(UserMessages.EMAIL_ALREADY_EXIST_ERROR);
            return;
        }

    }

    private void checkStatus()
    {
        String checkString = user.getStatus();
        if (!checkString.equals("NotApproved") && !checkString.equals("Approved"))
        {
            setCheckResult(UserMessages.WRONG_USER_STATUS_ERROR);
            return;
        }

    }

    /**
     * Необходим для валидцаии при авторизации, убрана проверка на уникальность
     */
    private void validateUsernameNoUnique()
    {
        String checkString = user.getUsername();
        if (checkString == null)
        {
            setCheckResult(UserMessages.EMPTY_LOGIN_FIELD_ERROR);
            return;
        }

        int checkLength = checkString.length();
        if (checkLength < MIN_LOGIN_SIZE || checkLength > MAX_LOGIN_SIZE)
        {
            setCheckResult(UserMessages.WRONG_LOGIN_SIZE_ERROR);
            return;
        }

        if (!isMatch(checkString, "(?!\\d|[ ])\\w+", Pattern.CASE_INSENSITIVE))
        {
            setCheckResult(UserMessages.WRONG_SYMBOLS_IN_LOGIN_ERROR);
            return;
        }

        return;
    }

    /**
     * Необходим для валидцаии при авторизации, убрана проверка на уникальность
     */
    public ValidateResponse validateUsernameLogin(String checkString)
    {
        if (checkString == null)
        {
            return new ValidateResponse(false, "login", UserMessages.EMPTY_LOGIN_FIELD_ERROR);
        }

        int checkLength = checkString.length();
        if (checkLength < MIN_LOGIN_SIZE || checkLength > MAX_LOGIN_SIZE)
        {
            return new ValidateResponse(false, "login", UserMessages.WRONG_LOGIN_SIZE_ERROR);
        }

        if (!isMatch(checkString, "(?!\\d|[ ])\\w+", Pattern.CASE_INSENSITIVE))
        {
            return new ValidateResponse(false, "login", UserMessages.WRONG_SYMBOLS_IN_LOGIN_ERROR);
        }

        return new ValidateResponse(true, "", checkResult);
    }

    private void setCheckResult(String result)
    {
        foundInvalid = true;
        checkResult = result;
    }
}