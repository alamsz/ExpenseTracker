package com.alamsz.inc.expensetracker.preference;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.alamsz.inc.expensetracker.R;

public class ExpenseTrackerPreference extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.expense_tracker_prefs);
        
        
          
    }
   
	 
	   
	    
}
