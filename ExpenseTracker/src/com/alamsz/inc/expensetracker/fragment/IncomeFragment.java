package com.alamsz.inc.expensetracker.fragment;


import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class IncomeFragment extends ExpenseTrackerFragment {
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        if (container == null) {
	            // We have different layouts, and in one of them this
	            // fragment's containing frame doesn't exist.  The fragment
	            // may still be created from its saved state, but there is
	            // no reason to try to create its view hierarchy because it
	            // won't be displayed.  Note this is not needed -- we could
	            // just run the code below, where we would create and return
	            // the view hierarchy; it would just never be used.
	            return null;
	        }
	        
	        View layout = (View)inflater.inflate(R.layout.income, container, false);
			initializeTabContent(layout);
			return layout;
	    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		/*AutomaticScalingLayout at = new AutomaticScalingLayout();
		at.scaleContents(view);*/
	}

	private void initializeTabContent(View layout) {
		EditText dateInputText = (EditText) layout
		.findViewById(R.id.dateInputText);
		
		if(dateInputText!=null){
			ExpenseTrackerActivity expActivity = (ExpenseTrackerActivity) getActivity();
			FormatHelper.setCurrentDateOnView(dateInputText);
			ArrayAdapter<String> fundDestAdapter = new ArrayAdapter<String>(
					expActivity.getApplicationContext(), R.layout.spinner_item,
					StaticVariables.fundCatList);
			fundDestAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

			List<String> incomeCategoryList = StaticVariables.listOfIncCat;
			ArrayAdapter<String> incomeCategoryAdapter = new ArrayAdapter<String>(
					expActivity.getApplicationContext(), R.layout.spinner_item,
					incomeCategoryList);
			incomeCategoryAdapter
					.setDropDownViewResource(R.layout.spinner_dropdown_item);
			Spinner categorySpinner = (Spinner) layout
					.findViewById(R.id.categorySpinner);
			categorySpinner.setAdapter(fundDestAdapter);

			Spinner typeSpinner = (Spinner) layout
					.findViewById(R.id.incomeCategorySpinner);
			typeSpinner.setAdapter(incomeCategoryAdapter);
		}
		AdView mAdView = (AdView) layout.findViewById(R.id.adIncomeView);
		AdUtility.displayAd(mAdView);
		
	}
	
}
