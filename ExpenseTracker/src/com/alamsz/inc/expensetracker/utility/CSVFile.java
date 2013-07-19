package com.alamsz.inc.expensetracker.utility;

import java.util.List;

public class CSVFile {
	private List<List<String>> listOfObject;
	private List<?> listHeader;
	private String title;
	public CSVFile(List<List<String>> listOfObject, List<?> listHeader,
			String title) {
		super();
		this.listOfObject = listOfObject;
		this.listHeader = listHeader;
		this.title = title;
	}
	public List<List<String>> getListOfObject() {
		return listOfObject;
	}
	public void setListOfObject(List<List<String>> listOfObject) {
		this.listOfObject = listOfObject;
	}
	public List<?> getListHeader() {
		return listHeader;
	}
	public void setListHeader(List<?> listHeader) {
		this.listHeader = listHeader;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
