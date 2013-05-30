package com.alamsz.inc.expensetracker;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
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
import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class ExpenseTrackerActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener {
	private static final String PEMASUKAN_DESCRIPTION = "Pemasukan";
	private static final String TABUNGAN_DESCRIPTION = "Tabungan";
	private static final String DEBET = "D";
	private static final String CASH_DESCRIPTION = "Cash";
	private static final String KREDIT = "K";
	private static final String CASH = "C";
	private static final String TABUNGAN = "T";
	private FinanceHelperDAO daoFinHelper;
	private EditText dateFieldChosen;
	static final int DATE_DIALOG_ID = 999;

	private int year;
	private int month;
	private int day;

	private View layout;
	private TabHost mTabHost;
	private HashMap mapTabInfo = new HashMap();
	private TabInfo mLastTab = null;

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
		setContentView(R.layout.tablayout);
		// Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
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
										R.drawable.apps_mobislenotes)),
				(tabInfo = new TabInfo(getString(R.string.transaction), InputTabFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ExpenseTrackerActivity
				.addTab(this,
						this.mTabHost,
						this.mTabHost.newTabSpec(getString(R.string.balance)).setIndicator(
								createTabView(getString(R.string.balance),
										R.drawable.apps_scoremobile)),
						(tabInfo = new TabInfo(getString(R.string.balance),
								BalanceTabFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		ExpenseTrackerActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec(getString(R.string.trans_hist)).setIndicator(
						createTabView(getString(R.string.trans_hist),
								R.drawable.apps_rootzwiki_smile)),
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
		getMenuInflater().inflate(R.menu.main, menu);
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
		if(tag.equals(getString(R.string.balance))){
			displayBalance();
		}
		if(tag.equals(getString(R.string.trans_hist))){
			refreshListOfTransaction();
		}

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

	private void showDatePicker(EditText dateField) {
		DateDialogFragment date = new DateDialogFragment();
		/**
		 * Set Up Current Date Into dialog
		 */
		//if(dateField.getText().equals("")){
			Calendar calender = Calendar.getInstance();
			Bundle args = new Bundle();
			args.putInt("year", calender.get(Calendar.YEAR));
			args.putInt("month", calender.get(Calendar.MONTH));
			args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
			
			date.setArguments(args);
		//}
		/**
		 * Set Call back to capture selected date
		 */
		date.setCallBack(ondate);
		date.show(getSupportFragmentManager(), "Date Picker");
	}

	OnDateSetListener ondate = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month,
				int day) {
			
			dateFieldChosen.setText(new StringBuilder().append(day).append("-")
					.append(month>9?String.valueOf(month+1):"0"+String.valueOf(month+1)).append("-").append(year)
					.append(" "));
			Log.d(getClass().getName(),String.valueOf(day));
			Log.d(getClass().getName(),month>9?String.valueOf(month+1):"0"+String.valueOf(month+1));
			Log.d(getClass().getName(),String.valueOf(year));
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

		List<FinanceHelper> listHistTransaction = null;
		if (!dateFrom.getText().toString().equals("")
				|| !dateTo.getText().toString().equals("") || !category.equals("")) {
			listHistTransaction = daoFinHelper.getListPerPeriod(dateFrom
					.getText().toString(), dateTo.getText().toString(),
					category);
		} else {
			listHistTransaction = daoFinHelper.getAllFinanceHelper();
			
		}
		ListView listPencatatKeuangan;
		listPencatatKeuangan = (ListView) findViewById(R.id.expandableListView1);
		

		TransactionAdapter adapter = new TransactionAdapter(this, listHistTransaction);
		listPencatatKeuangan.setAdapter(adapter);

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
						+ FinanceHelper.getCategoryDescription(category)
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

	private void displayBalance() {
		TextView saldoTotal = (TextView) findViewById(R.id.saldoView);
		TextView saldoTabungan = (TextView) findViewById(R.id.saldoTabunganView);
		TextView saldoCash = (TextView) findViewById(R.id.saldoCashView);
		saldoTotal.setText(getBalance());
		saldoCash.setText(getBalancePerCategory(TABUNGAN));
		saldoTabungan.setText(getBalancePerCategory(CASH));
	}

	/**
	 * method to get all transactions and displayed in list view
	 */
	private void refreshListOfTransaction() {
		ListView listPencatatKeuangan;
		listPencatatKeuangan = (ListView) findViewById(R.id.expandableListView1);
		List<FinanceHelper> values = daoFinHelper.getAllFinanceHelper();

		TransactionAdapter adapter = new TransactionAdapter(this, values);
		listPencatatKeuangan.setAdapter(adapter);
		// getBalance();
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
		helpDialog.show();
	}

	private String getBalance() {
		String balance = daoFinHelper.getBalance();
		if (balance == null)
			balance = "0";
		String saldoTotal = "Total Saldo : " + getBalanceInCurrency(balance);
		return saldoTotal;
	}
	
	private String getBalanceInCurrency(String balance){
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.JAPAN);
		nf.setCurrency(Currency.getInstance("IDR"));
		int balanceInt = 0;
		if(balance != null)
			balanceInt = Integer.parseInt(balance);
		return nf.format(balanceInt);
	}

	/**
	 * get balance of amount specific per category There are two category
	 * Tabungan (T) and Cash (C)
	 * 
	 * @param category
	 * @return
	 */
	private String getBalancePerCategory(String category) {
		String balancePerCategory = daoFinHelper
				.getBalancePerCategory(category);
		if (balancePerCategory == null)
			balancePerCategory = "0";
		return "Saldo " + FinanceHelper.getCategoryDescription(category)
				+ " : " + getBalanceInCurrency(balancePerCategory);
	}
}
