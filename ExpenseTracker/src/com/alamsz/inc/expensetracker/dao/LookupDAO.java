/**
 * 
 */
package com.alamsz.inc.expensetracker.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author AchmadA
 *
 */
public class LookupDAO  {
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	private String[] allColumns = { DatabaseHandler.TABLE_TYPE,
			DatabaseHandler.TABLE_CODE, DatabaseHandler.EN_DESC,
			DatabaseHandler.LOC_DESC, DatabaseHandler.STATUS,
			};
	
	public LookupDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public boolean open() {
		database = dbHandler.getWritableDatabase();
		if (database.isOpen()) {
			return true;
		}
		return false;
	}

	public void close() {
		dbHandler.close();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
