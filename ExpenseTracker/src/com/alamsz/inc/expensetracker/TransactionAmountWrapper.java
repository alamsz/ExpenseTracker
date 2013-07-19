package com.alamsz.inc.expensetracker;

public class TransactionAmountWrapper {
	public long getDateInput() {
		return dateInput;
	}
	public void setDateInput(long dateInput) {
		this.dateInput = dateInput;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public int getExpense() {
		return expense;
	}
	public void setExpense(int expense) {
		this.expense = expense;
	}
	public int getIncome() {
		return income;
	}
	public void setIncome(int income) {
		this.income = income;
	}
	private long dateInput;
	private int week;
	private int expense;
	private int income;
	private int month;
	private int year;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
}
