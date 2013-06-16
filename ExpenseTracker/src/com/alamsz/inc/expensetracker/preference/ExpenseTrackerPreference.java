package com.alamsz.inc.expensetracker.preference;


import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.alamsz.inc.expensetracker.ConfigurationListActivity;
import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;

public class ExpenseTrackerPreference extends SherlockPreferenceActivity  {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.expense_tracker_prefs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        
          
    }
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		 switch (item.getItemId()) {
		    case android.R.id.home:
		        // app icon in action bar clicked; go home
		    	
		    	Intent intent = new Intent(this, ExpenseTrackerActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
		        break;
		    }
		    return true;
	}

	    
}
