package com.ubt.alpha1e_edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class LessonTaskResultInfo extends BaseModel {

    public long userId;
    public int courseId;
    public int lessonId;
    public int taskId;
    public String taskName;
    public int qualityStar;
    public int efficiencyStar;
    public long addTime;
    public long updateTime;

    public LessonTaskResultInfo thiz;

    @Override
    public LessonTaskResultInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, LessonTaskResultInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<LessonTaskResultInfo> getModelList(String json) {
        ArrayList<LessonTaskResultInfo> result = new ArrayList<LessonTaskResultInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                //result.add(new LessonTaskInfo().getThiz(j_list.get(i).toString()));
                result.add(GsonImpl.get().toObject(j_list.get(i).toString(),LessonTaskResultInfo.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<LessonTaskResultInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(LessonTaskResultInfo info)
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
        return "LessonTaskResultInfo{" +
                "userId=" + userId +
                ", courseId=" + courseId +
                ", lessonId=" + lessonId +
                ", taskId=" + taskId +
                ", taskName=" + taskName +
                ", qualityStar=" + qualityStar +
                ", efficiencyStar=" + efficiencyStar +
                ", addTime=" + addTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
