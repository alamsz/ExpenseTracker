package com.alamsz.inc.expensetracker.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public class ExpenseCategoryBudgetDAO {
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { ConfigurationDAO.TABLE_TYPE,
			ConfigurationDAO.TABLE_CODE, ConfigurationDAO.LOC_DESC,
			ConfigurationDAO.STATUS, };
	public static final String BUDGET_AMOUNT = "budget_amount";
	/////
	// Expense category budget
	public static final String EXPENSE_CATEGORY_BUDGET_TABLE = "expense_budget";

	public ExpenseCategoryBudgetDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public boolean open() {
		boolean isOpen = false;
		if (database.isOpen()) {
			isOpen = true;
		} else {
			database = dbHandler.getWritableDatabase();
			isOpen = true;
		}
		return isOpen;
	}

	public void close() {
		dbHandler.close();
	}

	public List<?> getExpenseCategoryBudget(String tableType) {
		return null;
	}

	public void addNewConfiguration(ExpenseCategoryBudget budget) {

	}
}
