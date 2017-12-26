package com.ubt.alpha1e.behaviorhabits.model;

import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/18.
 */

public class HabitsEvent extends BaseModel{

    public String eventId;
    public String eventName;
    public String eventTime;
    public String eventType;
    public String status;
    public String score;


    public HabitsEvent thiz;

    @Override
    public HabitsEvent getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, HabitsEvent.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<HabitsEvent> getModelList(String json) {
        ArrayList<HabitsEvent> result = new ArrayList<HabitsEvent>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new HabitsEvent().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<HabitsEvent> infos) {

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
        return "HabitsEvent{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", eventType='" + eventType + '\'' +
                ", status='" + status + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
