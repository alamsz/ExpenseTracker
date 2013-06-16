package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationDesc;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ConfigurationDetailActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.conf_detail);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void addConfig(View view) {
		ConfigurationService confService = new ConfigurationService(
				ExpenseTrackerActivity.dbHandler);
		ConfigurationExpTracker conf = new ConfigurationExpTracker();
		List<ConfigurationDesc> confDescList = new ArrayList<ConfigurationDesc>();
		boolean validForProcess = true;
		EditText edCode = (EditText) findViewById(R.id.codeText);
		EditText edLocDesc = (EditText) findViewById(R.id.locDescText);
		EditText edEnDesc = (EditText) findViewById(R.id.enDescText);
		String edCodeStr = edCode.getText().toString();
		String edLocDescStr = edLocDesc.getText().toString();
		String edEnDescStr = edEnDesc.getText().toString();
		String tableType = StaticVariables.transCategory;
		ConfigurationExpTracker configTemp = null;
		String processMessage = String.format(
				getString(R.string.add_config_success), edCodeStr + " "
						+ edLocDescStr);
		if (edCodeStr.equals("") || edCodeStr.length() != 3) {
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
		if (validForProcess) {
			conf.setTableType(tableType);
			conf.setTableCode(edCodeStr);
			conf.setLocDesc(edLocDescStr);
			conf.setStatus(1);
			if (!edEnDescStr.equals("")) {
				ConfigurationDesc confDesc = new ConfigurationDesc(conf);
				confDesc.setDescLocale(DatabaseHandler.locale_US);
				confDesc.setDescription(edEnDescStr);
				confDescList.add(confDesc);
				conf.setConfigDescList(confDescList);
			}

			configTemp = confService.addNewData(conf);

			edCode.setText("");
			edEnDesc.setText("");
			edLocDesc.setText("");
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
			processMessage = String.format(
					getString(R.string.add_config_fails), edCodeStr + " "
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
