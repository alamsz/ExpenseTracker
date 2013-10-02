package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationDesc;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudget;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.appbrain.AppBrain;
import com.appbrain.AppBrainBanner;

public class ConfigurationDetailActivity extends SherlockFragmentActivity {
	public String confTableType;
	public String confTableCode = "";
	public String confLocDesc = "";
	public String confEnDesc = "";
	public String mode = "";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		confTableType = StaticVariables.confType;
		if (confTableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
			setContentView(R.layout.exp_detail);
		} else {
			setContentView(R.layout.conf_detail);
		}
		Intent intent = getIntent();
		AppBrain.init(this);
		setDetailOnShow(intent);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setDetailOnShow(Intent intent) {
		String status = intent.getStringExtra("btn_status");
		mode = "ADD";
		TextView txtHeading = (TextView) findViewById(R.id.confDetailTitle);
		txtHeading.setText(intent.getStringExtra("heading"));
		Switch statusSwitch = (Switch) findViewById(R.id.switchStatus);
		statusSwitch.setChecked(true);
		if (status != null && status.equals(getString(R.string.btn_save))) {
			mode = "MOD";

			EditText edCode = (EditText) findViewById(R.id.codeText);
			edCode.setFocusable(false);
			edCode.setFocusableInTouchMode(false);
			EditText edLocDesc = (EditText) findViewById(R.id.locDescText);
			EditText edEnDesc = (EditText) findViewById(R.id.enDescText);
			edCode.setText(intent.getStringExtra(getString(R.string.code)));
			edLocDesc.setText(intent
					.getStringExtra(getString(R.string.loc_desc)));
			statusSwitch.setChecked(intent.getIntExtra(getString(R.string.status),1)==1);
			edEnDesc.setText(intent.getStringExtra(DatabaseHandler.locale_US));
			EditText edWeekBudget = (EditText) findViewById(R.id.weeklyBudgeetText);
			EditText edMonthlyBudget = (EditText) findViewById(R.id.monthlyBudgetText);
			if (confTableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
				int intExtraWeeklyBudget = intent.getIntExtra(
						getString(R.string.weekly_budget), 0);
				if (intExtraWeeklyBudget > 0)
					edWeekBudget.setText(String.valueOf(intExtraWeeklyBudget));

				int intExtraMonthBudget = intent.getIntExtra(
						getString(R.string.monthly_budget), 0);
				if (intExtraMonthBudget > 0)
					edMonthlyBudget
							.setText(String.valueOf(intExtraMonthBudget));
			}

			Button btnSave = (Button) findViewById(R.id.btnAddorUpdConfig);
			btnSave.setText(status);
			btnSave.setVisibility(Button.VISIBLE);
			String[] keyword = DatabaseHandler.persistentConfig;
			// to make sure config persistent not erased
			for (int i = 0; i < keyword.length; i++) {
				String value = keyword[i];
				String confValue = confTableType+edCode.getText().toString();
				if(value.equals(confValue)){
					btnSave.setVisibility(Button.INVISIBLE);
					break;
				}
				
			}

		}
		addBanner();
	}

	public void addConfig(View view) {
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler, this);
		ConfigurationService confService = new ConfigurationService(dbHandler);
		ConfigurationExpTracker conf = new ConfigurationExpTracker();
		List<ConfigurationDesc> confDescList = new ArrayList<ConfigurationDesc>();
		boolean validForProcess = true;
		EditText edCode = (EditText) findViewById(R.id.codeText);
		EditText edLocDesc = (EditText) findViewById(R.id.locDescText);
		EditText edEnDesc = (EditText) findViewById(R.id.enDescText);
		EditText edWeeklyAmount = (EditText) findViewById(R.id.weeklyBudgeetText);
		EditText edMonthlyAmount = (EditText) findViewById(R.id.monthlyBudgetText);
		String edCodeStr = edCode.getText().toString();
		String edLocDescStr = edLocDesc.getText().toString();
		String edEnDescStr = edEnDesc.getText().toString();
		String weeklyAmountStr = edWeeklyAmount.getText().toString();
		String monthlyAmountStr = edMonthlyAmount.getText().toString();
		ExpenseCategoryBudget expBudget = null;

