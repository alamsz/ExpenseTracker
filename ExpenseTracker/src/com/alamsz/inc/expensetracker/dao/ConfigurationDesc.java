package com.alamsz.inc.expensetracker.dao;

public class ConfigurationDesc {
	private String tableType;
	private String tableCode;
	private String descLocale;
	private String description;
	public static String[] allColumns = { ConfigurationDAO.TABLE_TYPE,
		ConfigurationDAO.TABLE_CODE, ConfigurationDAO.DESC_LOCALE,
		ConfigurationDAO.DESC };
	public ConfigurationDesc() {
		super();
	}
	public ConfigurationDesc(ConfigurationExpTracker conf){
		tableType = conf.getTableType();
		tableCode = conf.getTableCode();
	}
	public String getTableType() {
		return tableType;
	}
	public void setTableType(String tableType) {
		this.tableType = tableType;
	}
	public String getTableCode() {
		return tableCode;
	}
	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}
	public String getDescLocale() {
		return descLocale;
	}
	public void setDescLocale(String descLocale) {
		this.descLocale = descLocale;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
