package com.ubt.alpha1e.data.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubt.alpha1e.R;

import com.ubt.alpha1e.utils.log.UbtLog;

/**
 * 升级数据库
 */
public class UpgradeOperater {

    public static final String TAG = "UpgradeOperater";
    private static UpgradeOperater thiz;
    private DBOperater mOperater;
    private static Context mContext = null;

    private UpgradeOperater() {
    }

    public static UpgradeOperater getInstance(Context context,
                                              String path, String dbName) {
        if (thiz == null) {
            thiz = new UpgradeOperater();
            mContext = context;
            SDContext sContext = new SDContext(context, path);

            initDBVersion(sContext,dbName);

            thiz.mOperater = new DBOperater(sContext, dbName, "");

            if(!isCorrectDbVersion()){//非正确数据库版本，重新初始话
                thiz.mOperater = new DBOperater(sContext, dbName, "");
            }
            UbtLog.d(TAG,"数据库版本："+DBOperater.DB_VERSION);
        }

        return thiz;
    }

    public static int initDBVersion(Context sContext,String dbName){

        //int version = MyDbVersion.initDBVersionFromSharePreferences(mContext);
        //if(version == -1){//第一次启动，为-1
            int version = -1;
            //判断是否存在db_version这张表，如果存在，取数据库表版本号，否则为1
            DBOperater.DB_VERSION = 9999;//大版本可以操作小版本数据库
            thiz.mOperater = new DBOperater(sContext, dbName, "");
            SQLiteDatabase db = thiz.mOperater.getWritableDatabase();
            String selectSql = "select count(*) as countNum from sqlite_master where type='table' and name='db_version';";
            Cursor cursor = db.rawQuery(selectSql,null);
            boolean isExist = false;
            while(cursor.moveToNext()){
                int countNum = cursor.getInt(cursor.getColumnIndex("countNum"));
                if(countNum != 0){//exist == 1
                    isExist = true;
                }
                break;
            }

            if(isExist){
                selectSql = "select version from db_version;";
                cursor = db.rawQuery(selectSql,null);
                while(cursor.moveToNext()){
                    version = cursor.getInt(cursor.getColumnIndex("version"));
                    saveDBVersion(version);
                    break;
                }
            }else{
                version = 1;
            }
            db.setVersion(version);
        //}

        DBOperater.DB_VERSION = version;
        return version;
    }

    public static void saveDBVersion(int version){
        MyDbVersion.saveDBVersionToSharePreferences(mContext,version);
    }

    public static boolean isCorrectDbVersion(){
        int dbVersion = -1;
        try{
            //如果数据库版本不对，则报错执行
            dbVersion = thiz.mOperater.getReadableDatabase().getVersion();
            return true;
        }catch (Exception ex){
            //Can't downgrade database from version 2 to 1
            String exMsg = ex.getMessage();

            UbtLog.d(TAG,"查询版本错误信息："+exMsg);
            if(exMsg.contains("Can't downgrade database from version") && exMsg.contains("to")){
                exMsg =  exMsg.substring("Can't downgrade database from version".length(),exMsg.indexOf("to")).trim();
                boolean flag = MyDbVersion.isStringNumber(exMsg);
                if(flag){
                    dbVersion = Integer.parseInt(exMsg);
                    DBOperater.DB_VERSION = dbVersion;
                    saveDBVersion(dbVersion);
                }
            }
        }
        return false;
    }

