package com.alamsz.inc.expensetracker.utility.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;

public class AboutActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.about);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void ratemyapp(View view){
		String myUrl ="https://play.google.com/store/apps/details?id=com.alamsz.inc.expensetracker";

		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)));
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
