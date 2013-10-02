package com.alamsz.inc.expensetracker.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.service.ExpenseTrackerService;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.alamsz.inc.expensetracker.utility.StaticVariables;
import com.google.ads.AdView;

public class HomeFragment extends ExpenseTrackerFragment implements OnRefreshListener{
	public static String financeTipsGood = "";
	public static String financeTipsGoodLimit = "";
	public static String financeTipsModerate = "";
	public static String financeTipsModerateLimit = "";
	public static String financeTipsBad = "";
	private static final String ZERO = "0";
	private ExpenseTrackerService expTrackerService;
	private int balance;
	String[] news;
	private AdView mAdView;
	View layout;
	String totalBalance;
	String cashBalance;
	String otherBalance;
	String balanceNext;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {

			return null;
		}

		layout = (View) inflater.inflate(R.layout.home, container, false);
		// get db connection from parent activity
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(
				ExpenseTrackerActivity.dbHandler, getActivity());
		expTrackerService = new ExpenseTrackerService(dbHandler);
		StaticVariables.notSet = getString(R.string.not_set);
		StaticVariables.notSetBudgetExpense = getString(R.string.notset_budget_expense);
		StaticVariables.expenseOverBudget = getString(R.string.over_budget_expense);
		StaticVariables.expenseOkayBudget = getString(R.string.okay_budget_expense);
		StaticVariables.totalCategoryText = getString(R.string.total_category);
		// display the balance when this fragment view are called
		displayHomeInformation(layout);

