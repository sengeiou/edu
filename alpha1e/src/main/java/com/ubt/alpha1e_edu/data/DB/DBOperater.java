package com.ubt.alpha1e_edu.data.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOperater extends SQLiteOpenHelper {

	private String mDbCreateStr = "";

	public static int DB_VERSION = 2;

	public DBOperater(Context context, String db_name, String db_create_str) {
		super(context, db_name, null, DB_VERSION);
		mDbCreateStr = db_create_str;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		if (mDbCreateStr != null && !mDbCreateStr.equals("")) {
			arg0.execSQL(mDbCreateStr);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
