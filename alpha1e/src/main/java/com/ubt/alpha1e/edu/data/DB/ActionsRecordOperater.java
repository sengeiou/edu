package com.ubt.alpha1e.edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.edu.data.FileTools;
import com.ubt.alpha1e.edu.data.model.ActionInfo;
import com.ubt.alpha1e.edu.data.model.ActionRecordInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ActionsRecordOperater {
    private static ActionsRecordOperater thiz;
    private DBOperater mOperater;


    private ActionsRecordOperater() {
    }

    public static ActionsRecordOperater getInstance(Context context,
                                                    String path, String dbName) {
        if (thiz == null) {
            thiz = new ActionsRecordOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public void addRecord(ActionRecordInfo info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("actions_download_logs", null, values);
    }

    public void updateRecord(ActionInfo action_info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(action_info);
        db.update("actions_download_logs", values, "actionId=?", new String[]{action_info.actionId + ""});
    }

    public void deleteRecord(long action_id) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        db.delete("actions_download_logs", "actionId=?", new String[]{action_id + ""});

    }

    public List<ActionRecordInfo> getAllRecoed() {


        List<ActionRecordInfo> result = new ArrayList<>();

        String need_delete = "(";
        long[] ids = getAllDownloads();
        for (int i = 0; i < ids.length; i++) {
            need_delete += ids[i];
            if (i < ids.length - 1){
                need_delete += ",";
            }
        }
        need_delete += ")";

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from actions_download_logs;";

        db.delete("actions_download_logs", "actionId NOT IN " + need_delete, null);
        Cursor cursor = db.rawQuery(str_get, null);

        try {
            while (cursor.moveToNext()) {
                ActionRecordInfo info = ConvertToModel(cursor);
                //倒序
                result.add(0,info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(ActionInfo model) {
        ContentValues values = new ContentValues();
        values.put("actionId", model.actionId);
        values.put("actionName", model.actionName);
        values.put("actionTitle", model.actionTitle);
        values.put("actionType", model.actionSonType);
        //values.put("actionImagePath", model.actionImagePath);
        values.put("actionImagePath", model.actionHeadUrl);
        values.put("actionPath", model.actionPath);
        values.put("actionVideoPath", model.actionVideoPath);
        values.put("actionDownloadTime", model.actionDownloadTime);
        values.put("actionPraiseTime", model.actionPraiseTime);
        values.put("actionDesciber", model.actionDesciber);
        values.put("actionTime", model.actionTime);
        values.put("hts_file_name", model.hts_file_name);
        values.put("isCollect", model.isCollect);
        values.put("isPraise", model.isPraise);
        values.put("actionBrowseTime", model.actionBrowseTime);
        return values;
    }

    private ContentValues ConvertToValue(ActionRecordInfo model) {
        ContentValues values = ConvertToValue(model.action);
        values.put("isDownLoadSuccess", model.isDownLoadSuccess);
        return values;
    }

    private ActionRecordInfo ConvertToModel(Cursor cursor) {

        ActionRecordInfo model = new ActionRecordInfo();
        model.action = new ActionInfo();


        model.action.actionId = cursor.getLong(cursor.getColumnIndex("actionId"));
        model.action.actionName = cursor.getString(cursor.getColumnIndex("actionName"));
        model.action.actionTitle = cursor.getString(cursor.getColumnIndex("actionTitle"));
        model.action.actionType = cursor.getInt(cursor.getColumnIndex("actionType"));
        model.action.actionImagePath = cursor.getString(cursor.getColumnIndex("actionImagePath"));
        model.action.actionPath = cursor.getString(cursor.getColumnIndex("actionPath"));
        model.action.actionVideoPath = cursor.getString(cursor.getColumnIndex("actionVideoPath"));
        model.action.actionDownloadTime = cursor.getLong(cursor.getColumnIndex("actionDownloadTime"));
        model.action.actionPraiseTime = cursor.getLong(cursor.getColumnIndex("actionPraiseTime"));
        model.action.actionDesciber = cursor.getString(cursor.getColumnIndex("actionDesciber"));
        model.action.actionTime = cursor.getLong(cursor.getColumnIndex("actionTime"));
        model.action.hts_file_name = cursor.getString(cursor.getColumnIndex("hts_file_name"));
        model.action.isCollect = cursor.getInt(cursor.getColumnIndex("isCollect"));
        model.action.isPraise = cursor.getInt(cursor.getColumnIndex("isPraise"));
        model.action.actionBrowseTime = cursor.getLong(cursor.getColumnIndex("actionBrowseTime"));
        model.isDownLoadSuccess = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isDownLoadSuccess")));

        return model;
    }

    private long[] getAllDownloads() {
        File actions_path = new File(FileTools.actions_download_cache);
        if (!actions_path.exists()) {
            return new long[0];
        }

        String[] action_zip_file_name = actions_path.list(new FilenameFilter() {
            public boolean accept(File f, String name) {
                return name.endsWith(".zip");
            }
        });
        long[] ids = new long[action_zip_file_name.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Long.parseLong(action_zip_file_name[i].split("\\.")[0]);
        }

        return ids;
    }
}
