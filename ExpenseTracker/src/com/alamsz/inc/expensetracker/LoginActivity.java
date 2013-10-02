package com.alamsz.inc.expensetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.appbrain.AppBrain;

public class LoginActivity extends SherlockFragmentActivity {
	String password;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		AppBrain.init(this);
		try{
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			boolean withPassword = pref.getBoolean("active_password", false);
			if(!withPassword){
				Intent intent = new Intent(this,ExpenseTrackerActivity.class);
				startActivityForResult(intent,0);
			}else{
				setContentView(R.layout.login);
				password = pref.getString("password", "1234");
			}
		}catch(Exception e){
			
		}
	}

	public void login(View view){
		EditText edPassword = (EditText) findViewById(R.id.edPassword);
		String passInput = edPassword.getText().toString();
		if(password == null || passInput.equals(password)){
			Intent intent = new Intent(this,ExpenseTrackerActivity.class);
			startActivityForResult(intent,0);
		}else{
			Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void ratemyapp(View view){
		String myUrl ="https://play.google.com/store/apps/details?id=com.alamsz.inc.bukukode";

		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)));
	}
	
	public void likeus(View view){
		String myUrl ="https://www.facebook.com/AlamszInc?ref=stream";

		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(myUrl)));
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		AppBrain.getAds().showInterstitial(this);
		finish();
		
	}
	
}
