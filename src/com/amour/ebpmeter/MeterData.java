package com.amour.ebpmeter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MeterData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ebpMeter.db";
	private static final int DATABASE_VERSION = 1;

	public static final String DEVICE_TABLE = "device";

	public static final String USER_TABLE = "user";
	// public static final String USER_ID = BaseColumns._ID;
	public static final String NAME = "name";

	public static final String RECORD_TABLE = "record";
	public static final String RECORD_ID = BaseColumns._ID;
	public static final String USER_ID = "user_id";
	public static final String CHECKING_TIME = "checking_time";
	public static final String HIGH_VALUE = "high_value";
	public static final String LOW_VALUE = "low_value";
	public static final String HEART_BEAT = "heart_beat";

	public MeterData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + RECORD_TABLE + " (" + RECORD_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_ID
				+ " TEXT NOT NULL, " + CHECKING_TIME + " TEXT NOT NULL,"
				+ HIGH_VALUE + " INTEGER," + LOW_VALUE + " INTEGER,"
				+ HEART_BEAT + " INTEGER" + ");";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + RECORD_TABLE);
		onCreate(db);
	}

	public void insert(String userId, String checkingTime, int highValue,
			int lowValue, int heartBeat) {
		SQLiteDatabase db = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USER_ID, userId);
		values.put(CHECKING_TIME, checkingTime);
		values.put(HIGH_VALUE, highValue);
		values.put(LOW_VALUE, lowValue);
		values.put(HEART_BEAT, heartBeat);

		db.insertOrThrow(RECORD_TABLE, null, values);
	}

	public long count() {
		SQLiteDatabase db = getReadableDatabase();
		return DatabaseUtils.queryNumEntries(db, RECORD_TABLE);
	}

	public Cursor all(Activity activity) {
		String[] from = { RECORD_ID, USER_ID, CHECKING_TIME, HIGH_VALUE,
				LOW_VALUE, HEART_BEAT };
		String order = CHECKING_TIME;

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(RECORD_TABLE, from, null, null, null, null,
				order);
		activity.startManagingCursor(cursor);

		return cursor;
	}

}