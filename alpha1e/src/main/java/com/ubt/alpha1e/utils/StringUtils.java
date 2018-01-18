package com.ubt.alpha1e.utils;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/18.
 */

public class StringUtils {

    /***
     * 判断字符串是否都是数字
     */
    public static boolean isStringNumber(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
