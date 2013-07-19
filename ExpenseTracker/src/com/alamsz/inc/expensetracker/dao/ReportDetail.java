package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import com.alamsz.inc.expensetracker.R;

import android.content.Context;

public class ReportDetail {
	private String category;
	private String period;
	private String amount;
	private String monthlyBudget;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getMonthlyBudget() {
		return monthlyBudget;
	}
	public void setMonthlyBudget(String monthlyBudget) {
		this.monthlyBudget = monthlyBudget;
	}
	
	public List<String> toList() {
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(this.getPeriod());
		listPenampung.add(this.getCategory());
		listPenampung.add(this.getAmount());
		listPenampung.add(this.getMonthlyBudget());
		
		return listPenampung;
	}
	
	public static List<String> getExpenseHeaderList(Context context){
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(context.getString(R.string.period));
		listPenampung.add(context.getString(R.string.exp_category));
		listPenampung.add(context.getString(R.string.amount));
		listPenampung.add(context.getString(R.string.monthly_budget));
		return listPenampung;
	}
	
	public static List<String> getIncomeHeaderList(Context context){
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(context.getString(R.string.period));
		listPenampung.add(context.getString(R.string.inc_category));
		listPenampung.add(context.getString(R.string.amount));
		listPenampung.add(context.getString(R.string.monthly_budget));
		return listPenampung;
	}
}
