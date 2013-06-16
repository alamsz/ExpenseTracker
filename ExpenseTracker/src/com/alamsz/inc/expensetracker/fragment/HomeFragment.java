package com.alamsz.inc.expensetracker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.dao.ExpenseTrackerDAO;
import com.alamsz.inc.expensetracker.utility.AdUtility;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.google.ads.AdView;

public class HomeFragment extends Fragment {
	public static String financeTipsGood = "";
	public static String financeTipsGoodLimit = "";
	public static String financeTipsModerate = "";
	public static String financeTipsModerateLimit = "";
	public static String financeTipsBad = "";
	private static final String ZERO = "0";
	private ExpenseTrackerDAO daoExpTracker;
	private AdView mAdView;
	 public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        if (container == null) {
	            // We have different layouts, and in one of them this
	            // fragment's containing frame doesn't exist.  The fragment
	            // may still be created from its saved state, but there is
	            // no reason to try to create its view hierarchy because it
	            // won't be displayed.  Note this is not needed -- we could
	            // just run the code below, where we would create and return
	            // the view hierarchy; it would just never be used.
	            return null;
	        }
	        
	        View layout = (View)inflater.inflate(R.layout.home, container, false);
	     // get db connection from parent activity
			this.daoExpTracker = ((ExpenseTrackerActivity) getActivity()).daoFinHelper;
			daoExpTracker.open();

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
		return daoExpTracker == null ? null : daoExpTracker
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

			String balancePerCategory = daoExpTracker == null ? null
					: daoExpTracker.getBalancePerCategory(category);
			if (balancePerCategory == null)
				balancePerCategory = ZERO;
			String categoryDescription = getResources().getString(R.string.T);
			if(category.equals(ExpenseTracker.CAT_CASH)){
				categoryDescription = getResources().getString(R.string.C);
			}
			return FormatHelper.getBalanceInCurrency(balancePerCategory);
		}

		// set display of current balance
		private void displayHomeInformation(View view) {
			TextView saldoTotal = (TextView) view.findViewById(R.id.saldoViewValue);
			TextView saldoTabungan = (TextView) view
					.findViewById(R.id.saldoTabunganViewValue);
			TextView saldoCash = (TextView) view.findViewById(R.id.saldoCashViewValue);
			TextView todayDate = (TextView) view.findViewById(R.id.todayViewValue);
			todayDate.setText(FormatHelper.getSystemDate());
			TextView tipsView = (TextView) view.findViewById(R.id.tipsValue);
			
			String totalBalance = getTotalBalanceFromDB();
			tipsView.setText(getFinanceTips(Integer.parseInt(totalBalance==null?"-1":totalBalance)));
			saldoTotal.setText(getBalance());
			saldoCash.setText(getBalancePerCategory(ExpenseTracker.CAT_CASH));
			saldoTabungan.setText(getBalancePerCategory(ExpenseTracker.CAT_SAVING));
			mAdView = (AdView) view.findViewById(R.id.adHomeView);
			AdUtility.displayAd(mAdView);
			
		}
		
		
		public String getFinanceTips(int balance){
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			financeTipsGood = sharedPrefs.getString("finance_tips_good", getString(R.string.finance_tips_good));
			financeTipsModerate = sharedPrefs.getString(
					"finance_tips_moderate", getString(R.string.finance_tips_warning));
			financeTipsBad = sharedPrefs.getString(
					"finance_tips_bad", getString(R.string.finance_tips_critical));
			financeTipsGoodLimit = sharedPrefs.getString(
					"finance_tips_good_limit", "1000000");
			financeTipsModerateLimit = sharedPrefs.getString(
					"finance_tips_moderate_limit", "100000");
			
			String financeTips ="";
			int goodLimit = financeTipsGoodLimit==null?1000000:Integer.parseInt(financeTipsGoodLimit);
			int moderateLimit = financeTipsModerateLimit==null?100000:Integer.parseInt(financeTipsModerateLimit);
			
			if (balance == -1){
				financeTips = "";
			}else if(balance > goodLimit){
				financeTips = financeTipsGood==null?getString(R.string.finance_tips_good):financeTipsGood;
			} else if(balance > moderateLimit){
				financeTips = financeTipsModerate==null?getString(R.string.finance_tips_warning):financeTipsModerate;
			 
			} else {
				financeTips = financeTipsBad==null?getString(R.string.finance_tips_critical):financeTipsBad;
			}
			return financeTips;
		}
}
