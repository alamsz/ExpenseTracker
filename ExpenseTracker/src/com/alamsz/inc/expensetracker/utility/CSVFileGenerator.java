package com.alamsz.inc.expensetracker.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alamsz.inc.expensetracker.dao.ExpenseTracker;


public class CSVFileGenerator {
	
	private static final String NEW_LINE = "\n";
	private static final String COMMA = ",";
	private List<List<String>> listOfObject;
	private List<?> listHeader;
	private String fullPathFileName;
	
	public CSVFileGenerator(String fullPathFileName){
		super();
	}
	public CSVFileGenerator(List<List<String>> listOfObject, List<?> listHeader,
			String fullPathFileName) {
		super();
		this.listOfObject = listOfObject;
		this.listHeader = listHeader;
		this.fullPathFileName = fullPathFileName;
	}
	
	public boolean generateCSVFile(){
		try {
			FileWriter writer = new FileWriter(fullPathFileName);
			
			for (Iterator iterator = listHeader.iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				writer.append(type);
				if(iterator.hasNext()){
					writer.append(COMMA);
				}
			}
			writer.append(NEW_LINE);
			if(listOfObject != null){
				for (Iterator iterator = listOfObject.iterator(); iterator
						.hasNext();) {
					List<String> type = (List<String>) iterator.next();
					for (int i = 0; i < listHeader.size(); i++) {
						writer.append(type.get(i));
						if (i < listHeader.size() - 1) {
							writer.append(COMMA);
						}
					}
					writer.append(NEW_LINE);
				}
			}
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public List<ExpenseTracker> importCSVFileToList() {
		FileReader fr;
		try {
			fr = new FileReader(fullPathFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		BufferedReader br = new BufferedReader(fr);
		String data = "";
		List<ExpenseTracker> expTrackerList = new ArrayList<ExpenseTracker>();
		try {
			while ((data = br.readLine()) != null) {

				String[] sarray = data.split(",");
				ExpenseTracker expTrackerTemp = new ExpenseTracker();
				expTrackerTemp.setDateInput(FormatHelper.formatDateToLong(sarray[0]));
				expTrackerTemp.setDescription(sarray[1]);
				expTrackerTemp.setCategory(sarray[2]);
				expTrackerTemp.setTransCategory(sarray[3]);
				String transType = sarray[4].equals("0")?ExpenseTracker.TYPE_DEBET:ExpenseTracker.TYPE_CREDIT;
				int amount = 0;
				if (transType.equals(ExpenseTracker.TYPE_CREDIT)) {
					amount = Integer.parseInt(sarray[4]);
				} else {
					amount = Integer.parseInt(sarray[5]);
				}
				
				expTrackerTemp.setAmount(amount);
				expTrackerTemp.setType(transType);
				expTrackerList.add(expTrackerTemp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return expTrackerList;
	}
}
