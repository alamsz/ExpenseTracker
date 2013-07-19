package com.alamsz.inc.expensetracker.dao;

public class PayRecPayment {
	private long idMaster;
	private long idTrans;

	private long id;
	private long dateInput;
	private String description;
	private int amount;
	private String type;
	private String transType;
	private String transCategory;
	private String category;
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	private boolean markAsTrans;
	public boolean isMarkAsTrans() {
		return markAsTrans;
	}

	public void setMarkAsTrans(boolean markAsTrans) {
		this.markAsTrans = markAsTrans;
	}

	public long getIdTrans() {
		return idTrans;
	}

	public void setIdTrans(long idTrans) {
		this.idTrans = idTrans;
	}

	public long getIdMaster() {
		return idMaster;
	}

	public void setIdMaster(long idMaster) {
		this.idMaster = idMaster;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransCategory() {
		return transCategory;
	}

	public void setTransCategory(String transCategory) {
		this.transCategory = transCategory;
	}

	private int weekinmonth;

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

	private int weekinyear;
	private int month;
	private int year;
}
