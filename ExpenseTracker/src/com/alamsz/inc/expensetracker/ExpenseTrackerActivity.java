package com.alamsz.inc.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.alamsz.inc.expensetracker.adapter.TransactionAdapter;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.FinanceHelper;
import com.alamsz.inc.expensetracker.dao.FinanceHelperDAO;
import com.alamsz.inc.expensetracker.fragment.BalanceTabFragment;
import com.alamsz.inc.expensetracker.fragment.DateDialogFragment;
import com.alamsz.inc.expensetracker.fragment.InputTabFragment;
import com.alamsz.inc.expensetracker.fragment.TransactionHistoryFragment;
import com.alamsz.inc.expensetracker.preference.ExpenseTrackerPreference;
import com.alamsz.inc.expensetracker.utility.CSVFileGenerator;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.activity.FileexplorerActivity;

public class ExpenseTrackerActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {
	private static final String PEMASUKAN_DESCRIPTION = "Pemasukan";
	private static final String TABUNGAN_DESCRIPTION = "Tabungan";
	private static final String DEBET = "D";
	private static final String CASH_DESCRIPTION = "Cash";
	private static final String KREDIT = "K";
	private static final String CASH = "C";
	private static final String TABUNGAN = "T";
	public FinanceHelperDAO daoFinHelper;
	private EditText dateFieldChosen;
	static final int DATE_DIALOG_ID = 999;
	private static final int RESULT_SETTINGS = 1;
	private static final int REQUEST_PATH = 0;

	private int year;
	private int month;
	private int day;

