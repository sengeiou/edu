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

            upgadeVersion2TO3(db);
            UbtLog.d(TAG,"2 升级 3 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            upgadeVersion3TO4(db);
            UbtLog.d(TAG,"3 升级 4 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            upgadeVersion4TO5(db);
            UbtLog.d(TAG,"4 升级 5 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            //2017.9.30
            upgadeVersion5TO6(db);
            UbtLog.d(TAG,"5 升级 6 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

        }else if(db.getVersion() == 2){ // 2 到 3 执行升级操作

            upgadeVersion2TO3(db);
            UbtLog.d(TAG,"2 升级 3 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            upgadeVersion3TO4(db);
            UbtLog.d(TAG,"3 升级 4 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            upgadeVersion4TO5(db);
            UbtLog.d(TAG,"4 升级 5 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            //2017.9.30
            upgadeVersion5TO6(db);
            UbtLog.d(TAG,"5 升级 6 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
        }else if(db.getVersion() == 3){
            //2017-07-13 update
            upgadeVersion3TO4(db);
            UbtLog.d(TAG,"3 升级 4 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());

            upgadeVersion4TO5(db);
            UbtLog.d(TAG,"4 升级 5 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
            //2017.9.30
            upgadeVersion5TO6(db);
            UbtLog.d(TAG,"5 升级 6 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
        } else if(db.getVersion() == 4){
            //2017-08-03 update
            upgadeVersion4TO5(db);
            UbtLog.d(TAG,"4 升级 5 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
            //2017.9.30
            upgadeVersion5TO6(db);
            UbtLog.d(TAG,"5 升级 6 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
        }else if(db.getVersion() == 5){  //2017/9/30 临时版本
            //2017.9.30
            upgadeVersion5TO6(db);
            UbtLog.d(TAG,"5 升级 6 结束，是否成功："+isSuccess+ "     升级后数据库版本："+db.getVersion());
        }
        return isSuccess;
    }

    public void upgadeVersion1TO2(SQLiteDatabase db){
        createTableFrom1TO2(db);
        insertDefaulteDataFrom1TO2(db);

        updateDBVersion(db,2);
    }

    public void upgadeVersion2TO3(SQLiteDatabase db){
        updateDefaulteDataFrom2TO3(db);
        updateDBVersion(db,3);
    }

    public void upgadeVersion3TO4(SQLiteDatabase db){
        updateDefaulteDataFrom3TO4(db);
        updateDBVersion(db,4);
    }

    public void upgadeVersion4TO5(SQLiteDatabase db){
        updateDefaulteDataFrom4TO5(db);
        updateDBVersion(db,5);
    }

    public void upgadeVersion5TO6(SQLiteDatabase db){
        updateDefaulteDataFrom5TO6(db);
        updateDBVersion(db,6);
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
                "  CONSTRAINT [sqlite_autoindex_actions_download_logs_1] PRIMARY KEY ([actionId],[actionLocalSonType],[actionLocalSortType]));";
        db.execSQL(createTableSQL);

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
        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(1,'Move back.hts','后退','Move back',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(2,'Move forward.hts','前进','Move forward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(3,'Move Leftward.hts','左移','Move Leftward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(4,'Move Rightward.hts','右移','Move Rightward',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(5,'Turn Left.hts','左转','Turn Left',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(6,'Turn Right.hts','右转','Turn Right',0,'');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(7,'Left foot shot.hts','左脚射门','Left foot shot',1,'gamepad_settings_item_left_foot_shot.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(8,'Right foot shot.hts','右脚射门','Right foot shot',1,'gamepad_settings_item_right_foot_shot.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(9,'Left tackle.hts','左脚铲球','Left tackle',1,'gamepad_settings_item_left_tackle.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(10,'Right tackle.hts','右脚铲球','Right tackle',1,'gamepad_settings_item_right_tackle.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(11,'Down up front.hts','前倒起身','Down up front',1,'gamepad_settings_item_down_up_front.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(12,'Pour up.hts','后倒起身','Pour up',1,'gamepad_settings_item_pour_up.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(14,'Ground step fine tuning.hts','碎步微调','Ground step fine tuning',1,'gamepad_settings_item_ground_step_fine_tuning.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(17,'Left front strike.hts','左手前击','Left front strike',2,'gamepad_settings_item_left_front_strike.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(18,'Right hand front strike.hts','右手前击','Right hand front strike',2,'gamepad_settings_item_right_hand_front_strike.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(19,'Left click.hts','左侧击','Left click',2,'gamepad_settings_item_left_click.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(20,'Right click.hts','右侧击','Right click',2,'gamepad_settings_item_right_click.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(21,'Defense.hts','防御','Defense',2,'gamepad_settings_item_defense.png');";
        db.execSQL(str_insert);

        str_insert = "insert into remote_action_list(action_index,action_name,action_name_ch,action_name_en,action_model_index,action_image_name) values(22,'Celebrate.hts','庆祝','Celebrate',2,'gamepad_settings_item_celebrate.png');";
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

    public void updateDefaulteDataFrom2TO3(SQLiteDatabase db){

        //更新足球员
        String updateSQL = "update remote_action_list set action_name='Backward.hts',action_name_ch='后退',action_name_en='Backward' where action_index=1;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Forward.hts',action_name_ch='前进',action_name_en='Forward' where action_index=2;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Leftward.hts',action_name_ch='左移',action_name_en='Leftward' where action_index=3;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Rightward.hts',action_name_ch='右移',action_name_en='Rightward' where action_index=4;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='TurnLeft.hts',action_name_ch='左转',action_name_en='TurnLeft' where action_index=5;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='TurnRight.hts',action_name_ch='右转',action_name_en='TurnRight' where action_index=6;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Shoot left.hts',action_name_ch='左脚射门',action_name_en='Shoot left' where action_index=7;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Shoot right.hts',action_name_ch='右脚射门',action_name_en='Shoot right' where action_index=8;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Left slide tackle.hts',action_name_ch='左脚铲球',action_name_en='Left slide tackle' where action_index=9;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Right slide tackle.hts',action_name_ch='右脚铲球',action_name_en='Right slide tackle' where action_index=10;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall forward rise.hts',action_name_ch='前倒起身',action_name_en='Fall forward rise' where action_index=11;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall backward rise.hts',action_name_ch='后倒起身',action_name_en='Fall backward rise' where action_index=12;";
        db.execSQL(updateSQL);

        //删除足球员(碎步微调） 先把碎步微调设置成其他7
        updateSQL = "update remote_info_logs set log_action_index = 7 where log_action_index = 14;";
        db.execSQL(updateSQL);
        updateSQL = "delete from remote_action_list where action_index=14;";
        db.execSQL(updateSQL);

        //更新格斗家
        updateSQL = "update remote_action_list set action_name='Left hits forward.hts',action_name_ch='左手前击',action_name_en='Left hits forward' where action_index=17;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Right hits forward.hts',action_name_ch='右手前击',action_name_en='Right hits forward' where action_index=18;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Hit left.hts',action_name_ch='左侧击',action_name_en='Hit left' where action_index=19;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Hit right.hts',action_name_ch='右侧击',action_name_en='Hit right' where action_index=20;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall forward rise.hts',action_name_ch='前倒起身',action_name_en='Fall forward rise',action_image_name='gamepad_settings_item_down_up_front_g.png' where action_index=21;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall backward rise.hts',action_name_ch='后倒起身',action_name_en='Fall backward rise',action_image_name='gamepad_settings_item_pour_up_g.png' where action_index=22;";
        db.execSQL(updateSQL);

        //update db version
        updateSQL = "update db_version set version= 3;";
        db.execSQL(updateSQL);
    }

    public void updateDefaulteDataFrom3TO4(SQLiteDatabase db){

        //更新足球员
        String updateSQL = "update remote_action_list set action_name='Backward1.hts',action_name_ch='后退',action_name_en='Backward' where action_index=1;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Forward1.hts',action_name_ch='前进',action_name_en='Forward' where action_index=2;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Leftward1.hts',action_name_ch='左移',action_name_en='Leftward' where action_index=3;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Rightward1.hts',action_name_ch='右移',action_name_en='Rightward' where action_index=4;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='TurnLeft1.hts',action_name_ch='左转',action_name_en='TurnLeft' where action_index=5;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='TurnRight1.hts',action_name_ch='右转',action_name_en='TurnRight' where action_index=6;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Shoot left1.hts',action_name_ch='左脚射门',action_name_en='Shoot left' where action_index=7;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Shoot right1.hts',action_name_ch='右脚射门',action_name_en='Shoot right' where action_index=8;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Left slide tackle1.hts',action_name_ch='左脚铲球',action_name_en='Left slide tackle' where action_index=9;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Right slide tackle1.hts',action_name_ch='右脚铲球',action_name_en='Right slide tackle' where action_index=10;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall forward rise1.hts',action_name_ch='前倒起身',action_name_en='Fall forward rise' where action_index=11;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Fall backward rise1.hts',action_name_ch='后倒起身',action_name_en='Fall backward rise' where action_index=12;";
        db.execSQL(updateSQL);

        //更新格斗家
        updateSQL = "update remote_action_list set action_name='Fall forward rise1.hts',action_name_ch='前倒起身',action_name_en='Fall forward rise',action_image_name='gamepad_settings_item_down_up_front_g.png' where action_index=21;";
        db.execSQL(updateSQL);
        updateSQL = "update remote_action_list set action_name='Fall backward rise1.hts',action_name_ch='后倒起身',action_name_en='Fall backward rise',action_image_name='gamepad_settings_item_pour_up_g.png' where action_index=22;";
        db.execSQL(updateSQL);

        //update db version
        updateSQL = "update db_version set version= 4;";
        db.execSQL(updateSQL);
    }

    public void updateDefaulteDataFrom4TO5(SQLiteDatabase db){

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

        //增加置顶字段
        String updateTableSQL = "alter table actions_online_cache_logs add actionHot INT;";
        db.execSQL(updateTableSQL);

        //update db version
        String updateSQL = "update db_version set version= 5;";
        db.execSQL(updateSQL);
    }

    //2017/9/30临时添加
    public void updateDefaulteDataFrom5TO6(SQLiteDatabase db){

        //更新足球员
        String updateSQL = "update remote_action_list set action_name='Backward2.hts',action_name_ch='后退',action_name_en='Backward' where action_index=1;";
        db.execSQL(updateSQL);

        updateSQL = "update remote_action_list set action_name='Forward2.hts',action_name_ch='前进',action_name_en='Forward' where action_index=2;";
        db.execSQL(updateSQL);



        //update db version
        updateSQL = "update db_version set version= 6;";
        db.execSQL(updateSQL);
    }

}
