package com.alamsz.inc.expensetracker.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class YearlyReportFragment extends ExpenseTrackerFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = (View) inflater.inflate(R.layout.yearly_report, container,
				false);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		TextView edYear = (TextView) layout.findViewById(R.id.year);
		edYear.setText(sdf.format(now));
		List <ConfigurationExpTracker> fundSourceList = new ArrayList<ConfigurationExpTracker>();
		fundSourceList.add(FormatHelper.initConfig(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE));
		ConfigurationService confService = new ConfigurationService(FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler, this.getActivity()));
		fundSourceList.addAll(confService.getConfigurationListFromDB(
						ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, true));
		ArrayAdapter<ConfigurationExpTracker> fundSourceAdapter = new ArrayAdapter<ConfigurationExpTracker>(
				getActivity().getApplicationContext(),
				R.layout.spinner_item, fundSourceList); 
		fundSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.fundSourceSpinner);
		categorySpinner.setAdapter(fundSourceAdapter);
		return layout;
	}
	
}
