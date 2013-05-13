package com.lee.dao;

import com.lee.model.DBUtiles.toolutil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;// 定义数据库版本号
	private static final String DBNAME = "alarmclock.db";// 定义数据库名
	
	public DBOpenHelper(Context context) {
		
		super(context, DBNAME, null, VERSION);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createtable="create table " + toolutil.tablename + " (" +toolutil.id + " INTEGER PRIMARY KEY " +
				", "+ toolutil.hour +" INTEGER ," + toolutil.minute +" INTEGER ," + toolutil.model + " INTEGER ," + toolutil.week + " TEXT , " +toolutil.state
				 +" INTEGER ," + toolutil.music + " TEXT " + ")";
		db.execSQL(createtable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	

}
