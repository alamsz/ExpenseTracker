package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alamsz.inc.expensetracker.TransactionAmountWrapper;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.SQLOperator;

public class ExpenseTrackerDAO {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { ExpenseTrackerDAO.ID,
			ExpenseTrackerDAO.DATE_INPUT, ExpenseTrackerDAO.DESCRIPTION,
			ExpenseTrackerDAO.AMOUNT, ExpenseTrackerDAO.TYPE,
			ExpenseTrackerDAO.FUND_SOURCE,
			ExpenseTrackerDAO.TRANSACTION_CATEGORY };
	private String[] allColumnsExpTrack = { ExpenseTrackerDAO.ID,
			ExpenseTrackerDAO.DATE_INPUT, ExpenseTrackerDAO.DESCRIPTION,
			ExpenseTrackerDAO.AMOUNT, ExpenseTrackerDAO.TYPE,
			ExpenseTrackerDAO.FUND_SOURCE, WEEK_IN_MONTH, WEEK_IN_YEAR, MONTH,
			YEAR };
	private String[] allColumnsExpCat = { ExpenseTrackerDAO.ID,
			ExpenseTrackerDAO.TRANSACTION_CATEGORY };
	public static final String ID = "_id";
	public static final String TRANSACTION_CATEGORY = "transaction_category";
	public static final String FUND_SOURCE = "category";
	public static final String WEEK_IN_MONTH = "week_in_month";
	public static final String WEEK_IN_YEAR = "week_in_year";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
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
		// close it when it still open
		if (database != null) {
			close();
		}
		// open or reopen the database
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
		String dateInput = FormatHelper.formatDateForDisplay(finHelp
				.getDateInput());
		List<Integer> weekmonthyear = FormatHelper

		.getWeekNoMonthAndYearFromDate(FormatHelper.stringToDate(dateInput));
		values.put(ExpenseTrackerDAO.WEEK_IN_MONTH, weekmonthyear.get(0)
				.intValue());
		values.put(ExpenseTrackerDAO.WEEK_IN_YEAR, weekmonthyear.get(1)
				.intValue());
		values.put(ExpenseTrackerDAO.MONTH, weekmonthyear.get(2).intValue());
		values.put(ExpenseTrackerDAO.YEAR, weekmonthyear.get(3).intValue());

		database.beginTransaction();
		long insertId = database.insert(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				null, values);
		Cursor cursor = database.query(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				allColumnsExpTrack, ExpenseTrackerDAO.ID + SQLOperator.EQUAL
						+ insertId, null, null, null, null);
		cursor.moveToFirst();

		// process only if category not null and contain some values
		Cursor cursorExpCat = null;
		if (finHelp.getTransCategory() != null
				&& !finHelp.getTransCategory().equals("")) {
			ContentValues valuesExpCat = new ContentValues();

			valuesExpCat.put(ExpenseTrackerDAO.ID, insertId);
			valuesExpCat.put(ExpenseTrackerDAO.TRANSACTION_CATEGORY,
					finHelp.getTransCategory());
			long insertIdExpCat = database.insert(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, null,
					valuesExpCat);

			cursorExpCat = database.query(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE,
					allColumnsExpCat, ExpenseTrackerDAO.ID + SQLOperator.EQUAL
							+ insertIdExpCat, null, null, null, null);
			cursorExpCat.moveToFirst();
		}

		ExpenseTracker finHelper = cursorToFinanceHelper(cursor, cursorExpCat);
		cursor.close();
		if (cursorExpCat != null) {
			cursorExpCat.close();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		return finHelper;

	}

