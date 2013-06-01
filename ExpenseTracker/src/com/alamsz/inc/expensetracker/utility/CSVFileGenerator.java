package com.alamsz.inc.expensetracker.utility;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class CSVFileGenerator {
	
	private static final String NEW_LINE = "\n";
	private static final String COMMA = ",";
	private List<List<String>> listOfObject;
	private List<?> listHeader;
	private String fullPathFileName;
	
	
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
			for (Iterator iterator = listOfObject.iterator(); iterator.hasNext();) {
				List<String> type = (List<String>) iterator.next();
				for (int i = 0; i < listHeader.size(); i++) {
					writer.append(type.get(i));
					if(i<listHeader.size()-1){
						writer.append(COMMA);
					}
				}
				writer.append(NEW_LINE);
			}
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
