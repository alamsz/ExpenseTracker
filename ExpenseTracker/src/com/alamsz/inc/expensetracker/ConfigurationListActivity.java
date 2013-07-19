package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.alamsz.inc.expensetracker.adapter.ConfigurationExpAdapter;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationDesc;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudget;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class ConfigurationListActivity extends SherlockFragmentActivity {
	private ConfigurationService confService;
	private AdView mAdView;
	List<ConfigurationExpTracker> confExpAdapterlist;
	protected ActionMode mActionMode;
	public int selectedItem = 0;
	String title;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		setContentView(R.layout.transaction_category_edit);
		title = getString(R.string.expense_category_list);
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler, this);
		confService = new ConfigurationService(dbHandler);

		setUpViewContent();
		// lv.setOnItemLongClickListener(checkedMultipleItemOnLongClick());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mAdView = (AdView) findViewById(R.id.adTransCategoryView);
		AdUtility.displayAd(mAdView);

	}

	private void setUpViewContent() {
		title = getString(R.string.expense_category_list);
		if (StaticVariables.confType.equals(ConfigurationDAO.INCOME_CATEGORY)) {
			confExpAdapterlist = StaticVariables.listOfConfIncCat;
			title = getString(R.string.income_category_list);
		} else if (StaticVariables.confType
				.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
			confExpAdapterlist = StaticVariables.listOfConfExpCat;
		}/*
		 * else if
		 * (StaticVariables.confType.equals(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE
		 * )){ confExpAdapterlist = StaticVariables.listOfFundSource; }
		 */
		else {
			confExpAdapterlist = confService.getConfigurationListFromDB(
					StaticVariables.confType, false);
			title = getString(R.string.configuration_list);
		}
		TextView transcatView = (TextView) this
				.findViewById(R.id.transCatTitle);
		transcatView.setText(title);
		ConfigurationExpAdapter confAdapter = new ConfigurationExpAdapter(this,
				confExpAdapterlist);
		ListView lv = (ListView) findViewById(R.id.listoftranscat);

		lv.setAdapter(confAdapter);
		confAdapter.notifyDataSetChanged();
		lv.setOnItemClickListener(editConfigurationShow());
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		super.onPostResume();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUpViewContent();

	}

	/*
	 * private OnItemLongClickListener checkedMultipleItemOnLongClick() {
	 * 
	 * return new OnItemLongClickListener() {
	 * 
	 * @Override public boolean onItemLongClick(AdapterView<?> parentView, View
	 * viewClicked, int arg2, long arg3) {
	 * 
	 * Toast.makeText(getApplicationContext(), "row clicked",
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * 
	 * return true;
	 * 
	 * } }; }
	 */
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		// Called when the action mode is created; startActionMode() was called
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			// Assumes that you have "contexual.xml" menu resources
			// inflater.inflate(R.menu.setting, menu);
			return true;
		}

		// Called each time the action mode is shown. Always called after
		// onCreateActionMode, but
		// may be called multiple times if the mode is invalidated.
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		// Called when the user selects a contextual menu item
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			}
			return true;
		}

		// Called when the user exits the action mode
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			selectedItem = 0;
		}
	};

	private OnItemClickListener editConfigurationShow() {
		Log.d("masuk", "test");
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View viewClicked,
					int position, long arg3) {

				ConfigurationExpTracker confTemp = confExpAdapterlist
						.get(position);

				ConfigurationExpTracker confWithAllDesc = confService
						.getConfigurationExpTrackerWithAllDesc(
								confTemp.getTableType(),
								confTemp.getTableCode());

			
				Intent intent = new Intent();
				ExpenseCategoryBudget expBudget = confWithAllDesc
						.getExpBudget();
				intent.setClass(ConfigurationListActivity.this,
						ConfigurationDetailActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
				intent.putExtra("btn_status", getString(R.string.btn_save));
				intent.putExtra("tableType", confWithAllDesc.getTableType());
				intent.putExtra(getString(R.string.code),
						confWithAllDesc.getTableCode());
				intent.putExtra(getString(R.string.status), confWithAllDesc.getStatus());
				if (StaticVariables.confType
						.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
					intent.putExtra("heading",
							getString(R.string.exp_category_detail));
				} else if (StaticVariables.confType
						.equals(ConfigurationDAO.INCOME_CATEGORY)) {
					intent.putExtra("heading",
							getString(R.string.inc_category_detail));
				} else {
					intent.putExtra("heading",
							getString(R.string.config_detail));
				}
				intent.putExtra(getString(R.string.loc_desc),
						confWithAllDesc.getLocDesc());
				if (expBudget != null) {
					intent.putExtra(getString(R.string.weekly_budget),
							expBudget.getBudgetAmountWeekly());
					intent.putExtra(getString(R.string.monthly_budget),
							expBudget.getBudgetAmountMonthly());
				}

				List<ConfigurationDesc> confDescList = confWithAllDesc
						.getConfigDescList();
				for (Iterator iterator = confDescList.iterator(); iterator
						.hasNext();) {
					ConfigurationDesc configurationDesc = (ConfigurationDesc) iterator
							.next();
					if (configurationDesc == null) {
						break;
					} else {
						intent.putExtra(configurationDesc.getDescLocale(),
								configurationDesc.getDescription());
					}

				}
				startActivity(intent);

			}

		};
	}

	public void showExpCatDetail(View view) {
		Button btnCode = (Button) view;

		Toast.makeText(getApplicationContext(), (String) btnCode.getText(),
				Toast.LENGTH_SHORT).show();
		if (mActionMode == null)
			mActionMode = ConfigurationListActivity.this
					.startActionMode(mActionModeCallback);
		// view.setSelected(true);
		if (!btnCode.isSelected()) {
			// btnCode.setTextColor(Color.RED);
			btnCode.setSelected(true);

			selectedItem++;
		} else {
			// btnCode.setTextColor(Color.BLACK);
			btnCode.setSelected(false);
			selectedItem--;
		}
		mActionMode.setTitle(selectedItem + " selected");
	}

	public void createNewCategory(View view) {
		Intent intent = new Intent(this, ConfigurationDetailActivity.class);
		if (title.equals(getString(R.string.expense_category_list))) {
			intent.putExtra("heading", getString(R.string.exp_category_detail));
		} else if (title.equals(getString(R.string.income_category_list))) {
			intent.putExtra("heading", getString(R.string.inc_category_detail));
		} else {
			intent.putExtra("heading", getString(R.string.config_detail));
		}
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
		String tableType = StaticVariables.confType;
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
		} else if (tableType.equals(ConfigurationDAO.INCOME_CATEGORY)) {
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
		} else if (tableType.equals(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE)) {
			StaticVariables.listOfFundSource = new ArrayList<ConfigurationExpTracker>();
			StaticVariables.fundCatList = new ArrayList<String>();
			StaticVariables.fundCatListCode = new ArrayList<String>();
			StaticVariables.mapOfFundCategory = new HashMap<String, String>();
			StaticVariables.listOfFundSource = confService.getConfigurationListFromDB(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, false);
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

	}

}
