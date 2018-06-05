package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class DownloadProgressInfo extends BaseModel {

    public int status = 0;        // 1 下载中 2 下载成功 3 未联网 0 下载失败
    public String progress = "";  // 下载进度
    public long  actionId = -1;   // 动作ID

    public DownloadProgressInfo thiz;

    @Override
    public DownloadProgressInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, DownloadProgressInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<DownloadProgressInfo> getModelList(String json) {
        ArrayList<DownloadProgressInfo> result = new ArrayList<DownloadProgressInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new DownloadProgressInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<DownloadProgressInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(DownloadProgressInfo info)
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
        return "DownloadProgressInfo{" +
                "status=" + status +
                ", progress=" + progress +
                ", actionId=" + actionId +
                '}';
    }
}
