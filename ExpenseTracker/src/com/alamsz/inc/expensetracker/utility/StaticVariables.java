package com.alamsz.inc.expensetracker.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;

public final class StaticVariables {
	//public static final String financeTipsGood = getString(R.string.finance_tips_good);
	public static final String financeTipsGoodLimit = "";
	public static final String financeTipsModerate = "";
	public static final String financeTipsModerateLimit = "";
	public static final String financeTipsBad = "";
	public static Map<String,ConfigurationExpTracker> mapOfExpenseCatBasedOnTableCode = new HashMap<String,ConfigurationExpTracker>();
	public static Map<String,ConfigurationExpTracker> mapOfIncomeCatBasedOnTableCode = new HashMap<String,ConfigurationExpTracker>();
	
	public static Map<String, ConfigurationExpTracker> mapOfFundCategory = new HashMap<String,ConfigurationExpTracker>();
	public static Map<String, String> mapOfTransType = new HashMap<String,String>();
	public static List<ConfigurationExpTracker> listOfConfIncCat = new ArrayList<ConfigurationExpTracker>();
	public static List<ConfigurationExpTracker> listOfConfExpCat = new ArrayList<ConfigurationExpTracker>();
	public static List<ConfigurationExpTracker> listOfFundSource = new ArrayList<ConfigurationExpTracker>();
	public  static Locale currencyLocale = Locale.getDefault();
	public  static Locale appLocale = Locale.getDefault();
	public static String prefLang = "in_ID";
	public static String confType="INC_CAT";
	public static List<String> newsArray;
	public static int newsIndex;
	public static String[] monthStrArr;
	//public static String payRecType;
	public static final String MODE_ADD = "ADD";
	public static final String MODE_MOD = "MOD";
	public static final String MODE = "mode";
	public static SQLiteDatabase database;
	public static String notSet = "";
	public static String notSetBudgetExpense = "";
	public static String expenseOverBudget = "";
	public static String expenseOkayBudget = "";
	public static String totalCategoryText = "";
}
