package com.ubt.alpha1e.behaviorhabits.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.data.model.BaseModel;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
@Parcelable
public class EventPlayStatus extends BaseModel implements Serializable{

    public String eventId;
    public String playAudioSeq;
    public String audioState;

    public EventPlayStatus thiz;

    @Override
    public EventPlayStatus getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, EventPlayStatus.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<EventPlayStatus> getModelList(String json) {
        ArrayList<EventPlayStatus> result = new ArrayList<EventPlayStatus>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new EventPlayStatus().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<EventPlayStatus> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(EventPlayStatus info)
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
        return "EventPlayStatus{" +
                "eventId=" + eventId +
                ", playAudioSeq='" + playAudioSeq + '\'' +
                ", audioState='" + audioState + '\'' +
                '}';
    }
}
