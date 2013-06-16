package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.SQLOperator;

public class ExpenseTrackerDAO {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { ExpenseTrackerDAO.ID,
			ExpenseTrackerDAO.DATE_INPUT, ExpenseTrackerDAO.DESCRIPTION,
			ExpenseTrackerDAO.AMOUNT, ExpenseTrackerDAO.TYPE,
			ExpenseTrackerDAO.FUND_SOURCE,ExpenseTrackerDAO.TRANSACTION_CATEGORY};
	private String[] allColumnsExpTrack = { ExpenseTrackerDAO.ID,
			ExpenseTrackerDAO.DATE_INPUT, ExpenseTrackerDAO.DESCRIPTION,
			ExpenseTrackerDAO.AMOUNT, ExpenseTrackerDAO.TYPE,
			ExpenseTrackerDAO.FUND_SOURCE};
	private String[] allColumnsExpCat = { ExpenseTrackerDAO.ID,ExpenseTrackerDAO.TRANSACTION_CATEGORY};
	public static final String ID = "_id";
	public static final String TRANSACTION_CATEGORY = "transaction_category";
	public static final String FUND_SOURCE = "category";
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String DESCRIPTION = "description";
	public static final String DATE_INPUT = "date_input";
	public static final String TRANSACTION_CATEGORY_TABLE = "transaction_category_tbl";
	// Finance Helper
	public static final String EXPENSETRACKER_TABLE = "finance_helper";

	public ExpenseTrackerDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public boolean open() {
		//close it when it still open
		if(database != null){
			close();
		}
		//open or reopen the database
		database = dbHandler.getWritableDatabase();
		if (database.isOpen()) {
			return true;
		}
		return false;
	}

	public void close() {
		dbHandler.close();
	}

	public ExpenseTracker addFinanceHelper(ExpenseTracker finHelp) {

		ContentValues values = new ContentValues();
		values.put(ExpenseTrackerDAO.DATE_INPUT, finHelp.getDateInput());
		values.put(ExpenseTrackerDAO.DESCRIPTION, finHelp.getDescription());
		values.put(ExpenseTrackerDAO.AMOUNT, finHelp.getAmount());
		values.put(ExpenseTrackerDAO.TYPE, finHelp.getType());
		values.put(ExpenseTrackerDAO.FUND_SOURCE, finHelp.getCategory());
		database.beginTransaction();
		long insertId = database.insert(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				null, values);
		Cursor cursor = database.query(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				allColumnsExpTrack, ExpenseTrackerDAO.ID + SQLOperator.EQUAL + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		
		//process only if category not null and contain some values
		Cursor cursorExpCat = null;
		if(finHelp.getTransCategory()!=null && !finHelp.getTransCategory().equals("")){
			ContentValues valuesExpCat = new ContentValues();
			
			valuesExpCat.put(ExpenseTrackerDAO.ID, insertId);
			valuesExpCat.put(ExpenseTrackerDAO.TRANSACTION_CATEGORY,
					finHelp.getTransCategory());
			long insertIdExpCat = database.insert(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, null,
					valuesExpCat);
			
			cursorExpCat = database.query(ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE,
					allColumnsExpCat, ExpenseTrackerDAO.ID + SQLOperator.EQUAL + insertIdExpCat, null, null,
					null, null);
			cursorExpCat.moveToFirst();
		}
		
		
		ExpenseTracker finHelper = cursorToFinanceHelper(cursor, cursorExpCat);
		cursor.close();
		if(cursorExpCat != null){
			cursorExpCat.close();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		return finHelper;

	}

	public boolean deleteFinanceHelper(ExpenseTracker finHelper) {

		try {
			long id = finHelper.getId();
			Log.d("id_Tobedeleted", String.valueOf(id));
			
			database.beginTransaction();
			int rowDetailAffected = database.delete(ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE,
					ExpenseTrackerDAO.ID + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE+id+SQLOperator.SINGLE_QUOTE, null);
			int rowHeadAffected = database.delete(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
					ExpenseTrackerDAO.ID + SQLOperator.EQUAL +SQLOperator.SINGLE_QUOTE+ id+SQLOperator.SINGLE_QUOTE, null);
			
			Log.d("delete", "deleted head :"+String.valueOf(rowHeadAffected)+" and detail"+String.valueOf(rowDetailAffected));
			database.setTransactionSuccessful();
			database.endTransaction();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public List<ExpenseTracker> getAllFinanceHelper() {
		List<ExpenseTracker> finHelperList = new ArrayList<ExpenseTracker>();
		String getAllFinanceQuery = SQLOperator.SELECT + EXPENSETRACKER_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ TRANSACTION_CATEGORY_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.SPACE
				+ EXPENSETRACKER_TABLE + SQLOperator.LEFT_JOIN
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.SPACE + TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.ON + EXPENSETRACKER_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.EQUAL
				+ TRANSACTION_CATEGORY_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.ORDER_BY
				+ ExpenseTrackerDAO.DATE_INPUT + SQLOperator.ASCENDING;
		Log.d("query", getAllFinanceQuery);
		Cursor curFinHelper = database.rawQuery(getAllFinanceQuery, null);
				/*DatabaseHandler.EXPENSETRACKER_TABLE, allColumns, null, null,
				null, null, DatabaseHandler.DATE_INPUT+ " asc;");*/
		curFinHelper.moveToFirst();
		while (!curFinHelper.isAfterLast()) {
			finHelperList.add(cursorToFinanceHelper(curFinHelper));
			curFinHelper.moveToNext();
		}
		curFinHelper.close();
		return finHelperList;

	}

	public List<ExpenseTracker> getListPerPeriod(String dateFrom, String dateTo,
			String category, String transType, String transCat) {
		List<ExpenseTracker> finHelperList = new ArrayList<ExpenseTracker>();
		long dateFromLong = 0;
		long dateToFromLong = 0;
		String argCat = category == null ? SQLOperator.EMPTY
				: category.equals(SQLOperator.EMPTY) ? ExpenseTrackerDAO.FUND_SOURCE : SQLOperator.SINGLE_QUOTE
						+ category + SQLOperator.SINGLE_QUOTE;
		StringBuffer constraint = new StringBuffer();
		if (dateFrom != null && !dateFrom.equals(SQLOperator.EMPTY)) {
			dateFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateFrom));
			constraint.append(ExpenseTrackerDAO.DATE_INPUT + SQLOperator.GE_THAN
					+ String.valueOf(dateFromLong));

		}
		if (dateTo != null && !dateTo.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			dateToFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateTo));
			constraint.append(ExpenseTrackerDAO.DATE_INPUT + SQLOperator.LE_THAN
					+ String.valueOf(dateToFromLong));

		}
		if (transType != null && !transType.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			
			constraint.append(ExpenseTrackerDAO.TYPE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE
					+ transType + SQLOperator.SINGLE_QUOTE);

		}
		if (transCat != null && !transCat.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			
			constraint.append(ExpenseTrackerDAO.TRANSACTION_CATEGORY + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE
					+ transCat + SQLOperator.SINGLE_QUOTE);

		}
		if (!argCat.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			constraint.append(ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL + argCat);

		}
		String getFinanceHelperPerPeriodQuery = SQLOperator.SELECT
				+ EXPENSETRACKER_TABLE + SQLOperator.DOT
				+ SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ TRANSACTION_CATEGORY_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.SPACE
				+ EXPENSETRACKER_TABLE + SQLOperator.LEFT_JOIN
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.SPACE + TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.ON + EXPENSETRACKER_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.EQUAL
				+ TRANSACTION_CATEGORY_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.WHERE
				+ constraint.toString() + SQLOperator.ORDER_BY
				+ ExpenseTrackerDAO.DATE_INPUT + SQLOperator.ASCENDING;
		Log.d("query",getFinanceHelperPerPeriodQuery);
		Cursor curFinHelper = database.rawQuery(getFinanceHelperPerPeriodQuery,null);
				/*DatabaseHandler.EXPENSETRACKER_TABLE, allColumns,
				constraint.toString(), null, null, null, DatabaseHandler.DATE_INPUT+ " asc;");*/

		curFinHelper.moveToFirst();
		while (!curFinHelper.isAfterLast()) {
			finHelperList.add(cursorToFinanceHelper(curFinHelper));
			curFinHelper.moveToNext();
		}
		curFinHelper.close();
		return finHelperList;

	}

