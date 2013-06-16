package com.alamsz.inc.expensetracker.dao;

import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	public static final String DB_NAME = "financeHelperDB.db";
	public static final String locale_US = Locale.US.toString();
	public static final String locale_UK = Locale.UK.toString();
	public static final String locale_indonesia = "in_ID";
	private static final int DB_VERSION = 6;
	//Configuration table
	public static final String CONFIGURATION_CREATE_STATEMENT = "CREATE TABLE "
			+ ConfigurationDAO.CONFIGURATION_TABLE
			+ "( "
			+ ConfigurationDAO.TABLE_TYPE
			+ " text , "
			+ ConfigurationDAO.TABLE_CODE
			+ " text , "
			+ ConfigurationDAO.LOC_DESC
			+ " text not null, "
			+ ConfigurationDAO.STATUS
			+ " text not null, PRIMARY KEY( "
			+ ConfigurationDAO.TABLE_TYPE
			+ ","
			+ ConfigurationDAO.TABLE_CODE
			+ " ))";
   //Description of configuration in many languages
	private static final String CONFIG_DESC_CREATE_STATEMENT = "CREATE TABLE "
	+ ConfigurationDAO.CONFIG_DESC_TABLE + "( " + ConfigurationDAO.TABLE_TYPE + " text , " + ConfigurationDAO.TABLE_CODE
	+ " text , " + ConfigurationDAO.DESC_LOCALE + " text not null, " + ConfigurationDAO.DESC
	+ " text not null, PRIMARY KEY( " + ConfigurationDAO.TABLE_TYPE + "," + ConfigurationDAO.TABLE_CODE
	+ ", " + ConfigurationDAO.DESC_LOCALE + " ))";

	//create the main table of expense tracker
	private static final String EXPENSE_TRACKER_CREATE_STATEMENT = "CREATE TABLE "
		+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
		+ "( "
		+ ExpenseTrackerDAO.ID
		+ " integer primary key autoincrement, "
		+ ExpenseTrackerDAO.DATE_INPUT
		+ " long not null, "
		+ ExpenseTrackerDAO.DESCRIPTION
		+ " text, "
		+ ExpenseTrackerDAO.AMOUNT
		+ " integer not null, "
		+ ExpenseTrackerDAO.TYPE
		+ " text not null, "
		+ ExpenseTrackerDAO.FUND_SOURCE
		+ " text not null)";
	// create transactio category table one to one with expense tracker
	private static final String TRANSACTION_CATEGORY_CREATE_STATEMENT = "CREATE TABLE "
		+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
		+ "( "
		+ ExpenseTrackerDAO.ID
		+ " integer primary key, "
		+ ExpenseTrackerDAO.TRANSACTION_CATEGORY
		+ " text not null)";
	
	private static final String EXPENSE_BUDGET_CREATE_STATEMENT = "CREATE TABLE "
			+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
			+ "( "
			+ ConfigurationDAO.TABLE_TYPE
			+ " text , "
			+ ConfigurationDAO.TABLE_CODE
			+ " text , "
			+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT
			+ " long not null,  PRIMARY KEY( "
			+ ConfigurationDAO.TABLE_TYPE
			+ ","
			+ ConfigurationDAO.TABLE_CODE
			+ " ))";

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create all tables
		//tables related with transaction
		db.execSQL(EXPENSE_TRACKER_CREATE_STATEMENT);
		db.execSQL(TRANSACTION_CATEGORY_CREATE_STATEMENT);
		// configuration table
		db.execSQL(CONFIGURATION_CREATE_STATEMENT);
		db.execSQL(CONFIG_DESC_CREATE_STATEMENT);
		db.execSQL(EXPENSE_BUDGET_CREATE_STATEMENT);
		insertDefaultCategoryConfiguration(db);

	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", Adjustment to existing data will be made");
		// backup the old table first before the data restore inside new table
		String upgrade = "ALTER TABLE "+ExpenseTrackerDAO.EXPENSETRACKER_TABLE+" RENAME TO EXPENSE_TABLE_TEMP";
		db.execSQL(upgrade);
		// ensure that the table created
		Cursor cr = db
				.rawQuery("select count(*) from EXPENSE_TABLE_TEMP", null);
		cr.moveToFirst();
		Log.d("upgrade_old_table", String.valueOf(cr.getInt(0)));
		cr.close();

		// drop all table first
		db.execSQL("DROP TABLE IF EXISTS "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "
				+ ConfigurationDAO.CONFIGURATION_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ConfigurationDAO.CONFIG_DESC_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE);

		onCreate(db);
		// insert value to the expense tracker table and expense category
		db.execSQL("INSERT INTO "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
				+ " select _id,date_input, description, amount, type, category from EXPENSE_TABLE_TEMP");
		// set existing transaction category into other
		db.execSQL("INSERT INTO "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
				+ " select _id,'OTH' from EXPENSE_TABLE_TEMP");

		// check expense tracker table
		cr = db.rawQuery("select count(*) from "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE, null);
		cr.moveToFirst();
		Log.d("upgrade_new_table", String.valueOf(cr.getInt(0)));
		cr.close();
		// check expense category table
		cr = db.rawQuery("select count(*) from "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, null);
		cr.moveToFirst();
		Log.d("check joined table", cr.getString(0));
		Log.d("check joined table", String.valueOf(cr.getCount()));
		cr.close();
		// delete the temporary table
		db.execSQL("DROP TABLE IF EXISTS EXPENSE_TABLE_TEMP");
		Log.i("INFO", "UPgrade process finished");

	}
	
	private void insertDefaultCategoryConfiguration(SQLiteDatabase db){
		String[] tableCodeExp = { "HOM", "REN", "TAX", "UTI", "ELE", "WAT",
				"PHO", "FOD", "CHL", "BST", "HEA", "INS", "FIT", "TRN", "CAR",
				"GAS", "REP", "LOA", "ENT", "CLO", "INV", "OTH" };
		String[] tableCodeInc = { "SAL", "REN", "INV", "OTH" };

		String[] expCatEnglish = {  "Home", "Rent", "Tax", "Utility",
				"Electricity", "Water", "Telephone", "Food", "Child Fund",
				"BabySitter/Servant", "Health", "Insurance", "Sport/Fitness",
				"Transportation", "Vehicle lease", "Gas", "Service", "Loan",
				"Entertainment", "Clothing", "Investment", "Other" };
		String[] incCatEnglish = { "Salary", "Rent Income",
				"Investment Income", "Other"};
		
		String[] expCatIndonesian = { "Rumah", "Biaya Sewa", "Pajak",
				"Peralatan", "Listrik", "Air", "Telepon", "Makanan",
				"Dana Anak", "Babysitter/Pembantu", "Kesehatan", "Asuransi",
				"Fitness", "Transportasi", "Cicilan Kendaraan", "Bensin",
				"Servis", "Pinjaman", "Hiburan", "Pakaian", "Investasi",
				"Lain - Lain" };
		String[] incCatIndonesian = { "Gaji", "Pendapatan Sewa",
				"Pendapatan Investasi", "Lain - Lain" };
		// set expense category
		
		
		for (int i = 0; i < incCatIndonesian.length; i++) {
			// set income category
			String incomeconfig = "INSERT INTO "
					+ ConfigurationDAO.CONFIGURATION_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.LOC_DESC + ", " + ConfigurationDAO.STATUS
					+ " ) " + "VALUES ( 'INC_CAT', '"+tableCodeInc[i]+"', '"+incCatIndonesian[i]+"', 1 )";
			
			// set default fund source

			// set config description for internationalisation
			String incomeconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + "," + ConfigurationDAO.DESC
					+ " ) " + "VALUES ( 'INC_CAT', '"+tableCodeInc[i]+"', '" + locale_US
					+ "','"+incCatEnglish[i]+"' )";
			

			// set config description for internationalisation
			String incomeconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + "," + ConfigurationDAO.DESC
					+ " ) " + "VALUES ( 'INC_CAT', '"+tableCodeInc[i]+"', '" + locale_UK
					+ "','"+incCatEnglish[i]+"' )";
			db.execSQL(incomeconfig);
			db.execSQL(incomeconfigDescUS);
			db.execSQL(incomeconfigDescUK);
			
		}
		for (int i = 0; i < expCatIndonesian.length; i++) {
			String expCatConfig = "INSERT INTO "
					+ ConfigurationDAO.CONFIGURATION_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.LOC_DESC + ", "
					+ ConfigurationDAO.STATUS + " ) "
					+ "VALUES ( 'EXP_CAT','"+tableCodeExp[i]+"', '"+expCatIndonesian[i]+"', 1 )";

			// set default fund source

			// set config description for internationalisation
			String expCatconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) "
					+ "VALUES ( 'EXP_CAT', '"+tableCodeExp[i]+"', '" + locale_US
					+ "', '"+expCatEnglish[i]+"' )";

			// set config description for internationalisation
			String expCatconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) "
					+"VALUES ( 'EXP_CAT', '"+tableCodeExp[i]+"', '" + locale_UK
					+ "', '"+expCatEnglish[i]+"' )";
			db.execSQL(expCatConfig);
			db.execSQL(expCatconfigDescUS);
			db.execSQL(expCatconfigDescUK);

		}

	

		
		
	
	}

	

}
