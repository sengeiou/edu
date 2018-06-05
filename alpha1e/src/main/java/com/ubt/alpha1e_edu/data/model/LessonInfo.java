package com.ubt.alpha1e_edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e_edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class LessonInfo extends BaseModel {

    public long userId;
    public int courseId;
    public int isDeleted;
    public int lessonDifficulty;
    public int status;
    public String lessonGuide;
    public String lessonIcon;
    public int lessonId;
    public String lessonName;
    public int lessonOrder;
    public String lessonPic;
    public String lessonText;
    public int taskDown;
    public String taskMd5;
    public int taskTotal;
    public String taskUrl;

    public LessonInfo thiz;

    @Override
    public LessonInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, LessonInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<LessonInfo> getModelList(String json) {
        ArrayList<LessonInfo> result = new ArrayList<LessonInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                result.add(new LessonInfo().getThiz(j_list.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<LessonInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(LessonInfo info)
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
        return "LessonInfo{" +
                "lessonId=" + lessonId +
                ", userId=" + userId +
                ", courseId=" + courseId +
                ", lessonDifficulty=" + lessonDifficulty +
                ", lessonOrder=" + lessonOrder +
                ", lessonName=" + lessonName +
                ", lessonIcon=" + lessonIcon +
                ", lessonPic=" + lessonPic +
                ", lessonText=" + lessonText +
                ", lessonGuide=" + lessonGuide +
                ", status=" + status +
                ", isDeleted=" + isDeleted +
                ", taskTotal=" + taskTotal +
                ", taskDown=" + taskDown +
                ", taskUrl=" + taskUrl +
                ", taskMd5=" + taskMd5 +
                '}';
    }
}
