package com.alamsz.inc.expensetracker.fragment;

import java.text.SimpleDateFormat;
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
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class MonthlyReportFragment extends ExpenseTrackerFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = (View) inflater.inflate(R.layout.monthly_report, container,
				false);
		
		ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter.createFromResource(
				getActivity().getApplicationContext(),
				R.array.months, R.layout.spinner_item);
		monthsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner monthSpinner = (Spinner) layout.findViewById(R.id.monthSpinner);
		monthSpinner.setAdapter(monthsAdapter);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		TextView edYear = (TextView) layout.findViewById(R.id.year);
		edYear.setText(sdf.format(now));
		sdf = new SimpleDateFormat("MM");
		monthSpinner.setSelection(Integer.parseInt(sdf.format(now))-1);
		
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
