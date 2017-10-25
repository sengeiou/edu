package com.ubt.alpha1e.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
@Parcelable
public class RemoteRoleActionInfo extends BaseModel{

    public int roleactionid;
    public int roleid;
    public String actionName;
    public String actionFileName;
    public String actionIcon;
    public String actionPath;
    public String actionId;
    public int actionType;
    public String bz;

    public RemoteRoleActionInfo thiz;

    @Override
    public RemoteRoleActionInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, RemoteRoleActionInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<RemoteRoleActionInfo> getModelList(String json) {
        ArrayList<RemoteRoleActionInfo> result = new ArrayList<RemoteRoleActionInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new RemoteRoleActionInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<RemoteRoleActionInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(RemoteRoleActionInfo info)
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
        return "RemoteRoleActionInfo{" +
                "roleactionid=" + roleactionid +"\'"+
                ", roleid=" + roleid +  '\'' +
                ", actionName='" + actionName + '\'' +
                ", actionFileName='" + actionFileName + '\'' +
                ", actionIcon='" + actionIcon + '\'' +
                ", actionId='" + actionId + '\'' +
                ", actionPath='" + actionPath + '\'' +
                ", actionType=" + actionType +
                ", bz=" + bz +
                '}';
    }
}
