package com.alamsz.inc.expensetracker.fragment;

import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class WeeklyReportFragment extends ExpenseTrackerFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = (View) inflater.inflate(R.layout.weekly_report,
				container, false);
		TextView edWeek = (TextView) layout.findViewById(R.id.week);
		String edWeekText = "";
		String startDate = "";
		String endDate = "";

		List<Date> dateList = FormatHelper
				.getWeekStartAndEndDateFromDate(new Date());
		startDate = FormatHelper.formatDateForDisplay(dateList.get(0));
		endDate = FormatHelper.formatDateForDisplay(dateList.get(1));
		edWeekText = startDate + " - " + endDate;
		edWeek.setText(edWeekText);
		ArrayAdapter<String> fundSourceAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(),
				R.layout.spinner_item, StaticVariables.fundCatList); 
		fundSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.fundSourceSpinner);
		categorySpinner.setAdapter(fundSourceAdapter);

		return layout;
	}

}
