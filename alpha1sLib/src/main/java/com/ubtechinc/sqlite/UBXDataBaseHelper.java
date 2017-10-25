package com.ubtechinc.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLITE 数据库实现类
 * @author chenlin
 *
 */
public class UBXDataBaseHelper extends SQLiteOpenHelper{
	
	/**
	 * 数据库的版本号
	 */
	private static final int DATA_BASE_VERSION = 1;
	private static final String DB_NAME="alpha1_db";
  
    public UBXDataBaseHelper(Context context){
        super(context,DB_NAME,null, DATA_BASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		//db.execSQL("create table AlphaInfo(name varchar(30), bluetooth varthcar(20), macaddr varchar(17)");
		db.execSQL("CREATE TABLE IF NOT EXISTS AlphaConnectRecord" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, bluetooth VARCHAR, macaddr VARCHAR,connectTime INT64)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion == 1 && newVersion == 2){
			//数据库版本，从1升级到2,数据升级的时候，onCreate方法没有执行
		}
	}

}
