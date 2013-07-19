package com.alamsz.inc.expensetracker.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.PayRecActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.adapter.PayRecAdapter;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class PayableFragment extends ExpenseTrackerFragment {
	ListView lv;
	ExpenseTrackerService expenseTrackerService;
	TextView txtHeading;
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

		View layout = (View) inflater.inflate(R.layout.payable_list, container,
				false);
		lv = (ListView) layout.findViewById(R.id.listofPayRec);
		txtHeading = (TextView) layout.findViewById(R.id.payRecTitle);
		txtHeading.setText(getString(R.string.list_of_payable));
		ImageButton btnCreateNew = (ImageButton) layout.findViewById(R.id.payRecBtnAddNew);
		btnCreateNew.setTag(PayRecMaster.PAYABLE_CODE);
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,getActivity());
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
		List<PayRecMaster> listOfPayRec = expenseTrackerService.getAllPayRecMasterByType(PayRecMaster.PAYABLE_CODE);

		PayRecAdapter payRecAdapter = new PayRecAdapter(getActivity(),
				listOfPayRec);
		lv.setAdapter(payRecAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				TextView txtDate = (TextView) arg1.findViewById(R.id.dateInput);
				Long tag = (Long) txtDate.getTag();
				Intent intent = new Intent(getActivity(),PayRecActivity.class);
				intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_MOD);
				intent.putExtra(PayRecMaster.ID, tag);
				String heading = txtHeading.getText().toString();
				boolean isPayable = heading.equals(getString(R.string.list_of_payable));
				String trans = PayRecMaster.RECEIVABLE_CODE;
				if(isPayable){
					trans = PayRecMaster.PAYABLE_CODE;
				}
				intent.putExtra("trans", trans);
				startActivity(intent);
				
				
			}
		});
		AdView mAdView = (AdView) layout.findViewById(R.id.adPayRecView);
		AdUtility.displayAd(mAdView);
		
		return layout;
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.d("diakses","diakses");
	}
	
	
}
