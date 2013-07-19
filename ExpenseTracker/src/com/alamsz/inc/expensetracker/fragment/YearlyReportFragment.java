package com.alamsz.inc.expensetracker.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

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