	public ExpenseTracker findByPKTransaction(long id) {
		Cursor cursoExpTrack;
		database.beginTransaction();
		cursoExpTrack = database.query(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				allColumnsExpTrack, ExpenseTrackerDAO.ID + SQLOperator.EQUAL
						+ id, null, null, null, null);
		cursoExpTrack.moveToFirst();
		Cursor cursorExpCat;
		cursorExpCat = database.query(
				ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, allColumnsExpCat,
				ExpenseTrackerDAO.ID + SQLOperator.EQUAL + id, null, null,
				null, null);
		cursorExpCat.moveToFirst();
		ExpenseTracker finHelper = null;
		while (!cursoExpTrack.isAfterLast()) {
			finHelper = cursorToFinanceHelper(cursoExpTrack, cursorExpCat);
			break;
		}

		if (cursoExpTrack != null) {
			cursoExpTrack.close();
			cursorExpCat.close();
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		return finHelper;
	}

	public ExpenseTracker modifyFinanceHelper(ExpenseTracker finHelp) {

		ContentValues values = new ContentValues();
		values.put(ExpenseTrackerDAO.DATE_INPUT, finHelp.getDateInput());
		values.put(ExpenseTrackerDAO.DESCRIPTION, finHelp.getDescription());
		values.put(ExpenseTrackerDAO.AMOUNT, finHelp.getAmount());
		values.put(ExpenseTrackerDAO.TYPE, finHelp.getType());
		values.put(ExpenseTrackerDAO.FUND_SOURCE, finHelp.getCategory());
		database.beginTransaction();
		long insertId = database.update(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				values,
				ExpenseTrackerDAO.ID + SQLOperator.EQUAL + finHelp.getId(),
				null);
		Cursor cursor = database.query(ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
				allColumnsExpTrack, ExpenseTrackerDAO.ID + SQLOperator.EQUAL
						+ insertId, null, null, null, null);
		cursor.moveToFirst();

		// process only if category not null and contain some values
		Cursor cursorExpCat = null;
		if (finHelp.getTransCategory() != null
				&& !finHelp.getTransCategory().equals("")) {
			ContentValues valuesExpCat = new ContentValues();

			valuesExpCat.put(ExpenseTrackerDAO.ID, insertId);
			valuesExpCat.put(ExpenseTrackerDAO.TRANSACTION_CATEGORY,
					finHelp.getTransCategory());
			long insertIdExpCat = database.update(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE, valuesExpCat,
					ExpenseTrackerDAO.ID + SQLOperator.EQUAL + insertId, null);

			cursorExpCat = database.query(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE,
					allColumnsExpCat, ExpenseTrackerDAO.ID + SQLOperator.EQUAL
							+ insertIdExpCat, null, null, null, null);
			cursorExpCat.moveToFirst();
		}

		ExpenseTracker finHelper = cursorToFinanceHelper(cursor, cursorExpCat);
		cursor.close();
		if (cursorExpCat != null) {
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
			int rowDetailAffected = database.delete(
					ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE,
					ExpenseTrackerDAO.ID + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + id
							+ SQLOperator.SINGLE_QUOTE, null);
			int rowHeadAffected = database.delete(
					ExpenseTrackerDAO.EXPENSETRACKER_TABLE,
					ExpenseTrackerDAO.ID + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + id
							+ SQLOperator.SINGLE_QUOTE, null);

			Log.d("delete", "deleted head :" + String.valueOf(rowHeadAffected)
					+ " and detail" + String.valueOf(rowDetailAffected));
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
		/*
		 * DatabaseHandler.EXPENSETRACKER_TABLE, allColumns, null, null, null,
		 * null, DatabaseHandler.DATE_INPUT+ " asc;");
		 */
		curFinHelper.moveToFirst();
		while (!curFinHelper.isAfterLast()) {
			finHelperList.add(cursorToFinanceHelper(curFinHelper));
			curFinHelper.moveToNext();
		}
		curFinHelper.close();
		return finHelperList;

	}

	public List<ExpenseTracker> getListPerPeriod(String dateFrom,
			String dateTo, String category, String transType, String transCat) {
		List<ExpenseTracker> finHelperList = new ArrayList<ExpenseTracker>();
		long dateFromLong = 0;
		long dateToFromLong = 0;
		String argCat = category == null ? SQLOperator.EMPTY : category
				.equals(SQLOperator.EMPTY) ? ExpenseTrackerDAO.FUND_SOURCE
				: SQLOperator.SINGLE_QUOTE + category
						+ SQLOperator.SINGLE_QUOTE;
		StringBuffer constraint = new StringBuffer();
		if (dateFrom != null && !dateFrom.equals(SQLOperator.EMPTY)) {
			dateFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateFrom));
			constraint.append(ExpenseTrackerDAO.DATE_INPUT
					+ SQLOperator.GE_THAN + String.valueOf(dateFromLong));

		}
		if (dateTo != null && !dateTo.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			dateToFromLong = FormatHelper.formatDateToLong(FormatHelper
					.stringToDate(dateTo));
			constraint.append(ExpenseTrackerDAO.DATE_INPUT
					+ SQLOperator.LE_THAN + String.valueOf(dateToFromLong));

		}
		if (transType != null && !transType.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}

			constraint.append(ExpenseTrackerDAO.TYPE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE + transType
					+ SQLOperator.SINGLE_QUOTE);

		}
		if (transCat != null && !transCat.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}

