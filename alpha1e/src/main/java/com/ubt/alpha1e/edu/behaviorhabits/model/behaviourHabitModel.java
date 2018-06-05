package com.ubt.alpha1e.edu.behaviorhabits.model;

import com.ubt.alpha1e.edu.data.model.BaseModel;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * @作者：ubt
 * @日期: 2018/3/6 19:53
 * @描述:
 */


public class behaviourHabitModel extends BaseModel {
    public String eventName;
    public String eventTime;
    behaviourHabitModel thiz;
    @Override
    public BaseModel getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, behaviourHabitModel.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }
    public static ArrayList<UserScore> getModelList(String json) {
        ArrayList<UserScore> result = new ArrayList<UserScore>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new UserScore().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<UserScore> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(UserScore  info)
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
        return "behaviourHabitModel{" +
                "eventTime=" + eventTime +
                ", eventName='" + eventName+
                '}';
    }
}
