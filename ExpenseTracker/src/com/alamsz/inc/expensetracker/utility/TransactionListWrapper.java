package com.alamsz.inc.expensetracker.utility;

import java.util.List;

public class TransactionListWrapper {
	private int totalExpense;
	private int totalIncome;
	private List<List<String>> expenseTrackerList;
	
	
	
	public TransactionListWrapper(int totalExpense, int totalIncome,
			List<List<String>> expenseTrackerList) {
		super();
		this.totalExpense = totalExpense;
		this.totalIncome = totalIncome;
		this.expenseTrackerList = expenseTrackerList;
	}
	public int getTotalExpense() {
		return totalExpense;
	}
	public void setTotalExpense(int totalExpense) {
		this.totalExpense = totalExpense;
	}
	public int getTotalIncome() {
		return totalIncome;
	}
	public void setTotalIncome(int totalIncome) {
		this.totalIncome = totalIncome;
	}
	public List<List<String>> getExpenseTrackerList() {
		return expenseTrackerList;
	}
	public void setExpenseTrackerList(List<List<String>> expenseTrackerList) {
		this.expenseTrackerList = expenseTrackerList;
	}
	
	
}
