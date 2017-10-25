package com.ubt.alpha1e.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.data.model.LessonTaskInfo;

import java.util.ArrayList;
import java.util.List;

public class LessonTaskRecordOperater {
    private static LessonTaskRecordOperater thiz;
    private DBOperater mOperater;

    private LessonTaskRecordOperater() {

    }

    public static LessonTaskRecordOperater getInstance(Context context,
                                                       String path, String dbName) {
        if (thiz == null) {
            thiz = new LessonTaskRecordOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public void addRecord(LessonTaskInfo info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("blockly_lesson_task", null, values);
    }

    public void updateRecord(LessonTaskInfo taskInfo) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(taskInfo);
        db.update("blockly_lesson_task", values, "task_id=? and user_id =?  and language = ?", new String[]{taskInfo.task_id + "",taskInfo.user_id+"", taskInfo.language });
    }

    public void deleteRecord(long taskId) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        db.delete("blockly_lesson_task", "task_id=?", new String[]{taskId + ""});
    }

    public List<LessonTaskInfo> getRecoedByLessonId(int lessonId,long userId,String language) {

        List<LessonTaskInfo> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from blockly_lesson_task where lesson_id = "+ lessonId +" and user_id = "+ userId + " and language = '" + language + "' order by task_order ;";

        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                LessonTaskInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(LessonTaskInfo model) {
        ContentValues values = new ContentValues();
        values.put("user_id", model.user_id);
        values.put("task_name", model.task_name);
        values.put("task_pic", model.task_pic);
        values.put("task_status", model.task_status);
        values.put("status", model.status);
        values.put("have_debris", model.have_debris);
        values.put("task_duration", model.task_duration);
        values.put("task_id", model.task_id);
        values.put("language", model.language);
        values.put("lesson_id", model.lesson_id);
        values.put("debris_name", model.debris_name);
        values.put("task_text", model.task_text);
        values.put("task_order", model.task_order);
        values.put("debris_pic", model.debris_pic);
        values.put("task_guide", model.task_guide);
        values.put("task_help", model.task_help);
        values.put("task_result", model.task_result);
        values.put("main_text", model.main_text);
        values.put("voice_file", model.voice_file);
        values.put("motion_file", model.motion_file);
        values.put("task_voice_en", model.task_voice_en);
        values.put("task_voice", model.task_voice);
        values.put("motion_name", model.motion_name);
        values.put("is_unlock", model.is_unlock);
        values.put("is_current_show", model.is_current_show);

        return values;
    }

    private LessonTaskInfo ConvertToModel(Cursor cursor) {

        LessonTaskInfo model = new LessonTaskInfo();

        model.user_id = cursor.getLong(cursor.getColumnIndex("user_id"));
        model.task_name = cursor.getString(cursor.getColumnIndex("task_name"));
        model.task_pic = cursor.getString(cursor.getColumnIndex("task_pic"));
        model.task_status = cursor.getInt(cursor.getColumnIndex("task_status"));
        model.status = cursor.getInt(cursor.getColumnIndex("status"));
        model.have_debris = cursor.getInt(cursor.getColumnIndex("have_debris"));
        model.task_duration = cursor.getInt(cursor.getColumnIndex("task_duration"));
        model.task_id = cursor.getInt(cursor.getColumnIndex("task_id"));
        model.language = cursor.getString(cursor.getColumnIndex("language"));
        model.lesson_id = cursor.getInt(cursor.getColumnIndex("lesson_id"));
        model.debris_name = cursor.getString(cursor.getColumnIndex("debris_name"));
        model.task_text = cursor.getString(cursor.getColumnIndex("task_text"));
        model.task_order = cursor.getInt(cursor.getColumnIndex("task_order"));
        model.debris_pic = cursor.getString(cursor.getColumnIndex("debris_pic"));
        model.task_guide = cursor.getString(cursor.getColumnIndex("task_guide"));
        model.task_help = cursor.getString(cursor.getColumnIndex("task_help"));
        model.task_result = cursor.getString(cursor.getColumnIndex("task_result"));
        model.main_text = cursor.getString(cursor.getColumnIndex("main_text"));
        model.voice_file = cursor.getString(cursor.getColumnIndex("voice_file"));
        model.motion_file = cursor.getString(cursor.getColumnIndex("motion_file"));
        model.task_voice_en = cursor.getString(cursor.getColumnIndex("task_voice_en"));
        model.task_voice = cursor.getString(cursor.getColumnIndex("task_voice"));
        model.motion_name = cursor.getString(cursor.getColumnIndex("motion_name"));
        model.is_unlock = cursor.getInt(cursor.getColumnIndex("is_unlock"));
        model.is_current_show = cursor.getInt(cursor.getColumnIndex("is_current_show"));

        return model;
    }

}
