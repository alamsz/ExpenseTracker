package com.alamsz.inc.expensetracker.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	public static final String DB_NAME = "financeHelperDB.db";

	// Finance Helper
	public static final String FINANCEHELPER_TABLE = "finance_helper";
	public static final String DATE_INPUT = "date_input";
	public static final String DESCRIPTION = "description";
	public static final String AMOUNT = "amount";
	public static final String TYPE = "type";
	public static final String CATEGORY = "category";
	public static final String SOURCE = "source";
	public static final String ID = "_id";
	// Configuration
	public static final String CONFIGURATION_TABLE = "config";
	public static final String CONFIG_DESC_TABLE = "config_desc";
	public static final String TABLE_TYPE = "table_type";
	public static final String TABLE_CODE = "table_code";
	public static final String LOC_DESC = "loc_desc";
	public static final String STATUS = "status";
	public static final String DESC_LOCALE = "locale";
	public static final String DESC = "desc";
	// Expense category budget
	public static final String EXPENSE_CATEGORY_BUDGET_TABLE = "expense_budget";
	public static final String BUDGET_AMOUNT = "budget_amount";

	private static final int DB_VERSION = 1;

	private static final String CONFIGURATION_CREATE_STATEMENT = "CREATE TABLE "
			+ CONFIGURATION_TABLE
			+ "( "
			+ TABLE_TYPE
			+ " text , "
			+ TABLE_CODE
			+ " text , "
			+ LOC_DESC
			+ " text not null, "
			+ STATUS
			+ " text not null, PRIMARY KEY( "
			+ TABLE_TYPE
			+ ","
			+ TABLE_CODE
			+ " ))";

	private static final String FINANCE_HELPER_CREATE_STATEMENT = "CREATE TABLE "
			+ FINANCEHELPER_TABLE
			+ "( "
			+ ID
			+ " integer primary key autoincrement, "
			+ DATE_INPUT
			+ " long not null, "
			+ DESCRIPTION
			+ " text, "
			+ AMOUNT
			+ " integer not null, "
			+ TYPE
			+ " text not null, "
			+ CATEGORY
			+ " text not null)";
	//, " + SOURCE + " text not null

	private static final String CONFIG_DESC_CREATE_STATEMENT = "CREATE TABLE "
			+ CONFIG_DESC_TABLE + "( " + TABLE_TYPE + " text , " + TABLE_CODE
			+ " text , " + DESC_LOCALE + " text not null, " + DESC
			+ " text not null, PRIMARY KEY( " + TABLE_TYPE + "," + TABLE_CODE
			+ ", " + DESC_LOCALE + " ))";

	private static final String EXPENSE_BUDGET_CREATE_STATEMENT = "CREATE TABLE "
			+ EXPENSE_CATEGORY_BUDGET_TABLE
			+ "( "
			+ TABLE_TYPE
			+ " text , "
			+ TABLE_CODE
			+ " text , "
			+ BUDGET_AMOUNT
			+ " long not null,  PRIMARY KEY( "
			+ TABLE_TYPE
			+ ","
			+ TABLE_CODE
			+ " ))";

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create all tables
		db.execSQL(FINANCE_HELPER_CREATE_STATEMENT);
		/*db.execSQL(CONFIGURATION_CREATE_STATEMENT);
		db.execSQL(CONFIG_DESC_CREATE_STATEMENT);
		db.execSQL(EXPENSE_BUDGET_CREATE_STATEMENT);*/
		//insertDefaultCategoryConfiguration(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		/*if(oldVersion <= 4){
			String upgrade = "ALTER TABLE "+FINANCEHELPER_TABLE+ " RENAME TO EXPENSE_TABLE_TEMP ADD COLUMN EXPENS_CAT";
			db.execSQL(upgrade);
		}*/
		db.execSQL("DROP TABLE IF EXISTS " + FINANCEHELPER_TABLE);
		// db.execSQL("DROP TABLE IF EXISTS " + LOOKUP_TABLE);
		onCreate(db);
		
		db.execSQL("DROP TABLE IF EXISTS " + FINANCEHELPER_TABLE);
		/*db.execSQL("DROP TABLE IF EXISTS " + CONFIGURATION_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + CONFIG_DESC_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_CATEGORY_BUDGET_TABLE);*/

	}
	
	public void insertDefaultCategoryConfiguration(SQLiteDatabase db){
		/*String config1 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( 'EXP_CAT', 'FUEL', 'Bensin', 1 )";
		String config2 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( 'EXP_CAT', 'FOOD', 'Makanan', 1 )";
		String config3 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( 'EXP_CAT', 'BILL', 'Tagihan', 1 )";*/
		
		//set config description for internationalization
		/*String configDesc1 = "INSERT INTO " +CONFIG_DESC_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+DESC_LOCALE+","+DESC+" ) " +
	    "VALUES ( 'EXP_CAT', 'FUEL', 'en','Fuel' )";
		String configDesc2 = "INSERT INTO " +CONFIG_DESC_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+DESC_LOCALE+","+DESC+" ) " +
	    "VALUES ( 'EXP_CAT', 'FOOD', 'en', 'Food' )";
		String configDesc3 = "INSERT INTO " +CONFIG_DESC_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+",, "+DESC_LOCALE+","+DESC+" ) " +
	    "VALUES ( 'EXP_CAT', 'BILL', 'en', 'Billin' )";
	*/
		
		//set locale to choose
		/*String configLocale1 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( 'DESC_LOCALE', 'en', 'English', 1 )";*/
		/*String configLocale2 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( DESC_LOCALE', 'en', 'English', 1)";
		String configLocale3 = "INSERT INTO " +CONFIGURATION_TABLE+ " ( "+TABLE_TYPE+
		", "+TABLE_CODE+", "+LOC_DESC+", "+STATUS+" ) " +
	    "VALUES ( DESC_LOCALE', 'en', 'English', 1)";*/
		/*db.execSQL(config1);
		db.execSQL(config2);
		db.execSQL(config3);
		db.execSQL(configDesc1);
		db.execSQL(configDesc2);
		db.execSQL(configDesc3);
		db.execSQL(configLocale1);*/
		
		/*db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		*/
		// set the description for other locale
		/*db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);
		db.insert(CONFIGURATION_TABLE,  null,null);*/
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
