package com.alamsz.inc.expensetracker.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ReportDetail;

public class ReportDetailAdapter extends BaseAdapter {

	private Activity activity;
	private List<ReportDetail> data;
	private static LayoutInflater inflater = null;

	// public ImageLoader imageLoader;
	static class ViewHolderTransaction {
		// public TextView txtPeriod;
		public TextView txtCategory;
		public TextView txtAmount;
		public TextView txtMonthlyBudget;
	}

	public ReportDetailAdapter(Activity a, List<ReportDetail> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.reportlist_detail, null);
			ViewHolderTransaction vHT = new ViewHolderTransaction();
			// vHT.txtPeriod = (TextView)vi.findViewById(R.id.detailPeriod);
			vHT.txtCategory = (TextView) vi.findViewById(R.id.detailCategory);
			vHT.txtAmount = (TextView) vi
					.findViewById(R.id.amountDetailCategory);
			vHT.txtMonthlyBudget = (TextView) vi
					.findViewById(R.id.detailMonthlyBudget);
			vi.setTag(vHT);
		}

		ReportDetail transaction = new ReportDetail();
		transaction = data.get(position);
		ViewHolderTransaction vHTItem = (ViewHolderTransaction) vi.getTag();
		// Setting all values in listview
		// vHTItem.txtPeriod.setText(transaction.getPeriod());
		vHTItem.txtCategory.setText(transaction.getCategory());
		vHTItem.txtAmount.setText(transaction.getAmount());
		vHTItem.txtMonthlyBudget.setText(transaction.getMonthlyBudget());
		if(transaction.getType().equals("INC_CAT")){
			vHTItem.txtMonthlyBudget.setVisibility(TextView.INVISIBLE);
			((TextView) vi
			.findViewById(R.id.detailMonthlyBudgetExp)).setVisibility(TextView.INVISIBLE);
		}else{
			vHTItem.txtMonthlyBudget.setVisibility(TextView.VISIBLE);
			((TextView) vi
					.findViewById(R.id.detailMonthlyBudgetExp)).setVisibility(TextView.VISIBLE);
		}
		return vi;
	}

}
