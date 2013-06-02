package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class ExpenseTrackerDAO {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { DatabaseHandler.ID,
			DatabaseHandler.DATE_INPUT, DatabaseHandler.DESCRIPTION,
			DatabaseHandler.AMOUNT, DatabaseHandler.TYPE,
			DatabaseHandler.CATEGORY };

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
		values.put(DatabaseHandler.DATE_INPUT, finHelp.getDateInput());
		values.put(DatabaseHandler.DESCRIPTION, finHelp.getDescription());
		values.put(DatabaseHandler.AMOUNT, finHelp.getAmount());
		values.put(DatabaseHandler.TYPE, finHelp.getType());
		values.put(DatabaseHandler.CATEGORY, finHelp.getCategory());
		long insertId = database.insert(DatabaseHandler.FINANCEHELPER_TABLE,
				null, values);
		Cursor cursor = database.query(DatabaseHandler.FINANCEHELPER_TABLE,
				allColumns, DatabaseHandler.ID + " = " + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		ExpenseTracker finHelper = cursorToFinanceHelper(cursor);
		cursor.close();

		return finHelper;

	}

	public boolean deleteFinanceHelper(ExpenseTracker finHelper) {

		try {
			long id = finHelper.getId();
			database.delete(DatabaseHandler.FINANCEHELPER_TABLE,
					DatabaseHandler.ID + " = " + id, null);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public List<ExpenseTracker> getAllFinanceHelper() {
		List<ExpenseTracker> finHelperList = new ArrayList<ExpenseTracker>();

		Cursor curFinHelper = database.query(
				DatabaseHandler.FINANCEHELPER_TABLE, allColumns, null, null,
				null, null, DatabaseHandler.DATE_INPUT+ " asc;");
		curFinHelper.moveToFirst();
		while (!curFinHelper.isAfterLast()) {
			finHelperList.add(cursorToFinanceHelper(curFinHelper));
			curFinHelper.moveToNext();
		}
		curFinHelper.close();
		return finHelperList;

	}

	public List<ExpenseTracker> getListPerPeriod(String dateFrom, String dateTo,
			String category) {
		List<ExpenseTracker> finHelperList = new ArrayList<ExpenseTracker>();
		long dateFromLong = 0;
		long dateToFromLong = 0;
		String argCat = category == null ? ""
				: category.equals("") ? DatabaseHandler.CATEGORY : "'"
						+ category + "'";
		StringBuffer constraint = new StringBuffer();
		if (dateFrom != null && !dateFrom.equals("")) {
			dateFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateFrom));
			constraint.append(DatabaseHandler.DATE_INPUT + " >= "
					+ String.valueOf(dateFromLong));

		}
		if (dateTo != null && !dateTo.equals("")) {
			if (!constraint.toString().equals("")) {
				constraint.append(" and ");
			}
			dateToFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateTo));
			constraint.append(DatabaseHandler.DATE_INPUT + " <= "
					+ String.valueOf(dateToFromLong));

		}
		if (!argCat.equals("")) {
			if (!constraint.toString().equals("")) {
				constraint.append(" and ");
			}
			constraint.append(DatabaseHandler.CATEGORY + " = " + argCat);

		}
		Cursor curFinHelper = database.query(
				DatabaseHandler.FINANCEHELPER_TABLE, allColumns,
				constraint.toString(), null, null, null, DatabaseHandler.DATE_INPUT+ " asc;");

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
						+ DatabaseHandler.FINANCEHELPER_TABLE, null);
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
						+ DatabaseHandler.FINANCEHELPER_TABLE
						+ " where category= '" + category + "'", null);
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
	private ExpenseTracker cursorToFinanceHelper(Cursor cursor) {
		ExpenseTracker finHelper = new ExpenseTracker();
		finHelper.setId(cursor.getLong(0));
		finHelper.setDateInput(cursor.getLong(1));
		finHelper.setDescription(cursor.getString(2));
		finHelper.setAmount(cursor.getInt(3));
		finHelper.setType(cursor.getString(4));
		finHelper.setCategory(cursor.getString(5));
		return finHelper;
	}

}