	public String getBalance() {
		Cursor curSaldo = database.rawQuery(
				"select sum(case when type='K' then -1*amount else amount end) as saldo from "
						+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE, null);
		curSaldo.moveToFirst();
		String getSaldo = curSaldo.getString(0);
		curSaldo.close();
		return getSaldo;
	}

	/**
	 * 
	 * @param category
	 * @return
	 */
	public String getBalancePerCategory(String category) {
		Cursor curSaldo = database.rawQuery(
				"select sum(case when type='K' then -1*amount else amount end) as saldo from "
						+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
						+ " where category= '" + category + SQLOperator.SINGLE_QUOTE, null);
		curSaldo.moveToFirst();
		String getSaldo = curSaldo.getString(0);
		curSaldo.close();
		return getSaldo;
	}

	/**
	 * set the result from cursor to finance helper object
	 * 
	 * @param cursor
	 * @return
	 */
	private ExpenseTracker cursorToFinanceHelper(Cursor cursor, Cursor cursorExpCat) {
		ExpenseTracker finHelper = new ExpenseTracker();
		finHelper.setId(cursor.getLong(0));
		finHelper.setDateInput(cursor.getLong(1));
		finHelper.setDescription(cursor.getString(2));
		finHelper.setAmount(cursor.getInt(3));
		finHelper.setType(cursor.getString(4));
		finHelper.setCategory(cursor.getString(5));
		if(cursorExpCat != null)
		finHelper.setTransCategory(cursorExpCat.getString(1));
		return finHelper;
	}
	
	private ExpenseTracker cursorToFinanceHelper(Cursor cursor) {
		ExpenseTracker finHelper = new ExpenseTracker();
		finHelper.setId(cursor.getLong(0));
		finHelper.setDateInput(cursor.getLong(1));
		finHelper.setDescription(cursor.getString(2));
		finHelper.setAmount(cursor.getInt(3));
		finHelper.setType(cursor.getString(4));
		finHelper.setCategory(cursor.getString(5));
		finHelper.setTransCategory(cursor.getString(6)==null?"":cursor.getString(6));
		return finHelper;
	}

}