	private View layout;
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;
	private List<List<String>> transactionHistoryList;
	static List<FinanceHelper> listHistTransaction = null;

	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout);
		// Step 1: Inflate layout
		setAppTheme(PreferenceManager.getDefaultSharedPreferences(this));
		// Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			Log.d("tag",savedInstanceState.getString("tab"));
			if(mTabHost!=null)
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
		daoFinHelper = new FinanceHelperDAO(dbHandler);
		// make sure that the tables are writeable
		daoFinHelper.open();
	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); // save the tab
																// selected
		super.onSaveInstanceState(outState);
	}

	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		ExpenseTrackerActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(getString(R.string.transaction))
						.setIndicator(
								createTabView(getString(R.string.transaction),
										R.drawable.input_transaction)),
				(tabInfo = new TabInfo(getString(R.string.transaction),
						InputTabFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ExpenseTrackerActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(getString(R.string.balance))
						.setIndicator(
								createTabView(getString(R.string.balance),
										R.drawable.balance)),
				(tabInfo = new TabInfo(getString(R.string.balance),
						BalanceTabFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ExpenseTrackerActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(getString(R.string.trans_hist))
						.setIndicator(
								createTabView(getString(R.string.trans_hist),
										R.drawable.history)),
				(tabInfo = new TabInfo(getString(R.string.trans_hist),
						TransactionHistoryFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged(getString(R.string.transaction));
		//
		mTabHost.setOnTabChangedListener(this);
	}

	private static void addTab(ExpenseTrackerActivity activity,
			TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	/*
	 * returns the tab view i.e. the tab icon and text
	 */
	private View createTabView(final String text, final int id) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_icon, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));
		((TextView) view.findViewById(R.id.tab_text)).setText(text);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

	@Override
	public void onTabChanged(String tag) {
		TabInfo newTab = (TabInfo) this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
		/*if (tag.equals(getString(R.string.balance))) {
			
		}*/
		/*
		 * if (tag.equals(getString(R.string.trans_hist))) {
		 * refreshListOfTransaction(); }
		 */

	}

	public void dateInputClick(View view) {
		dateFieldChosen = (EditText) findViewById(R.id.dateInputText);
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
		Intent intentDirChooser = new Intent(this, FileexplorerActivity.class);
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

			dateFieldChosen.setText(new StringBuilder()
					.append(day)
					.append("-")
					.append(month > 9 ? String.valueOf(month + 1) : "0"
							+ String.valueOf(month + 1)).append("-")
					.append(year).append(" "));
			Log.d(getClass().getName(), String.valueOf(day));
			Log.d(getClass().getName(), month > 9 ? String.valueOf(month + 1)
					: "0" + String.valueOf(month + 1));
			Log.d(getClass().getName(), String.valueOf(year));
		}
	};

	public void popUpShow(View view) {

		showPopUpSearchCriteria();

	}

	public void onClick(View view) {
		FinanceHelper financeHelper = new FinanceHelper();

		switch (view.getId()) {
		case R.id.button1:
			processTransaction(financeHelper);

			break;

		default:
			break;
		}

	}

	private void showTransactionHistory() {
		EditText dateFrom = (EditText) layout.findViewById(R.id.dateFrom);
		EditText dateTo = (EditText) layout.findViewById(R.id.dateTo);
		Spinner categorySpinner = (Spinner) layout
				.findViewById(R.id.categorySpinnerSearch);
		String category = "";
		if (!categorySpinner.getSelectedItem().toString().equals("")) {
			category = categorySpinner.getSelectedItem().toString()
					.equals(CASH_DESCRIPTION) ? CASH : TABUNGAN;
		}

		if (!dateFrom.getText().toString().equals("")
				|| !dateTo.getText().toString().equals("")
				|| !category.equals("")) {
			listHistTransaction = daoFinHelper.getListPerPeriod(dateFrom
					.getText().toString(), dateTo.getText().toString(),
					category);
		} else {
			listHistTransaction = daoFinHelper.getAllFinanceHelper();

		}
		ListView listPencatatKeuangan;
		listPencatatKeuangan = (ListView) findViewById(R.id.expandableListView1);

		TransactionAdapter adapter = new TransactionAdapter(this,
				listHistTransaction);
		listPencatatKeuangan.setAdapter(adapter);
		transactionHistoryList = prepareCSVList(listHistTransaction);
		listPencatatKeuangan.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				AlertDialog.Builder helpBuilder = new AlertDialog.Builder(arg1
						.getContext());
				helpBuilder.setTitle("Detil Transaksi");
				LayoutInflater inflater = getLayoutInflater();
				View searchCriteriaLayout = inflater.inflate(
						R.layout.transaction_detail, null);
				helpBuilder.setView(searchCriteriaLayout);
				layout = searchCriteriaLayout;
				FinanceHelper financehelper = listHistTransaction.get(position);
				EditText dateInput = (EditText) searchCriteriaLayout
						.findViewById(R.id.dateInputTextRO);
				dateInput.setText(FormatHelper
						.formatDateForDisplay(financehelper.getDateInput()));
				EditText description = (EditText) searchCriteriaLayout
						.findViewById(R.id.descriptionTextRO);
				description.setText(financehelper.getDescription());
				Spinner catSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.categorySpinnerRO);
				ArrayAdapter test = (ArrayAdapter) catSpin.getAdapter();
				catSpin.setSelection(test.getPosition(financehelper
						.getCategoryDescription()));
				Spinner typeSpin = (Spinner) searchCriteriaLayout
						.findViewById(R.id.typeSpinnerRO);
				ArrayAdapter test2 = (ArrayAdapter) typeSpin.getAdapter();
				typeSpin.setSelection(test2
						.getPosition(financehelper.getType().equals(
								getString(R.string.income)) ? getString(R.string.income)
								: getString(R.string.expense)));
				EditText amount = (EditText) searchCriteriaLayout
						.findViewById(R.id.amountTextRO);
				amount.setText(FormatHelper.getBalanceInCurrency(financehelper
						.getAmount()));
				helpBuilder.setPositiveButton("Close",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				/*helpBuilder.setNegativeButton("Delete",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								// TODO Auto-generated method stub

							}
						});
*/
				// Remember, create doesn't show the dialog
				AlertDialog helpDialog = helpBuilder.create();
				//((Button) helpDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.buttoncustom);
				helpDialog.show();

			}
		});

	}

	private List<List<String>> prepareCSVList(List<FinanceHelper> list) {
		List<List<String>> listOfObject = new ArrayList<List<String>>();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			FinanceHelper financeHelper = (FinanceHelper) iterator.next();
			List<String> listPenampung = FinanceHelper
					.convertFinanceHelperToList(financeHelper);
			listOfObject.add(listPenampung);
		}
		return listOfObject;
	}

	private void processTransaction(FinanceHelper financeHelper) {
		
		String type = KREDIT;
		String category = CASH;
		boolean validForProcess = true;
	
		EditText dateInput = (EditText) findViewById(R.id.dateInputText);
		String strDate = dateInput.getText().toString();
		if (strDate.equals("")) {
			dateInput.setError("Tanggal Diperlukan");
			validForProcess = false;
		} else {
			Date inputDate = FormatHelper.stringToDate(strDate);
			financeHelper
					.setDateInput(FormatHelper.formatDateToLong(inputDate));
		}

		EditText description = (EditText) findViewById(R.id.descriptionText);
		if (description.getText().toString().equals("")) {
			description.setError("Deskripsi Diperlukan");
			validForProcess = false;
		} else {
			financeHelper.setDescription(description.getText().toString());
		}
		EditText amount = (EditText) findViewById(R.id.amountText);
		if (amount.getText().toString().equals("")
				|| Integer.parseInt(amount.getText().toString()) <= 0) {
			amount.setError("Jumlah diperlukan dan harus lebih besar dari 0");
			validForProcess = false;
		} else {
			financeHelper.setAmount(Integer.parseInt(amount.getText()
					.toString()));
		}
		Spinner debet = (Spinner) findViewById(R.id.typeSpinner);
		if (debet.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (debet.getSelectedItem().equals(PEMASUKAN_DESCRIPTION)) {
			type = DEBET;
		}
		financeHelper.setType(type);
		Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		if (categorySpinner.getSelectedItem().toString().equals("")) {
			validForProcess = false;
		} else if (categorySpinner.getSelectedItem().equals(
				TABUNGAN_DESCRIPTION)) {
			category = TABUNGAN;
		}
		financeHelper.setCategory(category);
		String processMessage = "Semua inputan harus diisi, silahkan cek inputan";
		int messageDuration = Toast.LENGTH_LONG;
		if (validForProcess) {

			String saldo = daoFinHelper.getBalancePerCategory(category);
			Log.d(getClass().getName(), type);
			Log.d(getClass().getName(), saldo == null ? "error" : saldo);
			if (type.equals(DEBET)
					|| (saldo != null && type.equals(KREDIT)
							&& !saldo.equals("") && Integer.parseInt(amount
							.getText().toString()) <= Integer.parseInt(saldo))) {
				financeHelper = daoFinHelper.addFinanceHelper(financeHelper);

				processMessage = "Penambahan transaksi Id "
						+ financeHelper.toString() + " sukses.";
				messageDuration = Toast.LENGTH_SHORT;
				cleanUpFieldsAfterSaved(description, amount, debet,
						categorySpinner);

			} else {
				processMessage = "Jumlah "
						+ financeHelper.getCategoryDescription()
						+ " lebih kecil dari jumlah masukan";
				amount.setError(processMessage);
			}

		}
		Toast.makeText(getApplicationContext(), processMessage, messageDuration)
				.show();

	}

	private void cleanUpFieldsAfterSaved(EditText description, EditText amount,
			Spinner debet, Spinner categorySpinner) {
		description.setText("");
		amount.setText("");
		debet.setSelection(0);
		categorySpinner.setSelection(0);
	}

	
	
	private void showPopUpSearchCriteria() {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Cari Transaksi");
		helpBuilder.setMessage("Kriteria Pencarian");

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
		helpBuilder.setPositiveButton(getString(R.string.show_transaction),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						showTransactionHistory();
					}
				});
		
		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		//((Button) helpDialog.findViewById(android.R.id.button1)).setBackgroundResource(R.drawable.buttoncustom);
		helpDialog.show();
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, ExpenseTrackerPreference.class);
			startActivityForResult(i, RESULT_SETTINGS);
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
				saveResultFile(curFileName);
				Toast.makeText(getApplicationContext(),
						curFileName + " sukses dibuat", Toast.LENGTH_SHORT)
						.show();

			}
			break;
		}
	}

	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		/*
		 * StringBuilder builder = new StringBuilder();
		 * 
		 * builder.append("\n" + ); builder.append("\n" +
		 * sharedPrefs.getString("updates_interval", "-1")); builder.append("\n"
		 * + sharedPrefs.getString("welcome_message", "NULL"));
		 */
		FormatHelper.cur = Currency.getInstance(sharedPrefs.getString(
				getString(R.id.currency), "IDR"));
		setAppTheme(sharedPrefs);

	}

	private void setAppTheme(SharedPreferences sharedPrefs) {
		String bg = sharedPrefs.getString(getString(R.id.bg), "bg1");
		int themeId = 0;
		if (bg.equals("bg1")) {
			themeId = R.style.bg1;
		} else if (bg.equals("bg2")) {
			themeId = R.style.bg2;
		} else if (bg.equals("bg3")) {

			themeId = R.style.bg3;

		}
		Log.d("test",bg);
		getApplication().setTheme(themeId);
	}

	private boolean saveResultFile(String fileName) {

		CSVFileGenerator generator = new CSVFileGenerator(
				transactionHistoryList,
				FinanceHelper.getFinanceHelperHeader(getApplicationContext()),
				fileName);
		return generator.generateCSVFile();
	}
	
	public void checkAndReopenDAOFinHelper(){
	
	}

	@Override
	protected void onResume() {
		checkAndReopenDAOFinHelper();
		super.onResume();
	}

	@Override
	protected void onPause() {
		
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
		daoFinHelper = new FinanceHelperDAO(dbHandler);
		// make sure that the tables are writeable
		daoFinHelper.open();
	}

	@Override
	protected void onDestroy() {
		daoFinHelper.close();
		super.onDestroy();
	}
	
	

}
