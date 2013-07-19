package com.alamsz.inc.expensetracker.dao;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	public static final String DB_NAME = "financeHelperDB.db";
	public static final String locale_US = Locale.US.toString();
	public static final String locale_UK = Locale.UK.toString();
	public static final String locale_indonesia = "in_ID";
	public static final String[] persistentConfig = {"FND_SRCC","INC_CATREC","INC_CATTRF","EXP_CATTRF","EXP_CATPAY"};
	private static final int DB_VERSION = 8;
	// Configuration table
	public static final String CONFIGURATION_CREATE_STATEMENT = "CREATE TABLE "
			+ ConfigurationDAO.CONFIGURATION_TABLE + "( "
			+ ConfigurationDAO.TABLE_TYPE + " text , "
			+ ConfigurationDAO.TABLE_CODE + " text , "
			+ ConfigurationDAO.LOC_DESC + " text not null, "
			+ ConfigurationDAO.STATUS + " text not null, PRIMARY KEY( "
			+ ConfigurationDAO.TABLE_TYPE + "," + ConfigurationDAO.TABLE_CODE
			+ " ))";
	// Description of configuration in many languages
	private static final String CONFIG_DESC_CREATE_STATEMENT = "CREATE TABLE "
			+ ConfigurationDAO.CONFIG_DESC_TABLE + "( "
			+ ConfigurationDAO.TABLE_TYPE + " text , "
			+ ConfigurationDAO.TABLE_CODE + " text , "
			+ ConfigurationDAO.DESC_LOCALE + " text not null, "
			+ ConfigurationDAO.DESC + " text not null, PRIMARY KEY( "
			+ ConfigurationDAO.TABLE_TYPE + "," + ConfigurationDAO.TABLE_CODE
			+ ", " + ConfigurationDAO.DESC_LOCALE + " ))";

	// create the main table of expense tracker
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
			+ " text not null, "
			+ ExpenseTrackerDAO.WEEK_IN_MONTH
			+ " integer not null, "
			+ ExpenseTrackerDAO.WEEK_IN_YEAR
			+ " integer not null, "
			+ ExpenseTrackerDAO.MONTH
			+ " integer not null, "
			+ ExpenseTrackerDAO.YEAR + " integer not null)";
	// create transactio category table one to one with expense tracker
	private static final String TRANSACTION_CATEGORY_CREATE_STATEMENT = "CREATE TABLE "
			+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
			+ "( "
			+ ExpenseTrackerDAO.ID
			+ " integer primary key, "
			+ ExpenseTrackerDAO.TRANSACTION_CATEGORY + " text not null)";

	private static final String EXPENSE_BUDGET_CREATE_STATEMENT = "CREATE TABLE "
			+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
			+ "( "
			+ ConfigurationDAO.TABLE_TYPE
			+ " text , "
			+ ConfigurationDAO.TABLE_CODE
			+ " text , "
			+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_WEEKLY
			+ " integer , "
			+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_MONTHLY
			+ " integer ,  PRIMARY KEY( "
			+ ConfigurationDAO.TABLE_TYPE
			+ ","
			+ ConfigurationDAO.TABLE_CODE + " ))";

	// create the main table of expense tracker
	private static final String PAYABLE_RECEIVABLE_MASTER_CREATE_STATEMENT = "CREATE TABLE "
			+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE
			+ "( "
			+ PayRecMaster.ID
			+ " integer primary key autoincrement, "
			+ PayRecMaster.DATE_INPUT
			+ " long not null, "
			+ PayRecMaster.DESCRIPTION
			+ " text, "
			+ PayRecMaster.AMOUNT
			+ " integer not null, "
			+ PayRecMaster.TYPE
			+ " text not null, "
			+ PayRecMaster.DUE_DATE
			+ " long, "
			+ PayRecMaster.AMOUNT_PAID
			+ " integer not null, "
			+ PayRecMaster.MARK_COMPLETE
			+ " integer not null, "
			+ PayRecMaster.WEEK_IN_MONTH
			+ " integer not null, "
			+ PayRecMaster.WEEK_IN_YEAR
			+ " integer not null, "
			+ PayRecMaster.MONTH
			+ " integer not null, "
			+ PayRecMaster.YEAR
			+ " integer not null)";

	private static final String PAYABLE_RECEIVABLE_DETAIL_CREATE_STATEMENT = "CREATE TABLE "
			+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE
			+ "( "
			+ PayRecMaster.ID
			+ " integer primary key autoincrement, "
			+ PayRecMaster.ID_MASTER
			+ " integer not null, "
			+ PayRecMaster.ID_TRANS
			+ " integer , "
			+ PayRecMaster.DATE_INPUT
			+ " long not null, "
			+ PayRecMaster.DESCRIPTION
			+ " text, "
			+ PayRecMaster.AMOUNT
			+ " integer not null, "
			+ PayRecMaster.TYPE
			+ " text not null, "
			+ PayRecMaster.WEEK_IN_MONTH
			+ " integer not null, "
			+ PayRecMaster.WEEK_IN_YEAR
			+ " integer not null, "
			+ PayRecMaster.MONTH
			+ " integer not null, "
			+ PayRecMaster.YEAR
			+ " integer not null)";

	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// when user first install the item
		// create all tables
		// tables related with transaction
		db.execSQL(EXPENSE_TRACKER_CREATE_STATEMENT);
		db.execSQL(TRANSACTION_CATEGORY_CREATE_STATEMENT);
		// configuration table
		db.execSQL(CONFIGURATION_CREATE_STATEMENT);
		db.execSQL(CONFIG_DESC_CREATE_STATEMENT);
		db.execSQL(EXPENSE_BUDGET_CREATE_STATEMENT);

		// payable and receivable
		db.execSQL(PAYABLE_RECEIVABLE_MASTER_CREATE_STATEMENT);
		db.execSQL(PAYABLE_RECEIVABLE_DETAIL_CREATE_STATEMENT);

		// insert the default configuration
		insertDefaultFundSource(db, true);
		insertPersistentCategoryConfiguration(db, true);
		insertDefaultCategoryConfiguration(db, true);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		Log.w(DatabaseHandler.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion
						+ ", Adjustment to existing data will be made");
		// backup the old table first before the data restore inside new table
		String upgrade = "ALTER TABLE "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
				+ " RENAME TO EXPENSE_TABLE_TEMP";
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
		db.execSQL(EXPENSE_TRACKER_CREATE_STATEMENT);

		db.execSQL("DROP TABLE IF EXISTS "
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE);
		db.execSQL(EXPENSE_BUDGET_CREATE_STATEMENT);

		// create the payable and receivable
		db.execSQL("DROP TABLE IF EXISTS "
				+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE);
		db.execSQL("DROP TABLE IF EXISTS "
				+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE);
		db.execSQL(PAYABLE_RECEIVABLE_MASTER_CREATE_STATEMENT);
		db.execSQL(PAYABLE_RECEIVABLE_DETAIL_CREATE_STATEMENT);

		// need to insert these tables if the table is not exist yet
		if (oldVersion < 6) {
			db.execSQL("DROP TABLE IF EXISTS "
					+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ConfigurationDAO.CONFIGURATION_TABLE);
			db.execSQL("DROP TABLE IF EXISTS "
					+ ConfigurationDAO.CONFIG_DESC_TABLE);

			db.execSQL(TRANSACTION_CATEGORY_CREATE_STATEMENT);
			// configuration table
			db.execSQL(CONFIGURATION_CREATE_STATEMENT);
			db.execSQL(CONFIG_DESC_CREATE_STATEMENT);
			

		}

		// insert value to the expense tracker table and expense category

		Cursor crExpenseTable = db.rawQuery("select * from EXPENSE_TABLE_TEMP",
				null);
		crExpenseTable.moveToFirst();
		while (!crExpenseTable.isAfterLast()) {
			// Calendar calTemp = Calendar.getInstance();
			long date_input = crExpenseTable.getLong(1);
			Date dateInput = new Date(date_input);
			Log.d("date ", dateInput.toString());
			Log.d(" milis ", String.valueOf(crExpenseTable.getInt(1)));
			List<Integer> weekmonthyear = FormatHelper
					.getWeekNoMonthAndYearFromDate(dateInput);
			// calTemp.setTime(dateINput);
			// Log.d("time now ", calTemp.getTime().toString());
			int _id = crExpenseTable.getInt(0);

			String desc = crExpenseTable.getString(2);
			int amount = crExpenseTable.getInt(3);
			String type = crExpenseTable.getString(4);
			String category = crExpenseTable.getString(5);
			int weekinmonth = weekmonthyear.get(0).intValue();
			int weekinyear = weekmonthyear.get(1).intValue();
			int month = weekmonthyear.get(2).intValue();
			int year = weekmonthyear.get(3).intValue();
			db.execSQL("INSERT INTO " + ExpenseTrackerDAO.EXPENSETRACKER_TABLE
					+ " VALUES(" + _id + "," + date_input + ",'" + desc + "',"
					+ amount + ",'" + type + "','" + category + "',"
					+ weekinmonth + "," + weekinyear + "," + month + "," + year
					+ ")");
			crExpenseTable.moveToNext();
		}
		crExpenseTable.close();
		// set existing transaction category into other if migrate from version
		// < 1.3
		if (oldVersion < 6) {
			db.execSQL("INSERT INTO "
					+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
					+ " select _id,'OTH' from EXPENSE_TABLE_TEMP");
		}

		// check expense tracker table
		cr = db.rawQuery("select * from "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE, null);
		cr.moveToFirst();

		cr.close();
		// check expense category table
		cr = db.rawQuery("select *  from "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, null);
		cr.moveToFirst();

		cr.close();
		// delete the temporary table
		db.execSQL("DROP TABLE IF EXISTS EXPENSE_TABLE_TEMP");

		Log.i("INFO", "Upgrade process finished");
		
		db.setTransactionSuccessful();
		db.endTransaction();
		insertDefaultFundSource(db, true);
		if (oldVersion < 9)
			
			insertPersistentCategoryConfiguration(db, true);

		if (oldVersion < 6) {
			insertDefaultCategoryConfiguration(db, true);
		}

	}

	private void insertDefaultFundSource(SQLiteDatabase db, boolean executeAll) {
		String[] tableCodeFundSource = { "T", "C" };
		String[] fundSourceEnglish = { "Saving", "Cash" };
		String[] fundSourceIndonesia = { "Tabungan", "Tunai" };
		int count = 0;
		// only insert when there is no record yet
		Cursor checkFndSrc = db.rawQuery("select count(*) numOfRec from "+ ConfigurationDAO.CONFIGURATION_TABLE + " where "+ ConfigurationDAO.TABLE_TYPE +
				" = 'FND_SRC'", null);
		if(checkFndSrc != null){
			checkFndSrc.moveToFirst();
			count = checkFndSrc.getInt(0);
			checkFndSrc.close();
		}
		if(count == 0){
			for (int i = 0; i < fundSourceIndonesia.length; i++) {
				String fndSrcConfig = "INSERT INTO "
						+ ConfigurationDAO.CONFIGURATION_TABLE + " ( "
						+ ConfigurationDAO.TABLE_TYPE + ", "
						+ ConfigurationDAO.TABLE_CODE + ", "
						+ ConfigurationDAO.LOC_DESC + ", "
						+ ConfigurationDAO.STATUS + " ) " + "VALUES ( 'FND_SRC', '"
						+ tableCodeFundSource[i] + "', '" + fundSourceIndonesia[i]
						+ "', 1 )";
				// set config description for internationalisation
				String fndSrcConfigDescUS = "INSERT INTO "
						+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
						+ ConfigurationDAO.TABLE_TYPE + ", "
						+ ConfigurationDAO.TABLE_CODE + ", "
						+ ConfigurationDAO.DESC_LOCALE + ","
						+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'FND_SRC', '"
						+ tableCodeFundSource[i] + "', '" + locale_US + "','"
						+ fundSourceEnglish[i] + "' )";
				try{
					
					db.execSQL(fndSrcConfig);
					db.execSQL(fndSrcConfigDescUS);
				}catch(Exception e){
					
				}
				
			}
		}
		

	}
	private void insertPersistentCategoryConfiguration(SQLiteDatabase db,
			boolean executeAll) {

		String[] tableCodeExp = { "TRF","PAY" };
		
		String[] tableCodeInc = { "TRF","REC" };

		String[] expCatEnglish = {  "Transfer","Payable Payment" };
		String[] incCatEnglish = { "Transfer", "Receivable Payment" };

		String[] expCatIndonesian = {  "Transfer", "Pembayaran Hutang" };
		
		String[] incCatIndonesian = { "Transfer","Pembayaran Piutang" };
		// set expense category

		for (int i = 0; i < incCatIndonesian.length; i++) {
			// set income category
			String incomeconfig = "INSERT INTO "
					+ ConfigurationDAO.CONFIGURATION_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.LOC_DESC + ", "
					+ ConfigurationDAO.STATUS + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + incCatIndonesian[i] + "', 1 )";

			// set default fund source

			// set config description for internationalisation
			String incomeconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + locale_US + "','"
					+ incCatEnglish[i] + "' )";

			// set config description for internationalisation
			String incomeconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + locale_UK + "','"
					+ incCatEnglish[i] + "' )";
			try{
				db.execSQL(incomeconfig);
				db.execSQL(incomeconfigDescUS);
				db.execSQL(incomeconfigDescUK);
			}catch(Exception e){
				
			}
			

		}
		for (int i = 0; i < expCatIndonesian.length; i++) {
			
			String expCatConfig = "INSERT INTO "
					+ ConfigurationDAO.CONFIGURATION_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.LOC_DESC + ", "
					+ ConfigurationDAO.STATUS + " ) " + "VALUES ( 'EXP_CAT','"
					+ tableCodeExp[i] + "', '" + expCatIndonesian[i] + "', 1 )";

			// set default fund source

			// set config description for internationalisation
			String expCatconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'EXP_CAT', '"
					+ tableCodeExp[i] + "', '" + locale_US + "', '"
					+ expCatEnglish[i] + "' )";

			// set config description for internationalisation
			String expCatconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'EXP_CAT', '"
					+ tableCodeExp[i] + "', '" + locale_UK + "', '"
					+ expCatEnglish[i] + "' )";
			try{
				db.execSQL(expCatConfig);
				db.execSQL(expCatconfigDescUS);
				db.execSQL(expCatconfigDescUK);
			}catch(Exception e){
				
			}
			
		}

	}
	private void insertDefaultCategoryConfiguration(SQLiteDatabase db,
			boolean executeAll) {

		String[] tableCodeExp = { "HOM", "REN", "TAX", "UTI", "ELE", "WAT",
				"PHO", "FOD", "CHL", "BST", "HEA", "INS", "FIT", "TRN", "CAR",
				"GAS", "REP", "LOA", "ENT", "CLO", "INV", "OTH" };
		
		String[] tableCodeInc = { "SAL", "REN", "INV", "OTH" };

		String[] expCatEnglish = { "Home", "Rent", "Tax", "Utility",
				"Electricity", "Water", "Telephone", "Food", "Child Fund",
				"BabySitter/Servant", "Health", "Insurance", "Sport/Fitness",
				"Transportation", "Vehicle lease", "Gas", "Service", "Loan",
				"Entertainment", "Clothing", "Investment", "Other" };
		String[] incCatEnglish = { "Salary", "Rent Income",
				"Investment Income", "Other" };

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
					+ ConfigurationDAO.LOC_DESC + ", "
					+ ConfigurationDAO.STATUS + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + incCatIndonesian[i] + "', 1 )";

			// set default fund source

			// set config description for internationalisation
			String incomeconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + locale_US + "','"
					+ incCatEnglish[i] + "' )";

			// set config description for internationalisation
			String incomeconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'INC_CAT', '"
					+ tableCodeInc[i] + "', '" + locale_UK + "','"
					+ incCatEnglish[i] + "' )";
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
					+ ConfigurationDAO.STATUS + " ) " + "VALUES ( 'EXP_CAT','"
					+ tableCodeExp[i] + "', '" + expCatIndonesian[i] + "', 1 )";

			// set default fund source

			// set config description for internationalisation
			String expCatconfigDescUS = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'EXP_CAT', '"
					+ tableCodeExp[i] + "', '" + locale_US + "', '"
					+ expCatEnglish[i] + "' )";

			// set config description for internationalisation
			String expCatconfigDescUK = "INSERT INTO "
					+ ConfigurationDAO.CONFIG_DESC_TABLE + " ( "
					+ ConfigurationDAO.TABLE_TYPE + ", "
					+ ConfigurationDAO.TABLE_CODE + ", "
					+ ConfigurationDAO.DESC_LOCALE + ","
					+ ConfigurationDAO.DESC + " ) " + "VALUES ( 'EXP_CAT', '"
					+ tableCodeExp[i] + "', '" + locale_UK + "', '"
					+ expCatEnglish[i] + "' )";
			db.execSQL(expCatConfig);
			db.execSQL(expCatconfigDescUS);
			db.execSQL(expCatconfigDescUK);

		}

	}

}
