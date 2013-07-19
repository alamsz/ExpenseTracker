package com.alamsz.inc.expensetracker;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.fragment.DateDialogFragment;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class PayRecActivity extends SherlockFragmentActivity {
	String mode = StaticVariables.MODE_ADD;
	ExpenseTrackerService expTrackerService;
	String processMessage;
	EditText dateFieldChosen;
	Long idPayRec;
	String strAmount;
	String strDesc;
	String type;
	boolean markComplete;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.pay_rec_detail);
		Intent intent = getIntent();
		
		mode = intent.getStringExtra(StaticVariables.MODE);
		type = intent.getStringExtra("trans");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		EditText edDateInput = (EditText) findViewById(R.id.payRecEdDateInput);
		EditText edDescription = (EditText) findViewById(R.id.payRecEdDescription);
		EditText edAmount = (EditText) findViewById(R.id.payRecEdAmount);
		EditText edDueDate = (EditText) findViewById(R.id.payRecEdDueDate);
		EditText edAmountPaid = (EditText) findViewById(R.id.payRecEdAmountPaid);
		CheckBox checkMarkComplete = (CheckBox) findViewById(R.id.payRecCheckMarkComplete);
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,this);
		expTrackerService = new ExpenseTrackerService(dbHandler);
		TextView txtTitle =  (TextView) findViewById(R.id.payRecDetailTitle);
		if(type.equals(PayRecMaster.RECEIVABLE_CODE)){
			txtTitle.setText(getString(R.string.receivable));
		}else{
			txtTitle.setText(getString(R.string.payable));
		}
		FormatHelper.setCurrentDateOnView(edDateInput);
		FormatHelper.setCurrentDateOnView(edDueDate);
		Button btnProcess = (Button) findViewById(R.id.payRecBtnAddorUpdPayRec);
		btnProcess.setText(getString(R.string.btn_add));
		ImageButton btnPopUpPayment = (ImageButton) findViewById(R.id.payRecBtnAmountPaidPopUp);
		btnPopUpPayment.setClickable(false);
		if(mode.equals(StaticVariables.MODE_MOD)){
			Long strId = intent.getLongExtra(PayRecMaster.ID,0);
			PayRecMaster payRec = expTrackerService.findByPKPayRecMaster(strId);
			if(payRec!=null){
				btnProcess.setText(getString(R.string.btn_mod));
				edDateInput.setText(FormatHelper.formatDateForDisplay(payRec.getDateInput()));
				edDescription.setText(payRec.getDescription());
				edAmount.setText(String.valueOf(payRec.getAmount()));
				edDueDate.setText(FormatHelper.formatDateForDisplay(payRec.getDueDate()));
				edAmountPaid.setText(String.valueOf(payRec.getAmount_paid()));
				checkMarkComplete.setChecked(payRec.getMark_complete()==1?true:false);
				btnPopUpPayment.setClickable(true);
				idPayRec = strId;
				strAmount = String.valueOf(payRec.getAmount());
				strDesc = payRec.getDescription();
				markComplete = checkMarkComplete.isChecked();
			}
			
		}
		
	}

	
	public void addOrModPayRec(View view){
		boolean validForProcess = true;
		EditText edDateInput = (EditText) findViewById(R.id.payRecEdDateInput);
		EditText edDescription = (EditText) findViewById(R.id.payRecEdDescription);
		EditText edAmount = (EditText) findViewById(R.id.payRecEdAmount);
		EditText edDueDate = (EditText) findViewById(R.id.payRecEdDueDate);
		CheckBox checkMarkComplete = (CheckBox) findViewById(R.id.payRecCheckMarkComplete);
		
		String strDateInput = edDateInput.getText().toString();
		String strDescription = edDescription.getText().toString();
		strAmount = edAmount.getText().toString().equals("")?"0":edAmount.getText().toString();
		String strDueDate = edDueDate.getText().toString();
		boolean strMarkComplete = checkMarkComplete.isChecked();
		markComplete = strMarkComplete;
		if(strDescription.equals("")){
			validForProcess = false;
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.description));
			edDescription.setError(fieldRequired);
			
		}
		if(strAmount.equals("")){
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.amount));
			edAmount.setError(fieldRequired);
			validForProcess = false;
		}else if(strAmount.equals("0")){
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.amount));
			edAmount.setError(fieldRequired);
			validForProcess = false;
		}
		if(strDueDate.equals("")){
			validForProcess = false;
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.due_date));
			edDueDate.setError(fieldRequired);
			
		}
		
		if(validForProcess){
			PayRecMaster payRec = new PayRecMaster();
			payRec.setDateInput(FormatHelper.formatDateToLong(strDateInput));
			payRec.setAmount(Integer.parseInt(strAmount));
			payRec.setDueDate(FormatHelper.formatDateToLong(strDueDate));
			payRec.setDescription(strDescription);
			payRec.setMark_complete(strMarkComplete?1:0);
			payRec.setType(type);
			
			if(mode.equals(StaticVariables.MODE_ADD)){
				payRec.setAmount_paid(0);
				payRec = expTrackerService.addPayRecMasterData(payRec);
				processMessage = String.format(
						getString(R.string.add_success), strDescription );
				strDesc = payRec.getDescription();
				markComplete = payRec.getMark_complete()==1;
				idPayRec = payRec.getId();
				strAmount = String.valueOf(payRec.getAmount());
			}else{
				payRec.setId(idPayRec);
				expTrackerService.modifyPayRecMasterData(payRec);
				processMessage = String.format(
						getString(R.string.mod_success), strDescription );
			}
			Button btnProcess = (Button) findViewById(R.id.payRecBtnAddorUpdPayRec);
			btnProcess.setText(getString(R.string.btn_mod));
			ImageButton btnPopUpPayment = (ImageButton) findViewById(R.id.payRecBtnAmountPaidPopUp);
			btnPopUpPayment.setClickable(true);
		}else{
			processMessage = getString(R.string.allFieldValidation);
		}
		Toast.makeText(getApplicationContext(), processMessage, Toast.LENGTH_SHORT).show();
	}
	
	private void showDatePicker() {
		DateDialogFragment date = new DateDialogFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		// if(dateField.getText().equals("")){
		Calendar calender = Calendar.getInstance();
		Bundle args = new Bundle();
		args.putInt("year", calender.get(Calendar.YEAR));
		args.putInt("month", calender.get(Calendar.MONTH));
		args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));

		date.setArguments(args);
		// }
		/**
		 * Set Call back to capture selected date
		 */
		date.setCallBack(ondate);
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String formattedDay = FormatHelper.formatTwoDigitsDay(day);
			String formattedMonth = FormatHelper.formatTwoDigitsMonth(month);
			dateFieldChosen.setText(new StringBuilder().append(formattedDay)
					.append("-").append(formattedMonth).append("-")
					.append(year).append(" "));
			Log.d(getClass().getName(), formattedDay);
			Log.d(getClass().getName(), formattedMonth);
			Log.d(getClass().getName(), String.valueOf(year));
		}
	};
	
	public void payRecDueDateClick(View view){
		dateFieldChosen = (EditText) findViewById(R.id.payRecEdDueDate);
		showDatePicker();
	}
	
	public void payRecDateInputClick(View view){
		dateFieldChosen = (EditText) findViewById(R.id.payRecEdDateInput);
		showDatePicker();
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
	
	public void openPaymentList(View view){
		Intent intent = new Intent(this,PayRecPaymentActivity.class);
		intent.putExtra(PayRecMaster.ID_MASTER, idPayRec);
		intent.putExtra(PayRecMaster.AMOUNT, strAmount);
		intent.putExtra(PayRecMaster.DESCRIPTION, strDesc);
		intent.putExtra(PayRecMaster.MARK_COMPLETE, markComplete);
		intent.putExtra("trans", type);
		startActivity(intent);
		
	}
}
