package com.ubt.alpha1e_edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e_edu.data.model.RemoteRoleActionInfo;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

public class RemoteRoleActionOperater {

    public static final String TAG = "RemoteRoleActionOperater";

    private static RemoteRoleActionOperater thiz;
    private DBOperater mOperater;


    private RemoteRoleActionOperater() {
    }

    public static RemoteRoleActionOperater getInstance(Context context,
                                                       String path, String dbName) {
        if (thiz == null) {
            thiz = new RemoteRoleActionOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public boolean addRemoteRoleActions(List<RemoteRoleActionInfo> roleActionInfos) {
        boolean delSuccess = deleteRemoteRoleAction(roleActionInfos.get(0).roleid);
        if(!delSuccess){
            return delSuccess;
        }

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        int insertSize = roleActionInfos.size();
        ContentValues values = null;
        int successSize = 0;
        boolean isSuccess = true;

        for(RemoteRoleActionInfo info : roleActionInfos){
            values = ConvertToValue(info);
            //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
            long rowid = db.insert("remote_gamepad_role_action", null, values);
            if(rowid != -1){
                successSize++;
            }
        }

        if(insertSize != successSize){
            isSuccess = false;
        }
        UbtLog.d(TAG,"lihai---------insertSize="+insertSize + "----successSize="+successSize+"----isSuccess="+isSuccess);
        return isSuccess;
    }

    public boolean updateRemoteRoleAction(RemoteRoleActionInfo info) {
        boolean isSuccess = true;
        try {
            SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
            ContentValues values = ConvertToValue(info);
            int rowid = db.update("remote_gamepad_role_action", values, "roleactionid=? ", new String[]{info.roleactionid + ""});
            UbtLog.d(TAG,"lihai-----------updateRemoteRoleAction->"+rowid);
        }catch (Exception ex){
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean deleteRemoteRoleAction(int roleid) {
        boolean isSuccess = true;
        try {
            SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

            clearCustomData(db,roleid);

            String deleteSQL = "delete from remote_gamepad_role_action where roleid = "+roleid+";";
            UbtLog.d(TAG,"lihai-----------deleteSQL->"+deleteSQL);
            db.execSQL(deleteSQL);

        }catch (Exception ex){
            isSuccess = false;
        }
        return isSuccess;
    }

    public RemoteRoleActionInfo getRemoteRoleActionInfo(String roleid,String actionName,String actionFileName){
        RemoteRoleActionInfo roleActionInfo = null;
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from remote_gamepad_role_action where roleid = "+roleid;
        if(actionFileName != null && actionFileName.length() > 0){
            str_get += " and actionFileName = \""+actionFileName+"\";";
        }else{
            str_get += " and actionName = \""+actionName+"\";";
        }

        UbtLog.d(TAG,"lihai---------str_get->"+str_get);
        Cursor cursor = db.rawQuery(str_get, null);
        try {
            if (cursor.moveToFirst()) {
                roleActionInfo = ConvertToModel(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return roleActionInfo;
    }

    public List<RemoteRoleActionInfo> getAllRemoteRoleByRoleid(int roleid) {

        List<RemoteRoleActionInfo> result = new ArrayList<>();

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from remote_gamepad_role_action where roleid = "+roleid+";";
        UbtLog.d(TAG,"lihai---------str_get->"+str_get);
        Cursor cursor = db.rawQuery(str_get, null);

        try {
            while (cursor.moveToNext()) {
                RemoteRoleActionInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(RemoteRoleActionInfo model) {
        ContentValues values = new ContentValues();
        values.put("roleid", model.roleid);
        values.put("actionName", model.actionName);
        values.put("actionFileName", model.actionFileName);
        values.put("actionIcon", model.actionIcon);
        values.put("actionPath", model.actionPath);
        values.put("actionId", model.actionId);
        values.put("actionType", model.actionType);
        values.put("bz", model.bz);

        return values;
    }

    private RemoteRoleActionInfo ConvertToModel(Cursor cursor) {

        RemoteRoleActionInfo model = new RemoteRoleActionInfo();

        model.roleactionid = cursor.getInt(cursor.getColumnIndex("roleactionid"));
        model.roleid = cursor.getInt(cursor.getColumnIndex("roleid"));
        model.actionName = cursor.getString(cursor.getColumnIndex("actionName"));
        model.actionFileName = cursor.getString(cursor.getColumnIndex("actionFileName"));
        model.actionIcon = cursor.getString(cursor.getColumnIndex("actionIcon"));
        model.actionPath = cursor.getString(cursor.getColumnIndex("actionPath"));
        model.actionId = cursor.getString(cursor.getColumnIndex("actionId"));
        model.actionType = cursor.getInt(cursor.getColumnIndex("actionType"));
        model.bz = cursor.getString(cursor.getColumnIndex("bz"));
        return model;
    }

    public boolean clearCustomData(SQLiteDatabase db,int roleId){
        boolean flag = true;
        String deleteSQL = "delete from remote_action_list where action_model_index = "+roleId+";";
        db.execSQL(deleteSQL);
        deleteSQL = "delete from remote_info_logs where log_model_index = "+roleId+";";
        db.execSQL(deleteSQL);
        //UbtLog.d(TAG,"clearCustomData success");
        return flag;
    }

    public boolean initCustomData(List<RemoteRoleActionInfo> roleInfos){
        boolean flag = true;
        int roleid = roleInfos.get(0).roleid;
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        UbtLog.d(TAG,"初始遥控开始");
        String deleteSQL = "delete from remote_action_list where action_model_index = "+roleid+";";
        db.execSQL(deleteSQL);

        deleteSQL = "delete from remote_info_logs where log_model_index = "+roleid+";";
        db.execSQL(deleteSQL);


        //insert remote_action_list
        String str_update = "";
        for(int i=0;i < roleInfos.size();i++){
            str_update = "insert into remote_action_list(action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(\""
                    +roleInfos.get(i).actionFileName+"\",\""
                    +roleInfos.get(i).actionName+"\",\""
                    +roleInfos.get(i).actionName+"\","
                    +roleid+",\""
                    +roleInfos.get(i).actionIcon+"\");";
            //UbtLog.d(TAG,"lihai-----------str_update->"+str_update);
            db.execSQL(str_update);
        }

        String str_get = "select * from remote_action_list where action_model_index="+roleid+";";
        Cursor cursor = db.rawQuery(str_get, null);
        int action_index = -1;
        try {
            //insert remote_info_logs
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",1,2);";
            db.execSQL(str_update);
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",2,3);";
            db.execSQL(str_update);
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",3,4);";
            db.execSQL(str_update);
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",4,1);";
            db.execSQL(str_update);
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",5,5);";
            db.execSQL(str_update);
            str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+",6,6);";
            db.execSQL(str_update);

            int startBtn = 7;
            while(cursor.moveToNext()){
                action_index = cursor.getInt(cursor.getColumnIndex("action_index"));
                str_update = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values("+roleid+","+startBtn+","+action_index+");";
                db.execSQL(str_update);
                startBtn++;
                if(startBtn == 13){//default 6 ge
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        UbtLog.d(TAG,"初始遥控结束");
        return flag;
    }
}