		String tableType = confTableType;
		// only instantiate the object when the amount filled in
		if (!weeklyAmountStr.equals("") || !monthlyAmountStr.equals("")) {
			expBudget = new ExpenseCategoryBudget();
			expBudget.setTableType(tableType);
			expBudget.setTableCode(edCodeStr);
			// only set when value not empty
			if (!weeklyAmountStr.equals(""))
				expBudget.setBudgetAmountWeekly(Integer
						.parseInt(weeklyAmountStr));
			// only set when the value not empty
			if (!monthlyAmountStr.equals(""))
				expBudget.setBudgetAmountMonthly(Integer
						.parseInt(monthlyAmountStr));

		}
		ConfigurationExpTracker configTemp = null;
		String processMessage = String.format(
				getString(R.string.add_config_success), edCodeStr + " "
						+ edLocDescStr);
		if (edCodeStr.equals("")) {
			validForProcess = false;
			edCode.setError(getString(R.string.cat_code_required));
		}
		if (edLocDescStr.equals("")) {
			validForProcess = false;
			edLocDesc.setError(getString(R.string.loc_desc_required));

		}
		if (edEnDescStr.equals("")) {
			validForProcess = false;
			edLocDesc.setError(getString(R.string.en_desc_required));

		}
		ConfigurationExpTracker configExist = confService.findByPKConfig(tableType, edCodeStr);
		if(configExist != null && mode.equals("ADD")){
			validForProcess = false;
			
		}
		if (validForProcess) {
			conf.setTableType(tableType);
			conf.setTableCode(edCodeStr);
			conf.setLocDesc(edLocDescStr);
			Switch statusSwitch = (Switch) findViewById(R.id.switchStatus);
			conf.setStatus(statusSwitch.isChecked()?1:0);
			if (!edEnDescStr.equals("")) {
				ConfigurationDesc confDesc = new ConfigurationDesc(conf);
				confDesc.setDescLocale(DatabaseHandler.locale_US);
				confDesc.setDescription(edEnDescStr);
				confDescList.add(confDesc);
				conf.setConfigDescList(confDescList);
			}
			if (mode.equals("ADD")) {
				conf.setExpBudget(expBudget);
				configTemp = confService.addNewData(conf);
				edCode.setText("");
				edEnDesc.setText("");
				edLocDesc.setText("");
				edMonthlyAmount.setText("");
				edWeeklyAmount.setText("");
				if(configTemp.getStatus()==1){
					if (tableType.equals(ConfigurationDAO.INCOME_CATEGORY)) {
						
						StaticVariables.listOfConfIncCat.add(configTemp);
						StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
								configTemp.getTableCode(), configTemp);
						

					} else if (tableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)){
						
						StaticVariables.listOfConfExpCat.add(configTemp);
						StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
								configTemp.getTableCode(), configTemp);
						

					}else {
						StaticVariables.listOfFundSource.add(configTemp);
						StaticVariables.mapOfFundCategory.put(
								configTemp.getTableCode(), configTemp);
					}
				}
				
			} else {
				conf.setExpBudget(expBudget);
				configTemp = confService.updateConfigurationData(conf);
				String tableCode = configTemp.getTableCode();
				// check location, then remove and re add the list
				// check location, then remove and re add the list
				if (tableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
					StaticVariables.listOfConfExpCat = new ArrayList<ConfigurationExpTracker>();
					StaticVariables.listOfConfExpCat
							.add(initConfig(ConfigurationDAO.EXPENSE_CATEGORY));
					StaticVariables.listOfConfExpCat.addAll(confService
							.getConfigurationListFromDB(
									ConfigurationDAO.EXPENSE_CATEGORY,
									true));
					StaticVariables.mapOfExpenseCatBasedOnTableCode = new HashMap<String, ConfigurationExpTracker>();
					for (Iterator iterator = StaticVariables.listOfConfExpCat.iterator(); iterator
							.hasNext();) {
						ConfigurationExpTracker type = (ConfigurationExpTracker) iterator.next();
						StaticVariables.mapOfExpenseCatBasedOnTableCode.put(type.getTableCode(), type);
					}
					
				} else if (tableType
						.equals(ConfigurationDAO.INCOME_CATEGORY)) {
					StaticVariables.listOfConfIncCat = new ArrayList<ConfigurationExpTracker>();
					StaticVariables.listOfConfIncCat
							.add(initConfig(ConfigurationDAO.INCOME_CATEGORY));
					StaticVariables.listOfConfIncCat.addAll(confService
							.getConfigurationListFromDB(
									ConfigurationDAO.INCOME_CATEGORY,
									true));
					StaticVariables.mapOfIncomeCatBasedOnTableCode = new HashMap<String, ConfigurationExpTracker>();
					for (Iterator iterator = StaticVariables.listOfConfIncCat.iterator(); iterator
							.hasNext();) {
						ConfigurationExpTracker type = (ConfigurationExpTracker) iterator.next();
						StaticVariables.mapOfIncomeCatBasedOnTableCode.put(type.getTableCode(), type);
					}
				} else if (tableType
						.equals(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE)) {
					StaticVariables.listOfFundSource = new ArrayList<ConfigurationExpTracker>();
					StaticVariables.listOfFundSource
							.add(initConfig(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE));
					StaticVariables.listOfFundSource.addAll(confService
							.getConfigurationListFromDB(
									ConfigurationDAO.FUND_SOURCE_TABLE_TYPE,
									true));
					StaticVariables.mapOfFundCategory = new HashMap<String, ConfigurationExpTracker>();
					for (Iterator iterator = StaticVariables.listOfFundSource.iterator(); iterator
							.hasNext();) {
						ConfigurationExpTracker type = (ConfigurationExpTracker) iterator.next();
						StaticVariables.mapOfFundCategory.put(type.getTableCode(), type);
					}
				
				}
				processMessage = String.format(
						getString(R.string.mod_config_success), edCodeStr + " "
								+ edLocDescStr);
			}

		} else if(configExist!=null){
			processMessage = String.format(getString(R.string.config_record_exist),edCodeStr);
		} else {
			String message = mode.equals("ADD") ? getString(R.string.add_config_fails)
					: getString(R.string.mod_config_fails);
			processMessage = String.format(message, edCodeStr + " "
					+ edLocDescStr);
		}
		Toast.makeText(this, processMessage, Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, ConfigurationListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		return true;
	}

	private ConfigurationExpTracker initConfig(String tableType) {
		ConfigurationExpTracker configIncCat = new ConfigurationExpTracker();
		configIncCat.setTableType(tableType);
		configIncCat.setTableCode("");
		configIncCat.setLocDesc("");
		return configIncCat;
	}
	
	public void addBanner(){
		AppBrainBanner banner = new AppBrainBanner(getApplicationContext());
		banner.requestAd();
	}
}
