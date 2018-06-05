package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;
import com.ubt.alpha1e.edu.utils.GsonImpl;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class LessonTaskInfo extends BaseModel {

    public long user_id;
    public String task_name;
    public String task_pic;
    public int task_status;//是否上线、下线
    public int status;//是否通过
    public int have_debris;
    public int task_duration;
    public int task_id;
    public String language;
    public int lesson_id;
    public String debris_name;
    public String task_text;
    public int task_order;
    public String debris_pic;
    public String task_guide;
    public String task_help;
    public String task_result;
    public String main_text;
    public String voice_file;
    public String motion_file;
    public int is_unlock;
    public int is_current_show;
    public String task_voice_en;
    public String task_voice;
    public String motion_name;

    public String bz1;
    public String bz2;

    public LessonTaskInfo thiz;

    @Override
    public LessonTaskInfo getThiz(String json) {

        try {
            thiz = mMapper.readValue(json, LessonTaskInfo.class);
            return thiz;
        } catch (Exception e) {
            thiz = null;
            return null;
        }
    }

    public static ArrayList<LessonTaskInfo> getModelList(String json) {
        ArrayList<LessonTaskInfo> result = new ArrayList<LessonTaskInfo>();
        try {
            JSONArray j_list = new JSONArray(json);
            for (int i = 0; i < j_list.length(); i++) {
                //result.add(new LessonTaskInfo().getThiz(j_list.get(i).toString()));
                result.add(GsonImpl.get().toObject(j_list.get(i).toString(),LessonTaskInfo.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getModeslStr(ArrayList<LessonTaskInfo> infos) {

        try {
            return mMapper.writeValueAsString(infos);
        } catch (Exception e) {
            String error = e.getMessage();
            return Convert_fail;
        }
    }

    public static String getString(LessonTaskInfo info)
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
        return "LessonTaskInfo{" +
                "task_name=" + task_name +
                ", user_id=" + user_id +
                ", task_pic=" + task_pic +
                ", task_status=" + task_status +
                ", status=" + status +
                ", have_debris=" + have_debris +
                ", task_id=" + task_id +
                ", task_duration=" + task_duration +
                ", lesson_id=" + lesson_id +
                ", language=" + language +
                ", debris_name=" + debris_name +
                ", task_text=" + task_text +
                ", task_order=" + task_order +
                ", debris_pic=" + debris_pic +
                ", task_guide=" + task_guide +
                ", task_help=" + task_help +
                ", task_result=" + task_result +
                ", main_text=" + main_text +
                ", voice_file=" + voice_file +
                ", motion_file=" + motion_file +
                ", task_voice_en=" + task_voice_en +
                ", task_voice=" + task_voice +
                ", motion_name=" + motion_name +
                ", is_current_show=" + is_current_show +
                ", is_unlock=" + is_unlock +
                '}';
    }
}
