package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class ExpenseTracker {
	private long id;
	private long dateInput;
	private String description;
	private int amount;
	private String type;
	private String category;
	public static final String CAT_SAVING = "T";
	public static final String CAT_CASH = "C";
	public static final String TYPE_CREDIT = "K";

	public static final String TYPE_DEBET = "D";

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public long getDateInput() {
		return dateInput;
	}

	public void setDateInput(long dateInput) {
		this.dateInput = dateInput;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String sign = this.type.equals(TYPE_DEBET) ? "" : "-";
		return this.id + ". "
				+ FormatHelper.formatDateForDisplay(this.dateInput) + ": "
				+ this.description + "[" + sign
				+ FormatHelper.getBalanceInCurrency(amount) + "]";
	}

	public static List<String> getFinanceHelperHeader(Context context) {
		List<String> listHeader = new ArrayList<String>();
		listHeader.add(context.getString(R.string.date_input));
		listHeader.add(context.getString(R.string.description));
		listHeader.add(context.getString(R.string.expense_cat));
		listHeader.add(context.getString(R.string.expense_type));
		listHeader.add(context.getString(R.string.amount));
		return listHeader;
	}

	public static List<String> convertFinanceHelperToList(
			ExpenseTracker financeHelper) {
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(FormatHelper.formatDateForDisplay(financeHelper
				.getDateInput()));
		listPenampung.add(financeHelper.getDescription());
		listPenampung.add(financeHelper.getType());
		listPenampung.add(financeHelper.getCategory());
		listPenampung.add(String.valueOf(financeHelper.getAmount()));
		return listPenampung;
	}

}
