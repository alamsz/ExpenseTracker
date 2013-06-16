package com.alamsz.inc.expensetracker.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ConfigurationService {
	private ConfigurationDAO daoConfig;

	public ConfigurationService(DatabaseHandler dbHandler) {
		super();
		daoConfig = new ConfigurationDAO(dbHandler);
		daoConfig.open();
	}

	public List<String> getExpenseCategoryListFromDB(boolean withEmpty,
			boolean onlyActive) {
		String tableType = "EXP_CAT";
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

	public ConfigurationExpTracker addNewData(ConfigurationExpTracker conf) {
		return daoConfig.addNewConfiguration(conf);

	}
}
