package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.HashMap;
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
				if (tableType.equals(ConfigurationDAO.INCOME_CATEGORY)) {
					StaticVariables.listOfIncCat.add(configTemp.getLocDesc());
					StaticVariables.listOfConfIncCat.add(configTemp);
					StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
							configTemp.getTableCode(), configTemp);
					StaticVariables.mapOfIncomeCatBasedOnDesc.put(
							configTemp.getLocDesc(), configTemp);

				} else {
					StaticVariables.listOfExpCat.add(configTemp.getLocDesc());
					StaticVariables.listOfConfExpCat.add(configTemp);
					StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
							configTemp.getTableCode(), configTemp);
					StaticVariables.mapOfExpenseCatBasedOnDesc.put(
							configTemp.getLocDesc(), configTemp);

				}
			} else {
				conf.setExpBudget(expBudget);
				configTemp = confService.updateConfigurationData(conf);
				if (tableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
					ConfigurationExpTracker configOld = StaticVariables.mapOfExpenseCatBasedOnTableCode
							.get(configTemp.getTableCode());
					int positionOfList = StaticVariables.listOfConfExpCat
							.indexOf(configOld);
					StaticVariables.listOfConfExpCat.remove(positionOfList);
					StaticVariables.listOfConfExpCat.add(positionOfList,
							configTemp);
					StaticVariables.mapOfExpenseCatBasedOnDesc.remove(configOld
							.getLocDesc());
					StaticVariables.mapOfExpenseCatBasedOnTableCode
							.remove(configOld.getTableCode());
					StaticVariables.mapOfExpenseCatBasedOnDesc.put(
							configTemp.getLocDesc(), configTemp);
					StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
							configTemp.getTableCode(), configTemp);
				} else if (tableType.equals(ConfigurationDAO.INCOME_CATEGORY)) {
					ConfigurationExpTracker configOld = StaticVariables.mapOfIncomeCatBasedOnTableCode
							.get(configTemp.getTableCode());
					int positionOfList = StaticVariables.listOfConfIncCat
							.indexOf(configOld);
					StaticVariables.listOfConfIncCat.remove(positionOfList);
					StaticVariables.listOfConfIncCat.add(positionOfList,
							configTemp);
					StaticVariables.mapOfIncomeCatBasedOnDesc.remove(configOld
							.getLocDesc());
					StaticVariables.mapOfIncomeCatBasedOnTableCode
							.remove(configOld.getTableCode());
					StaticVariables.mapOfIncomeCatBasedOnDesc.put(
							configTemp.getLocDesc(), configTemp);
					StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
							configTemp.getTableCode(), configTemp);
				} else if (tableType.equals(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE)) {
					
					StaticVariables.listOfFundSource = new ArrayList<ConfigurationExpTracker>();
					StaticVariables.fundCatList = new ArrayList<String>();
					StaticVariables.fundCatListCode = new ArrayList<String>();
					StaticVariables.mapOfFundCategory = new HashMap<String, String>();
					StaticVariables.listOfFundSource = confService.getConfigurationListFromDB(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, false);
					
					//could not find better way to get code from the list;
					StaticVariables.fundCatList.add("");
					StaticVariables.fundCatListCode.add("");
					for (int i = 0; i < StaticVariables.listOfFundSource.size(); i++) {
						ConfigurationExpTracker configExp = StaticVariables.listOfFundSource.get(i);
						StaticVariables.mapOfFundCategory.put(configExp.getTableCode(), configExp.getLocDesc());
						if(configExp.getStatus()==1){
							StaticVariables.fundCatList.add(configExp.getLocDesc());
							StaticVariables.fundCatListCode.add(configExp.getTableCode());
						}
					}
					//Collections.sort(StaticVariables.fundCatList);
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

}
