package com.ubt.alpha1e.edu.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.edu.data.model.RemoteRoleInfo;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

public class RemoteRoleOperater {

    public static final String TAG = "RemoteRoleOperater";

    private static RemoteRoleOperater thiz;
    private DBOperater mOperater;


    private RemoteRoleOperater() {
    }

    public static RemoteRoleOperater getInstance(Context context,
                                                 String path, String dbName) {
        if (thiz == null) {
            thiz = new RemoteRoleOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }

        return thiz;
    }

    public long addRemoteRole(RemoteRoleInfo info) {
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        ContentValues values = ConvertToValue(info);
        //返回新添记录的行号。该行号是一个内部值，与主键id无关，发生错误返回-1
        long rowid = db.insert("remote_gamepad_role", null, values);

        UbtLog.d(TAG,"添加遥控角色rowid："+rowid);
        return rowid;
    }

    public boolean updateRemoteRole(RemoteRoleInfo info) {
        boolean isSuccess = true;
        try {
            SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
            ContentValues values = ConvertToValue(info);
            int rowid = db.update("remote_gamepad_role", values, "roleid=? ", new String[]{info.roleid + ""});
            UbtLog.d(TAG,"修改遥控角色rowid："+rowid);

        }catch (Exception ex){
            isSuccess = false;
        }
        return isSuccess;
    }

    public boolean deleteRemoteRole(RemoteRoleInfo info) {
        boolean isSuccess = true;
        try {
            SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
            String deleteSQL = "delete from remote_gamepad_role where roleid = "+info.roleid+";";
            UbtLog.d(TAG,"删除遥控器角色："+deleteSQL);
            db.execSQL(deleteSQL);

        }catch (Exception ex){
            isSuccess = false;
        }
        return isSuccess;
    }

    public List<RemoteRoleInfo> getAllRemoteRole() {

        List<RemoteRoleInfo> result = new ArrayList<>();

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String str_get = "select * from remote_gamepad_role ;";
        UbtLog.d(TAG,"查询所有的遥控器角色："+str_get);
        Cursor cursor = db.rawQuery(str_get, null);

        try {
            while (cursor.moveToNext()) {
                RemoteRoleInfo info = ConvertToModel(cursor);
                result.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private ContentValues ConvertToValue(RemoteRoleInfo model) {
        ContentValues values = new ContentValues();
        //values.put("roleid", model.roleid);
        values.put("roleName", model.roleName);
        values.put("roleIntroduction", model.roleIntroduction);
        values.put("roleIcon", model.roleIcon);
        values.put("bz", model.bz);

        return values;
    }

    private RemoteRoleInfo ConvertToModel(Cursor cursor) {

        RemoteRoleInfo model = new RemoteRoleInfo();

        model.roleid = cursor.getInt(cursor.getColumnIndex("roleid"));
        model.roleName = cursor.getString(cursor.getColumnIndex("roleName"));
        model.roleIntroduction = cursor.getString(cursor.getColumnIndex("roleIntroduction"));
        model.roleIcon = cursor.getString(cursor.getColumnIndex("roleIcon"));
        model.bz = cursor.getString(cursor.getColumnIndex("bz"));
        return model;
    }
}
