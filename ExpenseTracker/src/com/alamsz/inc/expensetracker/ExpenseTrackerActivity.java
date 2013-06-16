package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.dao.ExpenseTrackerDAO;
import com.alamsz.inc.expensetracker.fragment.DateDialogFragment;
import com.alamsz.inc.expensetracker.fragment.ExpenseFragment;
import com.alamsz.inc.expensetracker.fragment.HomeFragment;
import com.alamsz.inc.expensetracker.fragment.IncomeFragment;
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

public class ExpenseTrackerActivity extends TabSwipeActivity implements TabListener {
	public ExpenseTrackerDAO daoFinHelper;
	private EditText dateFieldChosen;
	static final int DATE_DIALOG_ID = 999;
	private static final int RESULT_SETTINGS = 1;
	private static final int REQUEST_PATH = 0;
	public static DatabaseHandler dbHandler;
	private View layout;
	private List<List<String>> transactionHistoryList;
	static List<ExpenseTracker> listHistTransaction = null;
	private ExpenseTrackerService expTrackerService;
	public ExpenseTracker finHelp;
	public static boolean deleteEnabled = true;
	public static int positionTab = 0;
	Spinner transCatSpinner;
	TextView transCatText;
	List<String> expCatList = new ArrayList<String>();
	List<String> incCatList = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.tablayout);
		// Step 1: Inflate layout
		//setAppTheme(PreferenceManager.getDefaultSharedPreferences(this));
		// Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			
		}

		dbHandler = new DatabaseHandler(getApplicationContext());
		daoFinHelper = new ExpenseTrackerDAO(dbHandler);
		// make sure that the tables are writeable
		daoFinHelper.open();
		expTrackerService = new ExpenseTrackerService();
		Log.d("Locale",StaticVariables.currencyLocale.toString());
		
		showUserSettings();
		initializeAllConfiguration();
		setTabId(positionTab);
	}

	private void initializeAllConfiguration() {
		ConfigurationService configurationService = new ConfigurationService(dbHandler);
		StaticVariables.listOfExpCat = new ArrayList<String>();
		StaticVariables.listOfIncCat = new ArrayList<String>();
		StaticVariables.listOfConfIncCat = new ArrayList<ConfigurationExpTracker>();
		StaticVariables.listOfConfExpCat = new ArrayList<ConfigurationExpTracker>();
		StaticVariables.mapOfExpenseCatBasedOnDesc = new HashMap();
		StaticVariables.mapOfExpenseCatBasedOnTableCode = new HashMap();
		StaticVariables.mapOfIncomeCatBasedOnDesc = new HashMap();
		StaticVariables.mapOfIncomeCatBasedOnTableCode = new HashMap();
		StaticVariables.prefLang = getString(R.string.preflang);
		//add empty option in dropdown list of expense category
		StaticVariables.listOfExpCat = configurationService.getExpenseCategoryListFromDB(true, true);
		Collections.sort(StaticVariables.listOfExpCat);
		// set all configuration into static variables
		StaticVariables.listOfConfExpCat = configurationService
				.getExpenseCategoryListFromDB(false);
		expCatList.add("");
		for (Iterator<ConfigurationExpTracker> iterator = StaticVariables.listOfConfExpCat
				.iterator(); iterator.hasNext();) {
			ConfigurationExpTracker configs = iterator.next();
			expCatList.add(configs.getLocDesc());
			StaticVariables.mapOfExpenseCatBasedOnTableCode.put(
					configs.getTableCode(), configs);
			StaticVariables.mapOfExpenseCatBasedOnDesc.put(
					configs.getLocDesc(), configs);

		}

		// add empty option in dropdown list of expense category
		StaticVariables.listOfIncCat = configurationService
				.getIncomeCategoryListFromDB(true, true);
		Collections.sort(StaticVariables.listOfIncCat);
		// set all configuration into static variables
		StaticVariables.listOfConfIncCat = configurationService
				.getIncomeCategoryListFromDB(false);
		incCatList.add("");
		for (Iterator<ConfigurationExpTracker> iterator = StaticVariables.listOfConfIncCat
				.iterator(); iterator.hasNext();) {
			ConfigurationExpTracker configs = iterator.next();
			incCatList.add(configs.getLocDesc());
			StaticVariables.mapOfIncomeCatBasedOnTableCode.put(
					configs.getTableCode(), configs);
			StaticVariables.mapOfIncomeCatBasedOnDesc.put(configs.getLocDesc(),
					configs);
			;
		}
		StaticVariables.mapOfFundCategory.put(ExpenseTracker.CAT_CASH, getString(R.string.C));
		StaticVariables.mapOfFundCategory.put(ExpenseTracker.CAT_SAVING, getString(R.string.T));
		StaticVariables.mapOfTransType.put(ExpenseTracker.TYPE_CREDIT, getString(R.string.K));
		StaticVariables.mapOfTransType.put(ExpenseTracker.TYPE_DEBET, getString(R.string.D));
	}

	protected void onSaveInstanceState(Bundle outState) {
		
		//outState.putString("tab", mTabHost.getCurrentTabTag()); // save the tab
																// selected
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		
	}

	private void initialiseTabHost(Bundle args) {
		addTab(getString(R.string.home), HomeFragment.class, null, getResources().getDrawable(R.drawable.home));
		addTab(getString(R.string.expense),ExpenseFragment.class, null, getResources().getDrawable(R.drawable.expense));
		addTab(getString(R.string.income),IncomeFragment.class, null, getResources().getDrawable(R.drawable.income));
		addTab(getString(R.string.transfer),TransferBalance.class, null, getResources().getDrawable(R.drawable.transfer));
		addTab(getString(R.string.trans_hist),TransactionHistoryFragment.class, null, getResources().getDrawable(R.drawable.history));
		
		
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.setting, menu);
		getSupportMenuInflater().inflate(R.menu.about_setting, menu);
		
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
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.category_input, R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.categorySpinnerSearch);
		categorySpinner.setAdapter(adapter);
		
		Spinner transTypeSpinner = (Spinner) layout.findViewById(R.id.typeSpinnerSearch);
		transCatSpinner = (Spinner) layout.findViewById(R.id.transCatSpinnerSearch);
		final ArrayAdapter<String> expenseCatAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, expCatList);
		expenseCatAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		final ArrayAdapter<String> incomeCatAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, incCatList);
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("");
		final ArrayAdapter<String> emptyAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, arrayList);
		emptyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		incomeCatAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		transCatText = (TextView) findViewById(R.id.transCategoryView);
		transTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String transType = (String) arg0.getSelectedItem();
				//only set some values if the type not empty
				if(transType.equals(getString(R.string.expense))){
					transCatSpinner.setAdapter(expenseCatAdapter);
					expenseCatAdapter.notifyDataSetChanged();
					
					
				}else if (transType.equals(getString(R.string.income))){
					transCatSpinner.setAdapter(incomeCatAdapter);
					incomeCatAdapter.notifyDataSetChanged();
					
				} else{
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
		
		String categorySpinnerValue = categorySpinner.getSelectedItem()
				.toString();
		if (!categorySpinnerValue.equals("")) {
			category = categorySpinnerValue.equals(getString(R.string.C)) ? ExpenseTracker.CAT_CASH
					: ExpenseTracker.CAT_SAVING;
		}
		Spinner transCatSpinnerHist = (Spinner) layout.findViewById(R.id.transCatSpinnerSearch);
		Spinner transTypeSpinnerHist = (Spinner) layout.findViewById(R.id.typeSpinnerSearch);
		String strTransCatSpinnerHist = (String) transCatSpinnerHist.getSelectedItem();
		String strTransTypeSpinnerHist = (String) transTypeSpinnerHist.getSelectedItem();
		String transCatDisplay = strTransCatSpinnerHist.equals("")?"":" ("+strTransCatSpinnerHist+")";
		//set the transtype for display
		TextView transTypeCat = (TextView) findViewById(R.id.typeCatHist);
		transTypeCat.setText(getString(R.string.expense_type)+" : "+strTransTypeSpinnerHist);
		//set the constraint value
		if(strTransCatSpinnerHist!=null && !strTransCatSpinnerHist.equals("")){
			ConfigurationExpTracker configTemp = strTransTypeSpinnerHist.equals(getString(R.string.expense))
						?StaticVariables.mapOfExpenseCatBasedOnDesc.get(strTransCatSpinnerHist):StaticVariables.mapOfIncomeCatBasedOnDesc.get(strTransCatSpinnerHist);
			strTransCatSpinnerHist = configTemp.getTableCode();
			
		}
		if(strTransTypeSpinnerHist!= null && !strTransTypeSpinnerHist.equals("")){
			strTransTypeSpinnerHist = strTransTypeSpinnerHist.equals(getString(R.string.expense))?ExpenseTracker.TYPE_CREDIT:ExpenseTracker.TYPE_DEBET;
		}
		
		listHistTransaction = daoFinHelper.getListPerPeriod(dateFromValue,
					dateToValue, category,strTransTypeSpinnerHist,strTransCatSpinnerHist);
		
		
		dateFromViewHist.setText(getString(R.string.date_from_display) + dateFromValue);
		dateToViewHist.setText(getString(R.string.date_to_display)+ dateToValue);
		categoryViewHist.setText(getString(R.string.expense_cat_display) + categorySpinnerValue);

		ListView listPencatatKeuangan;
		listPencatatKeuangan = (ListView) findViewById(R.id.expandableListView1);

		TransactionAdapter adapter = new TransactionAdapter(this,
				 listHistTransaction);
		listPencatatKeuangan.setAdapter(adapter);
		
		TransactionListWrapper tlw = expTrackerService
				.getTotalTransactionAndprepareCSVList(listHistTransaction);
		int diff = tlw.getTotalIncome()-tlw.getTotalExpense();
		transactionHistoryList = tlw.getExpenseTrackerList();
		TextView diffView = (TextView) findViewById(R.id.diffView);
		
		
		diffView.setText(getString(R.string.diff)+FormatHelper.getBalanceInCurrency(diff)
				+transCatDisplay);
		totalExpenseView.setText(getString(R.string.total_expense)
				+FormatHelper.getBalanceInCurrency(tlw.getTotalExpense()));
		totalIncomeView.setText(getString(R.string.total_income)
				+FormatHelper.getBalanceInCurrency(tlw.getTotalIncome()));
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
				finHelp = listHistTransaction
						.get(position);
				EditText dateInput = (EditText) searchCriteriaLayout
						.findViewById(R.id.dateInputTextRO);
				dateInput.setText(FormatHelper
						.formatDateForDisplay(finHelp.getDateInput()));
				EditText description = (EditText) searchCriteriaLayout
						.findViewById(R.id.descriptionTextRO);
				description.setText(finHelp.getDescription());
				Spinner catSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.categorySpinnerRO);
				ArrayAdapter fundSourceAdapter = (ArrayAdapter) catSpin.getAdapter();
				fundSourceAdapter.setDropDownViewResource(R.layout.spinner_item);
				String categoryDescription = finHelp.getCategory()
						.equals(ExpenseTracker.CAT_SAVING) ? getString(R.string.T)
						: getString(R.string.C);
				catSpin.setSelection(fundSourceAdapter.getPosition(categoryDescription));
				Spinner typeSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.typeSpinnerRO);
				ArrayAdapter transTypeAdapter = (ArrayAdapter) typeSpin.getAdapter();
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
				TextView transCatView = (TextView) searchCriteriaLayout.findViewById(R.id.transDetailTransCat);
				TextView fund = (TextView) searchCriteriaLayout.findViewById(R.id.transDetailCategory);
				//set into income category
				List<String> transCatList = new ArrayList<String>();
				String selectedItem = "";
				String transCatText ="";
				String transCatCategory = "";
				final int positionItem = position;
				final AdapterView listViewItem = listView;
				if(finHelp.getType().equals(ExpenseTracker.TYPE_DEBET)){
					transCatList = StaticVariables.listOfIncCat;
					ConfigurationExpTracker config = (ConfigurationExpTracker) StaticVariables.mapOfIncomeCatBasedOnTableCode.get(finHelp.getTransCategory());
					if(config != null){
						selectedItem = (String) config.getLocDesc();
					}
					
					transCatText = getString(R.string.inc_category);
					transCatCategory = getString(R.string.fund_desc);
				}
				//set into expense category
				else{
					transCatList = StaticVariables.listOfExpCat;
					ConfigurationExpTracker config = (ConfigurationExpTracker) StaticVariables.mapOfExpenseCatBasedOnTableCode.get(finHelp.getTransCategory());
					if(config != null){
						selectedItem = (String) config.getLocDesc();
					}
					transCatText = getString(R.string.exp_category);
					transCatCategory = getString(R.string.fund_source);
					
				}
				ArrayAdapter<String> transCategoryAdapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.spinner_item,
						transCatList);
				transCategoryAdapter
						.setDropDownViewResource(R.layout.spinner_dropdown_item);

				transCatSpinner.setAdapter(transCategoryAdapter);
				transCatSpinner.setSelection(transCategoryAdapter
						.getPosition(selectedItem));
				transCatView.setText(transCatText);
				fund.setText(transCatCategory);
				if(deleteEnabled){
					helpBuilder.setNeutralButton(getString(R.string.btn_delete), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							daoFinHelper.deleteFinanceHelper(finHelp);
							listHistTransaction.remove(positionItem);
							((TransactionAdapter)listViewItem.getAdapter()).notifyDataSetChanged();
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
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.date_input));
			dateInput.setError(fieldRequired);
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			financeHelper
					.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}

		EditText description = (EditText) findViewById(R.id.descriptionText);
		if (description.getText().toString().equals("")) {
			String descriptionRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.description));
			description.setError(descriptionRequired);
			validForProcess = false;
		} else {
			financeHelper.setDescription(description.getText().toString());
		}
		EditText amount = (EditText) findViewById(R.id.amountText);
		if (amount.getText().toString().equals("")
				|| Integer.parseInt(amount.getText().toString()) <= 0) {
			String amountRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.amount));
			amount.setError(amountRequired);
			validForProcess = false;
		} else {
			financeHelper.setAmount(Integer.parseInt(amount.getText()
					.toString()));
		}
		Spinner incCategorySpinner = (Spinner) findViewById(R.id.incomeCategorySpinner);
		ConfigurationExpTracker configTemp = (ConfigurationExpTracker) StaticVariables.mapOfIncomeCatBasedOnDesc.get(incCategorySpinner.getSelectedItem().toString());
		
		String incCategory = configTemp==null?"":configTemp.getTableCode();
		financeHelper.setTransCategory(incCategory);
		financeHelper.setType(ExpenseTracker.TYPE_DEBET);
		Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		if (categorySpinner.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (categorySpinner.getSelectedItem().equals(
				getString(R.string.T))) {
			category = ExpenseTracker.CAT_SAVING;
		}
		financeHelper.setCategory(category);
		String processMessage = getString(R.string.allFieldValidation);
		int messageDuration = Toast.LENGTH_LONG;
		if (validForProcess) {

			financeHelper = daoFinHelper.addFinanceHelper(financeHelper);
			processMessage = String.format(getString(R.string.transaction_success), financeHelper.toString());
			messageDuration = Toast.LENGTH_SHORT;
			cleanUpFieldsAfterSaved(description, amount,
						categorySpinner,incCategorySpinner);

			
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
		if(descriptionText!= null && !descriptionText.getText().toString().equals("")){
			expTrackerFrom.setDescription(descriptionText.getText().toString());
			expTrackerTo.setDescription(descriptionText.getText().toString());
		}
		Spinner categoryFrom = (Spinner) findViewById(R.id.categoryFromSpinner);
		String categoryFromStr = ExpenseTracker.CAT_CASH;
		if (categoryFrom.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (categoryFrom.getSelectedItem().equals(getString(R.string.T))) {
			categoryFromStr = ExpenseTracker.CAT_SAVING;
		}
		expTrackerFrom.setType(ExpenseTracker.TYPE_CREDIT);
		expTrackerFrom.setCategory(categoryFromStr);
		expTrackerFrom.setTransCategory("OTH");
		Spinner categoryTo = (Spinner) findViewById(R.id.categoryToSpinner);
		String categoryToStr = ExpenseTracker.CAT_CASH;
		if (categoryTo.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (categoryTo.getSelectedItem().equals(getString(R.string.T))) {
			categoryToStr = ExpenseTracker.CAT_SAVING;
		}
		expTrackerTo.setType(ExpenseTracker.TYPE_DEBET);
		expTrackerTo.setCategory(categoryToStr);
		expTrackerTo.setTransCategory("OTH");
		EditText amount = (EditText) findViewById(R.id.transferAmountText);
		String saldoFrom = daoFinHelper.getBalancePerCategory(categoryFromStr);
		//check so there should be no number format exception when the transfer is null
		saldoFrom = saldoFrom==null?"0":saldoFrom;
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

		if(!categoryFrom.getSelectedItem().toString().equals("") && !categoryTo.getSelectedItem().toString().equals("") && categoryFromStr.equals(categoryToStr)) {
			processMessage = getString(R.string.transfer_invalid);
			validForProcess = false;
		} else if(validForProcess && !categoryFrom.getSelectedItem().toString().equals("") && !categoryTo.getSelectedItem().toString().equals("")) {
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

			expTrackerFrom = daoFinHelper.addFinanceHelper(expTrackerFrom);
			expTrackerTo = daoFinHelper.addFinanceHelper(expTrackerTo);
			processMessage = String.format(
					getString(R.string.transfer_success),
					categoryFrom.getSelectedItem(),
					categoryTo.getSelectedItem(), FormatHelper.getBalanceInCurrency(amount.getText().toString()));
			messageDuration = Toast.LENGTH_SHORT;
			cleanUpFieldsAfterTransfer(descriptionText,amount, categoryFrom, categoryTo);		

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
				expTrackerService.saveResultFile(curFileName,
						getApplicationContext(), transactionHistoryList);
				Toast.makeText(getApplicationContext(),
						String.format(getString(R.string.file_saved), curFileName), Toast.LENGTH_SHORT)
						.show();

			}
			break;
		}
	}

	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String locales = sharedPrefs.getString(
				"currency", "enUS");
		String[] localeArray = {locales.substring(0, 2),locales.substring(2, 4)};
		StaticVariables.currencyLocale = null;
		Locale newLocale = new Locale(localeArray[0],localeArray[1]);
		StaticVariables.currencyLocale = newLocale;
		Log.d("currencyLocaleSet",localeArray[0] +" "+ localeArray[1]+" "+newLocale.toString()+" "+StaticVariables.currencyLocale.toString());
		HomeFragment.financeTipsGood = sharedPrefs.getString("finance_tips_good", getString(R.string.finance_tips_good));
		HomeFragment.financeTipsModerate = sharedPrefs.getString(
				"finance_tips_moderate", getString(R.string.finance_tips_warning));
		HomeFragment.financeTipsBad = sharedPrefs.getString(
				"finance_tips_bad", getString(R.string.finance_tips_critical));
		HomeFragment.financeTipsGoodLimit = sharedPrefs.getString(
				"finance_tips_good_limit", "1000000");
		HomeFragment.financeTipsModerateLimit = sharedPrefs.getString(
				"finance_tips_moderate_limit", "100000");
		deleteEnabled = sharedPrefs.getBoolean("enable_delete", true);
		
	}

	

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
	
		super.onResume();
		
	}

	@Override
	protected void onPause() {

		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		daoFinHelper.close();
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

	

	public void openSettingTab(View view) {
		
		Intent i = new Intent(this, ExpenseTrackerPreference.class);
		startActivityForResult(i, RESULT_SETTINGS);
	}
	
	public void openReportTab(View view) {
		Toast.makeText(getApplicationContext(), "Will be available in next version", Toast.LENGTH_SHORT).show();
		

	}
	
	public void modifyIncCategory(View view){
		StaticVariables.transCategory=ConfigurationDAO.INCOME_CATEGORY;
		ExpenseTrackerActivity.positionTab=2;
		Intent intent = new Intent(this, ConfigurationListActivity.class);
		startActivity(intent);
		
	}

	public void modifyExpCategory(View view){
		StaticVariables.transCategory=ConfigurationDAO.EXPENSE_CATEGORY;
		ExpenseTrackerActivity.positionTab=1;
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
	
	public void processExpense(View view){
//		Spinner catSpin = (Spinner) findViewById(R.id.expenseCategorySpinner);
//		String desc = catSpin.getSelectedItem().toString();
//		ConfigurationExpTracker config = (ConfigurationExpTracker)StaticVariables.mapOfExpenseCatBasedOnDesc.get(desc);
//		String code = config==null?"":config.getTableCode();
//		Toast.makeText(this, desc+" mempunyai table code "+code, Toast.LENGTH_LONG).show();
		
		ExpenseTracker financeHelper = new ExpenseTracker();
		String type = ExpenseTracker.TYPE_CREDIT;
		String category = ExpenseTracker.CAT_CASH;
		boolean validForProcess = true;

		EditText dateInput = (EditText) findViewById(R.id.expenseDateInputText);
		String strDate = dateInput.getText().toString();
		if (strDate.equals("")) {
			String fieldRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.date_input));
			dateInput.setError(fieldRequired);
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			financeHelper
					.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}

		EditText description = (EditText) findViewById(R.id.expenseDescriptionText);
		if (description.getText().toString().equals("")) {
			String descriptionRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.description));
			description.setError(descriptionRequired);
			validForProcess = false;
		} else {
			financeHelper.setDescription(description.getText().toString());
		}
		EditText amount = (EditText) findViewById(R.id.expenseAmountText);
		if (amount.getText().toString().equals("")
				|| Integer.parseInt(amount.getText().toString()) <= 0) {
			String amountRequired = String.format(getString(R.string.emptyTextValidation),getString(R.string.amount));
			amount.setError(amountRequired);
			validForProcess = false;
		} else {
			financeHelper.setAmount(Integer.parseInt(amount.getText()
					.toString()));
		}
		
		financeHelper.setType(ExpenseTracker.TYPE_CREDIT);
		Spinner categorySpinner = (Spinner) findViewById(R.id.expenseFundSourceSpinner);
		if (categorySpinner.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (categorySpinner.getSelectedItem().equals(
				getString(R.string.T))) {
			category = ExpenseTracker.CAT_SAVING;
		}
		financeHelper.setCategory(category);
		
		Spinner expCategorySpinner = (Spinner) findViewById(R.id.expenseCategorySpinner);
		ConfigurationExpTracker configTemp = (ConfigurationExpTracker) StaticVariables.mapOfExpenseCatBasedOnDesc.get(expCategorySpinner.getSelectedItem().toString());
		
		String expCategory = configTemp==null?"":configTemp.getTableCode();
		financeHelper.setTransCategory(expCategory);
		
		String processMessage = getString(R.string.allFieldValidation);
		int messageDuration = Toast.LENGTH_LONG;
		if (validForProcess) {

			String saldo = daoFinHelper.getBalancePerCategory(category);
			Log.d(getClass().getName(), type);
			Log.d(getClass().getName(), saldo == null ? "error" : saldo);
			if ((saldo != null && type.equals(ExpenseTracker.TYPE_CREDIT))
							&& !saldo.equals("") && Integer.parseInt(amount
							.getText().toString()) <= Integer.parseInt(saldo)) {
				financeHelper = daoFinHelper.addFinanceHelper(financeHelper);

				processMessage = String.format(getString(R.string.transaction_success), financeHelper.toString());
				messageDuration = Toast.LENGTH_SHORT;
				cleanUpFieldsAfterSaved(description, amount,
						categorySpinner,expCategorySpinner);

			} else if (saldo == null || saldo.equals("0") || saldo.equals("")){
				processMessage = String.format(getString(R.string.balance_empty_validation), categorySpinner.getSelectedItem());
						
			} else if (type.equals(ExpenseTracker.TYPE_CREDIT)
				 && Integer.parseInt(amount
						.getText().toString()) > Integer.parseInt(saldo)){
				processMessage = String.format(getString(R.string.expense_should_be_lower_than_balance),categorySpinner.getSelectedItem());
				amount.setError(processMessage);
			}
			else {
				
				processMessage = getString(R.string.allFieldValidation);
				
			}

		} else {
			processMessage = getString(R.string.allFieldValidation);
			
		}

		
		Toast.makeText(getApplicationContext(), processMessage, messageDuration)
				.show();
	}
	
	
	
	
}
