/**
 * 
 */
package com.alamsz.inc.expensetracker.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alamsz.inc.expensetracker.utility.SQLOperator;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

/**
 * @author AchmadA
 * 
 */
public class ConfigurationDAO {
	private static final int ACTIVE = 1;
	private SQLiteDatabase database;
	private DatabaseHandler dbHandler;
	
	public static final String DESC = "desc";
	public static final String DESC_LOCALE = "locale";
	public static final String STATUS = "status";
	public static final String LOC_DESC = "loc_desc";
	public static final String TABLE_CODE = "table_code";
	public static final String TABLE_TYPE = "table_type";
	public static final String CONFIG_DESC_TABLE = "config_desc";
	public static final String EXPENSE_CATEGORY = "EXP_CAT";
	public static final String INCOME_CATEGORY = "INC_CAT";
	// Configuration
	public static final String CONFIGURATION_TABLE = "config";

	public ConfigurationDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
	}

	public boolean open() {
		boolean isOpen = false;
		if (database != null && database.isOpen()) {
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

	public List<ConfigurationExpTracker> getConfigurationListPerLocale(
			String tableType, String locale,boolean onlyActive) {
		// get configuration first
		List<ConfigurationExpTracker> configDescList = new ArrayList<ConfigurationExpTracker>();

		String constraintActive = "";
		if(onlyActive)
			 constraintActive = SQLOperator.AND+STATUS+ SQLOperator.EQUAL + ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE + SQLOperator.DOT + SQLOperator.ALL_COLUMNS
				+ SQLOperator.COMA + CONFIG_DESC_TABLE + SQLOperator.DOT + DESC
				+ SQLOperator.FROM + CONFIGURATION_TABLE + SQLOperator.SPACE + CONFIGURATION_TABLE
				+ SQLOperator.LEFT_JOIN + CONFIG_DESC_TABLE + SQLOperator.SPACE + CONFIG_DESC_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.EQUAL + CONFIG_DESC_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.AND + CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + CONFIG_DESC_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.AND + DESC_LOCALE + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
				+ locale + SQLOperator.SINGLE_QUOTE + SQLOperator.WHERE + CONFIGURATION_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
				+ tableType + SQLOperator.SINGLE_QUOTE + constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		while (!csrConfigDesc.isAfterLast()) {
			// then get the description for each locale
			ConfigurationExpTracker configDescTemp = cursorToConfigurationComplete(csrConfigDesc);
			configDescList.add(configDescTemp);
			csrConfigDesc.moveToNext();
		}
		csrConfigDesc.close();

		return configDescList;
	}

	public ConfigurationExpTracker getConfigurationBasedOnDesc(
			String tableType, String description, String locale, boolean onlyActive) {
		String constraintActive = "";
		if(onlyActive)
			 constraintActive = SQLOperator.AND+STATUS+ SQLOperator.EQUAL + ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ CONFIG_DESC_TABLE + SQLOperator.DOT + DESC + SQLOperator.FROM
				+ CONFIGURATION_TABLE + SQLOperator.SPACE + CONFIGURATION_TABLE
				+ SQLOperator.LEFT_JOIN + CONFIG_DESC_TABLE + SQLOperator.SPACE
				+ CONFIG_DESC_TABLE + SQLOperator.ON + CONFIGURATION_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.EQUAL
				+ CONFIG_DESC_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.AND + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_CODE + SQLOperator.EQUAL + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + TABLE_CODE + SQLOperator.AND + DESC_LOCALE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + locale
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.WHERE
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
				+ SQLOperator.OPEN_BRACKET + CONFIGURATION_TABLE
				+ SQLOperator.DOT + LOC_DESC + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + description
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.OR + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + DESC + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + description
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.CLOSE_BRACKET+constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		ConfigurationExpTracker configDescTemp = cursorToConfigurationComplete(csrConfigDesc);
		csrConfigDesc.close();

		return configDescTemp;
	}
	public ConfigurationExpTracker getConfigurationBasedOnTableCode(
			String tableType, String tableCode, String locale, boolean onlyActive) {
		String constraintActive = "";
		if(onlyActive)
			 constraintActive = SQLOperator.AND+STATUS+ SQLOperator.EQUAL + ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ CONFIG_DESC_TABLE + SQLOperator.DOT + DESC + SQLOperator.FROM
				+ CONFIGURATION_TABLE + SQLOperator.SPACE + CONFIGURATION_TABLE
				+ SQLOperator.LEFT_JOIN + CONFIG_DESC_TABLE + SQLOperator.SPACE
				+ CONFIG_DESC_TABLE + SQLOperator.ON + CONFIGURATION_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.EQUAL
				+ CONFIG_DESC_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.AND + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_CODE + SQLOperator.EQUAL + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + TABLE_CODE + SQLOperator.AND + DESC_LOCALE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + locale
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.WHERE
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableCode
				+ SQLOperator.SINGLE_QUOTE 	+constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		ConfigurationExpTracker configDescTemp = cursorToConfigurationComplete(csrConfigDesc);
		csrConfigDesc.close();

		return configDescTemp;
	}
	public ConfigurationExpTracker addNewConfiguration(
			ConfigurationExpTracker conf) {
		ContentValues values = new ContentValues();
		values.put(TABLE_TYPE, conf.getTableType());
		values.put(TABLE_CODE, conf.getTableCode());
		values.put(LOC_DESC, conf.getLocDesc());
		values.put(STATUS, conf.getStatus());
		database.beginTransaction();
		database.insert(CONFIGURATION_TABLE, null, values);
		ConfigurationExpTracker configExp = getConfigurationBasedOnTableCode(conf.getTableType(), conf.getTableCode(), StaticVariables.prefLang, false);
		List<ConfigurationDesc> configDescList = conf.getConfigDescList();
		List<ConfigurationDesc> configDescListNew = new ArrayList<ConfigurationDesc>();
		if(configDescList != null){
			for (int i = 0; i < configDescList.size(); i++) {
				ConfigurationDesc configDescTemp = configDescList.get(i);
				configDescListNew.add(addNewConfigurationDesc(configDescTemp));

			}
		}
		
		configExp.setConfigDescList(configDescListNew);
		database.setTransactionSuccessful();
		database.endTransaction();
		return configExp;
	}

	public ConfigurationDesc addNewConfigurationDesc(ConfigurationDesc confDesc) {
		ContentValues values = new ContentValues();
		values.put(TABLE_TYPE, confDesc.getTableType());
		values.put(TABLE_CODE, confDesc.getTableCode());
		values.put(DESC_LOCALE, confDesc.getDescLocale());
		values.put(DESC, confDesc.getDescription());

		database.insert(CONFIG_DESC_TABLE, null, values);
		Cursor cursor = database.query(
				CONFIG_DESC_TABLE,
				ConfigurationDesc.allColumns,
				TABLE_TYPE + SQLOperator.EQUAL+SQLOperator.SINGLE_QUOTE + confDesc.getTableType() + SQLOperator.SINGLE_QUOTE+SQLOperator.AND
						+ TABLE_CODE + SQLOperator.EQUAL+SQLOperator.SINGLE_QUOTE + confDesc.getTableCode()
						+ SQLOperator.SINGLE_QUOTE+SQLOperator.AND + DESC_LOCALE + SQLOperator.EQUAL+SQLOperator.SINGLE_QUOTE
						+ confDesc.getDescLocale() + SQLOperator.SINGLE_QUOTE, null, null, null,
				null);
		cursor.moveToFirst();
		ConfigurationDesc configTemp = cursorToConfigDesc(cursor);
		cursor.close();

		return configTemp;
	}

	private ConfigurationExpTracker cursorToConfigurationComplete(Cursor csr) {
		ConfigurationExpTracker config = new ConfigurationExpTracker();
		config.setTableType(csr.getString(0));
		config.setTableCode(csr.getString(1));
		config.setLocDesc(csr.getString(2));
		config.setStatus(csr.getInt(3));
		if ((csr.getString(4) != null && !csr.getString(4).equals(
				SQLOperator.EMPTY))) {
			config.setLocDesc(csr.getString(4));
		}
		return config;
	}
	
	private ConfigurationExpTracker cursorToConfigurationOnly(Cursor csr) {
		ConfigurationExpTracker config = new ConfigurationExpTracker();
		config.setTableType(csr.getString(0));
		config.setTableCode(csr.getString(1));
		config.setLocDesc(csr.getString(2));
		config.setStatus(csr.getInt(3));
		
		return config;
	}

	private ConfigurationDesc cursorToConfigDesc(Cursor csr) {
		ConfigurationDesc configDesc = new ConfigurationDesc();
		configDesc.setTableType(csr.getString(0));
		configDesc.setTableCode(csr.getString(1));
		configDesc.setDescLocale(csr.getString(2));
		configDesc.setDescription(csr.getString(3));

		return configDesc;
	}

	public ConfigurationExpTracker changeStatus(String tableType, String tableCode,
			int newStatus) {
		ContentValues cv = new ContentValues();
		cv.put(STATUS, newStatus);
		database.beginTransaction();
		database.update(CONFIGURATION_TABLE, cv, TABLE_TYPE + SQLOperator.EQUAL
				+SQLOperator.SINGLE_QUOTE+ tableType +SQLOperator.SINGLE_QUOTE+ SQLOperator.AND + TABLE_CODE + SQLOperator.EQUAL
				+SQLOperator.SINGLE_QUOTE+ tableCode+SQLOperator.SINGLE_QUOTE, null);
		database.setTransactionSuccessful();
		database.endTransaction();
		return getConfigurationBasedOnTableCode(tableType, tableCode, StaticVariables.prefLang, false);
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
