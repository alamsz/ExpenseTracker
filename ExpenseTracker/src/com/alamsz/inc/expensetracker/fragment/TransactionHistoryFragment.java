package com.alamsz.inc.expensetracker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamsz.inc.expensetracker.R;

public class TransactionHistoryFragment extends Fragment {
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        if (container == null) {
	           
	            return null;
	        }
	        View layout = (View)inflater.inflate(R.layout.transactionhistory, container, false);
			initializeTabContent(layout);
	        return layout;
	    }
	  
	  private void initializeTabContent(View layout) {
		  	
	}
		
}
