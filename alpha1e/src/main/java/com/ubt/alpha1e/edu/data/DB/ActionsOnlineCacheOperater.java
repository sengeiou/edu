package com.ubt.alpha1e.edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.edu.data.model.ActionOnlineInfo;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

public class ActionsOnlineCacheOperater {

    public static final String TAG = "ActionsOnlineCacheOperater";

    private static ActionsOnlineCacheOperater thiz;
    private DBOperater mOperater;


    private ActionsOnlineCacheOperater() {
    }

    public static ActionsOnlineCacheOperater getInstance(Context context,
                                                         String path, String dbName) {
        if (thiz == null) {
            thiz = new ActionsOnlineCacheOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public void addOnlineCache(ActionOnlineInfo info,int localSonType,int localSortType) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info,localSonType,localSortType);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("actions_online_cache_logs", null, values);
        UbtLog.d(TAG,"lihai---------rowid->>>>>"+rowid);
    }

    public void updateOnlineCache(ActionOnlineInfo info,int localSonType,int localSortType) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValueForUpdate(info,localSonType,localSortType);
        int rowid = db.update("actions_online_cache_logs", values, "actionId=? and actionLocalSonType=? ", new String[]{info.actionId + "",localSonType+""});
        UbtLog.d(TAG,"lihai-----------updateOnlineCache->"+rowid);
    }

    public void updateOnlineCacheForDelCollect(long actionID ,long actionCollectTime) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String selectSql = "select actionCollectTime from actions_online_cache_logs where actionId = "+actionID;

        Cursor cursor = db.rawQuery(selectSql, null);
        int actionCollectTimetemp = 0;
        try {
            while (cursor.moveToNext()) {
                actionCollectTimetemp = cursor.getInt(cursor.getColumnIndex("actionCollectTime"));
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        actionCollectTimetemp--;

        String updateSQL = "update actions_online_cache_logs set isCollect = 0, actionCollectTime="+actionCollectTimetemp +" where actionId="+actionID;
        db.execSQL(updateSQL);
    }

    public static void deleteMyDynamic(long actionID) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String selectSql = "delete  from actions_online_cache_logs where actionId = "+actionID;



//        String updateSQL = "update actions_online_cache_logs set isCollect = 0, actionCollectTime="+actionCollectTimetemp +" where actionId="+actionID;
        db.execSQL(selectSql);
    }

    public void cleanOnlineCache() {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        db.execSQL("delete from actions_online_cache_logs;");
    }

    public List<ActionOnlineInfo> getAllOnlineCache(int actionSonType,int actionSortType) {

        List<ActionOnlineInfo> result = new ArrayList<>();

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from actions_online_cache_logs where actionLocalSonType = "+actionSonType+" and actionLocalSortType = "+ actionSortType +";";
        UbtLog.d(TAG,"lihai---------str_get->"+str_get);
        Cursor cursor = db.rawQuery(str_get, null);

        try {
            while (cursor.moveToNext()) {
                ActionOnlineInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(ActionOnlineInfo model,int localSonType,int localSortType) {
        ContentValues values = new ContentValues();

        values.put("actionId", model.actionId);
        values.put("actionName", model.actionName);
        values.put("actionTitle", model.actionTitle);
        values.put("loginUserId", model.loginUserId);
        values.put("userName", model.userName);
        values.put("userImage", model.userImage);
        values.put("actionImagePath", model.actionImagePath);
        values.put("actionHeadUrl", model.actionHeadUrl);
        values.put("actionStatus", model.actionStatus);
        values.put("actionType", model.actionType);
        values.put("actionSonType", model.actionSonType);
        values.put("actionSortType", model.actionSortType);
        values.put("actionLocalSonType", localSonType);
        values.put("actionLocalSortType", localSortType);
        values.put("actionDate", model.actionDate);
        values.put("actionPath", model.actionPath);
        values.put("actionVideoPath", model.actionVideoPath);
        values.put("actionTime", model.actionTime);
        values.put("actionResource", model.actionResource);
        values.put("actionResume", model.actionResume);
        values.put("actionDownloadTime", model.actionDownloadTime);
        values.put("actionCommentTime", model.actionCommentTime);
        values.put("actionPraiseTime", model.actionPraiseTime);
        values.put("actionDesciber", model.actionDesciber);
        values.put("actionBrowseTime", model.actionBrowseTime);
        values.put("actionCollectTime", model.actionCollectTime);
        values.put("isCollect", model.isCollect);
        values.put("isPraise", model.isPraise);
        values.put("actionHot", model.actionHot);

        return values;
    }

    private ContentValues ConvertToValueForUpdate(ActionOnlineInfo model,int localSonType,int localSortType) {
        ContentValues values = new ContentValues();
        values.put("actionId", model.actionId);
        values.put("actionLocalSonType", localSonType);
        values.put("actionDownloadTime", model.actionDownloadTime);
        values.put("actionCommentTime", model.actionCommentTime);
        values.put("actionPraiseTime", model.actionPraiseTime);
        values.put("actionDesciber", model.actionDesciber);
        values.put("actionBrowseTime", model.actionBrowseTime);
        values.put("actionCollectTime", model.actionCollectTime);
        values.put("isCollect", model.isCollect);
        values.put("isPraise", model.isPraise);
        values.put("actionHot", model.actionHot);

        return values;
    }

    private ActionOnlineInfo ConvertToModel(Cursor cursor) {

        ActionOnlineInfo model = new ActionOnlineInfo();

        model.actionId = cursor.getLong(cursor.getColumnIndex("actionId"));
        model.actionName = cursor.getString(cursor.getColumnIndex("actionName"));
        model.actionTitle = cursor.getString(cursor.getColumnIndex("actionTitle"));
        model.loginUserId = cursor.getLong(cursor.getColumnIndex("loginUserId"));
        model.userName = cursor.getString(cursor.getColumnIndex("userName"));
        model.userImage = cursor.getString(cursor.getColumnIndex("userImage"));
        model.actionImagePath = cursor.getString(cursor.getColumnIndex("actionImagePath"));
        model.actionHeadUrl = cursor.getString(cursor.getColumnIndex("actionHeadUrl"));
        model.actionStatus = cursor.getInt(cursor.getColumnIndex("actionStatus"));
        model.actionType = cursor.getInt(cursor.getColumnIndex("actionType"));
        model.actionSonType = cursor.getInt(cursor.getColumnIndex("actionSonType"));
        model.actionDate = cursor.getString(cursor.getColumnIndex("actionDate"));
        model.actionPath = cursor.getString(cursor.getColumnIndex("actionPath"));
        model.actionVideoPath = cursor.getString(cursor.getColumnIndex("actionVideoPath"));
        model.actionTime = cursor.getLong(cursor.getColumnIndex("actionTime"));
        model.actionResource = cursor.getInt(cursor.getColumnIndex("actionResource"));
        model.actionResume = cursor.getString(cursor.getColumnIndex("actionResume"));
        model.actionDownloadTime = cursor.getLong(cursor.getColumnIndex("actionDownloadTime"));
        model.actionCommentTime = cursor.getLong(cursor.getColumnIndex("actionCommentTime"));
        model.actionPraiseTime = cursor.getLong(cursor.getColumnIndex("actionPraiseTime"));
        model.actionDesciber = cursor.getString(cursor.getColumnIndex("actionDesciber"));
        model.actionBrowseTime = cursor.getLong(cursor.getColumnIndex("actionBrowseTime"));
        model.actionCollectTime = cursor.getLong(cursor.getColumnIndex("actionCollectTime"));
        model.isCollect = cursor.getInt(cursor.getColumnIndex("isCollect"));
        model.isPraise = cursor.getInt(cursor.getColumnIndex("isPraise"));
        model.actionHot = cursor.getInt(cursor.getColumnIndex("actionHot"));

        return model;
    }
}
