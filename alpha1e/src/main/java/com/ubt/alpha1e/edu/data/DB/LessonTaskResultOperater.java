package com.ubt.alpha1e.edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.edu.data.model.LessonTaskResultInfo;

import java.util.ArrayList;
import java.util.List;

public class LessonTaskResultOperater {
    private static LessonTaskResultOperater thiz;
    private DBOperater mOperater;

    private LessonTaskResultOperater() {

    }

    public static LessonTaskResultOperater getInstance(Context context,
                                                       String path, String dbName) {
        if (thiz == null) {
            thiz = new LessonTaskResultOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public void addRecord(LessonTaskResultInfo info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("blockly_lesson_task_result", null, values);
    }

    public void updateRecord(LessonTaskResultInfo taskInfo) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(taskInfo);
        db.update("blockly_lesson_task_result", values, "task_id=? and user_id = ? ", new String[]{taskInfo.taskId + "",taskInfo.userId + ""});
    }

    public void deleteRecord(int taskId) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        db.delete("blockly_lesson_task_result", "task_id=? ", new String[]{taskId + ""});

    }

    public List<LessonTaskResultInfo> getRecoedByLessonId(int courseId, int lessonId,long userId) {

        List<LessonTaskResultInfo> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from blockly_lesson_task_result where course_id = "+ courseId + " and lesson_id = "+ lessonId +" and user_id = " + userId +" ;";

        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                LessonTaskResultInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<LessonTaskResultInfo> getRecoedByCourseId(int courseId, long userId) {

        List<LessonTaskResultInfo> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from blockly_lesson_task_result where course_id = "+ courseId +" and user_id = " + userId +" ;";

        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                LessonTaskResultInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<LessonTaskResultInfo> getRecordByTaskId(int lessonId, int taskId, long userId) {

        List<LessonTaskResultInfo> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from blockly_lesson_task_result where lesson_id = "+ lessonId + " and task_id = " + taskId + " and user_id = " + userId +" ;";

        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                LessonTaskResultInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(LessonTaskResultInfo model) {

        ContentValues values = new ContentValues();
        values.put("user_id", model.userId);
        values.put("course_id", model.courseId);
        values.put("lesson_id", model.lessonId);
        values.put("task_id", model.taskId);
        values.put("task_name", model.taskName);
        values.put("efficiency_star", model.efficiencyStar);
        values.put("quality_star", model.qualityStar);
        values.put("add_time", model.addTime);
        values.put("update_time", model.updateTime);

        return values;
    }

    private LessonTaskResultInfo ConvertToModel(Cursor cursor) {

        LessonTaskResultInfo model = new LessonTaskResultInfo();

        model.userId = cursor.getLong(cursor.getColumnIndex("user_id"));
        model.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
        model.lessonId = cursor.getInt(cursor.getColumnIndex("lesson_id"));
        model.taskId = cursor.getInt(cursor.getColumnIndex("task_id"));
        model.taskName = cursor.getString(cursor.getColumnIndex("task_name"));
        model.efficiencyStar = cursor.getInt(cursor.getColumnIndex("efficiency_star"));
        model.qualityStar = cursor.getInt(cursor.getColumnIndex("quality_star"));
        model.addTime = cursor.getLong(cursor.getColumnIndex("add_time"));
        model.updateTime = cursor.getLong(cursor.getColumnIndex("update_time"));

        return model;
    }

}
