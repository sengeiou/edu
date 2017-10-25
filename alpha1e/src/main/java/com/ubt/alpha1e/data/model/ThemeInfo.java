package com.ubt.alpha1e.data.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ThemeInfo extends BaseModel {


    public String themeUrl = "";// 基础包地址
    public String themeExtendUrl = "";// 增量包地址
    public String themeVersion = "";// 主题版本
    public String themeSeq = "";// 主题id，id=-9999是本机主题
    public String themeSize = "";
    public String themeContext = "";
    public String themeImage = "";
    public String themeDetailImage = "";

    public String themeEffTime = "";// 生效时间（普通皮肤包无用）
    public String themeExpTime = "";// 失效时间（普通皮肤包无用）
    public String themeType = "";//皮肤类型：1节日类型，2普通类型

    public int downloadState = -1;//-1:没有下载;1:正在下载基础包;2:正在下载增量包;0:下载完毕

    private ThemeInfo thiz;

    @Override
    public ThemeInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, ThemeInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<ThemeInfo> getModelList(String json) {
        ArrayList<ThemeInfo> result = new ArrayList<ThemeInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new ThemeInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean checkOnLine() {
        if (themeUrl.toLowerCase().contains("http".toLowerCase())) {
            return true;
        }
        return false;
    }

    public static String getModeslStr(ArrayList<ThemeInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static ThemeInfo doClone(ThemeInfo _thiz) {
        String info = ThemeInfo.getModelStr(_thiz);
        return new ThemeInfo().getThiz(info);
    }
}
