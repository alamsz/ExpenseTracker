/**
 * 
 */
package com.alamsz.inc.expensetracker.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author AchmadA
 * 
 */
public class ConfigurationDAO {
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { DatabaseHandler.TABLE_TYPE,
			DatabaseHandler.TABLE_CODE, DatabaseHandler.LOC_DESC,
			DatabaseHandler.STATUS, };

	public ConfigurationDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public boolean open() {
		boolean isOpen = false;
		if (database.isOpen()) {
			isOpen = true;
		} else {
			database = dbHandler.getWritableDatabase();
			isOpen = true;
		}
		return isOpen;
	}

	public void close() {
		dbHandler.close();
	}

	public List<?> getConfiguration(String tableType) {
		return null;
	}

	public void addNewConfiguration(Configuration conf) {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
