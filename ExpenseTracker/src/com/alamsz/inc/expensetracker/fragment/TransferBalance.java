package com.alamsz.inc.expensetracker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;



public class TransferBalance extends ExpenseTrackerFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View layout = (View) inflater.inflate(R.layout.transfer_balance, container,
				false);
		initializeTabContent(layout);
		return layout;
	}
	
	private void initializeTabContent(View layout) {
		FormatHelper.setCurrentDateOnView((EditText) layout.findViewById(R.id.dateTransferText));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.spinner_item,
				StaticVariables.fundCatList);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), R.layout.spinner_item,
				StaticVariables.fundCatList);
		adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
		
		
		Spinner categorySpinner = (Spinner) layout.findViewById(R.id.categoryFromSpinner);
		categorySpinner.setAdapter(adapter);
		Spinner typeSpinner = (Spinner) layout.findViewById(R.id.categoryToSpinner);
		typeSpinner.setAdapter(adapter2);
		AdView mAdView = (AdView) layout.findViewById(R.id.adTransferView);
		AdUtility.displayAd(mAdView);
		
	}

}
