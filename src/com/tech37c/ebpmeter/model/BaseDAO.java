package com.tech37c.ebpmeter.model;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

@SuppressLint("CommitPrefEdits")
public class BaseDAO extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ebpMeter.db";
	private static final int DATABASE_VERSION = 1;
	public static final String BP_RECORD = "BP_RECORD";
	public static final String ID = BaseColumns._ID;
	public static final String Dev_Type = "Dev_Type";
	public static final String Dev_ID = "Dev_ID";
	public static final String User_ID = "User_ID";
	public static final String Measure_Time = "Measure_Time";
	public static final String HBP = "HBP";
	public static final String LBP = "LBP";
	public static final String Beat = "Beat";
	public static final String Create_Time = "Create_Time";
	

	public BaseDAO(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + BP_RECORD + " (" + 
	                  ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				      Dev_Type     + " INTEGER , " + 
	                  Dev_ID       + " INTEGER," + 
	                  User_ID      + " INTEGER," +
	                  Measure_Time + " DATETIME," +
	                  HBP          + " INTEGER," +
	                  LBP          + " INTEGER," +
	                  Beat         + " INTEGER," +
	                  Create_Time  + " DATETIME" + ");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + BP_RECORD);
		onCreate(db);
	}

	public long insert(String Dev_Type, String Dev_ID, String User_ID, String Measure_Time, 
			           String HBP, String LBP, String Beat) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("Dev_Type", Dev_Type);
		values.put("Dev_ID", Dev_ID);
		values.put("User_ID", User_ID);
		values.put("Measure_Time", Measure_Time);
		values.put("HBP", HBP);
		values.put("LBP", LBP);
		values.put("Beat", Beat);

		return db.insertOrThrow(BP_RECORD, null, values);
	}


	public long count() {
		SQLiteDatabase db = getReadableDatabase();
		return DatabaseUtils.queryNumEntries(db, BP_RECORD);
	}

	public Cursor all(Activity activity) {
		String[] from = { ID, Dev_Type, Dev_ID, User_ID, Measure_Time,
				         HBP, LBP, Beat, Create_Time};
		String order = ID;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BP_RECORD, from, null, null, null, null,
				order);
//		activity.startManagingCursor(cursor);

		return cursor;
	}
	
	public Cursor all() {
		String[] from = { ID, Dev_Type, Dev_ID, User_ID, Measure_Time,
		         HBP, LBP, Beat, Create_Time};
		String order = ID;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(BP_RECORD, from, null, null, null, null,
				order);
		return cursor;
	}

	@Override
	public synchronized void close() {
		super.close();
		SQLiteDatabase db = getReadableDatabase();
		if (db != null) {
			db.close();
		}
	}
}