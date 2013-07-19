package com.alamsz.inc.expensetracker.dao;

import java.util.List;

public class PayRecMaster {
	public static final String ID = "_id";
	public static final String ID_MASTER = "id_master";
	public static final String TRANSACTION_CATEGORY = "transaction_category";
	public static final String WEEK_IN_MONTH = "week_in_month";
	public static final String WEEK_IN_YEAR = "week_in_year";
	public static final String MONTH = "month";
	public static final String YEAR = "year";
	public static final String TYPE = "type";
	public static final String AMOUNT = "amount";
	public static final String AMOUNT_PAID = "amount_paid";
	public static final String MARK_COMPLETE = "mark_complete";
	public static final String DESCRIPTION = "description";
	public static final String DATE_INPUT = "date_input";
	public static final String DUE_DATE = "due_date";
	public static final String PAYABLE_RECEIVABLE_TABLE = "pay_rec_table";
	public static final String PAYABLE_RECEIVABLE_DETAIL_TABLE = "pay_rec_Detail_table";
	public static final String PAYABLE_CODE = "P";
	public static final String RECEIVABLE_CODE = "R";
	public static final String ID_TRANS = "id_trans";
	public static String[] allColumnsMaster = { ID, DATE_INPUT, DESCRIPTION, AMOUNT, TYPE,
		DUE_DATE, AMOUNT_PAID, MARK_COMPLETE,WEEK_IN_MONTH, WEEK_IN_YEAR,MONTH,YEAR };
	public static String[] allColumnsDetail = {  ID,ID_MASTER,ID_TRANS, DATE_INPUT, DESCRIPTION, AMOUNT,TYPE, WEEK_IN_MONTH, WEEK_IN_YEAR, MONTH, YEAR};

	private long id;
	private long dateInput;
	private String description;
	private int amount;
	private String type;
	private long dueDate;
	private int amount_paid;
	private int mark_complete;
	public int getAmount_paid() {
		return amount_paid;
	}

	public void setAmount_paid(int amount_paid) {
		this.amount_paid = amount_paid;
	}

	public int getMark_complete() {
		return mark_complete;
	}

	public void setMark_complete(int mark_complete) {
		this.mark_complete = mark_complete;
	}

	private String transCategory;
	private int weekinmonth;
	private int weekinyear;
	private int month;
	private int year;
	private List<PayRecPayment> payRecDetailList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public long getDueDate() {
		return dueDate;
	}

	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}

	public String getTransCategory() {
		return transCategory;
	}

	public void setTransCategory(String transCategory) {
		this.transCategory = transCategory;
	}

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

	public List<PayRecPayment> getPayRecDetailList() {
		return payRecDetailList;
	}

	public void setPayRecDetailList(List<PayRecPayment> payRecDetailList) {
		this.payRecDetailList = payRecDetailList;
	}
}
