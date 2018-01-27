package com.ubt.alpha1e.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/1/18.
 */

public class StringUtils {

    /***
     * 判断字符串是否都是数字
     */
    public static boolean isStringNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 年龄int转为 String
     *
     * @param type
     * @return
     */
    public static String getAgeStringBytype(String type) {
        String result = "";
        if (type.equals("5")) {
            result = "5岁及以下";
        } else if (type.equals("6")) {
            result = "6岁";
        } else if (type.equals("7")) {
            result = "7岁";
        } else if (type.equals("8")) {
            result = "8岁";
        } else if (type.equals("9")) {
            result = "9岁";
        } else if (type.equals("10")) {
            result = "10岁及以上";
        }

        return result;
    }

    /**
     * string转换为type
     *
     * @param str
     * @return
     */
    public static String getAgeByType(String str) {
        String result = "";
        if (str.equals("5岁及以下")) {
            result = "5";
        } else if (str.equals("6岁")) {
            result = "6";
        } else if (str.equals("7岁")) {
            result = "7";
        } else if (str.equals("8岁")) {
            result = "8";
        } else if (str.equals("9岁")) {
            result = "9";
        } else if (str.equals("10岁及以上")) {
            result = "10";
        }

        return result;
    }

    public static List<String> getAgeList(List<String> ageList){
        List<String> list = new ArrayList<>();
        for (int i=0;i<ageList.size();i++){
            if (ageList.get(i).equals("5")){
                list.add("5岁及以下");
            }else if (ageList.get(i).equals("6")){
                list.add("6岁");
            }else if (ageList.get(i).equals("7")){
                list.add("7岁");
            }else if (ageList.get(i).equals("8")){
                list.add("8岁");
            }else if (ageList.get(i).equals("9")){
                list.add("9岁");
            }else if (ageList.get(i).equals("10")){
                list.add("10岁及以上");
            }
        }
        return list;
    }

    public static List<String> getGradeList(List<String> ageList){
        List<String> list = new ArrayList<>();
        for (int i=0;i<ageList.size();i++){
            if (ageList.get(i).equals("0")){
                list.add("幼儿园大班");
            }else if (ageList.get(i).equals("1")){
                list.add("小学一年级");
            }else if (ageList.get(i).equals("2")){
                list.add("小学二年级");
            }else if (ageList.get(i).equals("3")){
                list.add("小学三年级");
            }else if (ageList.get(i).equals("4")){
                list.add("小学四年级");
            }else if (ageList.get(i).equals("5")){
                list.add("小学五年级以上");
            }
        }
        return list;
    }



    /**
     * 年龄int转为 String
     *
     * @param type
     * @return
     */
    public static String getGradeStringBytype(String type) {
        String result = "";
        if (type.equals("0")) {
            result = "幼儿园大班";
        } else if (type.equals("1")) {
            result = "小学一年级";
        } else if (type.equals("2")) {
            result = "小学二年级";
        } else if (type.equals("3")) {
            result = "小学三年级";
        } else if (type.equals("4")) {
            result = "小学四年级";
        } else if (type.equals("5")) {
            result = "小学五年级以上";
        }

        return result;
    }

    /**
     * string转换为type
     *
     * @param str
     * @return
     */
    public static String getGradeByType(String str) {
        String result = "";
        if (str.equals("幼儿园大班")) {
            result = "0";
        } else if (str.equals("小学一年级")) {
            result = "1";
        } else if (str.equals("小学二年级")) {
            result = "2";
        } else if (str.equals("小学三年级")) {
            result = "3";
        } else if (str.equals("小学四年级")) {
            result = "4";
        } else if (str.equals("小学五年级以上")) {
            result = "5";
        }

        return result;
    }
}
