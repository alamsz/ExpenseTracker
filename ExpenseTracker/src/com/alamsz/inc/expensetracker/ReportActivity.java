package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.adapter.ReportSummaryAdapter;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseCategoryBudget;
import com.alamsz.inc.expensetracker.dao.ReportSummary;
import com.alamsz.inc.expensetracker.fragment.MonthlyReportFragment;
import com.alamsz.inc.expensetracker.fragment.WeeklyReportFragment;
import com.alamsz.inc.expensetracker.fragment.YearlyReportFragment;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.CSVFile;
import com.alamsz.inc.expensetracker.utility.CSVFileGenerator;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.alamsz.inc.expensetracker.utility.activity.DirectoryChooserActivity;
import com.google.ads.AdView;

public class ReportActivity extends SherlockFragmentActivity {
	private ExpenseTrackerService expenseTrackerService;
	GraphicalView graphView;
	LinearLayout ll;
	CategorySeries catSeries;
	DefaultRenderer defRenderer;
	int yearInput;
	int monthInput;
	int weekInput;
	int dateInput;
	String fundTypeInput;
	String reportHeading ="";
	String legendTitle = "";
	SherlockFragment mFragment = null;
	int[] key;
	int startDate = 0;
	List<ReportSummary> reportSummaryList;
	public static String ACTIVE_TAB = "activeTab";
	DatabaseHandler dbHandler;
	boolean isSummary;
	private static final int REQUEST_PATH = 0;
	Map<Integer, TransactionAmountWrapper> transList;
	String transCategory = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// add tabs
		String yearly = getString(R.string.yearly_report);
		String monthly = getString(R.string.monthly_report);
		String weekly = getString(R.string.weekly_report);
		StaticVariables.monthStrArr = getResources().getStringArray(
				R.array.months);
		Tab tab1 = actionBar
				.newTab()
				.setText(yearly)
				.setTabListener(
						new ReportTabListener(this, yearly,
								YearlyReportFragment.class));
		actionBar.addTab(tab1);
		Tab tab2 = actionBar
				.newTab()
				.setText(monthly)
				.setTabListener(
						new ReportTabListener(this, monthly,
								MonthlyReportFragment.class));
		actionBar.addTab(tab2);
		Tab tab3 = actionBar
				.newTab()
				.setText(weekly)
				.setTabListener(
						new ReportTabListener(this, weekly,
								WeeklyReportFragment.class));

		actionBar.addTab(tab3);

