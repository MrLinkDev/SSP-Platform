package com.ssp.platform.validate;

import java.util.regex.*;

public abstract class Validator {

    public boolean isMatch(String field, String regex, int flags){
        Pattern fieldPattern = Pattern.compile(regex, flags);
        Matcher matcher = fieldPattern.matcher(field);

        return matcher.matches();
    }

    public boolean isMatch(String field, String regex){

        Pattern fieldPattern = Pattern.compile(regex);
        Matcher matcher = fieldPattern.matcher(field);

        return matcher.matches();
    }

    public boolean onlySpaces(String field){
        Pattern fieldPattern = Pattern.compile("[ ]*");
        Matcher matcher = fieldPattern.matcher(field);

        return matcher.matches();
    }
}
