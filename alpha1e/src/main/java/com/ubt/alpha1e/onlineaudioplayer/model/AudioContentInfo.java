package com.ubt.alpha1e.onlineaudioplayer.model;

import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @作者：ubt
 * @日期: 2018/4/11 14:29
 * @描述:
 */


public class AudioContentInfo extends BaseModel implements Serializable {
    public AudioContentInfo thiz;
    public String contentName;
    public String contentUrl;
    public Boolean isPlaying=false;
    public int index=-1;

    @Override
    public AudioContentInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, AudioContentInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<AudioContentInfo> getModelList(String json) {
        ArrayList<AudioContentInfo> result = new ArrayList<AudioContentInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new AudioContentInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<AudioContentInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(AudioContentInfo info)
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
        return "AudioContentInfo{" +
                "contentName=" + contentName +
                ", contentUrl='" + contentUrl + '\'' +
                ", isSelect='" + isPlaying + '\'' +
                ", index='" + index+ '\'' +
                '}';
    }
}
