package com.lee.dao;

import com.lee.model.DBUtiles.toolutil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlarmDao {
	private DBOpenHelper alarmDB;
	private SQLiteDatabase db;
	private Context context;
	public SQLiteDatabase getDatabase() {
		return db;
	}

	public AlarmDao(Context context) {
		
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public void  openDB() {
		db=alarmDB.getWritableDatabase();
	}
	
	/**
	 * 
	 * @param hour
	 * @param minute
	 * @param weeklast
	 * @return  返回ID
	 */
	public int  findById(int hour,int minute,String weeklast) {
		Cursor cur=db.query(toolutil.tablename, new String[]{toolutil.id}, toolutil.hour + "=" +hour +" and"
			     + toolutil.minute +" =" + minute+" and " +toolutil.week +" = " +weeklast, null, null, null, null);
		if(cur!=null)
		   {
		    cur.moveToFirst();
		    int id=cur.getInt(cur.getColumnIndex(toolutil.id));
		    cur.close();
		    return id;
		   }
		return -1;
	}
	/**
	 * 根据ID来更新闹钟设置
	 * @param id
	 * @param hour
	 * @param minute
	 * @param weeklast
	 * @param music
	 * @param lasttime
	 * @return 
	 */
	public boolean updaptData(int id,int hour,int minute,String weeklast,String music,int lasttime )
	  {
	     ContentValues value=new ContentValues();
	  value.put(toolutil.hour,hour);
	  value.put(toolutil.minute,minute);
	  value.put(toolutil.week,weeklast);
	  value.put(toolutil.music,music);
	  value.put(toolutil.lasttime,lasttime);
	  //value.put(toolutil.state,state);
	   return db.update(toolutil.tablename, value, toolutil.id +" =" +id, null)>0;
	  }
	/**
	 * 根据ID更新状态
	 * @param id
	 * @param state
	 * @return
	 */
	public boolean updatastate(int id,int state)
	  {
	   ContentValues value=new ContentValues();
	   value.put(toolutil.state, state);
	   return db.update(toolutil.tablename, value, toolutil.id+ " ="+id, null)>0;
	  }
	
	public void insertData(int hour,int minute,String weeklast,String music,int lasttime ,int state)
	  {
	     ContentValues value=new ContentValues();
	    value.put(toolutil.hour,hour);
	    value.put(toolutil.minute,minute);
	    value.put(toolutil.week,weeklast);
	    value.put(toolutil.music,music);
	    value.put(toolutil.lasttime,lasttime);
	    value.put(toolutil.state,state);
	    db.insert(toolutil.tablename, toolutil.music,value);//此处的红色标记是不用手动插入的字段而是系统自动分配的部分。如果你的id是自己定的这里可以设为空
	  }
	  public void deletedata(int id)
	  {
	   db.delete(toolutil.tablename,toolutil.id+"="+id , null);
	  }
	  public Cursor findcur(int id)
	  {
	   Cursor cur=db.query(toolutil.tablename, null, toolutil.id+ " ="+id, null, null, null, null);
	   return cur;
	   
	  }
	
	
	public void relese() {
		alarmDB.close();
		db.close();
		
	}
	
}
