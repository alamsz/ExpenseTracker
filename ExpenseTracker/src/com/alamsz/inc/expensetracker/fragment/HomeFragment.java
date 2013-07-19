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

public class HomeFragment extends ExpenseTrackerFragment {
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

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {

			return null;
		}

		View layout = (View) inflater.inflate(R.layout.home, container, false);
		// get db connection from parent activity
		DatabaseHandler dbHandler = FormatHelper.getDBHandler(
				ExpenseTrackerActivity.dbHandler, getActivity());
		expTrackerService = new ExpenseTrackerService(dbHandler);

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
		String categoryDescription = getResources().getString(R.string.T);
		if (category.equals(ExpenseTracker.CAT_CASH)) {
			categoryDescription = getResources().getString(R.string.C);
		}
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

		String totalBalance = getTotalBalanceFromDB();
		balance = Integer.parseInt(totalBalance == null ? "-1" : totalBalance);
		tipsView.setText(getFinanceTips(balance));
		saldoTotal.setText(getBalance());
		TextView txtBalanceCash = (TextView) view
				.findViewById(R.id.saldoCashView);
		txtBalanceCash
				.setText(String.format(getString(R.string.total_category),
						StaticVariables.mapOfFundCategory
								.get(ExpenseTracker.CAT_CASH)));
		TextView txtBalanceSaving = (TextView) view
				.findViewById(R.id.saldoTabunganView);
		txtBalanceSaving.setText(String.format(
				getString(R.string.total_category),
				getString(R.string.other_than_cash)));

		saldoCash.setText(getBalancePerCategory(ExpenseTracker.CAT_CASH));
		saldoTabungan.setText(FormatHelper
				.getBalanceInCurrency(expTrackerService
						.getBalanceOtherThanCash()));
		mAdView = (AdView) view.findViewById(R.id.adHomeView);
		AdUtility.displayAd(mAdView);

	}

	public String getFinanceTips(int balanceInput) {

		String financeTips = "";
		List<ConfigurationExpTracker> confExpCatList = StaticVariables.listOfConfExpCat;
		List<ConfigurationExpTracker> fndTypeList = StaticVariables.listOfFundSource;
		int size = StaticVariables.listOfConfExpCat.size();
		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);

		int monthInt = cal.get(Calendar.MONTH) + 1;
		int yearInt = cal.get(Calendar.YEAR);
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
							getString(R.string.total_category),
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
						String budgetStr = budgetAmountMonthly == 0 ? getString(R.string.not_set)
								: FormatHelper
										.getBalanceInCurrency(budgetAmountMonthly);
						String monthStr = StaticVariables.monthStrArr[monthInt - 1]
								+ " " + yearInt;
						if (budgetAmountMonthly == 0) {
							tips = String.format(
									getString(R.string.notset_budget_expense),
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });
						} else if (budgetAmountMonthly > 0
								&& expense > budgetAmountMonthly) {
							tips = String.format(
									getString(R.string.over_budget_expense),
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });

						} else {
							tips = String.format(
									getString(R.string.okay_budget_expense),
									new String[] { configTemp.getLocDesc(),
											monthStr, expenseStr, budgetStr });
						}

						StaticVariables.newsArray.add(tips);
					}

				}

			}
		} catch (Exception e) {

		}

		StaticVariables.newsIndex = 0;
		return financeTips;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		getFinanceTips(balance);
	}
}
