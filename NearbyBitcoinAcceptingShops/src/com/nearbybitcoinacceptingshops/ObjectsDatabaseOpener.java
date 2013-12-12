package com.nearbybitcoinacceptingshops;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ObjectsDatabaseOpener extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "com.nearbybitcoinacceptingshops";
	private static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME = "hashes";
	private static final String DICTIONARY_TABLE_CREATE = "CREATE TABLE "
			+ TABLE_NAME + " (" + "hash int " + ");";

	ObjectsDatabaseOpener(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DICTIONARY_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
