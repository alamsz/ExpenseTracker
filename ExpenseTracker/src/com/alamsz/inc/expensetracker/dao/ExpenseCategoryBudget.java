package com.alamsz.inc.expensetracker.dao;

public class ExpenseCategoryBudget {
	private String tableType;
	private String tableCode;
	private int budgetAmountWeekly;
	private int budgetAmountMonthly;

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public int getBudgetAmountWeekly() {
		return budgetAmountWeekly;
	}

	public void setBudgetAmountWeekly(int budgetAmountWeekly) {
		this.budgetAmountWeekly = budgetAmountWeekly;
	}

	public int getBudgetAmountMonthly() {
		return budgetAmountMonthly;
	}

	public void setBudgetAmountMonthly(int budgetAmountMonthly) {
		this.budgetAmountMonthly = budgetAmountMonthly;
	}

	public String getExpCategory() {
		return expCategory;
	}

	public void setExpCategory(String expCategory) {
		this.expCategory = expCategory;
	}

	private String expCategory;
}
