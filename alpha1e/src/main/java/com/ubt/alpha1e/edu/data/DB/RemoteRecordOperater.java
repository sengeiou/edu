package com.ubt.alpha1e.edu.data.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.edu.data.RemoteItem;
import com.ubt.alpha1e.edu.data.model.RemoteInfo;
import com.ubt.alpha1e.edu.ui.helper.RemoteHelper;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class RemoteRecordOperater {

    public static final String TAG = "RemoteRecordOperater";
    private static RemoteRecordOperater thiz;
    private DBOperater mOperater;

    public static enum ModelType {
        FOOTBALL_PLAYER, DANCER, FIGHTER,CUSTOM
    }

    private RemoteRecordOperater() {
    }

    public static RemoteRecordOperater getInstance(Context context,
                                                   String path, String dbName) {
        if (thiz == null) {
            thiz = new RemoteRecordOperater();
            SDContext sContext = new SDContext(context, path);
            thiz.mOperater = new DBOperater(sContext, dbName, "");
        }
        return thiz;
    }

    public RemoteInfo getRemoteInfoByModel(ModelType modelType, boolean isCN,String model) {
        SQLiteDatabase db = thiz.mOperater.getReadableDatabase();
        String str_get = "select log_btn_index,action_name,#,action_image_name from remote_info_logs left join remote_action_list on remote_info_logs.log_action_index = remote_action_list.action_index where log_model_index = $;";
        String _country = isCN ? "action_name_ch" : "action_name_en";
        String _model = null;
        if(model == null){
            _model = getModelIndex(modelType) + "";
        }else{
            _model = model;
        }

        str_get = str_get.replace("#", _country);
        str_get = str_get.replace("$", _model);
        RemoteInfo info = new RemoteInfo();
        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                int btn_index = cursor.getInt(cursor.getColumnIndex("log_btn_index"));
                RemoteItem item = getItemByIndex(btn_index, info);
                item.hts_name = cursor.getString(cursor.getColumnIndex("action_name"));
                item.image_name = cursor.getString(cursor.getColumnIndex("action_image_name"));
                item.show_name = cursor.getString(cursor.getColumnIndex(_country));
                //UbtLog.d(TAG,"更新遥控动作item.hts_name="+item.hts_name+"   item.image_name="+item.image_name+"  show_name="+item.show_name+"   btn_index="+btn_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }


    public void UpdateRemoteInfo(ModelType type, RemoteInfo info, List<RemoteItem> changes,String roleId) {
        for (int i = 0; i < changes.size(); i++) {
            UpdateRemoteInfo(type, changes.get(i), info,roleId);
        }
    }

    public List<RemoteItem> getAllActions(ModelType modelType, boolean isCN,String roleId) {
        List<RemoteItem> result = new ArrayList<>();
        SQLiteDatabase db = thiz.mOperater.getReadableDatabase();
        String str_get = "select action_name,#,action_image_name from remote_action_list where action_model_index=$;";
        String _country = isCN ? "action_name_ch" : "action_name_en";
        String _model = getModelIndex(modelType) + "";
        if(RemoteHelper.mCurrentType == RemoteRecordOperater.ModelType.CUSTOM){
            _model = roleId;
        }
        str_get = str_get.replace("#", _country);
        str_get = str_get.replace("$", _model);
        Cursor cursor = db.rawQuery(str_get, null);
        try {
            while (cursor.moveToNext()) {
                RemoteItem item = new RemoteItem();
                item.hts_name = cursor.getString(cursor.getColumnIndex("action_name"));
                item.image_name = cursor.getString(cursor.getColumnIndex("action_image_name"));
                item.show_name = cursor.getString(cursor.getColumnIndex(_country));
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void UpdateRemoteInfo(ModelType type, RemoteItem item, RemoteInfo info,String roleId) {

        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
        String actionModelIndex = "";
        if(type == RemoteRecordOperater.ModelType.CUSTOM){
            actionModelIndex = roleId ;
        }else{
            actionModelIndex = getModelIndex(type) + "";
        }
        String str_get = "select action_index from remote_action_list where action_name=\""
                        + item.hts_name + "\" and action_model_index=\"" + actionModelIndex + "\";";

        UbtLog.d(TAG,"更新遥控SQL:"+str_get);
        Cursor cursor = db.rawQuery(str_get, null);
        int action_index = -1;
        try {
            while (cursor.moveToNext()) {
                action_index = cursor.getInt(cursor.getColumnIndex("action_index"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //删除原有配置数据
        String srt_delete = "delete from remote_info_logs where log_model_index=@ and log_btn_index=#;";
        srt_delete = srt_delete.replace("@", actionModelIndex);
        srt_delete = srt_delete.replace("#", getIndexByItem(item, info) + "");
        UbtLog.d(TAG,"lihai----srt_delete:"+srt_delete);
        db.execSQL(srt_delete);


        //插入数据
        String str_update = "";
        str_update = "insert or replace into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(@,#,$);";
        str_update = str_update.replace("@", actionModelIndex);
        str_update = str_update.replace("#", getIndexByItem(item, info) + "");
        str_update = str_update.replace("$", action_index + "");
        UbtLog.d(TAG,"lihai----str_update::"+str_update);
        db.execSQL(str_update);
        UbtLog.d(TAG,"lihai----str_update:>"+str_update);
    }

    private int getModelIndex(ModelType type) {
        switch (type) {
            case FOOTBALL_PLAYER:
                return 1;
            case FIGHTER:
                return 2;
            case DANCER:
                return 3;
            case CUSTOM:
                return 4;
        }
        return -1;
    }

    private static int getIndexByItem(RemoteItem item, RemoteInfo info) {
        if (item.hashCode() == info.do_up.hashCode()) {
            return 1;
        }
        if (item.hashCode() == info.do_left.hashCode()) {
            return 2;
        }
        if (item.hashCode() == info.do_right.hashCode()) {
            return 3;
        }
        if (item.hashCode() == info.do_down.hashCode()) {
            return 4;
        }
        if (item.hashCode() == info.do_to_left.hashCode()) {
            return 5;
        }
        if (item.hashCode() == info.do_to_right.hashCode()) {
            return 6;
        }
        if (item.hashCode() == info.do_1.hashCode()) {
            return 7;
        }
        if (item.hashCode() == info.do_2.hashCode()) {
            return 8;
        }
        if (item.hashCode() == info.do_3.hashCode()) {
            return 9;
        }
        if (item.hashCode() == info.do_4.hashCode()) {
            return 10;
        }
        if (item.hashCode() == info.do_5.hashCode()) {
            return 11;
        }
        if (item.hashCode() == info.do_6.hashCode()) {
            return 12;
        }
        return -1;
    }

    public static void setItemByIndex(int index, RemoteItem item, RemoteInfo info) {
        switch (index) {
            case 1:
                info.do_up = item;
                break;
            case 2:
                info.do_left = item;
                break;
            case 3:
                info.do_right = item;
                break;
            case 4:
                info.do_down = item;
                break;
            case 5:
                info.do_to_left = item;
                break;
            case 6:
                info.do_to_right = item;
                break;
            case 7:
                info.do_1 = item;
                break;
            case 8:
                info.do_2 = item;
                break;
            case 9:
                info.do_3 = item;
                break;
            case 10:
                info.do_4 = item;
                break;
            case 11:
                info.do_5 = item;
                break;
            case 12:
                info.do_6 = item;
                break;
        }


    }

    public static RemoteItem getItemByIndex(int index, RemoteInfo info) {
        switch (index) {
            case 1:
                return info.do_up;
            case 2:
                return info.do_left;
            case 3:
                return info.do_right;
            case 4:
                return info.do_down;
            case 5:
                return info.do_to_left;
            case 6:
                return info.do_to_right;
            case 7:
                return info.do_1;
            case 8:
                return info.do_2;
            case 9:
                return info.do_3;
            case 10:
                return info.do_4;
            case 11:
                return info.do_5;
            case 12:
                return info.do_6;
        }
        return null;
    }

}
