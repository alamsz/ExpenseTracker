package com.alamsz.inc.expensetracker.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ConfigurationExpAdapter extends BaseAdapter {

	private static final String CREDIT = "K";
	private static final String DEBET = "D";
	private Activity activity;
	private List<ConfigurationExpTracker> data;
	private static LayoutInflater inflater = null;
	String tableType = "";
	public ConfigurationService confService = null;
	static class ViewHolder
	{
		public Button btnCode;
		public TextView txtDesc;
		public Switch swtStatus;
		
	}
	// public ImageLoader imageLoader;
	public ConfigurationExpAdapter(Activity a, List<ConfigurationExpTracker> d) {
		activity = a;
		confService = new ConfigurationService(FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler, activity));
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader=new ImageLoader(activity.getApplicationContext());
	}
	
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View vi = convertView;
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.trans_category_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.btnCode = (Button) vi.findViewById(R.id.btnCode);
			viewHolder.txtDesc = (TextView) vi.findViewById(R.id.description);
			viewHolder.swtStatus = (Switch) vi.findViewById(R.id.customSwitchOnOff);
			vi.setTag(viewHolder);
		}
		ViewHolder vHold = (ViewHolder) vi.getTag();
		ConfigurationExpTracker transaction = new ConfigurationExpTracker();
		transaction = data.get(position);
		tableType = transaction.getTableType();
		vHold.swtStatus.setVisibility(Button.VISIBLE);
		String[] keyword = DatabaseHandler.persistentConfig;
		// to make sure config persistent not erased
		for (int i = 0; i < keyword.length; i++) {
			String value = keyword[i];
			String confValue = transaction.getTableType()+transaction.getTableCode();
			if(value.equals(confValue)){
				vHold.swtStatus.setVisibility(Button.INVISIBLE);
				break;
			}
			
		}
		// Setting all values in listview
		
		vHold.btnCode.setText(transaction.getTableCode());
		vHold.txtDesc.setText(transaction.getLocDesc());
		vHold.swtStatus.setTag(transaction.getTableCode() + "_"
				+ transaction.getLocDesc());
		vHold.swtStatus.setChecked(transaction.getStatus() == 1);
		
		vHold.swtStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						String textStatus = buttonView.getTag().toString();
						String[] tableCodeDesc = textStatus.split("_");
						String tableCode = tableCodeDesc[0];
						int newStatus = 0;
						
						
						if (isChecked) {
							newStatus = 1;
							buttonView.setTextColor(Color.GREEN);
							

						} else {
							buttonView.setTextColor(Color.GRAY);
							

						}
						ConfigurationExpTracker configTemp = confService
								.changeConfigurationStatus(tableType,
										tableCode, newStatus);
						// check location, then remove and re add the list
						if (tableType.equals(ConfigurationDAO.EXPENSE_CATEGORY)) {
							StaticVariables.listOfConfExpCat = new ArrayList<ConfigurationExpTracker>();
							StaticVariables.listOfConfExpCat
									.add(initConfig(ConfigurationDAO.EXPENSE_CATEGORY));
							StaticVariables.listOfConfExpCat.addAll(confService
									.getConfigurationListFromDB(
											ConfigurationDAO.EXPENSE_CATEGORY,
											true));
							StaticVariables.mapOfExpenseCatBasedOnTableCode.remove(configTemp.getTableCode());
							StaticVariables.mapOfExpenseCatBasedOnTableCode.put(configTemp.getTableCode(), configTemp);
							
							
						} else if (tableType
								.equals(ConfigurationDAO.INCOME_CATEGORY)) {
							StaticVariables.listOfConfIncCat = new ArrayList<ConfigurationExpTracker>();
							StaticVariables.listOfConfIncCat
									.add(initConfig(ConfigurationDAO.INCOME_CATEGORY));
							StaticVariables.listOfConfIncCat.addAll(confService
									.getConfigurationListFromDB(
											ConfigurationDAO.INCOME_CATEGORY,
											true));
							StaticVariables.mapOfIncomeCatBasedOnTableCode.remove(configTemp.getTableCode());
							StaticVariables.mapOfIncomeCatBasedOnTableCode.put(configTemp.getTableCode(), configTemp);
							
							
						} else if (tableType
								.equals(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE)) {
							StaticVariables.listOfFundSource = new ArrayList<ConfigurationExpTracker>();
							StaticVariables.listOfFundSource
									.add(initConfig(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE));
							StaticVariables.listOfFundSource.addAll(confService
									.getConfigurationListFromDB(
											ConfigurationDAO.FUND_SOURCE_TABLE_TYPE,
											true));
							StaticVariables.mapOfFundCategory.remove(configTemp.getTableCode());
							StaticVariables.mapOfFundCategory.put(configTemp.getTableCode(), configTemp);
							
						
						}
						

					}
				});
		
		int r = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 5 + 50;
		int g = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 50 + 150;
		int b = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 30 + 100;
		vHold.btnCode.setBackgroundColor(Color.rgb(r, g, b));
		// btnCode.setOnClickListener();
		return vi;
	}
	
	private ConfigurationExpTracker initConfig(String tableType) {
		ConfigurationExpTracker configIncCat = new ConfigurationExpTracker();
		configIncCat.setTableType(tableType);
		configIncCat.setTableCode("");
		configIncCat.setLocDesc("");
		return configIncCat;
	}

}
