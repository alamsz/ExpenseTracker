package com.alamsz.inc.expensetracker.dao;

import java.util.List;

public class ConfigurationExpTracker {
	private String tableType;
	private String tableCode;
	private String locDesc;
	private int status;
	public static String[] allColumns = { ConfigurationDAO.TABLE_TYPE,
		ConfigurationDAO.TABLE_CODE, ConfigurationDAO.LOC_DESC,
		ConfigurationDAO.STATUS, };
	private List<ConfigurationDesc> configDescList;
	
	public List<ConfigurationDesc> getConfigDescList() {
		return configDescList;
	}
	public void setConfigDescList(List<ConfigurationDesc> configDescList) {
		this.configDescList = configDescList;
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
	public String getLocDesc() {
		return locDesc;
	}
	public void setLocDesc(String locDesc) {
		this.locDesc = locDesc;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
