package com.ubt.alpha1e_edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class CourseInfo extends BaseModel {

    public int courseId;
    public int courseOrder;
    public String courseName;
    public String courseIcon;
    public String coursePic;
    public String courseText;
    public String remark;
    public int isDelete;
    public long createTime;
    public int createBy;
    public long updateTime;
    public int updateBy;

    public CourseInfo thiz;

    @Override
    public CourseInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, CourseInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<CourseInfo> getModelList(String json) {
        ArrayList<CourseInfo> result = new ArrayList<CourseInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new CourseInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<CourseInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(CourseInfo info)
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
        return "CourseInfo{" +
                "courseId=" + courseId +
                ", courseOrder=" + courseOrder +
                ", courseName=" + courseName +
                ", courseIcon=" + courseIcon +
                ", coursePic=" + coursePic +
                ", courseText=" + courseText +
                ", remark=" + remark +
                ", isDelete=" + isDelete +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                ", updateTime=" + updateTime +
                ", updateBy=" + updateBy +
                '}';
    }
}
