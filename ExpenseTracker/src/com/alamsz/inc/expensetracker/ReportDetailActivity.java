package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.adapter.ReportDetailAdapter;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudget;
import com.alamsz.inc.expensetracker.dao.ReportDetail;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.CSVFile;
import com.alamsz.inc.expensetracker.utility.CSVFileGenerator;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.alamsz.inc.expensetracker.utility.activity.DirectoryChooserActivity;
import com.google.ads.AdView;

public class ReportDetailActivity extends SherlockFragmentActivity {
	private ExpenseTrackerService expenseTrackerService;
	private static final int REQUEST_PATH = 0;
	public String monthInput;
	public String dateInput;
	public String weekInput;
	public String yearInput;
	public String legendTitle;
	public String fundType;
	List<ReportDetail> expReportDetailList = new ArrayList<ReportDetail>();
	List<ReportDetail> incReportDetailList = new ArrayList<ReportDetail>();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.report_detail_in_list);
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler, this);
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
		Intent intent = getIntent();
		setupView(intent);
		AdView mAdView = (AdView) findViewById(R.id.adReportView);
		AdUtility.displayAd(mAdView);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			onBackPressed();
			//Intent intent = new Intent(this, ReportActivity.class);
			//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			//startActivity(intent);
			//break;
		}
		return true;
	}
	public void setupView(Intent intent) {
		String period = intent.getStringExtra("label");
		
		int dateInt = intent.getIntExtra("date", 0);
		int weekInput = intent.getIntExtra("week", 0);
		int monthInput = intent.getIntExtra("month", 0);
		int yearInput = intent.getIntExtra("year", 0);
		legendTitle = intent.getStringExtra("label");
		fundType = intent.getStringExtra("fundType");
		incReportDetailList = new ArrayList<ReportDetail>();
		expReportDetailList = new ArrayList<ReportDetail>();
		List<String[]> expAmount = expenseTrackerService.getDetailExpense(
				dateInt, weekInput, monthInput, yearInput, fundType);
		List<String[]> incAmount = expenseTrackerService.getDetailIncome(
				dateInt, weekInput, monthInput, yearInput, fundType);
		for (int i = 0; i < expAmount.size(); i++) {
			ReportDetail repExpense = new ReportDetail();
			String[] detail = expAmount.get(i);
			Log.d("code", detail[0]);

			ConfigurationExpTracker configTemp = StaticVariables.mapOfExpenseCatBasedOnTableCode
					.get(detail[1]);
			if (configTemp != null) {
				ExpenseCategoryBudget expBudget = configTemp.getExpBudget();
				repExpense.setPeriod(period);
				repExpense.setCategory(configTemp.getLocDesc());
				String strMonthlyBudget =  getString(R.string.not_set);
				if(expBudget != null && expBudget.getBudgetAmountMonthly()> 0){
					strMonthlyBudget = FormatHelper
					.getBalanceInCurrency(expBudget
							.getBudgetAmountMonthly());
				}
				repExpense.setMonthlyBudget(strMonthlyBudget);
				repExpense.setAmount(FormatHelper
						.getBalanceInCurrency(detail[0]));
				repExpense.setType("EXP_CAT");
				expReportDetailList.add(repExpense);

			}
		}

		for (int i = 0; i < incAmount.size(); i++) {
			ReportDetail repIncome = new ReportDetail();
			String[] detail = incAmount.get(i);
			Log.d("code", detail[0]);

			ConfigurationExpTracker configTemp = StaticVariables.mapOfIncomeCatBasedOnTableCode
					.get(detail[1]);
			if (configTemp != null) {
				ExpenseCategoryBudget expBudget = configTemp.getExpBudget();
				repIncome.setPeriod(period);
				repIncome.setCategory(configTemp.getLocDesc());
				String strMonthlyBudget =  getString(R.string.not_set);
				repIncome.setMonthlyBudget(strMonthlyBudget);
				repIncome.setAmount(FormatHelper
						.getBalanceInCurrency(detail[0]));
				repIncome.setType("INC_CAT");
				incReportDetailList.add(repIncome);

			}
		}
		TextView txtPeriodIncome = (TextView) findViewById(R.id.incomePeriod);
		txtPeriodIncome.setText(period);
		TextView txtPeriodExpense = (TextView) findViewById(R.id.expensePeriod);
		txtPeriodExpense.setText(period);
		ReportDetailAdapter repExpenseAdapter = new ReportDetailAdapter(this, expReportDetailList);
		ReportDetailAdapter repIncomeAdapter = new ReportDetailAdapter(this, incReportDetailList);
		ListView lvExpense = (ListView) findViewById(R.id.expandableListViewExpenseDetail);
		ListView lvIncome = (ListView) findViewById(R.id.expandableListViewIncomeDetail);
		lvExpense.setAdapter(repExpenseAdapter);
		lvIncome.setAdapter(repIncomeAdapter);
	}
	
	public void exportReportDetailToCSV(View view){
		Intent intentDirChooser = new Intent(this,
				DirectoryChooserActivity.class);
		startActivityForResult(intentDirChooser, REQUEST_PATH);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case REQUEST_PATH:
			if (resultCode == RESULT_OK) {
				String curFileName = data.getStringExtra("getFullPathName");

				
				List<List<String>> prepareContentInc = new ArrayList<List<String>>();
				for (Iterator iterator = incReportDetailList.iterator(); iterator
						.hasNext();) {

					ReportDetail repDetail = (ReportDetail) iterator.next();
					prepareContentInc.add(repDetail.toList());

				}
				List<String> headingList = ReportDetail.getIncomeHeaderList(this);
				CSVFile csvFileInc = new CSVFile(prepareContentInc, headingList,
						getString(R.string.summary));
				List<List<String>> prepareContentExp = new ArrayList<List<String>>();
				for (Iterator iterator = expReportDetailList.iterator(); iterator
						.hasNext();) {

					ReportDetail repDetail = (ReportDetail) iterator.next();
					prepareContentExp.add(repDetail.toList());

				}
				List<String> headingExpList = ReportDetail.getExpenseHeaderList(this);
				CSVFile csvFileExp = new CSVFile(prepareContentExp, headingExpList,
						getString(R.string.summary));
				List<CSVFile> csvFileList = new ArrayList<CSVFile>();
				csvFileList.add(csvFileInc);
				csvFileList.add(csvFileExp);
				CSVFileGenerator csvGenerator = new CSVFileGenerator(
						curFileName);
				csvGenerator.generateCSVFile(csvFileList);

				Toast.makeText(
						getApplicationContext(),
						String.format(getString(R.string.file_saved),
								curFileName), Toast.LENGTH_SHORT).show();

			}
			break;
		}
	}

}
