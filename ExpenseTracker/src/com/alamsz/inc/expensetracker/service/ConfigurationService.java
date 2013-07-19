package com.alamsz.inc.expensetracker.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudget;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudgetDAO;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ConfigurationService {
	private ConfigurationDAO daoConfig;
	private ExpenseCategoryBudgetDAO daoExpBudget;

	public ConfigurationService(DatabaseHandler dbHandler) {
		super();
		daoConfig = new ConfigurationDAO(dbHandler);
		daoExpBudget = new ExpenseCategoryBudgetDAO(daoConfig.open());

	}
	public ExpenseCategoryBudget getExpBudget(String tableCode){
		String tableType = ConfigurationDAO.EXPENSE_CATEGORY;
		return daoExpBudget.getExpenseCategoryBudget(tableType, tableCode);
	}
	public List<String> getExpenseCategoryListFromDB(boolean withEmpty,
			boolean onlyActive) {
		String tableType = "EXP_CAT";
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getExpenseCategoryListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);
		List<String> descList = new ArrayList<String>();
		if (withEmpty) {
			descList.add("");
		}
		for (Iterator iterator = configDescList.iterator(); iterator.hasNext();) {
			String tempDesc = ((ConfigurationExpTracker) iterator.next())
					.getLocDesc();
			descList.add(tempDesc);

		}
		return descList;
	}
	
	public List<String> getConfigurationListFromDB(String tableType, boolean withEmpty,
			boolean onlyActive) {
		
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getConfigurationListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);
		List<String> descList = new ArrayList<String>();
		if (withEmpty) {
			descList.add("");
		}
		for (Iterator iterator = configDescList.iterator(); iterator.hasNext();) {
			String tempDesc = ((ConfigurationExpTracker) iterator.next())
					.getLocDesc();
			descList.add(tempDesc);

		}
		return descList;
	}

	public List<String> getIncomeCategoryListFromDB(boolean withEmpty,
			boolean onlyActive) {
		String tableType = "INC_CAT";
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getConfigurationListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);
		List<String> descList = new ArrayList<String>();
		if (withEmpty) {
			descList.add("");
		}
		for (Iterator iterator = configDescList.iterator(); iterator.hasNext();) {
			String tempDesc = ((ConfigurationExpTracker) iterator.next())
					.getLocDesc();
			descList.add(tempDesc);

		}
		return descList;
	}

	public List<ConfigurationExpTracker> getExpenseCategoryListFromDB(
			boolean onlyActive) {
		String tableType = "EXP_CAT";
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getExpenseCategoryListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);

		return configDescList;
	}
	
	public List<ConfigurationExpTracker> getConfigurationListFromDB(String tableType,
			boolean onlyActive) {
		
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getConfigurationListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);

		return configDescList;
	}

	public List<ConfigurationExpTracker> getIncomeCategoryListFromDB(
			boolean onlyActive) {
		String tableType = "INC_CAT";
		List<ConfigurationExpTracker> configDescList = daoConfig
				.getConfigurationListPerLocale(tableType,
						StaticVariables.prefLang, onlyActive);

		return configDescList;
	}

	public String getCodeOfExpenseCategoryDesc(String description) {
		ConfigurationExpTracker config = (ConfigurationExpTracker) StaticVariables.mapOfExpenseCatBasedOnDesc
				.get(description);
		if (config == null) {
			return "";
		}
		return config.getTableCode();
	}

	public ConfigurationExpTracker changeConfigurationStatus(String tableType,
			String tableCode, int newStatus) {
		return daoConfig.changeStatus(tableType, tableCode, newStatus);

	}
	public ConfigurationExpTracker findByPKConfig(String tableType, String tableCode){
		return daoConfig.findByPK(tableType, tableCode);
	}

	public ConfigurationExpTracker addNewData(ConfigurationExpTracker conf) {

		return daoConfig.addNewConfiguration(conf);

	}

	public ConfigurationExpTracker updateConfigurationData(
			ConfigurationExpTracker conf) {
		return daoConfig.updateConfiguration(conf);

	}

	public ConfigurationExpTracker getConfigurationExpTrackerWithAllDesc(
			String tableType, String tableCode) {
		ConfigurationExpTracker configExpTracker = daoConfig.getAllWithDesc(
				tableType, tableCode);
		ExpenseCategoryBudget expBudget = daoExpBudget
				.getExpenseCategoryBudget(configExpTracker.getTableType(),
						configExpTracker.getTableCode());
		configExpTracker.setExpBudget(expBudget);
		return configExpTracker;
	}
}
