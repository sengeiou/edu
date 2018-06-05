package com.ubt.alpha1e.edu.onlineaudioplayer.model;

import com.ubt.alpha1e.edu.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.edu.data.model.BaseModel;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * @作者：ubt
 * @日期: 2018/4/11 13:48
 * @描述:
 */


public class CategoryContentInfo extends BaseModel {
    public CategoryContentInfo thiz;
    public String categoryId;
    public String categoryName;

    @Override
    public CategoryContentInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, CategoryContentInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<PlayContentInfo> getModelList(String json) {
        ArrayList<PlayContentInfo> result = new ArrayList<PlayContentInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new PlayContentInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<PlayContentInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(PlayContentInfo info)
    {
        try {
            return  GsonImpl.get().toJson(info);

        }catch (Exception e)
        {
            return  "";
        }
    }

    @Override
    public String toString() {
        return "CategoryContentInfo{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
