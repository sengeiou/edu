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
public class HabitsEventInfo extends BaseModel implements Serializable{

    public String eventId;
    public String eventName;
    public String eventTime;
    public String eventSwitch;
    public String eventType;
    public String eventIconSelect;
    public String eventIconUnSelect;

    public HabitsEventInfo thiz;

    @Override
    public HabitsEventInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, HabitsEventInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<HabitsEventInfo> getModelList(String json) {
        ArrayList<HabitsEventInfo> result = new ArrayList<HabitsEventInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new HabitsEventInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<HabitsEventInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(HabitsEventInfo info)
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
        return "HabitsEventInfo{" +
                "eventId=" + eventId +
                ", eventName='" + eventName + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", eventSwitch=" + eventSwitch + '\'' +
                ", eventType=" + eventType + '\'' +
                ", eventIconSelect=" + eventIconSelect + '\'' +
                ", eventIconUnSelect=" + eventIconUnSelect + '\'' +
                '}';
    }
}