		// check if there is a saved state to select active tab
		if (savedInstanceState != null) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(ACTIVE_TAB));
		}
		dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,
				this);
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,
				this);
		expenseTrackerService = new ExpenseTrackerService(dbHandler);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save active tab
		outState.putInt(ACTIVE_TAB, getSupportActionBar()
				.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	private void showMonthlyReport(int month, int year, String fundType) {
		expenseTrackerService = new ExpenseTrackerService(
				ExpenseTrackerActivity.dbHandler);
		Map<Integer, TransactionAmountWrapper> transList = expenseTrackerService
				.getMonthlyTransactionSummary(month, year, fundType);
		Calendar calTemp = Calendar.getInstance();
		calTemp.set(Calendar.MONTH, month - 1);
		calTemp.set(Calendar.YEAR, year);
		int numOfWeeks = calTemp.getMaximum(Calendar.WEEK_OF_MONTH);
		monthInput = month;
		yearInput = year;
		ll = (LinearLayout) findViewById(R.id.reportView);

		graphView = ExpenseTrackerChartView.getMonthlyBarChartInstance(this,
				numOfWeeks, getString(R.string.week_prefix), transList);
		graphView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = graphView
						.getCurrentSeriesAndPoint();
				if (seriesSelection != null) {

					if (seriesSelection.getSeriesIndex() == 1|| seriesSelection.getSeriesIndex() == 0) {
						int weekNum = seriesSelection.getPointIndex() + 1;
						List<String[]> detailPieExpense = new ArrayList<String[]>();
						if (seriesSelection.getSeriesIndex() == 1) {
							detailPieExpense = expenseTrackerService
								.getDetailExpense(0, weekNum, monthInput,
										yearInput,fundTypeInput);
						}else{
							detailPieExpense = expenseTrackerService
							.getDetailIncome(0, weekNum, monthInput,
									yearInput,fundTypeInput);
						}
						ll.removeView(graphView);
						legendTitle = getString(R.string.week_prefix) + " "
								+ weekNum + ", "
								+ StaticVariables.monthStrArr[monthInput - 1]
								+ " " + yearInput;
						List<Object> listOfObject = ExpenseTrackerChartView
								.getNewPieChartInstance(
										getApplicationContext(),
										detailPieExpense, legendTitle);
						graphView = (GraphicalView) listOfObject.get(0);
						catSeries = (CategorySeries) listOfObject.get(1);
						defRenderer = (DefaultRenderer) listOfObject.get(2);
						graphView.repaint();
						graphView.setOnClickListener(pieChartOnClickListener(seriesSelection.getSeriesIndex() == 1?"EXP_CAT":"INC_CAT"));
						ll.addView(graphView);
					}
				}

			}

		});
		ll.setBackgroundColor(Color.BLACK);
		ll.removeAllViews();
		ll.addView(graphView);
	}

	private void showYearlyReport(int year, String fundType) {

		Map<Integer, TransactionAmountWrapper> transList = expenseTrackerService
				.getYearlyTransactionSummary(year, fundType);

		yearInput = year;
		int numOfMonths = 12;

		ll = (LinearLayout) findViewById(R.id.reportView);

		graphView = ExpenseTrackerChartView.getYearlyBarChartInstance(this,
				numOfMonths, StaticVariables.monthStrArr, transList);
		graphView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = graphView
						.getCurrentSeriesAndPoint();
				if (seriesSelection != null) {

					if (seriesSelection.getSeriesIndex() == 1 || seriesSelection.getSeriesIndex() == 0) {
						int month = seriesSelection.getPointIndex() + 1;

						legendTitle = StaticVariables.monthStrArr[month - 1]
								+ " " + yearInput;
						List<String[]> detailPieExpense = new ArrayList<String[]>();
						if (seriesSelection.getSeriesIndex() == 1) {
						detailPieExpense = expenseTrackerService
								.getDetailExpense(0, 0, month, yearInput,fundTypeInput);
						}else{
							detailPieExpense = expenseTrackerService
							.getDetailIncome(0, 0, month, yearInput,fundTypeInput);
					
						}
						ll.removeView(graphView);
						List<Object> listOfObject = ExpenseTrackerChartView
								.getNewPieChartInstance(
										getApplicationContext(),
										detailPieExpense, legendTitle);
						graphView = (GraphicalView) listOfObject.get(0);
						catSeries = (CategorySeries) listOfObject.get(1);
						defRenderer = (DefaultRenderer) listOfObject.get(2);
						graphView.repaint();
						graphView.setOnClickListener(pieChartOnClickListener(seriesSelection.getSeriesIndex() == 1?"EXP_CAT":"INC_CAT"));
						ll.addView(graphView);
					}
				}

			}

		});
		ll.setBackgroundColor(Color.BLACK);
		ll.removeAllViews();
		ll.addView(graphView);
	}

	private void showWeeklyReport(int month, int year, int week,String fundType,
			String startDateInput, String endDate) {
		 
		Map<Integer, TransactionAmountWrapper> transList = expenseTrackerService
				.getWeeklyTransactionSummary(week, month, year, fundType);
		// List<Date> dateList = FormatHelper
		// .getWeekStartAndEndDateFromWeekInAMonth(week, month - 1, year);
		int numOfWeeks = 6;
		startDate = Integer.parseInt(startDateInput.substring(0, 2));
		monthInput = month;
		yearInput = year;
		weekInput = week;
		ll = (LinearLayout) findViewById(R.id.reportView);
		String[] labelSeries = FormatHelper.getWeeksDatesFromDate(FormatHelper
				.stringToDate(startDateInput));
		graphView = ExpenseTrackerChartView.getWeeklyBarChartInstance(this,
				numOfWeeks, startDate, transList, labelSeries);
		graphView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = graphView
						.getCurrentSeriesAndPoint();
				if (seriesSelection != null) {
					Log.d("series",
							String.valueOf(seriesSelection.getSeriesIndex()));
					Log.d("series selection", String.valueOf(seriesSelection));
					Toast.makeText(getApplicationContext(),
							String.valueOf(seriesSelection.getSeriesIndex()),
							Toast.LENGTH_SHORT).show();
					if (seriesSelection.getSeriesIndex() == 1 || seriesSelection.getSeriesIndex() == 0) {
						int dateNum = (int) Math.round(seriesSelection
								.getXValue()) + startDate - 1;
						List<String[]> detailPie = new ArrayList<String[]>();
						if(seriesSelection.getSeriesIndex() == 1){
							detailPie = expenseTrackerService
							.getDetailExpense(dateNum, weekInput,
									monthInput, yearInput,fundTypeInput);
						}else{
							detailPie = expenseTrackerService
							.getDetailIncome(dateNum, weekInput,
									monthInput, yearInput,fundTypeInput);
						}
							
						ll.removeView(graphView);
						legendTitle = dateNum + " "
								+ StaticVariables.monthStrArr[monthInput - 1]
								+ " " + yearInput;
						List<Object> listOfObject = ExpenseTrackerChartView
								.getNewPieChartInstance(
										getApplicationContext(),
										detailPie, legendTitle);
						graphView = (GraphicalView) listOfObject.get(0);
						catSeries = (CategorySeries) listOfObject.get(1);
						defRenderer = (DefaultRenderer) listOfObject.get(2);
						graphView.repaint();
						graphView.setOnClickListener(pieChartOnClickListener(seriesSelection.getSeriesIndex() == 1?"EXP_CAT":"INC_CAT"));
						ll.addView(graphView);
					}
				}

			}

		});
		ll.setBackgroundColor(Color.BLACK);
		ll.removeAllViews();
		ll.addView(graphView);
	}

	String[] period;

	public void getWeeklyReportList(View view) {
		TextView edWeek = (TextView) findViewById(R.id.week);
		String edWeekText = edWeek.getText().toString();
		legendTitle = edWeekText;
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		if (!edWeekText.equals("")) {
			String startDate = edWeekText.substring(0, 10);
			String endDate = edWeekText.substring(edWeekText.length() - 10,
					edWeekText.length());
			List<Integer> weekmonthyear = FormatHelper
					.getWeekNoMonthAndYearFromDate(FormatHelper
							.stringToDate(startDate));
			period = FormatHelper.getWeeksDatesFromDate(FormatHelper
					.stringToDate(startDate));
			key = new int[period.length];
			for (int i = 0; i < period.length; i++) {
				String dateNow = period[i];
				long dateNowMilis = FormatHelper.formatDateToLong(dateNow);
				key[i] = Integer.parseInt(dateNow.substring(0, 2));
			}
			weekInput = weekmonthyear.get(0);
			monthInput = weekmonthyear.get(2);
			yearInput = weekmonthyear.get(3);
			transList = expenseTrackerService.getWeeklyTransactionSummary(
					weekInput, monthInput, yearInput,fundTypeInput);

			ReportSummaryAdapter rsa = prepareReportSummaryInAdapter(transList,
					period, key);
			View inflater = getLayoutInflater().inflate(
					R.layout.report_in_list, null);
			ListView lv = (ListView) inflater
					.findViewById(R.id.expandableListViewSummary);
			AdView mAdView = (AdView) inflater.findViewById(R.id.adReportView);
			AdUtility.displayAd(mAdView);
			lv.setAdapter(rsa);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {

					Intent intent = new Intent(getApplicationContext(),
							ReportDetailActivity.class);
					legendTitle = period[position];
					intent.putExtra("label", legendTitle);
					intent.putExtra("date", key[position]);
					intent.putExtra("week", weekInput);
					intent.putExtra("month", monthInput);
					intent.putExtra("year", yearInput);
					intent.putExtra("fundType", fundTypeInput);
					startActivity(intent);

				}
			});
			ll = (LinearLayout) findViewById(R.id.reportView);
			ll.removeAllViews();
			ll.setBackgroundColor(Color.WHITE);
			ll.addView(inflater);
		}

	}

	private ReportSummaryAdapter prepareReportSummaryInAdapter(
			Map<Integer, TransactionAmountWrapper> transList, String[] period,
			int[] key) {
		List<ReportSummary> repSumList = getReportSummaryFromTransactionAmountWrapper(
				transList, period, key);
		ReportSummaryAdapter rsa = new ReportSummaryAdapter(this, repSumList);
		return rsa;
	}

	private List<ReportSummary> getReportSummaryFromTransactionAmountWrapper(
			Map<Integer, TransactionAmountWrapper> transList, String[] period,
			int[] key) {
		List<ReportSummary> repSumList = new ArrayList<ReportSummary>();

		for (int i = 0; i < period.length; i++) {
			ReportSummary repTemp = new ReportSummary();

			TransactionAmountWrapper taw = transList.get(key[i]);
			repTemp.setPeriod(period[i]);

			if (taw != null) {
				repTemp.setAmountIncome(FormatHelper.getBalanceInCurrency(taw
						.getIncome()));
				repTemp.setAmountExpense(FormatHelper.getBalanceInCurrency(taw
						.getExpense()));
			} else {
				repTemp.setAmountIncome(FormatHelper.getBalanceInCurrency(0));
				repTemp.setAmountExpense(FormatHelper.getBalanceInCurrency(0));
			}
			repSumList.add(repTemp);
		}
		return repSumList;
	}

	public void getWeeklyReport(View view) {
		TextView edWeek = (TextView) findViewById(R.id.week);
		String edWeekText = edWeek.getText().toString();
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		
		if (!edWeekText.equals("")) {
			String startDate = edWeekText.substring(0, 10);
			String endDate = edWeekText.substring(edWeekText.length() - 10,
					edWeekText.length());
			List<Integer> weekmonthyear = FormatHelper
					.getWeekNoMonthAndYearFromDate(FormatHelper
							.stringToDate(startDate));
			showWeeklyReport(weekmonthyear.get(2), weekmonthyear.get(3),
					weekmonthyear.get(0), fundTypeInput, startDate, endDate);
		}
	}

	public void getMonthlyReport(View view) {
		TextView edYear = (TextView) findViewById(R.id.year);
		Spinner edMonth = (Spinner) findViewById(R.id.monthSpinner);
		int month = edMonth.getSelectedItemPosition() + 1;
		yearInput = Integer.parseInt(edYear.getText().toString());
		monthInput = month;
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		

		showMonthlyReport(monthInput, yearInput, fundTypeInput);
	}

	public void getMonthlyReportList(View view) {
		TextView edYear = (TextView) findViewById(R.id.year);
		Spinner edMonth = (Spinner) findViewById(R.id.monthSpinner);
		int month = edMonth.getSelectedItemPosition() + 1;
		yearInput = Integer.parseInt(edYear.getText().toString());
		monthInput = month;
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		legendTitle = StaticVariables.monthStrArr[monthInput - 1] + " "
				+ yearInput;
		transList = expenseTrackerService.getMonthlyTransactionSummary(
				monthInput, yearInput, fundTypeInput);
		Calendar calTemp = Calendar.getInstance();
		calTemp.set(Calendar.MONTH, month - 1);
		calTemp.set(Calendar.YEAR, yearInput);
		int numOfWeeks = calTemp.getMaximum(Calendar.WEEK_OF_MONTH);
		period = new String[] { "1", "2", "3", "4", "5", "6" };
		key = new int[] { 1, 2, 3, 4, 5, 6 };
		ReportSummaryAdapter rsa = prepareReportSummaryInAdapter(transList,
				period, key);
		View inflater = getLayoutInflater().inflate(R.layout.report_in_list,
				null);
		AdView mAdView = (AdView) inflater.findViewById(R.id.adReportView);
		AdUtility.displayAd(mAdView);
		ListView lv = (ListView) inflater
				.findViewById(R.id.expandableListViewSummary);
		lv.setAdapter(rsa);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				Intent intent = new Intent(getApplicationContext(),
						ReportDetailActivity.class);
				legendTitle = getString(R.string.week_prefix) + " "
						+ key[position] + ", "
						+ StaticVariables.monthStrArr[monthInput - 1] + " "
						+ yearInput;
				intent.putExtra("label", legendTitle);
				intent.putExtra("date", 0);
				intent.putExtra("week", key[position]);
				intent.putExtra("month", monthInput);
				intent.putExtra("year", yearInput);
				intent.putExtra("fundType", fundTypeInput);
				startActivity(intent);

			}
		});
		ll = (LinearLayout) findViewById(R.id.reportView);
		ll.removeAllViews();
		ll.setBackgroundColor(Color.WHITE);
		ll.addView(inflater);
	}

	public void getYearlyReport(View view) {
		TextView edYear = (TextView) findViewById(R.id.year);
		yearInput = Integer.parseInt(edYear.getText().toString());
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		int pos = getSpinnerPosition(fndSpin);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		
		showYearlyReport(yearInput, fundTypeInput);
	}

	public void getYearlyReportList(View view) {
		TextView edYear = (TextView) findViewById(R.id.year);
		yearInput = Integer.parseInt(edYear.getText().toString());
		Spinner fndSpin = (Spinner) findViewById(R.id.fundSourceSpinner);
		int pos = getSpinnerPosition(fndSpin);
		fundTypeInput = ((ConfigurationExpTracker)fndSpin.getSelectedItem()).getTableCode();
		legendTitle = ""+yearInput;
		transList = expenseTrackerService
				.getYearlyTransactionSummary(yearInput,fundTypeInput);

		int numOfMonths = 12;

		period = getResources().getStringArray(R.array.months);
		key = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		ReportSummaryAdapter rsa = prepareReportSummaryInAdapter(transList,
				period, key);
		View inflater = getLayoutInflater().inflate(R.layout.report_in_list,
				null);
		AdView mAdView = (AdView) inflater.findViewById(R.id.adReportView);
		AdUtility.displayAd(mAdView);
		ListView lv = (ListView) inflater
				.findViewById(R.id.expandableListViewSummary);
		lv.setAdapter(rsa);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				Intent intent = new Intent(getApplicationContext(),
						ReportDetailActivity.class);
				legendTitle = StaticVariables.monthStrArr[position] + " "
						+ yearInput;
				intent.putExtra("label", legendTitle);
				intent.putExtra("date", 0);
				intent.putExtra("week", 0);
				intent.putExtra("month", key[position]);
				intent.putExtra("year", yearInput);
				intent.putExtra("fundType", fundTypeInput);
				startActivity(intent);

			}
		});
		ll = (LinearLayout) findViewById(R.id.reportView);
		ll.removeAllViews();
		ll.setBackgroundColor(Color.WHITE);
		ll.addView(inflater);

	}

	private int getSpinnerPosition(Spinner inputSpinner) {
		int pos = inputSpinner.getSelectedItemPosition();
		if(pos < 0){
			pos = 0;
		}
		return pos;
	}

	public void getNextYearReport(View view) {
		int next = 1;
		prepareYearlyReport(next);
	}

	public void getNextWeekReport(View view) {
		int next = 1;
		prepareWeeklyReport(next);
	}

	public void getPrevWeekReport(View view) {
		int next = -1;
		Log.d("preWeekReport", String.valueOf(next));
		prepareWeeklyReport(next);
	}

	private void prepareWeeklyReport(int next) {
		TextView edWeek = (TextView) findViewById(R.id.week);
		String edWeekText = edWeek.getText().toString();
		if (!edWeekText.equals("")) {
			String startDate = edWeekText.substring(0, 11);
			String endDate = edWeekText.substring(edWeekText.length() - 11,
					edWeekText.length());
			Date dateInput = FormatHelper.stringToDate(endDate);
			List<Integer> weekmonthyear = FormatHelper
					.getWeekNoMonthAndYearFromDate(dateInput);
			if (next < 0) {
				dateInput = FormatHelper.stringToDate(startDate);
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateInput);
			cal.add(Calendar.DATE, next);
			List<Date> dateList = FormatHelper
					.getWeekStartAndEndDateFromDate(cal.getTime());
			startDate = FormatHelper.formatDateForDisplay(dateList.get(0));
			endDate = FormatHelper.formatDateForDisplay(dateList.get(1));
			edWeekText = startDate + " - " + endDate;
			edWeek.setText(edWeekText);
		}

	}

	private void prepareYearlyReport(int next) {
		TextView edYear = (TextView) findViewById(R.id.year);
		int year = Integer.parseInt(edYear.getText().toString()) + next;
		edYear.setText(String.valueOf(year));
		// showYearlyReport(year);
	}

	public void getPrevYearReport(View view) {
		int next = -1;
		prepareYearlyReport(next);
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

	// show detail of expenses when clicked
	private OnClickListener pieChartOnClickListener(String transCat) {
		transCategory = transCat;
		return new OnClickListener() {
			ConfigurationService configService = new ConfigurationService(
					dbHandler);
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.expensetracker_tooltip,
					null);
			
			
			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = graphView
						.getCurrentSeriesAndPoint();
				String category = "";
				TextView txtCategory = (TextView) layout.findViewById(R.id.toolTipTransCat);
				TextView txtType = (TextView) layout.findViewById(R.id.toolTipType);
				String transCats = transCategory.equals("INC_CAT")?getString(R.string.inc_category):getString(R.string.exp_category);
				String typeCat = transCategory.equals("INC_CAT")?getString(R.string.income):getString(R.string.expense);
				txtCategory.setText(transCats);
				txtType.setText(typeCat);
				if (seriesSelection != null) {
					TextView headingTxt = (TextView) layout
							.findViewById(R.id.heading);
					headingTxt.setText(legendTitle);
					for (int i = 0; i < catSeries.getItemCount(); i++) {

						defRenderer.getSeriesRendererAt(i).setHighlighted(
								i == seriesSelection.getPointIndex());
						if (i == seriesSelection.getPointIndex()) {
							category = catSeries.getCategory(i);
							ConfigurationExpTracker config = new ConfigurationExpTracker();
							for (Iterator iterator = configService.getExpenseCategoryListFromDB(false).iterator(); iterator
									.hasNext();) {
								ConfigurationExpTracker type = (ConfigurationExpTracker) iterator.next();
								if(category.equals(type.getLocDesc())){
									config = type;
									break;
								}
							}
							ExpenseCategoryBudget expBudget = configService
									.getExpBudget(config.getTableCode());
							if (expBudget != null) {
								TextView txt9 = (TextView) layout
										.findViewById(R.id.textView9);
								txt9.setText(FormatHelper
										.getBalanceInCurrency(expBudget
												.getBudgetAmountMonthly()));
							}

						}

					}
					Toast toolTip = Toast.makeText(
							ReportActivity.this,
							"Chart data point index "
									+ seriesSelection.getPointIndex()
									+ " selected" + " point value="
									+ seriesSelection.getValue(),
							Toast.LENGTH_SHORT);
					toolTip.setGravity(Gravity.BOTTOM, 0, 0);
					TextView txt7 = (TextView) layout
							.findViewById(R.id.textView7);
					txt7.setText(category);
					TextView txt8 = (TextView) layout
							.findViewById(R.id.textView8);
					txt8.setText(FormatHelper.getBalanceInCurrency((int) Math
							.round(seriesSelection.getValue())));
					toolTip.setView(layout);
					toolTip.setDuration(Toast.LENGTH_LONG);
					toolTip.show();
					graphView.repaint();
				}

			}
		};
	}

	public void exportReportSummaryToCSV(View view) {
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
				String curFileName = data.getStringExtra("getFullPathName")+".xls";

				reportSummaryList = getReportSummaryFromTransactionAmountWrapper(
						transList, period, key);
				List<List<String>> prepareContent = new ArrayList<List<String>>();
				for (Iterator iterator = reportSummaryList.iterator(); iterator
						.hasNext();) {

					ReportSummary repSum = (ReportSummary) iterator.next();
					prepareContent.add(repSum.toListOfString());

				}
				List<String> headingList = ReportSummary.getHeaderList(this);
				CSVFile csvFile = new CSVFile(prepareContent, headingList,
						getString(R.string.summary)+" "+legendTitle);
				List<CSVFile> csvFileList = new ArrayList<CSVFile>();
				csvFileList.add(csvFile);
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
