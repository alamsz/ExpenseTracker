package com.alamsz.inc.expensetracker;

import java.util.Collections;
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.adapter.ConfigurationExpAdapter;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class ConfigurationListActivity extends SherlockFragmentActivity {
	private ConfigurationService confService;
	private AdView mAdView;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.transaction_category_edit);
		String title = getString(R.string.expense_category_list);
		confService = new ConfigurationService(ExpenseTrackerActivity.dbHandler);
		List<ConfigurationExpTracker> confExpAdapterlist = null;
		if (StaticVariables.transCategory
				.equals(ConfigurationDAO.INCOME_CATEGORY)) {
			confExpAdapterlist = StaticVariables.listOfConfIncCat;
			title = getString(R.string.income_category_list);
		} else {
			confExpAdapterlist = StaticVariables.listOfConfExpCat;
		}
		TextView transcatView = (TextView) this
				.findViewById(R.id.transCatTitle);
		transcatView.setText(title);
		ConfigurationExpAdapter confAdapter = new ConfigurationExpAdapter(this,
				confExpAdapterlist);
		ListView lv = (ListView) this.findViewById(R.id.listoftranscat);
		lv.setAdapter(confAdapter);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mAdView = (AdView) findViewById(R.id.adTransCategoryView);
		AdUtility.displayAd(mAdView);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub

		View view = super.onCreateView(name, context, attrs);
		//
		return view;
	}

	public void showExpCatDetail(View view) {
		Log.d("test", "test");
	}

	public void createNewCategory(View view) {
		Intent intent = new Intent(this, ConfigurationDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);

		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home

			Intent intent = new Intent(this, ExpenseTrackerActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);
			break;
		}
		return true;
	}

	public void changeStatus(View view) {
		Switch statusSwitch = (Switch) view
				.findViewById(R.id.customSwitchOnOff);

		String textStatus = statusSwitch.getTag().toString();
		String[] tableCodeDesc = textStatus.split("_");
		String tableType = StaticVariables.transCategory;
		String tableCode = tableCodeDesc[0];
		int newStatus = 0;
		String textDescStr = tableCodeDesc[1];
		List<String> listOfTransCat = StaticVariables.listOfIncCat;

		listOfTransCat = StaticVariables.listOfExpCat;

		if (statusSwitch.isChecked()) {
			newStatus = 1;
			statusSwitch.setTextColor(Color.GREEN);
			listOfTransCat.add(textDescStr);
			Collections.sort(listOfTransCat);

		} else {
			statusSwitch.setTextColor(Color.GRAY);
			listOfTransCat.remove(textDescStr);

		}
		ConfigurationExpTracker configTemp = confService
				.changeConfigurationStatus(tableType, tableCode, newStatus);
		// check location, then remove and re add the list
		if (tableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
			ConfigurationExpTracker configOld = StaticVariables.mapOfExpenseCatBasedOnTableCode
					.get(tableCode);
			int positionOfList = StaticVariables.listOfConfExpCat
					.indexOf(configOld);
			StaticVariables.listOfConfExpCat.remove(positionOfList);
			StaticVariables.listOfConfExpCat.add(positionOfList, configTemp);
			StaticVariables.mapOfExpenseCatBasedOnDesc.remove(configOld
					.getLocDesc());
			StaticVariables.mapOfExpenseCatBasedOnTableCode.remove(configOld
					.getTableCode());
			StaticVariables.mapOfExpenseCatBasedOnDesc.put(
					configTemp.getLocDesc(), configTemp);
			StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
					configTemp.getTableCode(), configTemp);
		} else {
			ConfigurationExpTracker configOld = StaticVariables.mapOfIncomeCatBasedOnTableCode
					.get(tableCode);
			int positionOfList = StaticVariables.listOfConfIncCat
					.indexOf(configOld);
			StaticVariables.listOfConfIncCat.remove(positionOfList);
			StaticVariables.listOfConfIncCat.add(positionOfList, configTemp);
			StaticVariables.mapOfIncomeCatBasedOnDesc.remove(configOld
					.getLocDesc());
			StaticVariables.mapOfIncomeCatBasedOnTableCode.remove(configOld
					.getTableCode());
			StaticVariables.mapOfIncomeCatBasedOnDesc.put(
					configTemp.getLocDesc(), configTemp);
			StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
					configTemp.getTableCode(), configTemp);
		}

	}

}
