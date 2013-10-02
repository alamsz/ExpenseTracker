package com.alamsz.inc.expensetracker;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.adapter.PayRecPaymentAdapter;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.dao.PayRecPayment;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class PayRecPaymentActivity extends SherlockFragmentActivity {
	private ExpenseTrackerService expenseTrackerService;
	ListView lv;
	Long idPayRec;
	int amountTotal;
	String strDesc;
	String strAmount;
	String type;
	List<PayRecPayment> payRecPaymentList;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.pay_rec_payment_list);
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,this);
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
		Intent intent = getIntent();
		Long id = intent.getLongExtra(PayRecMaster.ID_MASTER, 0);
		idPayRec = id;
		strAmount = intent.getStringExtra(PayRecMaster.AMOUNT);
		type = intent.getStringExtra("trans");
		TextView txtHeading = (TextView) findViewById(R.id.payRecTitle);
		TextView txtPayRecTotal = (TextView) findViewById(R.id.payRecTotalPayRec);
		strDesc = intent.getStringExtra(PayRecMaster.DESCRIPTION);
		String heading = String.format(getString(R.string.payable_total), strDesc);
		txtHeading.setText(getString(R.string.payable_payment_list));
		if(type.equals(PayRecMaster.RECEIVABLE_CODE)){
			heading = String.format(getString(R.string.receivable_total), strDesc);
			txtHeading.setText(getString(R.string.receivable_payment_list));
		}
		txtPayRecTotal.setText(heading+FormatHelper.getBalanceInCurrency(strAmount));
		payRecPaymentList = expenseTrackerService.getAllPayRecDetailByIDMaster(id);
		PayRecPaymentAdapter paymentAdapter = new PayRecPaymentAdapter(this,payRecPaymentList);
		lv = (ListView) findViewById(R.id.listofPayRecPayment);
		lv.setAdapter(paymentAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(getApplicationContext(), PayRecPaymentDetailActivity.class);
				TextView txtDate = (TextView) arg1.findViewById(R.id.dateInput);
				Long tag = (Long) txtDate.getTag();
				intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_MOD);
				intent.putExtra(PayRecMaster.ID, tag);
				intent.putExtra(PayRecMaster.ID_MASTER, idPayRec);
				intent.putExtra(PayRecMaster.AMOUNT, strAmount);
				intent.putExtra(PayRecMaster.DESCRIPTION, strDesc);
				intent.putExtra("trans", type);
				startActivity(intent);
				
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,this);
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
	}

	public void createNewPayRecPayment(View view){
		Intent intent = new Intent(getApplicationContext(), PayRecPaymentDetailActivity.class);
		intent.putExtra("trans", type);
		intent.putExtra(PayRecMaster.ID_MASTER, idPayRec);
		intent.putExtra(PayRecMaster.AMOUNT, strAmount);
		intent.putExtra(PayRecMaster.DESCRIPTION, strDesc);
		intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_ADD);
		startActivity(intent);
		
	}

	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home

			backToCallerActivityScreen();
			break;
		}
		return true;
	}

	private void backToCallerActivityScreen() {
		Intent intent = new Intent(this, PayRecActivity.class);
		intent.putExtra("trans", type);
		intent.putExtra(PayRecMaster.ID, idPayRec);
		intent.putExtra(PayRecMaster.AMOUNT, strAmount);
		intent.putExtra(PayRecMaster.DESCRIPTION, strDesc);
		intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_MOD);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);
	}
	
}
