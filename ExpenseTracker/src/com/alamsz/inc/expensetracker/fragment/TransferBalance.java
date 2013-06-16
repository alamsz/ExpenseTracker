package com.alamsz.inc.expensetracker.fragment;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.google.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;



public class TransferBalance extends Fragment {

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
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.category_input,R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.category_input,R.layout.spinner_item);
		adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner categorySpinner = (Spinner) layout.findViewById(R.id.categoryFromSpinner);
		categorySpinner.setAdapter(adapter);
		Spinner typeSpinner = (Spinner) layout.findViewById(R.id.categoryToSpinner);
		typeSpinner.setAdapter(adapter2);
		AdView mAdView = (AdView) layout.findViewById(R.id.adTransferView);
		AdUtility.displayAd(mAdView);
		
	}

}
