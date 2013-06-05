package com.alamsz.inc.expensetracker.dao;

public class ExpenseCategoryBudget {
	private String tableType;
	private String tableCode;
	private long budgetAmount;
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
	public long getBudgetAmount() {
		return budgetAmount;
	}
	public void setBudgetAmount(long budgetAmount) {
		this.budgetAmount = budgetAmount;
	}
	public String getExpCategory() {
		return expCategory;
	}
	public void setExpCategory(String expCategory) {
		this.expCategory = expCategory;
	}
	private String expCategory;
}
