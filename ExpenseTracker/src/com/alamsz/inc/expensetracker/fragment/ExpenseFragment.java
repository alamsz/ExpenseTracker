package com.alamsz.inc.expensetracker.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class ExpenseFragment extends ExpenseTrackerFragment {
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}

		View layout = (View) inflater.inflate(R.layout.expense, container,
				false);
		AdView mAdView = (AdView) layout.findViewById(R.id.adExpenseView);
		AdUtility.displayAd(mAdView);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onViewCreated(view, savedInstanceState);
		initializeTabContent(view);
		/*
		 * AutomaticScalingLayout at = new AutomaticScalingLayout();
		 * at.scaleContents(view);
		 */
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
	}

	
	

	

	private void initializeTabContent(View layout) {
		
		
		EditText expenseDateInputText = 	(EditText) layout
			.findViewById(R.id.expenseDateInputText);
		if(expenseDateInputText !=null){
			ExpenseTrackerActivity expActivity = (ExpenseTrackerActivity) getActivity();
			FormatHelper.setCurrentDateOnView(expenseDateInputText);
			
			ArrayAdapter<ConfigurationExpTracker> fundSourceAdapter = new ArrayAdapter<ConfigurationExpTracker>(
					expActivity.getApplicationContext(),
					R.layout.spinner_item, StaticVariables.listOfFundSource); 
			fundSourceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			
			
			
			ArrayAdapter<ConfigurationExpTracker> expCategoryAdapter = new ArrayAdapter<ConfigurationExpTracker>(
					expActivity.getApplicationContext(),
					R.layout.spinner_item, StaticVariables.listOfConfExpCat);
			expCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			Spinner categorySpinner = (Spinner) layout
					.findViewById(R.id.expenseFundSourceSpinner);
			categorySpinner.setAdapter(fundSourceAdapter);
			Spinner typeSpinner = (Spinner) layout
					.findViewById(R.id.expenseCategorySpinner);
			typeSpinner.setAdapter(expCategoryAdapter);
		}
		

	}

}
