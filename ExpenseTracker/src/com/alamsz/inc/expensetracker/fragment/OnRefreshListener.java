package com.alamsz.inc.expensetracker.fragment;

import android.widget.TextView;

public interface OnRefreshListener {
	public void onRefresh(TextView txtSaldoOther, TextView txtSaldoCash,
			TextView txtSaldo, TextView finTips);
}
