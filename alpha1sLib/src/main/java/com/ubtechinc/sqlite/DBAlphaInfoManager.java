package com.ubtechinc.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ubtechinc.base.AlphaInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存在数据库中已经连接过的机器人信息
 * @author chenlin
 *
 */
public class DBAlphaInfoManager {
	private UBXDataBaseHelper mHelper;
	private SQLiteDatabase mDb;
	
	public DBAlphaInfoManager(Context context){
		mHelper = new UBXDataBaseHelper(context);
		
		mDb = mHelper.getWritableDatabase();

		//deleteAllData();
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDataBase(){
		mDb.close();
	}
	
	public boolean isExist(AlphaInfo info){
		boolean bRet = false;
		
		try{
			String existSql = "select * from AlphaConnectRecord where macaddr=?";
			Cursor c = mDb.rawQuery(existSql, new String[]{info.getMacAddr()});  
		if (c.moveToNext()){
			bRet = true;
		}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return bRet;
	}
	
	public void updateDeviceName(String mac, String blueName, String newName){
		ContentValues values = new ContentValues();
		
		values.put("name", newName);
		values.put("bluetooth", blueName);
		values.put("macaddr", mac);
		values.put("connectTime", System.currentTimeMillis());

		String[] args = {mac};  
		mDb.update("AlphaConnectRecord", values, "macaddr=?", args);
		
		return;
	}
	
	public void deleteAllData(){
		String existSql = "delete from AlphaConnectRecord";
		
		mDb.beginTransaction();
		
		mDb.execSQL(existSql);
		try{
			mDb.setTransactionSuccessful();
		}finally{
			mDb.endTransaction();
		}
	}
	
	public void deleteDevice(String mac){
//		String existSql = "delete from AlphaConnectRecord where macaddr=?";
//		
//		mDb.beginTransaction();
//		
//		mDb.execSQL(existSql, new Object[]{mac});
//		try{
//			mDb.setTransactionSuccessful();
//		}finally{
//			mDb.endTransaction();
//		}
		
		mDb.delete("AlphaConnectRecord", "macaddr=?", new String[]{mac});
	}
	
	/**
	 * 添加到信息到数据库
	 * @param info
	 * @return
	 */
	public boolean addAlphaInfo(AlphaInfo info){
		boolean bRet = true;
		
		//deleteDevice(info.getMacAddr());
		if (info.getMacAddr() == null || info.getMacAddr().equals(""))
			return false;
		
		if (isExist(info)){
			//deleteDevice(info.getMacAddr());
			//return bRet;
			//(String mac, String blueName, String newName)
			updateDeviceName(info.getMacAddr(), info.getBlueToothName(), info.getName());
			
			return bRet;
		}
		
		mDb.beginTransaction();
		
		mDb.execSQL("INSERT INTO AlphaConnectRecord VALUES(null, ?, ?, ?, ?)", new Object[]{info.getName(), info.getBlueToothName(), info.getMacAddr(),System.currentTimeMillis()});
		try {
			mDb.setTransactionSuccessful();
		}finally{
			mDb.endTransaction();
		}
		
		return bRet;
	}
	
	/**
	 * 获取所有信息
	 * @return
	 */
	public List<AlphaInfo> getAllInfo(){
		
		ArrayList<AlphaInfo> listInfo = new ArrayList<AlphaInfo>();
		
		Cursor c = null;
		try{
			c = queryTheCursor();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		while(c.moveToNext()){
			AlphaInfo info = new AlphaInfo();
			
			String name = c.getString(c.getColumnIndex("name"));
			String blueToothName = c.getString(c.getColumnIndex("bluetooth"));
			String macAddr = c.getString(c.getColumnIndex("macaddr"));
			
			info.setName(name);
			info.setBlueToothName(blueToothName);
			info.setMacAddr(macAddr);
			
			listInfo.add(info);
		}
		
		c.close();
		
		return listInfo;
	}
	
	/** 
     * query all persons, return cursor 
     * @return  Cursor 
     */  
    public Cursor queryTheCursor() {  
        Cursor c = mDb.rawQuery("SELECT * FROM AlphaConnectRecord order by connectTime desc limit 0,10", null);
        return c;  
    } 
}
