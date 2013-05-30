package com.alamsz.inc.expensetracker.dao;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

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


    public static String getCategoryDescription(String category){
    	return category.equals("T")?"Tabungan":category.equals("C")?"Cash":"";
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


	public String getAmountInCurrency(){
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		nf.setCurrency(Currency.getInstance("IDR"));
		return nf.format(getAmount());
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
		return this.id+". "+FormatHelper.formatDateForDisplay(this.dateInput)+ ": "+this.description+"["+sign+getAmountInCurrency()+"]";
	}

}