			constraint.append(ExpenseTrackerDAO.TRANSACTION_CATEGORY
					+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + transCat
					+ SQLOperator.SINGLE_QUOTE);

		}
		if (!argCat.equals(SQLOperator.EMPTY)) {
			if (!constraint.toString().equals(SQLOperator.EMPTY)) {
				constraint.append(SQLOperator.AND);
			}
			constraint.append(ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL
					+ argCat);

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
		Log.d("query", getFinanceHelperPerPeriodQuery);
		Cursor curFinHelper = database.rawQuery(getFinanceHelperPerPeriodQuery,
				null);
		/*
		 * DatabaseHandler.EXPENSETRACKER_TABLE, allColumns,
		 * constraint.toString(), null, null, null, DatabaseHandler.DATE_INPUT+
		 * " asc;");
		 */

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
						+ " where category= '" + category
						+ SQLOperator.SINGLE_QUOTE, null);
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
	public String getBalanceOtherThanCash() {
		Cursor curSaldo = database.rawQuery(
				"select sum(case when type='K' then -1*amount else amount end) as saldo from "
						+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
						+ " where category <> 'C'", null);
		curSaldo.moveToFirst();
		String getSaldo = curSaldo.getString(0);
		curSaldo.close();
		return getSaldo;
	}

	public int calculateExpensePerCategory(String category, String fundSource,
			int month, int year) {
		String queryString = "select sum(amount) as saldo from "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.SPACE
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
				+ SQLOperator.LEFT_JOIN
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.SPACE + TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.ON + EXPENSETRACKER_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.EQUAL
				+ TRANSACTION_CATEGORY_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.WHERE
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + category
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
				+ ExpenseTrackerDAO.MONTH + SQLOperator.EQUAL
				+ String.valueOf(month) + SQLOperator.AND
				+ ExpenseTrackerDAO.YEAR + SQLOperator.EQUAL + year
				+ SQLOperator.AND + ExpenseTrackerDAO.TYPE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + "K" + SQLOperator.SINGLE_QUOTE;
		if (fundSource != null && !fundSource.equals("")) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.TYPE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE + fundSource
					+ SQLOperator.SINGLE_QUOTE;
		}
		Cursor curSaldo = database.rawQuery(queryString, null);
		curSaldo.moveToFirst();
		int getSaldo = curSaldo.getInt(0);
		curSaldo.close();
		return getSaldo;
	}

	public Map<Integer, TransactionAmountWrapper> getWeeklyTransactionSummary(
			int week, int month, int year, String fundType) {
		String queryString = SQLOperator.SELECT
				+ "sum(case when type='K' then amount else 0 end) as expense, "
				+ "sum(case when type='D' then amount else 0 end) as income, "
				+ ExpenseTrackerDAO.DATE_INPUT + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.WHERE
				+ ExpenseTrackerDAO.WEEK_IN_MONTH + SQLOperator.EQUAL + week
				+ SQLOperator.AND + ExpenseTrackerDAO.MONTH + SQLOperator.EQUAL
				+ month + SQLOperator.AND + ExpenseTrackerDAO.YEAR
				+ SQLOperator.EQUAL + year;
		if (fundType != null && !fundType.equals("")) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE+ fundType+ SQLOperator.SINGLE_QUOTE;
		}
		queryString = queryString + " group by " + ExpenseTrackerDAO.DATE_INPUT;

		Cursor curSaldo = database.rawQuery(queryString, null);
		curSaldo.moveToFirst();
		Map<Integer, TransactionAmountWrapper> listTrans = new HashMap<Integer, TransactionAmountWrapper>();

		while (!curSaldo.isAfterLast()) {
			TransactionAmountWrapper taw = new TransactionAmountWrapper();
			taw.setDateInput(curSaldo.getLong(2));
			String dateString = FormatHelper.formatDateForDisplay(taw
					.getDateInput());
			taw.setExpense(curSaldo.getInt(0));
			taw.setIncome(curSaldo.getInt(1));
			taw.setMonth(month);
			taw.setYear(year);
			listTrans.put(Integer.parseInt(dateString.substring(0, 2)), taw);
			curSaldo.moveToNext();
		}

		curSaldo.close();
		return listTrans;
	}

	public Map<Integer, TransactionAmountWrapper> getMonthlyTransactionSummary(
			int month, int year, String fundType) {
		String queryString = SQLOperator.SELECT
				+ "sum(case when type='K' then amount else 0 end) as expense,sum(case when type='D' then amount else 0 end) as income, "
				+ ExpenseTrackerDAO.WEEK_IN_MONTH + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.WHERE
				+ ExpenseTrackerDAO.MONTH + SQLOperator.EQUAL + month
				+ SQLOperator.AND + ExpenseTrackerDAO.YEAR + SQLOperator.EQUAL
				+ year;
		if (fundType != null && !fundType.equals("")) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE+ fundType+ SQLOperator.SINGLE_QUOTE;
		}
		queryString = queryString + " group by "
				+ ExpenseTrackerDAO.WEEK_IN_MONTH;
		Cursor curSaldo = database.rawQuery(queryString, null);
		curSaldo.moveToFirst();
		Map<Integer, TransactionAmountWrapper> listTrans = new HashMap<Integer, TransactionAmountWrapper>();

		while (!curSaldo.isAfterLast()) {
			TransactionAmountWrapper taw = new TransactionAmountWrapper();
			taw.setWeek(curSaldo.getInt(2));
			// Calendar caltemp = Calendar.getInstance();
			// caltemp.setTime(new Date(taw.getDateInput()));
			// int dd = caltemp.get(Calendar.DAY_OF_MONTH);
			taw.setExpense(curSaldo.getInt(0));
			taw.setIncome(curSaldo.getInt(1));
			taw.setMonth(month);
			taw.setYear(year);
			listTrans.put(taw.getWeek(), taw);
			curSaldo.moveToNext();
		}

		curSaldo.close();
		return listTrans;
	}

	public List<String[]> getDetailCategoryBasedOnType(int date, int week,
			int month, int year, String type, String fundType) {
		String queryString = SQLOperator.SELECT + "sum(amount) as expense, "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + " "
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE
				+ SQLOperator.LEFT_JOIN
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE + " "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE + SQLOperator.ON
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.DOT
				+ ExpenseTrackerDAO.ID + SQLOperator.EQUAL
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY_TABLE
				+ SQLOperator.DOT + ExpenseTrackerDAO.ID + SQLOperator.WHERE
				+ ExpenseTrackerDAO.YEAR + SQLOperator.EQUAL + year
				+ SQLOperator.AND + TYPE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + type + SQLOperator.SINGLE_QUOTE;
		if (date != 0) {
			Long dateNow = FormatHelper.formatDateToLong(date + "-" + month
					+ "-" + year);
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.DATE_INPUT + SQLOperator.EQUAL
					+ dateNow;
		}
		if (month != 0) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.MONTH + SQLOperator.EQUAL + month;
		}
		if (week != 0) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.WEEK_IN_MONTH + SQLOperator.EQUAL
					+ week;
		}
		if (fundType != null && !fundType.equals("")) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE+ fundType+ SQLOperator.SINGLE_QUOTE;
		}
		queryString = queryString + " group by "
				+ ExpenseTrackerDAO.TRANSACTION_CATEGORY;
		Cursor curSaldo = database.rawQuery(queryString, null);
		curSaldo.moveToFirst();
		List<String[]> listTrans = new ArrayList<String[]>();

		while (!curSaldo.isAfterLast()) {

			listTrans.add(new String[] { String.valueOf(curSaldo.getInt(0)),
					curSaldo.getString(1) });
			curSaldo.moveToNext();
		}

		curSaldo.close();
		return listTrans;
	}

	/**
	 * set the result from cursor to finance helper object
	 * 
	 * @param cursor
	 * @return
	 */
	private ExpenseTracker cursorToFinanceHelper(Cursor cursor,
			Cursor cursorExpCat) {
		ExpenseTracker finHelper = new ExpenseTracker();
		finHelper.setId(cursor.getLong(0));
		finHelper.setDateInput(cursor.getLong(1));
		finHelper.setDescription(cursor.getString(2));
		finHelper.setAmount(cursor.getInt(3));
		finHelper.setType(cursor.getString(4));
		finHelper.setCategory(cursor.getString(5));
		finHelper.setWeekinmonth(cursor.getInt(6));
		finHelper.setWeekinyear(cursor.getInt(7));
		finHelper.setMonth(cursor.getInt(8));
		finHelper.setYear(cursor.getInt(9));
		if (cursorExpCat != null)
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
		finHelper.setWeekinmonth(cursor.getInt(6));
		finHelper.setWeekinyear(cursor.getInt(7));
		finHelper.setMonth(cursor.getInt(8));
		finHelper.setYear(cursor.getInt(9));
		finHelper.setTransCategory(cursor.getString(10) == null ? "" : cursor
				.getString(10));
		return finHelper;
	}

	public Map<Integer, TransactionAmountWrapper> getYearlyTransactionSummary(
			int year, String fundType) {
		String queryString = SQLOperator.SELECT
				+ "sum(case when type='K' then amount else 0 end) as expense"
				+ ",sum(case when type='D' then amount else 0 end) as income, "
				+ ExpenseTrackerDAO.MONTH + SQLOperator.FROM
				+ ExpenseTrackerDAO.EXPENSETRACKER_TABLE + SQLOperator.WHERE
				+ ExpenseTrackerDAO.YEAR + SQLOperator.EQUAL + year;

		if (fundType != null && !fundType.equals("")) {
			queryString = queryString + SQLOperator.AND
					+ ExpenseTrackerDAO.FUND_SOURCE + SQLOperator.EQUAL
					+ SQLOperator.SINGLE_QUOTE+ fundType+ SQLOperator.SINGLE_QUOTE;
		}
		queryString = queryString + " group by " + ExpenseTrackerDAO.MONTH;
		Cursor curSaldo = database.rawQuery(queryString, null);
		curSaldo.moveToFirst();
		Map<Integer, TransactionAmountWrapper> listTrans = new HashMap<Integer, TransactionAmountWrapper>();

		while (!curSaldo.isAfterLast()) {
			TransactionAmountWrapper taw = new TransactionAmountWrapper();
			taw.setMonth(curSaldo.getInt(2));
			taw.setExpense(curSaldo.getInt(0));
			taw.setIncome(curSaldo.getInt(1));
			taw.setYear(year);
			listTrans.put(taw.getMonth(), taw);
			curSaldo.moveToNext();
		}

		curSaldo.close();
		return listTrans;
	}

	/**** Payable and Receivable section ****/
	public PayRecMaster addPayRecRecord(PayRecMaster payRec) {

		ContentValues values = new ContentValues();
		values.put(PayRecMaster.DATE_INPUT, payRec.getDateInput());
		values.put(PayRecMaster.DESCRIPTION, payRec.getDescription());
		values.put(PayRecMaster.AMOUNT, payRec.getAmount());
		values.put(PayRecMaster.TYPE, payRec.getType());
		values.put(PayRecMaster.DUE_DATE, payRec.getDueDate());
		values.put(PayRecMaster.AMOUNT_PAID, payRec.getAmount_paid());
		values.put(PayRecMaster.MARK_COMPLETE, payRec.getMark_complete());
		List<Integer> weekmonthyear = FormatHelper
				.getWeekNoMonthAndYearFromDate(new Date(payRec.getDateInput()));
		values.put(PayRecMaster.WEEK_IN_MONTH, weekmonthyear.get(0).intValue());
		values.put(PayRecMaster.WEEK_IN_YEAR, weekmonthyear.get(1).intValue());
		values.put(PayRecMaster.MONTH, weekmonthyear.get(2).intValue());
		values.put(PayRecMaster.YEAR, weekmonthyear.get(3).intValue());

		database.beginTransaction();
		long insertId = database.insert(PayRecMaster.PAYABLE_RECEIVABLE_TABLE,
				null, values);

		database.setTransactionSuccessful();
		database.endTransaction();
		PayRecMaster payRecMaster = findByPKPayRecMaster(insertId);
		return payRecMaster;

	}

	public PayRecMaster modifyPayRecRecord(PayRecMaster payRec) {

		ContentValues values = new ContentValues();
		values.put(PayRecMaster.DATE_INPUT, payRec.getDateInput());
		values.put(PayRecMaster.DESCRIPTION, payRec.getDescription());
		values.put(PayRecMaster.AMOUNT, payRec.getAmount());
		values.put(PayRecMaster.TYPE, payRec.getType());
		values.put(PayRecMaster.DUE_DATE, payRec.getDueDate());

		values.put(PayRecMaster.MARK_COMPLETE, payRec.getMark_complete());
		List<Integer> weekmonthyear = FormatHelper
				.getWeekNoMonthAndYearFromDate(new Date(payRec.getDateInput()));
		values.put(PayRecMaster.WEEK_IN_MONTH, weekmonthyear.get(0).intValue());
		values.put(PayRecMaster.WEEK_IN_YEAR, weekmonthyear.get(1).intValue());
		values.put(PayRecMaster.MONTH, weekmonthyear.get(2).intValue());
		values.put(PayRecMaster.YEAR, weekmonthyear.get(3).intValue());

		database.beginTransaction();
		long insertId = database.update(PayRecMaster.PAYABLE_RECEIVABLE_TABLE,
				values, PayRecMaster.ID + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + payRec.getId()
						+ SQLOperator.SINGLE_QUOTE, null);
		// PayRecMaster payRecMaster = findByPKPayRecMaster(insertId);

		database.setTransactionSuccessful();
		database.endTransaction();
		return payRec;

	}

	public boolean modifyPayRecRecordAmountPaid(long amountPaid, long id) {

		ContentValues values = new ContentValues();
		;
		values.put(PayRecMaster.AMOUNT_PAID, amountPaid);

		database.beginTransaction();
		long insertId = database.update(PayRecMaster.PAYABLE_RECEIVABLE_TABLE,
				values, PayRecMaster.ID + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + id
						+ SQLOperator.SINGLE_QUOTE, null);
		// PayRecMaster payRecMaster = findByPKPayRecMaster(insertId);

		database.setTransactionSuccessful();
		database.endTransaction();
		return insertId == 1;

	}

	public PayRecPayment addPayRecPaymentRecord(PayRecPayment payRec) {

		ContentValues values = new ContentValues();
		values.put(PayRecMaster.ID_MASTER, payRec.getIdMaster());
		values.put(PayRecMaster.ID_TRANS, payRec.getIdTrans());
		values.put(PayRecMaster.DATE_INPUT, payRec.getDateInput());
		values.put(PayRecMaster.DESCRIPTION, payRec.getDescription());
		values.put(PayRecMaster.AMOUNT, payRec.getAmount());
		values.put(PayRecMaster.TYPE, payRec.getType());

		List<Integer> weekmonthyear = FormatHelper
				.getWeekNoMonthAndYearFromDate(new Date(payRec.getDateInput()));
		values.put(PayRecMaster.WEEK_IN_MONTH, weekmonthyear.get(0).intValue());
		values.put(PayRecMaster.WEEK_IN_YEAR, weekmonthyear.get(1).intValue());
		values.put(PayRecMaster.MONTH, weekmonthyear.get(2).intValue());
		values.put(PayRecMaster.YEAR, weekmonthyear.get(3).intValue());

		database.beginTransaction();
		ExpenseTracker expTracker = new ExpenseTracker();
		if (payRec.isMarkAsTrans()) {

			expTracker.setDateInput(payRec.getDateInput());
			expTracker.setDescription(payRec.getDescription());
			expTracker.setAmount(payRec.getAmount());
			expTracker.setTransCategory(payRec.getTransCategory());
			expTracker.setType(payRec.getTransType());
			expTracker.setCategory(payRec.getCategory());
			expTracker = addFinanceHelper(expTracker);
			values.put(PayRecMaster.ID_TRANS, expTracker.getId());
		}

		long insertId = database.insert(
				PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE, null, values);
		PayRecMaster payRecMaster = findByPKPayRecMaster(payRec.getIdMaster());
		modifyPayRecRecordAmountPaid(
				payRec.getAmount() + payRecMaster.getAmount_paid(),
				payRec.getIdMaster());
		database.setTransactionSuccessful();
		database.endTransaction();
		PayRecPayment payRecPayment = findByPKPayRecPayment(insertId);
		return payRecPayment;

	}

	// the payment should not modifiable, but deletable instead
	/*
	 * public PayRecPayment modifyPayRecDetailRecord(PayRecPayment payRec) {
	 * 
	 * ContentValues values = new ContentValues();
	 * values.put(PayRecMaster.ID_MASTER, payRec.getIdMaster());
	 * values.put(PayRecMaster.DATE_INPUT, payRec.getDateInput());
	 * values.put(PayRecMaster.DESCRIPTION, payRec.getDescription());
	 * values.put(PayRecMaster.AMOUNT, payRec.getAmount());
	 * values.put(PayRecMaster.TYPE, payRec.getType()); List<Integer>
	 * weekmonthyear = FormatHelper .getWeekNoMonthAndYearFromDate(new
	 * Date(payRec.getDateInput())); values.put(PayRecMaster.WEEK_IN_MONTH,
	 * weekmonthyear.get(0) .intValue()); values.put(PayRecMaster.WEEK_IN_YEAR,
	 * weekmonthyear.get(1) .intValue()); values.put(PayRecMaster.MONTH,
	 * weekmonthyear.get(2).intValue()); values.put(PayRecMaster.YEAR,
	 * weekmonthyear.get(3).intValue());
	 * 
	 * 
	 * database.beginTransaction(); long insertId =
	 * database.update(PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE,
	 * values,PayRecMaster.ID+SQLOperator.EQUAL +payRec.getId()+SQLOperator.AND
	 * +PayRecMaster.ID_MASTER+SQLOperator.EQUAL +payRec.getIdMaster(),null);
	 * //PayRecDetail payRecDetail = findByPKPayRecDetail(payRec.getId());
	 * 
	 * database.setTransactionSuccessful(); database.endTransaction(); return
	 * payRec;
	 * 
	 * }
	 */

	public boolean deletePayRecPayment(PayRecPayment payRecPayment) {
		database.beginTransaction();
		PayRecPayment paymentTemp = findByPKPayRecPayment(payRecPayment.getId());
		long amount = paymentTemp.getAmount();
		PayRecMaster payRecTemp = findByPKPayRecMaster(payRecPayment
				.getIdMaster());
		long amountPaid = payRecTemp.getAmount_paid();
		long amountPaidNow = amountPaid - amount;
		if (amountPaidNow < 0)
			amountPaidNow = 0;
		modifyPayRecRecordAmountPaid(amountPaidNow, payRecTemp.getId());
		if (paymentTemp.getIdTrans() > 0) {
			ExpenseTracker expTracker = findByPKTransaction(paymentTemp
					.getIdTrans());
			if (expTracker != null) {
				deleteFinanceHelper(expTracker);
			}
		}
		long insertId = database.delete(
				PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE,
				PayRecMaster.ID_MASTER + SQLOperator.EQUAL
						+ payRecPayment.getIdMaster() + SQLOperator.AND
						+ PayRecMaster.ID + SQLOperator.EQUAL
						+ payRecPayment.getId(), null);
		database.setTransactionSuccessful();
		database.endTransaction();
		return insertId == 1;
	}

	public boolean deletePayRecMaster(PayRecMaster payRecMaster) {
		database.beginTransaction();
		long insertId = database
				.delete(PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE,
						PayRecMaster.ID + SQLOperator.EQUAL
								+ SQLOperator.SINGLE_QUOTE
								+ payRecMaster.getId()
								+ SQLOperator.SINGLE_QUOTE, null);
		return insertId == 1;
	}

	public List<PayRecMaster> getPayRecMasterByType(String type) {
		List<PayRecMaster> payRecMasterList = new ArrayList<PayRecMaster>();
		String getAllPayRecMasterQuery = SQLOperator.SELECT
				+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE + SQLOperator.DOT
				+ SQLOperator.ALL_COLUMNS + SQLOperator.FROM
				+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE + SQLOperator.SPACE
				+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE + SQLOperator.WHERE
				+ PayRecMaster.PAYABLE_RECEIVABLE_TABLE + SQLOperator.DOT
				+ PayRecMaster.TYPE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + type + SQLOperator.SINGLE_QUOTE
				+ SQLOperator.ORDER_BY + ExpenseTrackerDAO.DATE_INPUT
				+ SQLOperator.ASCENDING;
		Log.d("query", getAllPayRecMasterQuery);
		Cursor curPayRecMasters = database.rawQuery(getAllPayRecMasterQuery,
				null);
		/*
		 * DatabaseHandler.EXPENSETRACKER_TABLE, allColumns, null, null, null,
		 * null, DatabaseHandler.DATE_INPUT+ " asc;");
		 */
		curPayRecMasters.moveToFirst();
		while (!curPayRecMasters.isAfterLast()) {
			PayRecMaster payRecMaster = cursorToPayRecMaster(curPayRecMasters);
			// payRecMaster.setPayRecDetailList(getPayRecPaymentByMasterId(payRecMaster.getId()));
			payRecMasterList.add(payRecMaster);
			curPayRecMasters.moveToNext();
		}
		curPayRecMasters.close();
		return payRecMasterList;
	}

	public List<PayRecPayment> getPayRecPaymentByMasterId(long pkId) {
		List<PayRecPayment> payRecDetailList = new ArrayList<PayRecPayment>();
		String getAllPayRecDetailQuery = SQLOperator.SELECT
				+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.FROM
				+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE
				+ SQLOperator.SPACE
				+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE
				+ SQLOperator.WHERE
				+ PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE
				+ SQLOperator.DOT + PayRecMaster.ID_MASTER + SQLOperator.EQUAL
				+ pkId + SQLOperator.ORDER_BY + ExpenseTrackerDAO.DATE_INPUT
				+ SQLOperator.ASCENDING;
		Log.d("query", getAllPayRecDetailQuery);
		Cursor curPayRecDetail = database.rawQuery(getAllPayRecDetailQuery,
				null);
		/*
		 * DatabaseHandler.EXPENSETRACKER_TABLE, allColumns, null, null, null,
		 * null, DatabaseHandler.DATE_INPUT+ " asc;");
		 */
		curPayRecDetail.moveToFirst();
		while (!curPayRecDetail.isAfterLast()) {
			payRecDetailList.add(cursorToPayRecPayment(curPayRecDetail));
			curPayRecDetail.moveToNext();
		}
		curPayRecDetail.close();
		return payRecDetailList;
	}

	public PayRecPayment findByPKPayRecPayment(long id) {
		Cursor cursor = database.query(
				PayRecMaster.PAYABLE_RECEIVABLE_DETAIL_TABLE,
				PayRecMaster.allColumnsDetail, PayRecMaster.ID
						+ SQLOperator.EQUAL + id, null, null, null, null);
		PayRecPayment payRecPayment = null;
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				payRecPayment = cursorToPayRecPayment(cursor);
				break;
			}

			cursor.close();

		}

		return payRecPayment;

	}

	public PayRecMaster findByPKPayRecMaster(long id) {
		Cursor cursor = database.query(PayRecMaster.PAYABLE_RECEIVABLE_TABLE,
				PayRecMaster.allColumnsMaster, PayRecMaster.ID
						+ SQLOperator.EQUAL + id, null, null, null, null);
		PayRecMaster payRecMaster = null;
		if (cursor != null) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				payRecMaster = cursorToPayRecMaster(cursor);
				break;
			}

			// payRecMaster.setPayRecDetailList(getPayRecPaymentByMasterId(payRecMaster.getId()));
			cursor.close();

		}

		return payRecMaster;

	}

	private PayRecMaster cursorToPayRecMaster(Cursor cursor) {
		PayRecMaster payRecMaster = new PayRecMaster();
		payRecMaster.setId(cursor.getLong(0));
		payRecMaster.setDateInput(cursor.getLong(1));
		payRecMaster.setDescription(cursor.getString(2));
		payRecMaster.setAmount(cursor.getInt(3));
		payRecMaster.setType(cursor.getString(4));
		payRecMaster.setDueDate(cursor.getLong(5));
		payRecMaster.setAmount_paid(cursor.getInt(6));
		payRecMaster.setMark_complete(cursor.getInt(7));
		payRecMaster.setWeekinmonth(cursor.getInt(8));
		payRecMaster.setWeekinyear(cursor.getInt(9));
		payRecMaster.setMonth(cursor.getInt(10));
		payRecMaster.setYear(cursor.getInt(11));
		return payRecMaster;
	}

	private PayRecPayment cursorToPayRecPayment(Cursor cursor) {
		PayRecPayment payRecPayment = new PayRecPayment();
		payRecPayment.setId(cursor.getLong(0));
		payRecPayment.setIdMaster(cursor.getLong(1));
		payRecPayment.setIdTrans(cursor.getLong(2));
		payRecPayment.setDateInput(cursor.getLong(3));
		payRecPayment.setDescription(cursor.getString(4));
		payRecPayment.setAmount(cursor.getInt(5));
		payRecPayment.setType(cursor.getString(6));
		payRecPayment.setWeekinmonth(cursor.getInt(7));
		payRecPayment.setWeekinyear(cursor.getInt(8));
		payRecPayment.setMonth(cursor.getInt(9));
		payRecPayment.setYear(cursor.getInt(10));
		return payRecPayment;
	}
}
