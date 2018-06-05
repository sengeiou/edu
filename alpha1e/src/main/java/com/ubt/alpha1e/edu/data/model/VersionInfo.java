package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class VersionInfo extends BaseModel {

    public String upgradeType = "";
    public String module_id = "";
    public String remark = "";
    public String fromVersion = "";
    public String md5 = "";
    public String MD5Main = "";
    public String updateType = "";
    public String toVersion = "";
    public String urlMain = "";
    public String url = "";

    public VersionInfo thiz;

    @Override
    public VersionInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, VersionInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<VersionInfo> getModelList(String json) {
        ArrayList<VersionInfo> result = new ArrayList<VersionInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new VersionInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<VersionInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(VersionInfo info)
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
        return "VersionInfo{" +
                "upgradeType=" + upgradeType +
                ", module_id=" + module_id +
                ", fromVersion=" + fromVersion +
                ", toVersion=" + toVersion +
                ", md5=" + md5 +
                ", MD5Main='" + MD5Main + '\'' +
                ", updateType=" + updateType +
                ", url='" + url + '\'' +
                ", urlMain='" + urlMain + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
