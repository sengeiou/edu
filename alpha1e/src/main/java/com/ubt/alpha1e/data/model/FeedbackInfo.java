package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
@Parcelable
public class FeedbackInfo extends BaseModel implements Serializable{

    public int feedbackId;
    public String feedbackName;
    public String feedbackIntroduction;
    public String bz;

    public FeedbackInfo thiz;

    @Override
    public FeedbackInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, FeedbackInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<FeedbackInfo> getModelList(String json) {
        ArrayList<FeedbackInfo> result = new ArrayList<FeedbackInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new FeedbackInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<FeedbackInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(FeedbackInfo info)
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
        return "FeedbackInfo{" +
                "feedbackId=" + feedbackId +
                ", feedbackName='" + feedbackName + '\'' +
                ", feedbackIntroduction='" + feedbackIntroduction + '\'' +
                ", bz=" + bz +
                '}';
    }
}
