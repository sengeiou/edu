package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/12.
 */
@Parcelable
public class RemoteRoleInfo extends BaseModel implements Serializable{

    public int roleid;
    public String roleName;
    public String roleIntroduction;
    public String roleIcon;
    public String bz;

    public RemoteRoleInfo thiz;

    @Override
    public RemoteRoleInfo getThiz(String json) {
        try {
            thiz = mMapper.readValue(json, RemoteRoleInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<RemoteRoleInfo> getModelList(String json) {
        ArrayList<RemoteRoleInfo> result = new ArrayList<RemoteRoleInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new RemoteRoleInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<RemoteRoleInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(RemoteRoleInfo  info)
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
        return "RemoteRoleInfo{" +
                "roleid=" + roleid +
                ", roleName='" + roleName + '\'' +
                ", roleIntroduction='" + roleIntroduction + '\'' +
                ", roleIcon=" + roleIcon +
                ", bz=" + bz +
                '}';
    }
}
