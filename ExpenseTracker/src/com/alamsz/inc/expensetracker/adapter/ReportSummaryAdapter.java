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
import com.alamsz.inc.expensetracker.dao.ReportSummary;

public class ReportSummaryAdapter extends BaseAdapter {
	
	private Activity activity;
    private List<ReportSummary>data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader;
    static class ViewHolderTransaction{
    	 public TextView txtPeriod; 
         public TextView txtIncome; 
         public TextView txtExpense; 
    }
    public ReportSummaryAdapter(Activity a,List<ReportSummary> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
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
		View vi=convertView;
        if(convertView==null){
            vi = inflater.inflate(R.layout.reportlist_summary, null);
            ViewHolderTransaction vHT = new ViewHolderTransaction();
            vHT.txtPeriod = (TextView)vi.findViewById(R.id.period);
            vHT.txtIncome = (TextView)vi.findViewById(R.id.amountIncome);
            vHT.txtExpense = (TextView)vi.findViewById(R.id.amountExpense);
            vi.setTag(vHT);
        }
     
        ReportSummary transaction = new ReportSummary();
        transaction = data.get(position);
        ViewHolderTransaction vHTItem = (ViewHolderTransaction) vi.getTag();
         // Setting all values in listview
        vHTItem.txtPeriod.setText(transaction.getPeriod());
        vHTItem.txtIncome.setText(transaction.getAmountIncome());
        vHTItem.txtExpense.setText(transaction.getAmountExpense());
        return vi;
	}

}
