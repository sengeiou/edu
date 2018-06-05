package com.ubt.alpha1e_edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class UpgradeProgressInfo extends BaseModel {

    public int status = 0;        //1 下载中 2 下载成功 0 下载失败
    public String progress = "";  //下载进度
    public String totalSize = ""; //文件总大小

    public UpgradeProgressInfo thiz;

    @Override
    public UpgradeProgressInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, UpgradeProgressInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<UpgradeProgressInfo> getModelList(String json) {
        ArrayList<UpgradeProgressInfo> result = new ArrayList<UpgradeProgressInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new UpgradeProgressInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<UpgradeProgressInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(UpgradeProgressInfo info)
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
        return "UpgradeProgressInfo{" +
                "status=" + status +
                ", progress=" + progress +
                ", totalSize=" + totalSize +
                '}';
    }
}
