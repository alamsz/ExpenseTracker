package com.alamsz.inc.expensetracker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	public static final String DB_NAME = "financeHelperDB.db";
	public static final String FINANCEHELPER_TABLE = "finance_helper";
	public static final String LOOKUP_TABLE = "lookup";
	public static final String DATE_INPUT = "date_input";
	public static final String DESCRIPTION = "description";
	public static final String AMOUNT = "amount";
	public static final String TYPE = "type";
	public static final String CATEGORY = "category";
	public static final String ID = "_id";
	public static final String TABLE_TYPE = "table_type";
	public static final String TABLE_CODE = "table_code";
	public static final String EN_DESC = "en_desc";
	public static final String LOC_DESC = "loc_desc";
	public static final String STATUS = "status";
	
	private static final int DB_VERSION = 1;

	private static final String LOOKUP_CREATE_STATEMENT = "CREATE TABLE "
			+ LOOKUP_TABLE + "( " + TABLE_TYPE
			+ " text , " + TABLE_CODE
			+ " text , " + EN_DESC + " text not null, " + LOC_DESC
			+ " text not null, " + STATUS + " text not null, PRIMARY KEY( "+ TABLE_TYPE+","+TABLE_CODE+" ))";
	
	
	private static final String DB_CREATE_STATEMENT = "CREATE TABLE "
		+ FINANCEHELPER_TABLE + "( " + ID
		+ " integer primary key autoincrement, " + DATE_INPUT
		+ " long not null, " + DESCRIPTION + " text, " + AMOUNT
		+ " integer not null, " + TYPE + " text not null, "+ CATEGORY + " text not null)";
	
	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

		
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {

		arg0.execSQL(DB_CREATE_STATEMENT);
		arg0.execSQL(LOOKUP_CREATE_STATEMENT);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + FINANCEHELPER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + LOOKUP_TABLE);
		onCreate(db);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