		return layout;
	}

	
	
	private String getBalance() {

		String balance = getTotalBalanceFromDB();
		if (balance == null)
			balance = ZERO;
		String totalBalance = FormatHelper.getBalanceInCurrency(balance);
		return totalBalance == null ? ZERO : totalBalance;
	}

	private String getTotalBalanceFromDB() {
		return expTrackerService == null ? ZERO : expTrackerService
				.getBalance();
	}

	/**
	 * get balance of amount specific per category There are two category
	 * Tabungan (T) and Cash (C)
	 * 
	 * @param category
	 * @return
	 */
	private String getBalancePerCategory(String category) {

		String balancePerCategory = expTrackerService == null ? null
				: expTrackerService.getBalancePerCategory(category);
		if (balancePerCategory == null)
			balancePerCategory = ZERO;
		
		return FormatHelper.getBalanceInCurrency(balancePerCategory);
	}

	// set display of current balance
	private void displayHomeInformation(View view) {
		TextView saldoTotal = (TextView) view.findViewById(R.id.saldoViewValue);
		TextView saldoTabungan = (TextView) view
				.findViewById(R.id.saldoTabunganViewValue);
		TextView saldoCash = (TextView) view
				.findViewById(R.id.saldoCashViewValue);
		TextView todayDate = (TextView) view.findViewById(R.id.todayViewValue);
		todayDate.setText(FormatHelper.getSystemDate());
		TextView tipsView = (TextView) view.findViewById(R.id.tipsValue);

	    totalBalance = getTotalBalanceFromDB();
		balance = Integer.parseInt(totalBalance == null ? "-1" : totalBalance);
		tipsView.setText(getFinanceTips(balance));
		balanceNext = getBalance();
		saldoTotal.setText(balanceNext);
		TextView txtBalanceCash = (TextView) view
				.findViewById(R.id.saldoCashView);
		ConfigurationExpTracker configurationExpTrackerCash = StaticVariables.mapOfFundCategory
				.get(ExpenseTracker.CAT_CASH);
		String cashDesc = configurationExpTrackerCash==null?"Cash":configurationExpTrackerCash.toString();
		txtBalanceCash
				.setText(String.format(getString(R.string.total_category),
						cashDesc));
		TextView txtBalanceSaving = (TextView) view
				.findViewById(R.id.saldoTabunganView);
		txtBalanceSaving.setText(String.format(
				getString(R.string.total_category),
				getString(R.string.other_than_cash)));

		cashBalance = getBalancePerCategory(ExpenseTracker.CAT_CASH);
		saldoCash.setText(cashBalance);
		otherBalance = FormatHelper
				.getBalanceInCurrency(expTrackerService
						.getBalanceOtherThanCash());
		saldoTabungan.setText(otherBalance);
		mAdView = (AdView) view.findViewById(R.id.adHomeView);
		AdUtility.displayAd(mAdView);

	}

	public String getFinanceTips(int balanceInput) {

		String financeTips = "";
		
		StaticVariables.newsArray = new ArrayList<String>();
		try {
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			financeTipsGood = sharedPrefs.getString("finance_tips_good",
					getString(R.string.finance_tips_good));
			financeTipsModerate = sharedPrefs.getString(
					"finance_tips_moderate",
					getString(R.string.finance_tips_warning));
			financeTipsBad = sharedPrefs.getString("finance_tips_bad",
					getString(R.string.finance_tips_critical));
			financeTipsGoodLimit = sharedPrefs.getString(
					"finance_tips_good_limit", "1000000");
			financeTipsModerateLimit = sharedPrefs.getString(
					"finance_tips_moderate_limit", "100000");

			int goodLimit = financeTipsGoodLimit == null ? 1000000 : Integer
					.parseInt(financeTipsGoodLimit);
			int moderateLimit = financeTipsModerateLimit == null ? 100000
					: Integer.parseInt(financeTipsModerateLimit);

			if (balanceInput == -1) {
				financeTips = "";
			} else if (balanceInput > goodLimit) {
				financeTips = financeTipsGood == null ? getString(R.string.finance_tips_good)
						: financeTipsGood;
			} else if (balanceInput > moderateLimit) {
				financeTips = financeTipsModerate == null ? getString(R.string.finance_tips_warning)
						: financeTipsModerate;

			} else {
				financeTips = financeTipsBad == null ? getString(R.string.finance_tips_critical)
						: financeTipsBad;
			}
		} catch (Exception e) {
			financeTips = "setting tips error, recheck limit input";
		}
		// add finance tips no matter what the result
		StaticVariables.newsArray.add(financeTips);
		updateNews();

		StaticVariables.newsIndex = 0;
		return financeTips;
	}



	private void updateNews() {
		List<ConfigurationExpTracker> confExpCatList = StaticVariables.listOfConfExpCat;
		List<ConfigurationExpTracker> fndTypeList = StaticVariables.listOfFundSource;
		int size = StaticVariables.listOfConfExpCat.size();
		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);

		int monthInt = cal.get(Calendar.MONTH) + 1;
		int yearInt = cal.get(Calendar.YEAR);
		
		// has been reported some problem in this area, catch it for now
		try {
			for (Iterator iterator = fndTypeList.iterator(); iterator.hasNext();) {
				ConfigurationExpTracker configurationExpTracker = (ConfigurationExpTracker) iterator
						.next();
				if (configurationExpTracker != null
						&& configurationExpTracker.getStatus() == 1) {
					String balance = expTrackerService
							.getBalancePerCategory(configurationExpTracker
									.getTableCode());
					
					String tips = String.format(
							StaticVariables.totalCategoryText,
							configurationExpTracker.getLocDesc())
							+ " \n "
							+ FormatHelper.getBalanceInCurrency(balance);

					StaticVariables.newsArray.add(tips);
				}

			}

			for (int i = 0; i < confExpCatList.size(); i++) {
				ConfigurationExpTracker configTemp = confExpCatList.get(i);
				if (configTemp != null) {
					// only display expense category that still active
					if (configTemp.getStatus() == 1) {
						int expense = expTrackerService
								.calculateExpensePerCategory(monthInt, yearInt,
										configTemp.getTableCode());
						String tips = "";
						String expenseStr = FormatHelper
								.getBalanceInCurrency(expense);
						int budgetAmountMonthly = configTemp.getExpBudget()
								.getBudgetAmountMonthly();
						
						String budgetStr = budgetAmountMonthly == 0 ? StaticVariables.notSet
								: FormatHelper
										.getBalanceInCurrency(budgetAmountMonthly);
						String monthStr = StaticVariables.monthStrArr[monthInt - 1]
								+ " " + yearInt;
						if (budgetAmountMonthly == 0) {
							
							tips = String.format(
									StaticVariables.notSetBudgetExpense,
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });
						} else if (budgetAmountMonthly > 0
								&& expense > budgetAmountMonthly) {
							
							tips = String.format(
									StaticVariables.expenseOverBudget,
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });

						} else {
							
							tips = String.format(
									StaticVariables.expenseOkayBudget,
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });
						}

						StaticVariables.newsArray.add(tips);
					}

				}

			}
		} catch (Exception e) {

		}
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		getFinanceTips(balance);
	}

	@Override
	public void onRefresh(TextView txtSaldoOther, TextView txtSaldoCash, TextView txtSaldo, TextView finTips) {
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(
				ExpenseTrackerActivity.dbHandler, getActivity());
		 expTrackerService = new ExpenseTrackerService(dbHandler);
		 totalBalance = getTotalBalanceFromDB();
		 balanceNext = getBalance();
		 cashBalance = getBalancePerCategory(ExpenseTracker.CAT_CASH);
		 otherBalance = FormatHelper
					.getBalanceInCurrency(expTrackerService
							.getBalanceOtherThanCash());
		if(txtSaldo !=null){
			try{
				txtSaldo.setText(balanceNext);
				txtSaldoCash.setText(cashBalance);
				txtSaldoOther.setText(otherBalance);
				String TipsFin = StaticVariables.newsArray.get(0);
				StaticVariables.newsArray = new ArrayList<String>();
				StaticVariables.newsArray.add(TipsFin);
				updateNews();
				finTips.setText(StaticVariables.newsArray.get(0));
			}
			catch(Exception e){
			}
	
		}
		 
	}



	
}
