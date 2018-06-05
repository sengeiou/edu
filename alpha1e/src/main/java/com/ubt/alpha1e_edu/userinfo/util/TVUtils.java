package com.ubt.alpha1e_edu.userinfo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author：liuhai
 * @date：2017/11/8 10:24
 * @modifier：ubt
 * @modify_date：2017/11/8 10:24
 * [A brief description]
 * version
 */

public class TVUtils {
    /**
     * 是否正常字符串
     * @param str
     * @return
     */
    public static boolean isCorrectStr(String str) {
        return str.equals(stringFilter(str));
    }


    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
