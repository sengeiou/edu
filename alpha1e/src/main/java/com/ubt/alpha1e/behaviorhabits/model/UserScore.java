package com.ubt.alpha1e.behaviorhabits.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class UserScore<T> extends BaseModel {

    public String userId;
    public String totalScore;
    public String percent;
    public T details;

    public UserScore thiz;

    @Override
    public UserScore getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, UserScore.class);
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
        return "UserScore{" +
                "userId=" + userId +
                ", totalScore='" + totalScore + '\'' +
                ", percent='" + percent + '\'' +
                '}';
    }
}
