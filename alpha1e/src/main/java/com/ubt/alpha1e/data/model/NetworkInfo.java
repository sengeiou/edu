package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class NetworkInfo extends BaseModel {

    public boolean status = false;        //1 已连接 0 未连接
    public String name = "";      //当前连接网络名称
    public String ip = "";      //当前连接网络名称

    public NetworkInfo thiz;

    @Override
    public NetworkInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, NetworkInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<NetworkInfo> getModelList(String json) {
        ArrayList<NetworkInfo> result = new ArrayList<NetworkInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new NetworkInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<NetworkInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(NetworkInfo info)
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
        return "NetworkInfo{" +
                "status=" + status +
                ", name=" + name +
                ", ip=" + ip +
                '}';
    }
}
