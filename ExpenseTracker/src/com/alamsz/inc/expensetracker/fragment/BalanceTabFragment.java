package com.alamsz.inc.expensetracker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.ExpenseTrackerActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.dao.ExpenseTrackerDAO;
import com.alamsz.inc.expensetracker.utility.FormatHelper;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

public class BalanceTabFragment extends Fragment {
	private static final String ZERO = "0";
	private ExpenseTrackerDAO daoExpTracker;
	private AdView mAdView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		View layout = (View) inflater.inflate(R.layout.balance, container,
				false);
		// get db connection from parent activity
		this.daoExpTracker = ((ExpenseTrackerActivity) getActivity()).daoFinHelper;
		daoExpTracker.open();

		// display the balance when this fragment view are called
		displayBalance(layout);
		mAdView = (AdView) layout.findViewById(R.id.ad);
		mAdView.setAdListener(new AdListener() {

			@Override
			public void onReceiveAd(Ad arg0) {
				if (arg0 != null)
					Log.d(getClass().getName(),
							"Receiving ad : " + arg0.toString());

			}

			@Override
			public void onPresentScreen(Ad arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLeaveApplication(Ad arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				if (arg1 != null)
					Log.d(getActivity().getApplicationInfo().name,
							"failed to revceive ad : " + arg1.toString());

			}

			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub

			}
		});

		AdRequest adRequest = new AdRequest();
		adRequest.addKeyword("sporting goods");
		mAdView.loadAd(adRequest);

		return layout;
	}

	private String getBalance() {

		String balance = daoExpTracker == null ? null : daoExpTracker
				.getBalance();
		if (balance == null)
			balance = ZERO;
		String saldoTotal = getResources().getString(R.string.total_balance)
				+ getResources().getString(R.string.dividerDoubleDot)
				+ FormatHelper.getBalanceInCurrency(balance);
		return saldoTotal == null ? ZERO : saldoTotal;
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
		return getResources().getString(R.string.total_balance) + categoryDescription
				+ getResources().getString(R.string.dividerDoubleDot) + FormatHelper.getBalanceInCurrency(balancePerCategory);
	}

	// set display of current balance
	private void displayBalance(View view) {
		TextView saldoTotal = (TextView) view.findViewById(R.id.saldoView);
		TextView saldoTabungan = (TextView) view
				.findViewById(R.id.saldoTabunganView);
		TextView saldoCash = (TextView) view.findViewById(R.id.saldoCashView);
		saldoTotal.setText(getBalance());
		saldoCash.setText(getBalancePerCategory(ExpenseTracker.CAT_SAVING));
		saldoTabungan.setText(getBalancePerCategory(ExpenseTracker.CAT_CASH));
	}
}
