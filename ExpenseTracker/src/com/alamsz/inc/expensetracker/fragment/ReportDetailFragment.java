package com.alamsz.inc.expensetracker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.alamsz.inc.expensetracker.R;

public class ReportDetailFragment extends ExpenseTrackerFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(container == null){
			return null;
		}
		// TODO Auto-generated method stub
		View layout = inflater.inflate(R.layout.report_detail_in_list,container, false);
		
		return layout;
	}

}
