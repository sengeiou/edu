package com.ubt.alpha1e.data.model;

import android.util.Log;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Parcelable
public class NewActionInfo extends ActionInfo{

    public List<FrameActionInfo> frameActions;
    public String editerId = "";
    public String actionDir_local = "";//新建动作文件夹路径
    public String actionPath_local = "";//新建动作完整路径 actionDir_local/name.hts
    public String actionZip_local = "";
    public String actionPublishCover;

    private NewActionInfo newActionInfo;


    public NewActionInfo()
    {
        actionId = -1;
    }

    public NewActionInfo getThiz(String json_str) {
        try {
            String str = json_str;
            return getThiz(new JSONObject(json_str));
        } catch (JSONException e) {
            return null;
        }
    }

    public void addFrame(int[] datas) {
        Log.i("yuyong----------", "addFrame");
        FrameActionInfo f_info = new FrameActionInfo();
        f_info.setData(datas);
        if (frameActions == null)
            frameActions = new ArrayList<>();
        this.frameActions.add(f_info);
    }

    public NewActionInfo getThiz(JSONObject json) {

        try {
            JSONArray frame_list = json.getJSONArray("frameActions");
        } catch (JSONException e1) {
            try {
                JSONObject frame = json.getJSONObject("frameActions");
                JSONArray array = new JSONArray();
                array.put(frame);
                json.put("frameActions", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            newActionInfo = GsonImpl.get().toObject(json.toString(),NewActionInfo.class);
//            thiz = mMapper.readValue(json.toString(), NewActionInfo.class);
            return newActionInfo;
        } catch (Exception e) {
            newActionInfo = null;
            return null;
        }
    }

    public int getTitleTime() {
        if (frameActions == null || frameActions.size() == 0) {
            return 0;
        }
        int totle_time = 0;
        for (int i = 0; i < frameActions.size(); i++) {
            totle_time += frameActions.get(i).totle_time;
        }
        return totle_time;
    }

//    public static String getModelStr(NewActionInfo info) {
//
//        try {
//            return mMapper.writeValueAsString(info);
//        } catch (Exception e) {
//            String error = e.getMessage();
//            return "";
//        }
//    }

//    public static String getModeslStr(ArrayList<NewActionInfo> infos) {
//
//        try {
//            return mMapper.writeValueAsString(infos);
//        } catch (Exception e) {
//            String error = e.getMessage();
//            return "";
//        }
//    }

    public static String getString(ArrayList<NewActionInfo> infos)
    {
        try {
            return  GsonImpl.get().toJson(infos);

        }catch (Exception e)
        {
            return  "";
        }
    }
    public static String getString(NewActionInfo  infos)
    {
        try {
            return  GsonImpl.get().toJson(infos);

        }catch (Exception e)
        {
            return  "";
        }
    }


    public int getSize() {
        if (frameActions == null)
            return -1;
        return frameActions.size();
    }

    public FrameActionInfo getFrameIndex(int i) {
        return frameActions.get(i);
    }
}
