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
	private ExpenseCategoryBudgetDAO daoExpCatBudget;
	public static final String DESC = "desc";
	public static final String DESC_LOCALE = "locale";
	public static final String STATUS = "status";
	public static final String LOC_DESC = "loc_desc";
	public static final String TABLE_CODE = "table_code";
	public static final String TABLE_TYPE = "table_type";
	public static final String CONFIG_DESC_TABLE = "config_desc";
	public static final String EXPENSE_CATEGORY = "EXP_CAT";
	public static final String INCOME_CATEGORY = "INC_CAT";
	public static final String FUND_SOURCE_TABLE_TYPE = "FND_SRC";
	// Configuration
	public static final String CONFIGURATION_TABLE = "config";

	public ConfigurationDAO(DatabaseHandler dbHandlerInput) {
		dbHandler = dbHandlerInput;
		// daoExpCatBudget = new ExpenseCategoryBudgetDAO(dbHandler);
		// daoExpCatBudget.open();
	}

	public SQLiteDatabase open() {
		boolean isOpen = false;
		if (StaticVariables.database != null && StaticVariables.database.isOpen()) {
			isOpen = true;
			
		} else {
			StaticVariables.database = dbHandler.getWritableDatabase();
			isOpen = true;
		}
		database = StaticVariables.database;
		daoExpCatBudget = new ExpenseCategoryBudgetDAO(database);
		return StaticVariables.database;
	}
	
	public void close() {
		dbHandler.close();
	}

	public List<ConfigurationExpTracker> getExpenseCategoryListPerLocale(
			String tableType, String locale, boolean onlyActive) {
		// get configuration first
		List<ConfigurationExpTracker> configDescList = new ArrayList<ConfigurationExpTracker>();
		open();
		String constraintActive = "";
		if (onlyActive)
			constraintActive = SQLOperator.AND + STATUS + SQLOperator.EQUAL
					+ ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_WEEKLY
				+ SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_MONTHLY
				+ SQLOperator.COMA + CONFIG_DESC_TABLE + SQLOperator.DOT + DESC
				+ SQLOperator.FROM + CONFIGURATION_TABLE + SQLOperator.SPACE
				+ CONFIGURATION_TABLE + SQLOperator.LEFT_JOIN
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.SPACE
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_CODE + SQLOperator.LEFT_JOIN
				+ CONFIG_DESC_TABLE + SQLOperator.SPACE + CONFIG_DESC_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + CONFIG_DESC_TABLE + SQLOperator.DOT
				+ TABLE_CODE + SQLOperator.AND + DESC_LOCALE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + locale
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.WHERE
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		while (!csrConfigDesc.isAfterLast()) {
			// then get the description for each locale
			ConfigurationExpTracker configDescTemp = cursorToExpenseCategoryWithLocalizedDesc(csrConfigDesc);
			configDescList.add(configDescTemp);
			csrConfigDesc.moveToNext();
		}
		csrConfigDesc.close();

		return configDescList;
	}

	public List<ConfigurationExpTracker> getConfigurationListPerLocale(
			String tableType, String locale, boolean onlyActive) {
		// get configuration first
		open();
		List<ConfigurationExpTracker> configDescList = new ArrayList<ConfigurationExpTracker>();

		String constraintActive = "";
		if (onlyActive)
			constraintActive = SQLOperator.AND + STATUS + SQLOperator.EQUAL
					+ ACTIVE;
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
				+ SQLOperator.SINGLE_QUOTE + constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		while (!csrConfigDesc.isAfterLast()) {
			// then get the description for each locale
			ConfigurationExpTracker configDescTemp = cursorToConfigurationWithLocalizedDesc(csrConfigDesc);
			configDescList.add(configDescTemp);
			csrConfigDesc.moveToNext();
		}
		csrConfigDesc.close();

		return configDescList;
	}

	public ConfigurationExpTracker getExpenseCatBasedOnDesc(String tableType,
			String description, String locale, boolean onlyActive) {
		String constraintActive = "";
		open();
		if (onlyActive)
			constraintActive = SQLOperator.AND + STATUS + SQLOperator.EQUAL
					+ ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_WEEKLY
				+ SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_MONTHLY
				+ SQLOperator.COMA

				+ CONFIG_DESC_TABLE + SQLOperator.DOT + DESC + SQLOperator.FROM
				+ CONFIGURATION_TABLE + SQLOperator.SPACE + CONFIGURATION_TABLE
				+ SQLOperator.LEFT_JOIN
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.SPACE
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_CODE + SQLOperator.LEFT_JOIN
				+ CONFIG_DESC_TABLE + SQLOperator.SPACE + CONFIG_DESC_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + CONFIG_DESC_TABLE + SQLOperator.DOT
				+ TABLE_CODE + SQLOperator.AND + DESC_LOCALE
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
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.CLOSE_BRACKET
				+ constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		ConfigurationExpTracker configDescTemp = cursorToConfigurationWithLocalizedDesc(csrConfigDesc);
		csrConfigDesc.close();

		return configDescTemp;
	}

	public ConfigurationExpTracker getExpenseCatBasedOnTableCode(
			String tableType, String tableCode, String locale,
			boolean onlyActive) {
		open();
		String constraintActive = "";
		if (onlyActive)
			constraintActive = SQLOperator.AND + STATUS + SQLOperator.EQUAL
					+ ACTIVE;
		String queryConfigDesc = SQLOperator.SELECT + CONFIGURATION_TABLE
				+ SQLOperator.DOT + SQLOperator.ALL_COLUMNS + SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_WEEKLY
				+ SQLOperator.COMA
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT
				+ ExpenseCategoryBudgetDAO.BUDGET_AMOUNT_MONTHLY
				+ SQLOperator.COMA

				+ CONFIG_DESC_TABLE + SQLOperator.DOT + DESC + SQLOperator.FROM
				+ CONFIGURATION_TABLE + SQLOperator.SPACE + CONFIGURATION_TABLE
				+ SQLOperator.LEFT_JOIN
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.SPACE
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL
				+ ExpenseCategoryBudgetDAO.EXPENSE_CATEGORY_BUDGET_TABLE
				+ SQLOperator.DOT + TABLE_CODE + SQLOperator.LEFT_JOIN
				+ CONFIG_DESC_TABLE + SQLOperator.SPACE + CONFIG_DESC_TABLE
				+ SQLOperator.ON + CONFIGURATION_TABLE + SQLOperator.DOT
				+ TABLE_TYPE + SQLOperator.EQUAL + CONFIG_DESC_TABLE
				+ SQLOperator.DOT + TABLE_TYPE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + CONFIG_DESC_TABLE + SQLOperator.DOT
				+ TABLE_CODE + SQLOperator.AND + DESC_LOCALE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + locale
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.WHERE
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
				+ CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_CODE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableCode
				+ SQLOperator.SINGLE_QUOTE + constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		csrConfigDesc.moveToFirst();
		ConfigurationExpTracker configDescTemp = cursorToExpenseCategoryWithLocalizedDesc(csrConfigDesc);
		csrConfigDesc.close();

		return configDescTemp;
	}

	public ConfigurationExpTracker getConfigurationBasedOnTableCode(
			String tableType, String tableCode, String locale,
			boolean onlyActive) {
		open();
		String constraintActive = "";
		if (onlyActive)
			constraintActive = SQLOperator.AND + STATUS + SQLOperator.EQUAL
					+ ACTIVE;
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
				+ SQLOperator.SINGLE_QUOTE + constraintActive;
		Log.d("query", queryConfigDesc);
		Cursor csrConfigDesc = database.rawQuery(queryConfigDesc, null);
		ConfigurationExpTracker configDescTemp = null;
		if(csrConfigDesc.getCount()>=1){
			csrConfigDesc.moveToFirst();
			configDescTemp = cursorToConfigurationWithLocalizedDesc(csrConfigDesc);
			
		}
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
		open();
		database.beginTransaction();
		database.insert(CONFIGURATION_TABLE, null, values);

		ConfigurationExpTracker configExp = getConfigurationBasedOnTableCode(
				conf.getTableType(), conf.getTableCode(),
				StaticVariables.prefLang, false);
		if (conf.getExpBudget() != null) {
			configExp.setExpBudget(daoExpCatBudget.addNewExpenseBudget(conf
					.getExpBudget()));
		}
		List<ConfigurationDesc> configDescList = conf.getConfigDescList();
		List<ConfigurationDesc> configDescListNew = new ArrayList<ConfigurationDesc>();
		if (configDescList != null) {
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

	public ConfigurationExpTracker findByPK(String tableType, String tableCode){
		open();
		Cursor dbQuery = database.query(CONFIGURATION_TABLE, ConfigurationExpTracker.allColumns,
				TABLE_TYPE + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
				+ tableType + SQLOperator.SINGLE_QUOTE
				+ SQLOperator.AND + TABLE_CODE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + tableCode
				+ SQLOperator.SINGLE_QUOTE, null,null,null,null);
		dbQuery.moveToFirst();
		ConfigurationExpTracker configTemp = null;
		while (!dbQuery.isAfterLast()) {
			configTemp = cursorToConfigurationOnly(dbQuery);
			break;
			
		}
		dbQuery.close();
		return configTemp;
	}
	public ConfigurationExpTracker updateConfiguration(
			ConfigurationExpTracker conf) {
		ContentValues values = new ContentValues();
		values.put(TABLE_TYPE, conf.getTableType());
		values.put(TABLE_CODE, conf.getTableCode());
		values.put(LOC_DESC, conf.getLocDesc());
		values.put(STATUS, conf.getStatus());
		open();
		database.beginTransaction();
		database.update(CONFIGURATION_TABLE, values,
				TABLE_TYPE + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
						+ conf.getTableType() + SQLOperator.SINGLE_QUOTE
						+ SQLOperator.AND + TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + conf.getTableCode()
						+ SQLOperator.SINGLE_QUOTE, null);
		ConfigurationExpTracker configExp = getConfigurationBasedOnTableCode(
				conf.getTableType(), conf.getTableCode(),
				StaticVariables.prefLang, false);
		ExpenseCategoryBudget expBudget = daoExpCatBudget
				.getExpenseCategoryBudget(conf.getTableType(),
						conf.getTableCode());
		if (expBudget != null) {
			// if null, then delete the existing, if not then update the
			// existing
			if (conf.getExpBudget() == null) {
				daoExpCatBudget.deleteExpenseBudget(expBudget);
			} else {
				configExp.setExpBudget(daoExpCatBudget.updateExpenseBudget(conf
						.getExpBudget()));
			}
		} else {
			configExp.setExpBudget(daoExpCatBudget.addNewExpenseBudget(conf
					.getExpBudget()));
		}
		List<ConfigurationDesc> configDescList = conf.getConfigDescList();
		List<ConfigurationDesc> configDescListNew = new ArrayList<ConfigurationDesc>();
		if (configDescList != null) {
			for (int i = 0; i < configDescList.size(); i++) {
				ConfigurationDesc configDescTemp = configDescList.get(i);
				configDescListNew.add(updateConfigurationDesc(configDescTemp));

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
		open();
		database.insert(CONFIG_DESC_TABLE, null, values);
		Cursor cursor = database.query(CONFIG_DESC_TABLE,
				ConfigurationDesc.allColumns, TABLE_TYPE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getTableType()
						+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
						+ TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getTableCode()
						+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
						+ DESC_LOCALE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getDescLocale()
						+ SQLOperator.SINGLE_QUOTE, null, null, null, null);
		cursor.moveToFirst();
		ConfigurationDesc configTemp = cursorToConfigDesc(cursor);
		cursor.close();

		return configTemp;
	}

	public ConfigurationDesc updateConfigurationDesc(ConfigurationDesc confDesc) {
		ContentValues values = new ContentValues();
		values.put(TABLE_TYPE, confDesc.getTableType());
		values.put(TABLE_CODE, confDesc.getTableCode());
		values.put(DESC_LOCALE, confDesc.getDescLocale());
		values.put(DESC, confDesc.getDescription());
		open();
		database.update(CONFIG_DESC_TABLE, values,
				TABLE_TYPE + SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
						+ confDesc.getTableType() + SQLOperator.SINGLE_QUOTE
						+ SQLOperator.AND + TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getTableCode()
						+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
						+ DESC_LOCALE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getDescLocale()
						+ SQLOperator.SINGLE_QUOTE, null);
		Cursor cursor = database.query(CONFIG_DESC_TABLE,
				ConfigurationDesc.allColumns, TABLE_TYPE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getTableType()
						+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
						+ TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getTableCode()
						+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND
						+ DESC_LOCALE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + confDesc.getDescLocale()
						+ SQLOperator.SINGLE_QUOTE, null, null, null, null);
		cursor.moveToFirst();
		ConfigurationDesc configTemp = cursorToConfigDesc(cursor);
		cursor.close();

		return configTemp;
	}

	private ConfigurationExpTracker cursorToExpenseCategoryWithLocalizedDesc(
			Cursor csr) {
		ConfigurationExpTracker config = new ConfigurationExpTracker();
		config.setTableType(csr.getString(0));
		config.setTableCode(csr.getString(1));
		config.setLocDesc(csr.getString(2));
		config.setStatus(csr.getInt(3));

		ExpenseCategoryBudget expBudget = new ExpenseCategoryBudget();
		expBudget.setTableType(config.getTableType());
		expBudget.setTableCode(config.getTableCode());
		try {
			expBudget.setBudgetAmountWeekly(csr.getInt(4));

		} catch (Exception e) {
			expBudget.setBudgetAmountWeekly(0);
		}
		try {

			expBudget.setBudgetAmountMonthly(csr.getInt(5));
		} catch (Exception e) {
			expBudget.setBudgetAmountMonthly(0);

		}
		config.setExpBudget(expBudget);
		if ((csr.getString(6) != null && !csr.getString(6).equals(
					SQLOperator.EMPTY))) {
				config.setLocDesc(csr.getString(6));
		}
		
		return config;
	}

	private ConfigurationExpTracker cursorToConfigurationWithLocalizedDesc(
			Cursor csr) {
		ConfigurationExpTracker config = new ConfigurationExpTracker();
		config.setTableType(csr.getString(0));
		config.setTableCode(csr.getString(1));
		config.setLocDesc(csr.getString(2));
		config.setStatus(csr.getInt(3));

		try {
			if ((csr.getString(4) != null && !csr.getString(4).equals(
					SQLOperator.EMPTY))) {
				config.setLocDesc(csr.getString(4));
			}
		} catch (Exception e) {

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

	public ConfigurationExpTracker getAllWithDesc(String tableType,
			String tableCode) {
		open();
		Cursor crTemp = database.query(CONFIGURATION_TABLE + " "
				+ CONFIGURATION_TABLE, ConfigurationExpTracker.allColumns,
				CONFIGURATION_TABLE + SQLOperator.DOT + TABLE_TYPE
						+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
						+ tableType + SQLOperator.SINGLE_QUOTE
						+ SQLOperator.AND + CONFIGURATION_TABLE
						+ SQLOperator.DOT + TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + tableCode
						+ SQLOperator.SINGLE_QUOTE, null, null, null, null);
		crTemp.moveToFirst();

		ConfigurationExpTracker configTemp = cursorToConfigurationOnly(crTemp);
		if (crTemp != null)
			crTemp.close();
		Cursor crTempDetail = database.query(CONFIG_DESC_TABLE + " "
				+ CONFIG_DESC_TABLE, ConfigurationDesc.allColumns,
				CONFIG_DESC_TABLE + SQLOperator.DOT + TABLE_TYPE
						+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE
						+ tableType + SQLOperator.SINGLE_QUOTE
						+ SQLOperator.AND + CONFIG_DESC_TABLE + SQLOperator.DOT
						+ TABLE_CODE + SQLOperator.EQUAL
						+ SQLOperator.SINGLE_QUOTE + tableCode
						+ SQLOperator.SINGLE_QUOTE, null, null, null, null);

		List<ConfigurationDesc> listOfCrTempDetail = new ArrayList<ConfigurationDesc>();
		crTempDetail.moveToFirst();
		while (!crTempDetail.isAfterLast()) {
			ConfigurationDesc configDesc = cursorToConfigDesc(crTempDetail);
			listOfCrTempDetail.add(configDesc);
			crTempDetail.moveToNext();
		}
		if (crTempDetail != null)
			crTempDetail.close();

		configTemp.setConfigDescList(listOfCrTempDetail);
		return configTemp;

	}

	public ConfigurationExpTracker changeStatus(String tableType,
			String tableCode, int newStatus) {
		ContentValues cv = new ContentValues();
		cv.put(STATUS, newStatus);
		open();
		database.beginTransaction();
		database.update(CONFIGURATION_TABLE, cv, TABLE_TYPE + SQLOperator.EQUAL
				+ SQLOperator.SINGLE_QUOTE + tableType
				+ SQLOperator.SINGLE_QUOTE + SQLOperator.AND + TABLE_CODE
				+ SQLOperator.EQUAL + SQLOperator.SINGLE_QUOTE + tableCode
				+ SQLOperator.SINGLE_QUOTE, null);
		database.setTransactionSuccessful();
		database.endTransaction();
		return getConfigurationBasedOnTableCode(tableType, tableCode,
				StaticVariables.prefLang, false);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
