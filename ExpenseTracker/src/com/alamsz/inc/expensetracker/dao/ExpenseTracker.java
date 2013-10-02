package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ExpenseTracker {
	private long id;
	private long dateInput;
	private String description;
	private int amount;
	private String type;
	private String category;
	private String transCategory;
	private int weekinmonth;
	private int weekinyear;
	private int month;
	private int year;
	

	public int getWeekinmonth() {
		return weekinmonth;
	}

	public void setWeekinmonth(int weekinmonth) {
		this.weekinmonth = weekinmonth;
	}

	public int getWeekinyear() {
		return weekinyear;
	}

	public void setWeekinyear(int weekinyear) {
		this.weekinyear = weekinyear;
	}

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
	

	public String getTransCategory() {
		return transCategory;
	}

	public void setTransCategory(String exp_category) {
		this.transCategory = exp_category;
	}

	public static List<String> getFinanceHelperHeader(Context context) {
		List<String> listHeader = new ArrayList<String>();
		listHeader.add(context.getString(R.string.date_input));
		listHeader.add(context.getString(R.string.description));
		listHeader.add(context.getString(R.string.fund_source));
		listHeader.add(context.getString(R.string.exp_category));
		listHeader.add(context.getString(R.string.expense));
		listHeader.add(context.getString(R.string.income));
		return listHeader;
	}

	public static List<String> convertFinanceHelperToList(
			ExpenseTracker financeHelper) {
		List<String> listPenampung = new ArrayList<String>();
		if(financeHelper != null){
			listPenampung.add(FormatHelper.formatDateForDisplay(financeHelper
					.getDateInput()));
			listPenampung.add(financeHelper.getDescription());
			ConfigurationExpTracker configurationExpTracker = StaticVariables.mapOfFundCategory.get(financeHelper.getCategory());
			listPenampung.add(configurationExpTracker==null?"":configurationExpTracker.getLocDesc());
			ConfigurationExpTracker confTrans = null;
			if(financeHelper.getType().equals(TYPE_DEBET)){
				confTrans= (ConfigurationExpTracker) StaticVariables.mapOfExpenseCatBasedOnTableCode.get(financeHelper.getTransCategory());
			} else{
				confTrans= (ConfigurationExpTracker) StaticVariables.mapOfIncomeCatBasedOnTableCode.get(financeHelper.getTransCategory());
			}
			String transCategory = confTrans==null?"":confTrans.getLocDesc();
			listPenampung.add(transCategory);
			int amountExpense = financeHelper.getType().equals(TYPE_DEBET)?0:financeHelper.getAmount();
			int amountIncome = financeHelper.getType().equals(TYPE_CREDIT)?0:financeHelper.getAmount();
			listPenampung.add(String.valueOf(amountExpense));
			listPenampung.add(String.valueOf(amountIncome));
		}
		
		
		return listPenampung;
	}
	

		
	
	

}
