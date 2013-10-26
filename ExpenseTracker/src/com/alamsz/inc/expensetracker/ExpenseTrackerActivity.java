package com.alamsz.inc.expensetracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.alamsz.inc.expensetracker.adapter.TransactionAdapter;
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.DbExportImport;
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.fragment.DateDialogFragment;
import com.alamsz.inc.expensetracker.fragment.ExpenseFragment;
import com.alamsz.inc.expensetracker.fragment.HomeFragment;
import com.alamsz.inc.expensetracker.fragment.IncomeFragment;
import com.alamsz.inc.expensetracker.fragment.PayableFragment;
import com.alamsz.inc.expensetracker.fragment.ReceivableFragment;
import com.alamsz.inc.expensetracker.fragment.TransactionHistoryFragment;
import com.alamsz.inc.expensetracker.fragment.TransferBalance;
import com.alamsz.inc.expensetracker.preference.ExpenseTrackerPreference;
import com.alamsz.inc.expensetracker.service.ConfigurationService;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.alamsz.inc.expensetracker.utility.TransactionListWrapper;
import com.alamsz.inc.expensetracker.utility.activity.AboutActivity;
import com.alamsz.inc.expensetracker.utility.activity.DirectoryChooserActivity;

public class ExpenseTrackerActivity extends TabSwipeActivity implements
		TabListener {
	// public ExpenseTrackerDAO daoFinHelper;
	private EditText dateFieldChosen;
	static final int DATE_DIALOG_ID = 999;
	private static final int RESULT_SETTINGS = 1;
	private static final int REQUEST_PATH = 0;
	public static final int BACKUP_DB = 123;
	public static final int RESTORE_DB = 321;
	public static DatabaseHandler dbHandler;
	private View layout;
	private List<List<String>> transactionHistoryList;
	static List<ExpenseTracker> listHistTransaction = null;
	public ExpenseTrackerService expTrackerService;
	public ConfigurationService configService;
	public ExpenseTracker finHelp;
	public static boolean deleteEnabled = true;
	public static int positionTab = 0;
	boolean needToShow = false;
	Spinner transCatSpinner;
	TextView transCatText;

	String processMessage = "";
	Spinner expCategorySpinner;
	int messageDuration = 0;
	ExpenseTracker financeHelper;
	Class callingActivity;
	Intent dbData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StaticVariables.monthStrArr = getResources().getStringArray(
				R.array.months);
		showUserSettings();
		
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			return;
		}
		if (findViewById(R.id.tabHeading) != null) {
			HomeFragment homeFragment = new HomeFragment();
			homeFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
					.add(R.id.tabHeading, homeFragment).commit();
		}

		dbHandler = new DatabaseHandler(getApplicationContext());
		// daoFinHelper = new ExpenseTrackerDAO(dbHandler);
		// make sure that the tables are writeable
		// daoFinHelper.open();
		expTrackerService = new ExpenseTrackerService(
				FormatHelper.getDBHandler(dbHandler, this));
		configService = new ConfigurationService(dbHandler);
		Log.d("Locale", StaticVariables.currencyLocale.toString());

		initializeAllConfiguration();
		setTabId(positionTab);
	}

	private void initializeAllConfiguration() {
		ConfigurationService configurationService = new ConfigurationService(
				dbHandler);

		StaticVariables.listOfConfIncCat = new ArrayList<ConfigurationExpTracker>();
		StaticVariables.listOfConfExpCat = new ArrayList<ConfigurationExpTracker>();
		StaticVariables.listOfConfIncCat.add(FormatHelper
				.initConfig(ConfigurationDAO.INCOME_CATEGORY));
		StaticVariables.listOfConfExpCat.add(FormatHelper
				.initConfig(ConfigurationDAO.EXPENSE_CATEGORY));
		StaticVariables.mapOfExpenseCatBasedOnTableCode = new HashMap<String, ConfigurationExpTracker>();

		StaticVariables.mapOfIncomeCatBasedOnTableCode = new HashMap<String, ConfigurationExpTracker>();
		StaticVariables.prefLang = getString(R.string.preflang);

		// set all configuration into static variables
		StaticVariables.listOfConfExpCat.addAll(configurationService
				.getExpenseCategoryListFromDB(true));

		for (Iterator<ConfigurationExpTracker> iterator = StaticVariables.listOfConfExpCat
				.iterator(); iterator.hasNext();) {
			ConfigurationExpTracker configs = iterator.next();

			StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
					configs.getTableCode(), configs);

		}

		// set all configuration into static variables
		StaticVariables.listOfConfIncCat.addAll(configurationService
				.getIncomeCategoryListFromDB(true));

		for (Iterator<ConfigurationExpTracker> iterator = StaticVariables.listOfConfIncCat
				.iterator(); iterator.hasNext();) {
			ConfigurationExpTracker configs = iterator.next();

			StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
					configs.getTableCode(), configs);

		}
		StaticVariables.listOfFundSource = new ArrayList<ConfigurationExpTracker>();
		StaticVariables.listOfFundSource.add(FormatHelper
				.initConfig(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE));
		StaticVariables.listOfFundSource.addAll(configurationService
				.getConfigurationListFromDB(
						ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, true));
		StaticVariables.mapOfFundCategory = new HashMap<String, ConfigurationExpTracker>();
		for (int i = 0; i < StaticVariables.listOfFundSource.size(); i++) {
			ConfigurationExpTracker configTemp = StaticVariables.listOfFundSource
					.get(i);

			StaticVariables.mapOfFundCategory.put(configTemp.getTableCode(),
					configTemp);

		}
		// Collections.sort(StaticVariables.fundCatList);
		// StaticVariables.mapOfFundCategory.put(ExpenseTracker.CAT_CASH,
		// getString(R.string.C));
		// StaticVariables.mapOfFundCategory.put(ExpenseTracker.CAT_SAVING,
		// getString(R.string.T));
		StaticVariables.mapOfTransType.put(ExpenseTracker.TYPE_CREDIT,
				getString(R.string.K));
		StaticVariables.mapOfTransType.put(ExpenseTracker.TYPE_DEBET,
				getString(R.string.D));
	}

	protected void onSaveInstanceState(Bundle outState) {

		// outState.putString("tab", mTabHost.getCurrentTabTag()); // save the
		// tab
		// selected
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

	}

	private void initialiseTabHost(Bundle args) {
		addTab(getString(R.string.home), HomeFragment.class, null,
				getResources().getDrawable(R.drawable.home));
		addTab(getString(R.string.expense), ExpenseFragment.class, null,
				getResources().getDrawable(R.drawable.expense));
		addTab(getString(R.string.income), IncomeFragment.class, null,
				getResources().getDrawable(R.drawable.income));
		addTab(getString(R.string.transfer), TransferBalance.class, null,
				getResources().getDrawable(R.drawable.transfer));
		addTab(getString(R.string.trans_hist),
				TransactionHistoryFragment.class, null, getResources()
						.getDrawable(R.drawable.history));
		addTab(getString(R.string.payable), PayableFragment.class, null,
				getResources().getDrawable(R.drawable.payable));
		addTab(getString(R.string.receivable), ReceivableFragment.class, null,
				getResources().getDrawable(R.drawable.receivable));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.setting, menu);
		getSupportMenuInflater().inflate(R.menu.about_setting, menu);
		getSupportMenuInflater().inflate(R.menu.report, menu);
		return true;
	}

	public void expenseDateInputClick(View view) {
		dateFieldChosen = (EditText) findViewById(R.id.expenseDateInputText);
		showDatePicker(dateFieldChosen);
	}

	public void dateInputClick(View view) {
		dateFieldChosen = (EditText) findViewById(R.id.dateInputText);
		showDatePicker(dateFieldChosen);
	}

	public void dateTransferClick(View view) {
		dateFieldChosen = (EditText) findViewById(R.id.dateTransferText);
		showDatePicker(dateFieldChosen);
	}

	public void dateFromClick(View view) {
		dateFieldChosen = (EditText) layout.findViewById(R.id.dateFrom);
		showDatePicker(dateFieldChosen);
	}

	public void dateToClick(View view) {
		dateFieldChosen = (EditText) layout.findViewById(R.id.dateTo);
		showDatePicker(dateFieldChosen);
	}

	public void exportToCSV(View view) {

		Intent intentDirChooser = new Intent(this,
				DirectoryChooserActivity.class);
		intentDirChooser.putExtra("fileClickable", false);
		startActivityForResult(intentDirChooser, REQUEST_PATH);

	}

	private void showDatePicker(EditText dateField) {
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

	public void popUpShow(View view) {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle(getString(R.string.searchTransaction));
		helpBuilder.setMessage(getString(R.string.searchCriteria));

		LayoutInflater inflater = getLayoutInflater();
		View searchCriteriaLayout = inflater.inflate(R.layout.searchcriteria,
				null);
		helpBuilder.setView(searchCriteriaLayout);
		layout = searchCriteriaLayout;
		List<ConfigurationExpTracker> fundSourceList = new ArrayList<ConfigurationExpTracker>();
		fundSourceList.add(FormatHelper
				.initConfig(ConfigurationDAO.FUND_SOURCE_TABLE_TYPE));
		fundSourceList.addAll(configService.getConfigurationListFromDB(
				ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, false));
		ArrayAdapter<ConfigurationExpTracker> adapter = new ArrayAdapter<ConfigurationExpTracker>(
				this, R.layout.spinner_item, fundSourceList);

		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.categorySpinnerSearch);
		categorySpinner.setAdapter(adapter);

		Spinner transTypeSpinner = (Spinner) layout
				.findViewById(R.id.typeSpinnerSearch);
		transCatSpinner = (Spinner) layout
				.findViewById(R.id.transCatSpinnerSearch);
		List<ConfigurationExpTracker> expCatList = new ArrayList<ConfigurationExpTracker>();
		expCatList.add(FormatHelper
				.initConfig(ConfigurationDAO.EXPENSE_CATEGORY));
		expCatList.addAll(configService.getConfigurationListFromDB(
				ConfigurationDAO.EXPENSE_CATEGORY, false));
		final ArrayAdapter<ConfigurationExpTracker> expenseCatAdapter = new ArrayAdapter<ConfigurationExpTracker>(
				this, R.layout.spinner_item, expCatList);
		expenseCatAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_item);
		List<ConfigurationExpTracker> incCatList = new ArrayList<ConfigurationExpTracker>();
		incCatList.add(FormatHelper
				.initConfig(ConfigurationDAO.INCOME_CATEGORY));
		incCatList.addAll(configService.getConfigurationListFromDB(
				ConfigurationDAO.INCOME_CATEGORY, false));
		final ArrayAdapter<ConfigurationExpTracker> incomeCatAdapter = new ArrayAdapter<ConfigurationExpTracker>(
				this, R.layout.spinner_item, incCatList);
		ArrayList<ConfigurationExpTracker> arrayList = new ArrayList<ConfigurationExpTracker>();
		final ArrayAdapter<ConfigurationExpTracker> emptyAdapter = new ArrayAdapter<ConfigurationExpTracker>(
				this, R.layout.spinner_item, arrayList);
		emptyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		incomeCatAdapter
				.setDropDownViewResource(R.layout.spinner_dropdown_item);
		transCatText = (TextView) findViewById(R.id.transCategoryView);
		transTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						String transType = (String) arg0.getSelectedItem();
						// only set some values if the type not empty
						if (transType.equals(getString(R.string.expense))) {
							transCatSpinner.setAdapter(expenseCatAdapter);
							expenseCatAdapter.notifyDataSetChanged();

						} else if (transType.equals(getString(R.string.income))) {
							transCatSpinner.setAdapter(incomeCatAdapter);
							incomeCatAdapter.notifyDataSetChanged();

						} else {
							transCatSpinner.setAdapter(emptyAdapter);
							emptyAdapter.notifyDataSetChanged();

						}
						// TODO Auto-generated method stub

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});
		helpBuilder.setPositiveButton(getString(R.string.show_transaction),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						showTransactionHistory();
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		// ((Button)
		// helpDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.buttoncustom);
		helpDialog.show();

	}

	private void showTransactionHistory() {
		EditText dateFrom = (EditText) layout.findViewById(R.id.dateFrom);
		EditText dateTo = (EditText) layout.findViewById(R.id.dateTo);
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.categorySpinnerSearch);
		String category = "";
		TextView dateFromViewHist = (TextView) findViewById(R.id.dateFromViewHist);
		TextView dateToViewHist = (TextView) findViewById(R.id.dateToViewHist);
		TextView categoryViewHist = (TextView) findViewById(R.id.categoryViewHist);
		TextView totalExpenseView = (TextView) findViewById(R.id.totalExpense);
		TextView totalIncomeView = (TextView) findViewById(R.id.totalIncome);
		String dateFromValue = dateFrom.getText().toString();
		String dateToValue = dateTo.getText().toString();

		String categorySpinnerValue = "";

		if (getSpinnerPosition(categorySpinner) > 0) {

			try {
				categorySpinnerValue = categorySpinner.getSelectedItem()
						.toString();
				category = ((ConfigurationExpTracker) categorySpinner
						.getSelectedItem()).getTableCode();
			} catch (Exception e) {

				// processMessage =
				// "error, please check fund source/destination configuration";
			}

		}

		Spinner transCatSpinnerHist = (Spinner) layout
				.findViewById(R.id.transCatSpinnerSearch);
		Spinner transTypeSpinnerHist = (Spinner) layout
				.findViewById(R.id.typeSpinnerSearch);
		ConfigurationExpTracker confTransCatSpinnerHist = (ConfigurationExpTracker) transCatSpinnerHist
				.getSelectedItem();
		String strTransTypeSpinnerHist = (String) transTypeSpinnerHist
				.getSelectedItem();

		String transCatDisplay = "";
		String strTransCatSpinnerHist = "";
		if (confTransCatSpinnerHist != null
				&& !confTransCatSpinnerHist.getLocDesc().isEmpty()) {
			transCatDisplay = " (" + confTransCatSpinnerHist.getLocDesc() + ")";
			strTransCatSpinnerHist = confTransCatSpinnerHist.getTableCode();
		}
		// set the transtype for display
		TextView transTypeCat = (TextView) findViewById(R.id.typeCatHist);
		transTypeCat.setText(getString(R.string.expense_type) + " : "
				+ strTransTypeSpinnerHist);
		// set the constraint value

		if (strTransTypeSpinnerHist != null
				&& !strTransTypeSpinnerHist.equals("")) {
			strTransTypeSpinnerHist = strTransTypeSpinnerHist
					.equals(getString(R.string.expense)) ? ExpenseTracker.TYPE_CREDIT
					: ExpenseTracker.TYPE_DEBET;
		}

		listHistTransaction = expTrackerService.getListPerPeriod(dateFromValue,
				dateToValue, category, strTransTypeSpinnerHist,
				strTransCatSpinnerHist);

		dateFromViewHist.setText(getString(R.string.date_from_display)
				+ dateFromValue);
		dateToViewHist.setText(getString(R.string.date_to_display)
				+ dateToValue);
		categoryViewHist.setText(getString(R.string.expense_cat_display)
				+ categorySpinnerValue);

		ListView listPencatatKeuangan;
		listPencatatKeuangan = (ListView) findViewById(R.id.expandableListView1);
		if (listHistTransaction == null) {
			listHistTransaction = new ArrayList<ExpenseTracker>();
		}
		TransactionAdapter adapter = new TransactionAdapter(this,
				listHistTransaction);
		listPencatatKeuangan.setAdapter(adapter);

		TransactionListWrapper tlw = expTrackerService
				.getTotalTransactionAndprepareCSVList(listHistTransaction);
		int diff = tlw.getTotalIncome() - tlw.getTotalExpense();
		transactionHistoryList = tlw.getExpenseTrackerList();
		TextView diffView = (TextView) findViewById(R.id.diffView);

		diffView.setText(getString(R.string.diff)
				+ FormatHelper.getBalanceInCurrency(diff) + transCatDisplay);
		totalExpenseView.setText(getString(R.string.total_expense)
				+ FormatHelper.getBalanceInCurrency(tlw.getTotalExpense()));
		totalIncomeView.setText(getString(R.string.total_income)
				+ FormatHelper.getBalanceInCurrency(tlw.getTotalIncome()));
		listPencatatKeuangan
				.setOnItemClickListener(onTransactionDetailResultClick());

	}

	private OnItemClickListener onTransactionDetailResultClick() {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View arg1,
					int position, long arg3) {

				AlertDialog.Builder helpBuilder = new AlertDialog.Builder(
						arg1.getContext());
				helpBuilder.setTitle(getString(R.string.transaction_detail));
				LayoutInflater inflater = getLayoutInflater();
				View searchCriteriaLayout = inflater.inflate(
						R.layout.transaction_detail, null);
				helpBuilder.setView(searchCriteriaLayout);

				layout = searchCriteriaLayout;
				finHelp = listHistTransaction.get(position);
				EditText dateInput = (EditText) searchCriteriaLayout
						.findViewById(R.id.dateInputTextRO);
				dateInput.setText(FormatHelper.formatDateForDisplay(finHelp
						.getDateInput()));
				EditText description = (EditText) searchCriteriaLayout
						.findViewById(R.id.descriptionTextRO);
				description.setText(finHelp.getDescription());
				Spinner catSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.categorySpinnerRO);

				ArrayAdapter<ConfigurationExpTracker> fundSourceAdapter = new ArrayAdapter<ConfigurationExpTracker>(
						getApplicationContext(), R.layout.spinner_item,
						configService.getConfigurationListFromDB(
								ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, false));
				fundSourceAdapter
						.setDropDownViewResource(R.layout.spinner_item);
				catSpin.setAdapter(fundSourceAdapter);
				// String categoryDescription = finHelp.getCategory().equals(
				// ExpenseTracker.CAT_SAVING) ? getString(R.string.T)
				// : getString(R.string.C);
				int i = 0;
				for (Iterator iterator = configService
						.getConfigurationListFromDB(
								ConfigurationDAO.FUND_SOURCE_TABLE_TYPE, false)
						.iterator(); iterator.hasNext();) {
					ConfigurationExpTracker type = (ConfigurationExpTracker) iterator
							.next();
					if (type != null
							&& type.getTableCode() != null
							&& type.getTableCode()
									.equals(finHelp.getCategory())) {
						catSpin.setSelection(i);
						break;
					}
					i++;
				}

				Spinner typeSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.typeSpinnerRO);
				ArrayAdapter<String> transTypeAdapter = (ArrayAdapter<String>) typeSpin
						.getAdapter();
				transTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
				typeSpin.setSelection(transTypeAdapter
						.getPosition(finHelp.getType().equals(
								ExpenseTracker.TYPE_DEBET) ? getString(R.string.income)
								: getString(R.string.expense)));
				EditText amount = (EditText) searchCriteriaLayout
						.findViewById(R.id.amountTextRO);
				amount.setText(FormatHelper.getBalanceInCurrency(finHelp
						.getAmount()));
				Spinner transCatSpinner = (Spinner) searchCriteriaLayout
						.findViewById(R.id.transCatSpinnerRO);
				TextView transCatView = (TextView) searchCriteriaLayout
						.findViewById(R.id.transDetailTransCat);
				TextView fund = (TextView) searchCriteriaLayout
						.findViewById(R.id.transDetailCategory);
				// set into income category
				List<ConfigurationExpTracker> transCatList = new ArrayList<ConfigurationExpTracker>();
				String transCatText = "";
				String transCatCategory = "";
				final int positionItem = position;
				final AdapterView listViewItem = listView;
				ConfigurationExpTracker config = new ConfigurationExpTracker();
				if (finHelp.getType().equals(ExpenseTracker.TYPE_DEBET)) {
					transCatList = configService.getConfigurationListFromDB(
							ConfigurationDAO.INCOME_CATEGORY, false);
					config = (ConfigurationExpTracker) StaticVariables.mapOfIncomeCatBasedOnTableCode
							.get(finHelp.getTransCategory());

					transCatText = getString(R.string.inc_category);
					transCatCategory = getString(R.string.fund_desc);
				}
				// set into expense category
				else {
					transCatList = configService.getConfigurationListFromDB(
							ConfigurationDAO.EXPENSE_CATEGORY, false);
					config = (ConfigurationExpTracker) StaticVariables.mapOfExpenseCatBasedOnTableCode
							.get(finHelp.getTransCategory());

					transCatText = getString(R.string.exp_category);
					transCatCategory = getString(R.string.fund_source);

				}
				ArrayAdapter<ConfigurationExpTracker> transCategoryAdapter = new ArrayAdapter<ConfigurationExpTracker>(
						getApplicationContext(), R.layout.spinner_item,
						transCatList);
				transCategoryAdapter
						.setDropDownViewResource(R.layout.spinner_dropdown_item);
				int itempos = 0;
				if (config != null) {
					for (Iterator iterator = transCatList.iterator(); iterator
							.hasNext();) {
						ConfigurationExpTracker configurationExpTracker = (ConfigurationExpTracker) iterator
								.next();
						String tableCodeTemp = "";
						if (configurationExpTracker != null) {
							tableCodeTemp = configurationExpTracker
									.getTableCode();
						}
						if (tableCodeTemp.equals(config.getTableCode())) {
							break;
						}
						itempos++;

					}
				}

				transCatSpinner.setAdapter(transCategoryAdapter);
				transCatSpinner.setSelection(itempos);
				transCatView.setText(transCatText);
				fund.setText(transCatCategory);
				if (deleteEnabled) {
					helpBuilder.setNeutralButton(
							getString(R.string.btn_delete),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									expTrackerService
											.deleteFinanceHelper(finHelp);
									listHistTransaction.remove(positionItem);
									((TransactionAdapter) listViewItem
											.getAdapter())
											.notifyDataSetChanged();
								}
							});
				}

				helpBuilder.setPositiveButton(getString(R.string.btn_close),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				AlertDialog helpDialog = helpBuilder.create();
				helpDialog.show();

			}
		};
	}

	public void processIncome(View view) {
		ExpenseTracker financeHelper = new ExpenseTracker();
		String category = ExpenseTracker.CAT_CASH;
		boolean validForProcess = true;

		EditText dateInput = (EditText) findViewById(R.id.dateInputText);
		String strDate = dateInput.getText().toString();
		if (strDate.equals("")) {
			String fieldRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.date_input));
			dateInput.setError(fieldRequired);
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			financeHelper
					.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}

		EditText description = (EditText) findViewById(R.id.descriptionText);
		if (description.getText().toString().equals("")) {
			String descriptionRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.description));
			description.setError(descriptionRequired);
			validForProcess = false;
		} else {
			financeHelper.setDescription(description.getText().toString());
		}
		EditText amount = (EditText) findViewById(R.id.amountText);
		if (amount.getText().toString().equals("")
				|| Integer.parseInt(amount.getText().toString()) <= 0) {
			String amountRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.amount));
			amount.setError(amountRequired);
			validForProcess = false;
		} else {
			financeHelper.setAmount(Integer.parseInt(amount.getText()
					.toString()));
		}
		Spinner incCategorySpinner = (Spinner) findViewById(R.id.incomeCategorySpinner);
		ConfigurationExpTracker configTemp = null;
		if (incCategorySpinner.getSelectedItem() != null) {
			configTemp = (ConfigurationExpTracker) incCategorySpinner
					.getSelectedItem();
		}

		String incCategory = configTemp == null ? "" : configTemp
				.getTableCode();
		financeHelper.setTransCategory(incCategory);
		financeHelper.setType(ExpenseTracker.TYPE_DEBET);
		Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		if (getSpinnerPosition(categorySpinner) <= 0) {
			validForProcess = false;
		}
		// else if (categorySpinner.getSelectedItem().equals(
		// getString(R.string.T))) {
		category = ((ConfigurationExpTracker) categorySpinner.getSelectedItem())
				.getTableCode();

		// }
		financeHelper.setCategory(category);
		String processMessage = getString(R.string.allFieldValidation);
		int messageDuration = Toast.LENGTH_LONG;
		if (validForProcess) {

			financeHelper = expTrackerService.addFinanceHelper(financeHelper);
			processMessage = String.format(
					getString(R.string.transaction_success),
					financeHelper.toString());
			messageDuration = Toast.LENGTH_SHORT;
			cleanUpFieldsAfterSaved(description, amount, categorySpinner,
					incCategorySpinner);

		} else {
			processMessage = getString(R.string.allFieldValidation);

		}

		Toast.makeText(getApplicationContext(), processMessage, messageDuration)
				.show();

	}

	public void transferBalance(View view) {

		String type = ExpenseTracker.TYPE_CREDIT;
		String category = ExpenseTracker.CAT_CASH;
		boolean validForProcess = true;
		ExpenseTracker expTrackerFrom = new ExpenseTracker();
		ExpenseTracker expTrackerTo = new ExpenseTracker();
		EditText dateTransaction = (EditText) findViewById(R.id.dateTransferText);
		String strDate = dateTransaction.getText().toString();
		String processMessage = getString(R.string.allFieldValidation);
		if (strDate.equals("")) {
			String fieldRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.date_input));
			dateTransaction.setError(fieldRequired);
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			expTrackerFrom.setDateInput(FormatHelper
					.formatDateToLong(inputDate));
			expTrackerTo.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}
		expTrackerFrom.setDescription(getString(R.string.transfer));
		expTrackerTo.setDescription(getString(R.string.transfer));
		EditText descriptionText = (EditText) findViewById(R.id.transferDescriptionText);
		if (descriptionText != null
				&& !descriptionText.getText().toString().equals("")) {
			expTrackerFrom.setDescription(descriptionText.getText().toString());
			expTrackerTo.setDescription(descriptionText.getText().toString());
		}
		Spinner categoryFrom = (Spinner) findViewById(R.id.categoryFromSpinner);
		String categoryFromStr = "";
		if (getSpinnerPosition(categoryFrom) <= 0) {
			validForProcess = false;
		}

		if (getSpinnerPosition(categoryFrom) > 0) {

			try {
				categoryFromStr = ((ConfigurationExpTracker) categoryFrom
						.getSelectedItem()).getTableCode();
				;
			} catch (Exception e) {
				validForProcess = false;
				processMessage = "error, please check fund source/destination configuration";
			}

		}
		// else if
		// (categoryFrom.getSelectedItem().equals(getString(R.string.T))) {
		// categoryFromStr = ExpenseTracker.CAT_SAVING;
		// }
		expTrackerFrom.setType(ExpenseTracker.TYPE_CREDIT);
		expTrackerFrom.setCategory(categoryFromStr);
		expTrackerFrom.setTransCategory("TRF");
		Spinner categoryTo = (Spinner) findViewById(R.id.categoryToSpinner);
		String categoryToStr = "";
		if (getSpinnerPosition(categoryTo) <= 0) {
			validForProcess = false;
		}
		// else if (categoryTo.getSelectedItem().equals(getString(R.string.T)))
		// {
		// categoryToStr = ExpenseTracker.CAT_SAVING;
		// }
		if (getSpinnerPosition(categoryTo) > 0) {

			try {
				categoryToStr = ((ConfigurationExpTracker) categoryTo
						.getSelectedItem()).getTableCode();
				;
			} catch (Exception e) {
				validForProcess = false;
				processMessage = "error, please check fund source/destination configuration";
			}

		}

		expTrackerTo.setType(ExpenseTracker.TYPE_DEBET);
		expTrackerTo.setCategory(categoryToStr);
		expTrackerTo.setTransCategory("TRF");
		EditText amount = (EditText) findViewById(R.id.transferAmountText);
		String saldoFrom = expTrackerService
				.getBalancePerCategory(categoryFromStr);
		// check so there should be no number format exception when the transfer
		// is null
		saldoFrom = saldoFrom == null ? "0" : saldoFrom;
		if (amount.getText().toString().equals("")
				|| Integer.parseInt(amount.getText().toString()) <= 0) {
			String amountRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.amount));
			amount.setError(amountRequired);
			validForProcess = false;
		} else if (Integer.parseInt(amount.getText().toString()) > Integer
				.parseInt(saldoFrom)) {
			processMessage = String.format(
					getString(R.string.expense_should_be_lower_than_balance),
					categoryFrom.getSelectedItem());
			amount.setError(processMessage);
			validForProcess = false;

		}

		if (getSpinnerPosition(categoryFrom) > 0
				&& getSpinnerPosition(categoryTo) > 0
				&& categoryFromStr.equals(categoryToStr)) {
			processMessage = getString(R.string.transfer_invalid);
			validForProcess = false;
		} else if (validForProcess && getSpinnerPosition(categoryFrom) > 0
				&& getSpinnerPosition(categoryTo) > 0) {
			expTrackerFrom.setAmount(Integer.parseInt(amount.getText()
					.toString()));
			expTrackerTo.setAmount(Integer
					.parseInt(amount.getText().toString()));
		}
		// process income to the destination and process expense from the source

		// expTracker.setCategory(category);

		int messageDuration = Toast.LENGTH_LONG;

		if (validForProcess) {

			Log.d(getClass().getName(), type);

			expTrackerFrom = expTrackerService.addFinanceHelper(expTrackerFrom);
			expTrackerTo = expTrackerService.addFinanceHelper(expTrackerTo);
			processMessage = String.format(
					getString(R.string.transfer_success), categoryFrom
							.getSelectedItem(), categoryTo.getSelectedItem(),
					FormatHelper.getBalanceInCurrency(amount.getText()
							.toString()));
			messageDuration = Toast.LENGTH_SHORT;
			cleanUpFieldsAfterTransfer(descriptionText, amount, categoryFrom,
					categoryTo);

		}

		Toast.makeText(getApplicationContext(), processMessage, messageDuration)
				.show();

	}

	private void cleanUpFieldsAfterSaved(EditText description, EditText amount,
			Spinner categorySpinner, Spinner transCategorySpinner) {
		description.setText("");
		amount.setText("");
		categorySpinner.setSelection(0);
		transCategorySpinner.setSelection(0);
	}

	private void cleanUpFieldsAfterTransfer(EditText desc, EditText amount,
			Spinner categoryFrom, Spinner categoryTo) {
		desc.setText("");
		amount.setText("");
		categoryFrom.setSelection(0);
		categoryTo.setSelection(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, ExpenseTrackerPreference.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;
		case R.id.about_settings:
			Intent aboutInt = new Intent(this, AboutActivity.class);
			startActivity(aboutInt);
			break;
		case R.id.report:
			openReportTab(null);
			break;

		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;

		case REQUEST_PATH:
			if (resultCode == RESULT_OK) {
				String curFileName = data.getStringExtra("getFullPathName");
				expTrackerService.saveResultFile(curFileName + ".xls",
						getApplicationContext(), transactionHistoryList);
				Toast.makeText(
						getApplicationContext(),
						String.format(getString(R.string.file_saved),
								curFileName), Toast.LENGTH_SHORT).show();

			}
			break;

		case BACKUP_DB:
			if (resultCode == RESULT_OK) {
				boolean valid = false;
				String curFileName = "";
				if (DbExportImport.SdIsPresent()) {
					curFileName = data.getStringExtra("getFullPathName");
					valid = DbExportImport.exportDb(new File(curFileName
							+ ".pkdb"));

				}
				String processMessage = String.format(
						getString(R.string.export_db_success), curFileName
								+ ".pkdb");

				if (valid) {

				} else {
					processMessage = getString(R.string.db_export_fail);

				}
				Toast.makeText(this, processMessage, Toast.LENGTH_LONG).show();
			}
			break;
		case RESTORE_DB:
			if (resultCode == RESULT_OK) {
				dbData = data;
				new AlertDialog.Builder(this)
						.setTitle("")
						.setMessage(
								String.format(
										getString(R.string.db_restore_confirmation),
										dbData.getStringExtra("getFullPathName")))
						.setPositiveButton(getString(R.string.btn_yes),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										boolean valid = false;
										String curFileName = "";
										if (DbExportImport.SdIsPresent()) {
											curFileName = dbData
													.getStringExtra("getFullPathName");
											valid = DbExportImport
													.restoreDb(new File(
															curFileName));

										}
										String processMessage = String
												.format(getString(R.string.restore_db_success),
														curFileName);
										if (valid) {

										} else {
											processMessage = String
													.format(getString(R.string.db_restore_fail),
															curFileName);

										}
										Toast.makeText(getApplicationContext(),
												processMessage,
												Toast.LENGTH_LONG).show();

									}
								})
						.setNegativeButton(getString(R.string.btn_no),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();

			}
			break;
		}
	}

	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String locales = sharedPrefs.getString("currency", "enUS");
		String preflang = sharedPrefs.getString("preflang", "in_ID");
		String[] localeArray = { locales.substring(0, 2),
				locales.substring(2, 4) };
		StaticVariables.currencyLocale = null;
		Locale newLocale = new Locale(localeArray[0], localeArray[1]);
		Locale defaultLang = Locale.getDefault();
		StaticVariables.currencyLocale = newLocale;
		Log.d("currencyLocaleSet",
				localeArray[0] + " " + localeArray[1] + " "
						+ newLocale.toString() + " "
						+ StaticVariables.currencyLocale.toString());
		Configuration config = getBaseContext().getResources()
				.getConfiguration();
		try {

			config.locale = new Locale(preflang);
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());

			HomeFragment.financeTipsGood = sharedPrefs.getString(
					"finance_tips_good", getString(R.string.finance_tips_good));
			HomeFragment.financeTipsModerate = sharedPrefs.getString(
					"finance_tips_moderate",
					getString(R.string.finance_tips_warning));
			HomeFragment.financeTipsBad = sharedPrefs.getString(
					"finance_tips_bad",
					getString(R.string.finance_tips_critical));
			HomeFragment.financeTipsGoodLimit = sharedPrefs.getString(
					"finance_tips_good_limit", "1000000");
			HomeFragment.financeTipsModerateLimit = sharedPrefs.getString(
					"finance_tips_moderate_limit", "100000");
			deleteEnabled = sharedPrefs.getBoolean("enable_delete", true);

		} catch (Exception e) {
			Locale.setDefault(defaultLang);
		}

	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {

		super.onResume();
		dbHandler = FormatHelper.getDBHandler(ExpenseTrackerActivity.dbHandler,
				this);
		// daoFinHelper = new ExpenseTrackerDAO(dbHandler);
		// make sure that the tables are writeable
		// daoFinHelper.open();
		expTrackerService = new ExpenseTrackerService(
				FormatHelper.getDBHandler(dbHandler, this));
		configService = new ConfigurationService(dbHandler);
		showUserSettings();
	}

	@Override
	protected void onPause() {

		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (expTrackerService != null)
			expTrackerService.close();
		super.onDestroy();
	}

	public void openExpenseTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(1);

	}

	public void openIncomeTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(2);
	}

	public void openTransferTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(3);

	}

	public void openTransactionHistTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(4);

	}

	public void openPayableTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(5);

	}

	public void openReceivableTab(View view) {
		getSupportActionBar().setSelectedNavigationItem(6);

	}

	public void openSettingTab(View view) {

		Intent i = new Intent(this, ExpenseTrackerPreference.class);
		startActivityForResult(i, RESULT_SETTINGS);
	}

	public void openReportTab(View view) {
		Intent intent = new Intent();
		intent.setClass(this, ReportActivity.class);
		startActivity(intent);

	}

	public void modifyIncCategory(View view) {
		StaticVariables.confType = ConfigurationDAO.INCOME_CATEGORY;
		ExpenseTrackerActivity.positionTab = 2;
		Intent intent = new Intent(this, ConfigurationListActivity.class);
		startActivity(intent);

	}

	public void modifyExpCategory(View view) {
		StaticVariables.confType = ConfigurationDAO.EXPENSE_CATEGORY;
		ExpenseTrackerActivity.positionTab = 1;
		Intent intent = new Intent(this, ConfigurationListActivity.class);
		startActivity(intent);
	}

	public void modifyFundCategory(View view) {
		StaticVariables.confType = ConfigurationDAO.FUND_SOURCE_TABLE_TYPE;

		Intent intent = new Intent(this, ConfigurationListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void processExpense(View view) {
		// Spinner catSpin = (Spinner)
		// findViewById(R.id.expenseCategorySpinner);
		// String desc = catSpin.getSelectedItem().toString();
		// ConfigurationExpTracker config =
		// (ConfigurationExpTracker)StaticVariables.mapOfExpenseCatBasedOnDesc.get(desc);
		// String code = config==null?"":config.getTableCode();
		// Toast.makeText(this, desc+" mempunyai table code "+code,
		// Toast.LENGTH_LONG).show();

		financeHelper = new ExpenseTracker();
		String type = ExpenseTracker.TYPE_CREDIT;
		String category = ExpenseTracker.CAT_CASH;
		boolean validForProcess = true;
		needToShow = true;
		EditText dateInput = (EditText) findViewById(R.id.expenseDateInputText);
		String strDate = dateInput.getText().toString();
		if (strDate.equals("")) {
			String fieldRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.date_input));
			dateInput.setError(fieldRequired);
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			financeHelper
					.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}

		final EditText description = (EditText) findViewById(R.id.expenseDescriptionText);
		if (description.getText().toString().equals("")) {
			String descriptionRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.description));
			description.setError(descriptionRequired);
			validForProcess = false;
		} else {
			financeHelper.setDescription(description.getText().toString());
		}
		final EditText amount = (EditText) findViewById(R.id.expenseAmountText);
		String amountStr = amount.getText().toString();
		if (amountStr.equals("") || Integer.parseInt(amountStr) <= 0) {
			String amountRequired = String.format(
					getString(R.string.emptyTextValidation),
					getString(R.string.amount));
			amount.setError(amountRequired);
			validForProcess = false;
		} else {
			financeHelper.setAmount(Integer.parseInt(amountStr));
		}

		financeHelper.setType(ExpenseTracker.TYPE_CREDIT);
		final Spinner categorySpinner = (Spinner) findViewById(R.id.expenseFundSourceSpinner);
		if (getSpinnerPosition(categorySpinner) <= 0) {
			validForProcess = false;
		}
		// else if (categorySpinner.getSelectedItem().equals(
		// getString(R.string.T))) {
		// int selectedItem = getSpinnerPosition(categorySpinner);
		category = ((ConfigurationExpTracker) categorySpinner.getSelectedItem())
				.getTableCode();
		// }
		financeHelper.setCategory(category);

		expCategorySpinner = (Spinner) findViewById(R.id.expenseCategorySpinner);
		ConfigurationExpTracker configTemp = (ConfigurationExpTracker) expCategorySpinner
				.getSelectedItem();

		String expCategory = configTemp == null ? "" : configTemp
				.getTableCode();
		financeHelper.setTransCategory(expCategory);

		processMessage = getString(R.string.allFieldValidation);
		messageDuration = Toast.LENGTH_LONG;
		if (validForProcess) {
			needToShow = true;
			String saldo = expTrackerService.getBalancePerCategory(category);
			Log.d(getClass().getName(), type);
			Log.d(getClass().getName(), saldo == null ? "error" : saldo);
			if ((saldo != null && type.equals(ExpenseTracker.TYPE_CREDIT))
					&& !saldo.equals("")
					&& Integer.parseInt(amountStr) <= Integer.parseInt(saldo)) {
				int month = Integer.parseInt(strDate.substring(3, 5));
				int year = Integer.parseInt(strDate.substring(6, 10));
				int currentExpense = expTrackerService
						.calculateExpensePerCategory(month, year, expCategory);
				/*
				 * int budgetMonthly = configTemp.getExpBudget()
				 * .getBudgetAmountMonthly();
				 * 
				 * if (budgetMonthly > 0 && budgetMonthly < currentExpense +
				 * Integer.parseInt(amountStr)) { needToShow = false;
				 * 
				 * new AlertDialog.Builder(this)
				 * .setTitle(getString(R.string.over_budget_title)) .setMessage(
				 * String.format( getString(R.string.over_budget_confirmation),
				 * configTemp.getLocDesc()))
				 * .setPositiveButton(getString(R.string.btn_yes), new
				 * DialogInterface.OnClickListener() { public void onClick(
				 * DialogInterface dialog, int which) { financeHelper =
				 * expTrackerService .addFinanceHelper(financeHelper);
				 * 
				 * processMessage = String
				 * .format(getString(R.string.transaction_success),
				 * financeHelper .toString()); messageDuration =
				 * Toast.LENGTH_SHORT; cleanUpFieldsAfterSaved( description,
				 * amount, categorySpinner, expCategorySpinner); Toast.makeText(
				 * getApplicationContext(), processMessage,
				 * messageDuration).show(); } })
				 * .setNegativeButton(getString(R.string.btn_no), new
				 * DialogInterface.OnClickListener() { public void onClick(
				 * DialogInterface dialog, int which) {
				 * 
				 * } }).show(); }
				 */
				// only process this if needed
				if (needToShow) {
					financeHelper = expTrackerService
							.addFinanceHelper(financeHelper);

					processMessage = String.format(
							getString(R.string.transaction_success),
							financeHelper.toString());
					messageDuration = Toast.LENGTH_SHORT;
					cleanUpFieldsAfterSaved(description, amount,
							categorySpinner, expCategorySpinner);
				}

			} else if (saldo == null || saldo.equals("0") || saldo.equals("")) {
				processMessage = String.format(
						getString(R.string.balance_empty_validation),
						categorySpinner.getSelectedItem());

			} else if (type.equals(ExpenseTracker.TYPE_CREDIT)
					&& Integer.parseInt(amountStr) > Integer.parseInt(saldo)) {
				processMessage = String
						.format(getString(R.string.expense_should_be_lower_than_balance),
								categorySpinner.getSelectedItem());
				amount.setError(processMessage);
			} else {

				processMessage = getString(R.string.allFieldValidation);

			}

		} else {
			processMessage = getString(R.string.allFieldValidation);

		}
		// only showing, when it is not shown before
		if (needToShow)
			Toast.makeText(getApplicationContext(), processMessage,
					messageDuration).show();
	}

	public void getPrevNews(View view) {
		try {
			StaticVariables.newsIndex -= 1;
			if (StaticVariables.newsIndex < 0) {
				StaticVariables.newsIndex = StaticVariables.newsArray.size() - 1;
			}
			TextView txtTips = (TextView) findViewById(R.id.tipsValue);
			txtTips.setText(StaticVariables.newsArray
					.get(StaticVariables.newsIndex));

		} catch (Exception e) {
			System.err.println();
		}

	}

	public void getNextNews(View view) {
		try {
			StaticVariables.newsIndex += 1;
			if (StaticVariables.newsArray.size() - 1 < StaticVariables.newsIndex) {
				StaticVariables.newsIndex = 0;
			}
			TextView txtTips = (TextView) findViewById(R.id.tipsValue);
			txtTips.setText(StaticVariables.newsArray
					.get(StaticVariables.newsIndex));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void createNewPayable(View view) {
		Intent intent = new Intent(getApplicationContext(),
				PayRecActivity.class);
		intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_ADD);
		String trans = PayRecMaster.PAYABLE_CODE;
		intent.putExtra("trans", trans);
		startActivity(intent);

	}

	public void createNewReceivable(View view) {
		Intent intent = new Intent(getApplicationContext(),
				PayRecActivity.class);
		intent.putExtra(StaticVariables.MODE, StaticVariables.MODE_ADD);
		String trans = PayRecMaster.RECEIVABLE_CODE;
		intent.putExtra("trans", trans);
		startActivity(intent);

	}

	private int getSpinnerPosition(Spinner inputSpinner) {
		int pos = inputSpinner.getSelectedItemPosition();
		if (pos < 0) {
			pos = 0;
		}
		return pos;
	}

	public void exportDB(View view) {
		Intent intentDirChooser = new Intent(this,
				DirectoryChooserActivity.class);
		intentDirChooser.putExtra("fileClickable", false);
		startActivityForResult(intentDirChooser, BACKUP_DB);

	}

	public void restoreDB(View view) {
		Intent intentDirChooser = new Intent(this,
				DirectoryChooserActivity.class);
		intentDirChooser.putExtra("fileClickable", true);
		startActivityForResult(intentDirChooser, RESTORE_DB);

	}
}