    public boolean execUpgradeOperater(){

        boolean isSuccess = true;
        SQLiteDatabase db = thiz.mOperater.getWritableDatabase();

        if(db.getVersion() == 1){// 1 到 2 执行升级操作
            upgadeVersion1TO2(db);
            UbtLog.d(TAG,"1 升级 2 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

        }
        return isSuccess;
    }

    public void upgadeVersion1TO2(SQLiteDatabase db){
        createTableFrom1TO2(db);
        insertDefaulteDataFrom1TO2(db);

        updateDBVersion(db,2);
    }

    private void updateDBVersion(SQLiteDatabase db,int version){
        DBOperater.DB_VERSION = version;
        db.setVersion(DBOperater.DB_VERSION);
        saveDBVersion(DBOperater.DB_VERSION);
    }

    public void createTableFrom1TO2(SQLiteDatabase db){

        String offForeignKeySQL = "PRAGMA foreign_keys = off;";
        db.execSQL(offForeignKeySQL);

        db.execSQL("DROP TABLE IF EXISTS db_version");
        db.execSQL("DROP TABLE IF EXISTS remote_action_list;");
        db.execSQL("DROP TABLE IF EXISTS remote_info_logs");
        db.execSQL("DROP TABLE IF EXISTS remote_gamepad_role");
        db.execSQL("DROP TABLE IF EXISTS remote_info_logs");
        db.execSQL("DROP TABLE IF EXISTS actions_online_cache_logs");

        String createTableSQL = "CREATE TABLE IF NOT EXISTS [remote_action_list] (\n" +
                "  [action_index] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [action_name] NVARCHAR NOT NULL, \n" +
                "  [action_name_ch] NVARCHAR NOT NULL, \n" +
                "  [action_name_en] VARCHAR NOT NULL, \n" +
                "  [action_model_index] INT NOT NULL, \n" +
                "  [action_image_name] VARCHAR, \n" +
                "  CONSTRAINT [sqlite_autoindex_remote_action_list_1]);";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE IF NOT EXISTS [remote_info_logs] (\n" +
                "  [log_index] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \n" +
                "  [log_model_index] INT NOT NULL , \n" +
                "  [log_btn_index] INT NOT NULL , \n" +
                "  [log_action_index] INT NOT NULL,\n" +
                "  CONSTRAINT [f_log_action_index]);";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE IF NOT EXISTS [remote_gamepad_role] (\n" +
                "  [roleid] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [roleName] NVARCHAR, \n" +
                "  [roleIntroduction] NVARCHAR, \n" +
                "  [roleIcon] VARCHAR, \n" +
                "  [bz] VARCHAR, \n" +
                "  CONSTRAINT [sqlite_autoindex_remote_gamepad_role_1]);";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE IF NOT EXISTS [remote_gamepad_role_action] (\n" +
                "  [roleactionid] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [roleid] int, \n" +
                "  [actionName] NVARCHAR, \n" +
                "  [actionFileName] VARCHAR,  \n" +
                "  [actionIcon] VARCHAR,\n" +
                "  [actionType] int,   \n" +
                "  [actionId] VARCHAR,   \n" +
                "  [actionPath] VARCHAR,\n" +
                "  [bz] VARCHAR, \n" +
                "  CONSTRAINT [sqlite_autoindex_remote_gamepad_role_action_1]);";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE [actions_online_cache_logs] (\n" +
                "  [actionId] INT64 NOT NULL, \n" +
                "  [actionName] NVARCHAR, \n" +
                "  [actionTitle] NVARCHAR, \n" +
                "  [loginUserId] INT64, \n" +
                "  [userName] VARCHAR, \n" +
                "  [userImage] VARCHAR, \n" +
                "  [actionImagePath] VARCHAR, \n" +
                "  [actionHeadUrl] VARCHAR, \n" +
                "  [actionStatus] INT,\n" +
                "  [actionType] INT,\n" +
                "  [actionSonType] INT, \n" +
                "  [actionSortType] INT,  \n" +
                "  [actionLocalSonType] INT, \n" +
                "  [actionLocalSortType] INT, \n" +
                "  [actionDate] INT64, \n" +
                "  [actionFilePath] VARCHAR, \n" +
                "  [actionPath] VARCHAR, \n" +
                "  [actionVideoPath] VARCHAR, \n" +
                "  [actionTime] INT64,\n" +
                "  [actionOriginalId] VARCHAR,\n" +
                "  [actionResource] VARCHAR,\n" +
                "  [actionResume] VARCHAR,\n" +
                "  [actionDownloadTime] INT64,\n" +
                "  [actionCommentTime] INT64,\n" +
                "  [actionPraiseTime] INT64,\n" +
                "  [actionDesciber] VARCHAR,\n" +
                "  [actionBrowseTime] INT64,\n" +
                "  [actionCollectTime] INT64,\n" +
                "  [isCollect] INT,\n" +
                "  [isPraise] INT,\n" +
                "  [actionHot] INT,\n" +
                "  CONSTRAINT [sqlite_autoindex_actions_download_logs_1] PRIMARY KEY ([actionId],[actionLocalSonType],[actionLocalSortType]));";
        db.execSQL(createTableSQL);

        createBlockly(db);

        createTableSQL = "CREATE TABLE [db_version] (\n" +
                "  [version] INT NOT NULL\n" +
                ");";
        db.execSQL(createTableSQL);
    }

    public void insertDefaulteDataFrom1TO2(SQLiteDatabase db){

        //init remote_gamepad_role
        String str_insert = "insert into remote_gamepad_role(roleid,roleName,roleIntroduction,roleIcon,bz) values(1,\""+mContext.getResources().getString(R.string.ui_remote_role_footballer)+"\",\""
                +mContext.getResources().getString(R.string.ui_remote_role_footballer_introduction)+"\","+R.drawable.gamepad_model_football+",'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_gamepad_role(roleid,roleName,roleIntroduction,roleIcon,bz) values(2,\""+mContext.getResources().getString(R.string.ui_remote_role_fighter)+"\",\""
                +mContext.getResources().getString(R.string.ui_remote_role_fighter_introduction)+"\","+R.drawable.gamepad_model_fighter+",'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_gamepad_role(roleid,roleName,roleIntroduction,roleIcon,bz) values(3,\""+mContext.getResources().getString(R.string.ui_remote_role_dancer)+"\",\""
                +mContext.getResources().getString(R.string.ui_remote_role_dancer_introduction)+"\","+R.drawable.gamepad_model_dancer+",'');";
        db.execSQL(str_insert);

        //init remote_action_list
        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(1,'Backward.hts','后退','Move back',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(2,'Forward.hts','前进','Move forward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(3,'Leftward.hts','左移','Move Leftward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(4,'Rightward.hts','右移','Move Rightward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(5,'TurnLeft.hts','左转','Turn Left',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(6,'TurnRight.hts','右转','Turn Right',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(7,'Shoot left.hts','左脚射门','Left foot shot',1,'remoter_football_leftshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(8,'Shoot right.hts','右脚射门','Right foot shot',1,'remoter_football_rightshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(9,'Left slide tackle.hts','左脚铲球','Left tackle',1,'remoter_football_lefttackle_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(10,'Right slide tackle.hts','右脚铲球','Right tackle',1,'remoter_football_righttackle_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(11,'Fall forward rise.hts','前倒起身','Down up front',1,'remoter_football_getupinfront_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(12,'Fall backward rise.hts','后倒起身','Pour up',1,'remoter_football_backup_select.xml');";
        db.execSQL(str_insert);


        //格斗家
        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(17,'Left hits forward.hts','左手前击','Left hits forward',2,'remoter_boxing_lefthandheadshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(18,'Right hits forward.hts','右手前击','Right hits forward',2,'remoter_boxing_righthandheadshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(19,'Hit left.hts','左侧击','Hit left',2,'remoter_boxing_lefthandsideshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(20,'Hit right.hts','右侧击','Hit right',2,'remoter_boxing_righthandsideshoot_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(21,'Fall forward rise.hts','前倒起身','Fall forward rise',2,'remoter_boxing_getupinfront_select.xml');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(22,'Fall backward rise.hts','后倒起身','Fall backward rise',2,'remoter_boxing_backup_select.xml');";
        db.execSQL(str_insert);


        //init remote_info_logs
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,1,2);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,2,3);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,3,4);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,4,1);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,5,5);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,6,6);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,7,9);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,8,10);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,9,8);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,10,7);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,11,12);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(1,12,11);";
        db.execSQL(str_insert);


        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,1,2);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,2,3);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,3,4);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,4,1);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,5,5);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,6,6);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,7,18);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,8,17);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,9,19);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,10,20);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,11,21);";
        db.execSQL(str_insert);
        str_insert = "insert into remote_info_logs(log_model_index,log_btn_index,log_action_index) values(2,12,22);";
        db.execSQL(str_insert);

        str_insert = "insert into db_version(version) values(2);";
        db.execSQL(str_insert);
    }

    public void createBlockly(SQLiteDatabase db){

        String createTableSQL = "CREATE TABLE IF NOT EXISTS [blockly_lesson] (\n" +
                "  [lesson_index] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [user_id] INT64 , \n" +
                "  [course_id] INT , \n" +
                "  [is_deleted] INT , \n" +
                "  [lesson_difficulty] INT , \n" +
                "  [status] INT , \n" +
                "  [lesson_guide] VARCHAR, \n" +
                "  [lesson_icon] VARCHAR, \n" +
                "  [lesson_id] INT , \n" +
                "  [lesson_name] VARCHAR, \n" +
                "  [lesson_order] INT, \n" +
                "  [lesson_pic] VARCHAR, \n" +
                "  [lesson_text] VARCHAR, \n" +
                "  [task_down] INT, \n" +
                "  [task_md5] VARCHAR, \n" +
                "  [task_total] INT, \n" +
                "  [task_url] VARCHAR, \n" +
                "  [bz1] VARCHAR, \n" +
                "  [bz2] VARCHAR \n" +
                "  );";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE IF NOT EXISTS [blockly_lesson_task] (\n" +
                "  [task_index] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [user_id] INT64, \n" +
                "  [task_name] VARCHAR , \n" +
                "  [task_pic] VARCHAR, \n" +
                "  [task_status] INT NOT NULL, \n" +
                "  [status] INT , \n" +
                "  [have_debris] INT, \n" +
                "  [task_duration] INT, \n" +
                "  [task_id] INT NOT NULL, \n" +
                "  [language] VARCHAR, \n" +
                "  [lesson_id] INT NOT NULL, \n" +
                "  [debris_name] VARCHAR, \n" +
                "  [task_text] VARCHAR, \n" +
                "  [task_order] INT, \n" +
                "  [debris_pic] VARCHAR, \n" +
                "  [task_guide] VARCHAR, \n" +
                "  [task_help] VARCHAR, \n" +
                "  [task_result] VARCHAR, \n" +
                "  [main_text] VARCHAR, \n" +
                "  [voice_file] VARCHAR, \n" +
                "  [motion_file] VARCHAR, \n" +
                "  [task_voice_en] VARCHAR, \n" +
                "  [task_voice] VARCHAR, \n" +
                "  [motion_name] VARCHAR, \n" +
                "  [is_unlock] int, \n" +
                "  [is_current_show] int, \n" +
                "  [bz1] VARCHAR, \n" +
                "  [bz2] VARCHAR \n" +
                "  );";
        db.execSQL(createTableSQL);

        createTableSQL = "CREATE TABLE IF NOT EXISTS [blockly_lesson_task_result] (\n" +
                "  [task_index] integer NOT NULL PRIMARY KEY autoincrement, \n" +
                "  [user_id] INT64, \n" +
                "  [course_id] INT, \n" +
                "  [lesson_id] INT, \n" +
                "  [task_id] INT, \n" +
                "  [task_name] VARCHAR , \n" +
                "  [efficiency_star] INT, \n" +
                "  [quality_star] INT, \n" +
                "  [add_time] INT64, \n" +
                "  [update_time] INT64, \n" +
                "  [bz1] VARCHAR, \n" +
                "  [bz2] VARCHAR \n" +
                "  );";
        db.execSQL(createTableSQL);

    }



}
