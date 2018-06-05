package com.ubt.alpha1e_edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e_edu.data.model.LessonInfo;

import java.util.ArrayList;
import java.util.List;

public class LessonRecordOperater {
    private static LessonRecordOperater thiz;
    private DBOperater mOperater;

    private LessonRecordOperater() {

    }

    public static LessonRecordOperater getInstance(Context context,
                                                   String path, String dbName) {
        if (thiz == null) {
            thiz = new LessonRecordOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public void addRecord(LessonInfo info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("blockly_lesson", null, values);
    }

    public void updateRecord(LessonInfo lessonInfo) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(lessonInfo);
        db.update("blockly_lesson", values, "lesson_id=? and user_id=? ", new String[]{lessonInfo.lessonId + "", lessonInfo.userId+""});
    }

    public void deleteRecord(long lessonId) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        db.delete("blockly_lesson", "lesson_id=? ", new String[]{lessonId + ""});
    }

    public List<LessonInfo> getRecoedByCourseId(int courseId,long userId) {

        List<LessonInfo> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from blockly_lesson where course_id = "+ courseId +" and user_id = " + userId + " order by lesson_order ;";

        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                LessonInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(LessonInfo model) {
        ContentValues values = new ContentValues();
        values.put("user_id", model.userId);
        values.put("course_id", model.courseId);
        values.put("is_deleted", model.isDeleted);
        values.put("status", model.status);
        values.put("lesson_difficulty", model.lessonDifficulty);
        values.put("lesson_guide", model.lessonGuide);
        values.put("lesson_icon", model.lessonIcon);
        values.put("lesson_id", model.lessonId);
        values.put("lesson_name", model.lessonName);
        values.put("lesson_order", model.lessonOrder);
        values.put("lesson_pic", model.lessonPic);
        values.put("lesson_text", model.lessonText);
        values.put("task_down", model.taskDown);
        values.put("task_md5", model.taskMd5);
        values.put("task_total", model.taskTotal);
        values.put("task_url", model.taskUrl);
        values.put("bz1", "");
        values.put("bz2", "");

        return values;
    }

    private LessonInfo ConvertToModel(Cursor cursor) {

        LessonInfo model = new LessonInfo();

        model.userId = cursor.getInt(cursor.getColumnIndex("user_id"));
        model.courseId = cursor.getInt(cursor.getColumnIndex("course_id"));
        model.isDeleted = cursor.getInt(cursor.getColumnIndex("is_deleted"));
        model.lessonDifficulty = cursor.getInt(cursor.getColumnIndex("lesson_difficulty"));
        model.status = cursor.getInt(cursor.getColumnIndex("status"));
        model.lessonGuide = cursor.getString(cursor.getColumnIndex("lesson_guide"));
        model.lessonIcon = cursor.getString(cursor.getColumnIndex("lesson_icon"));
        model.lessonId = cursor.getInt(cursor.getColumnIndex("lesson_id"));
        model.lessonName = cursor.getString(cursor.getColumnIndex("lesson_name"));
        model.lessonOrder = cursor.getInt(cursor.getColumnIndex("lesson_order"));
        model.lessonPic = cursor.getString(cursor.getColumnIndex("lesson_pic"));
        model.lessonText = cursor.getString(cursor.getColumnIndex("lesson_text"));
        model.taskDown = cursor.getInt(cursor.getColumnIndex("task_down"));
        model.taskMd5 = cursor.getString(cursor.getColumnIndex("task_md5"));
        model.taskTotal = cursor.getInt(cursor.getColumnIndex("task_total"));
        model.taskUrl = cursor.getString(cursor.getColumnIndex("task_url"));

        return model;
    }

}
