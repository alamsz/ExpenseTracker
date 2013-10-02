package com.alamsz.inc.expensetracker.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alamsz.inc.expensetracker.utility.SQLOperator;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ExpenseCategoryBudgetDAO {
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	public static final String BUDGET_AMOUNT_WEEKLY = "budget_amount_weekly";
	public static final String BUDGET_AMOUNT_MONTHLY = "budget_amount_monthly";

	private String[] allColumns = { ConfigurationDAO.TABLE_TYPE,
			ConfigurationDAO.TABLE_CODE, BUDGET_AMOUNT_WEEKLY,
			BUDGET_AMOUNT_MONTHLY };

	// ///
	// Expense category budget
	public static final String EXPENSE_CATEGORY_BUDGET_TABLE = "expense_budget";

	public ExpenseCategoryBudgetDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public ExpenseCategoryBudgetDAO(SQLiteDatabase db) {
		database = db;
	}

	public boolean open() {
		boolean isOpen = false;
		if (StaticVariables.database!=null && StaticVariables.database.isOpen()) {
			isOpen = true;
		} else {
			StaticVariables.database = dbHandler.getWritableDatabase();
			isOpen = true;
		}
		database = StaticVariables.database;
		return isOpen;
	}

	public void close() {
		dbHandler.close();
	}

	public ExpenseCategoryBudget getExpenseCategoryBudget(String tableType,
			String tableCode) {
		String expenseBudgetQuery = SQLOperator.SELECT
				+ EXPENSE_CATEGORY_BUDGET_TABLE + SQLOperator.DOT
				+ SQLOperator.ALL_COLUMNS + SQLOperator.FROM
				+ EXPENSE_CATEGORY_BUDGET_TABLE + SQLOperator.SPACE
				+ EXPENSE_CATEGORY_BUDGET_TABLE + SQLOperator.WHERE
				+ EXPENSE_CATEGORY_BUDGET_TABLE + SQLOperator.DOT
				+ ConfigurationDAO.TABLE_TYPE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
				+ EXPENSE_CATEGORY_BUDGET_TABLE + SQLOperator.DOT
				+ ConfigurationDAO.TABLE_CODE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + tableCode
				+ SQLOperator.SINGLE_QUOTE;

		Cursor expBudgetCur = database.rawQuery(expenseBudgetQuery, null);
		ExpenseCategoryBudget expBudget = null;
		if(expBudgetCur != null){
			expBudgetCur.moveToFirst();
			
			while (!expBudgetCur.isAfterLast()) {
				// then get the description for each locale
				expBudget = cursorToExpenseBudget(expBudgetCur);

				expBudgetCur.moveToNext();
			}
			expBudgetCur.close();
		}
		
		return expBudget;
	}

	private ExpenseCategoryBudget cursorToExpenseBudget(Cursor expBudgetCur) {
		ExpenseCategoryBudget expBudget = new ExpenseCategoryBudget();
		expBudget.setTableType(expBudgetCur.getString(0));
		expBudget.setTableCode(expBudgetCur.getString(1));
		expBudget.setBudgetAmountWeekly(expBudgetCur.getInt(2));
		expBudget.setBudgetAmountMonthly(expBudgetCur.getInt(3));
		return expBudget;
	}

	public ExpenseCategoryBudget addNewExpenseBudget(
			ExpenseCategoryBudget budget) {
		if (budget != null) {
			ContentValues values = new ContentValues();
			values.put(ConfigurationDAO.TABLE_TYPE, budget.getTableType());
			values.put(ConfigurationDAO.TABLE_CODE, budget.getTableCode());
			values.put(BUDGET_AMOUNT_WEEKLY, budget.getBudgetAmountWeekly());
			values.put(BUDGET_AMOUNT_MONTHLY, budget.getBudgetAmountMonthly());
			database.beginTransaction();
			database.insert(EXPENSE_CATEGORY_BUDGET_TABLE, null, values);

			database.setTransactionSuccessful();
			database.endTransaction();
		}

		return budget;
	}

	public ExpenseCategoryBudget updateExpenseBudget(
			ExpenseCategoryBudget budget) {
		if (budget != null) {
			ContentValues values = new ContentValues();
			values.put(ConfigurationDAO.TABLE_TYPE, budget.getTableType());
			values.put(ConfigurationDAO.TABLE_CODE, budget.getTableCode());
			values.put(BUDGET_AMOUNT_WEEKLY, budget.getBudgetAmountWeekly());
			values.put(BUDGET_AMOUNT_MONTHLY, budget.getBudgetAmountMonthly());
			database.beginTransaction();
			database.update(EXPENSE_CATEGORY_BUDGET_TABLE, values,
					ConfigurationDAO.TABLE_TYPE + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + budget.getTableType()
							+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
							+ ConfigurationDAO.TABLE_CODE + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + budget.getTableCode()
							+ SQLOperator.SINGLE_QUOTE, null);

			database.setTransactionSuccessful();
			database.endTransaction();
		}

		return budget;
	}
	
	public void deleteExpenseBudget(ExpenseCategoryBudget expBudget){
		database.delete(EXPENSE_CATEGORY_BUDGET_TABLE, ConfigurationDAO.TABLE_TYPE + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + expBudget.getTableType()
							+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
							+ ConfigurationDAO.TABLE_CODE + SQLOperator.EQUAL
							+ SQLOperator.SINGLE_QUOTE + expBudget.getTableCode()
							+ SQLOperator.SINGLE_QUOTE, null);
	}
}
