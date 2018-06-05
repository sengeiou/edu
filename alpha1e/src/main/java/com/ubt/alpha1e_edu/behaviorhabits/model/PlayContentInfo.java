package com.ubt.alpha1e_edu.behaviorhabits.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.data.model.BaseModel;
import com.ubt.alpha1e_edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
@Parcelable
public class PlayContentInfo extends BaseModel {

    public String contentId;
    public String contentName;
    public String contentUrl;
    public String isSelect;

    public PlayContentInfo thiz;

    @Override
    public PlayContentInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, PlayContentInfo.class);
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
        return "PlayContentInfo{" +
                "contentId=" + contentId +
                ", contentName='" + contentName + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                ", isSelect='" + isSelect + '\'' +
                '}';
    }
}
