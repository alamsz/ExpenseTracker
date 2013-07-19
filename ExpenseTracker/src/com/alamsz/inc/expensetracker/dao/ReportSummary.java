package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ReportSummary {
	private String period;
	private String amountIncome;
	private String amountExpense;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getAmountIncome() {
		return amountIncome;
	}

	public void setAmountIncome(String amountIncome) {
		this.amountIncome = amountIncome;
	}

	public String getAmountExpense() {
		return amountExpense;
	}

	public void setAmountExpense(String amountExpense) {
		this.amountExpense = amountExpense;
	}

	public List<String> toListOfString() {
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(this.getPeriod());
		listPenampung.add(this.getAmountIncome());
		listPenampung.add(this.getAmountExpense());

		return listPenampung;
	}

	public static List<String> getHeaderList(Context context) {
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(context.getString(R.string.period));
		listPenampung.add(context.getString(R.string.income));
		listPenampung.add(context.getString(R.string.expense));

		return listPenampung;
	}

}
