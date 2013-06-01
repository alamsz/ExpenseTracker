package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;



public class FinanceHelper {
	private long id;
	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}




	private long dateInput;
	private String description;
	private int amount;
	private String type;
	private String category;
	
	

	public String getCategory() {
		return category;
	}


    public String getCategoryDescription(){
    	return category.equals("T")?"Tabungan":category.equals("C")?"Cash":"";
    }
    
    public static String getCategoryDescription(String category2){
    	return category2.equals("T")?"Tabungan":category2.equals("C")?"Cash":"";
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
		String sign = this.type.equals("D")?"":"-";
		return this.id+". "+FormatHelper.formatDateForDisplay(this.dateInput)+ ": "+this.description+"["+sign+FormatHelper.getBalanceInCurrency(amount)+"]";
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




	public static List<String> convertFinanceHelperToList(FinanceHelper financeHelper) {
		List<String> listPenampung = new ArrayList<String>();
		listPenampung.add(FormatHelper.formatDateForDisplay(financeHelper.getDateInput()));
		listPenampung.add(financeHelper.getDescription());
		listPenampung.add(financeHelper.getType());
		listPenampung.add(financeHelper.getCategoryDescription());
		listPenampung.add(String.valueOf(financeHelper.getAmount()));
		return listPenampung;
	}

}
