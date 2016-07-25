package com.ttm.tlrb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Helen on 2016/7/25.
 *
 */
public class VerifyUtil {
    /**
     * 验证邮箱
     * @param email 邮箱
     * @return true
     */
    public static boolean checkEmail(String email){
        boolean flag;
        try{
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机号码
     * @param mobileNumber 手机号
     * @return true
     */
    public static boolean checkMobileNumber(String mobileNumber){
        boolean flag;
        try{
            Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0-9]))\\d{8})");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        }catch(Exception e){
            flag = false;
        }
        return flag;
    }
}
