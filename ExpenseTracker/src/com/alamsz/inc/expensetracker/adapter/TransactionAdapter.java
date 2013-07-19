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
import com.alamsz.inc.expensetracker.dao.ExpenseTracker;
import com.alamsz.inc.expensetracker.utility.FormatHelper;




public class TransactionAdapter extends BaseAdapter {
	private static final String CREDIT = "K";
	private static final String DEBET = "D";
	private Activity activity;
    private List<ExpenseTracker>data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader;
    static class ViewHolderTransaction{
    	 public TextView txtCategory; 
         public TextView txtDescription ; 
         public TextView txtIncome; 
         public TextView txtExpense; 
    }
    public TransactionAdapter(Activity a,List<ExpenseTracker> d) {
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
            vi = inflater.inflate(R.layout.list_row, null);
            ViewHolderTransaction vHT = new ViewHolderTransaction();
            vHT.txtCategory = (TextView)vi.findViewById(R.id.category_list);
            vHT.txtDescription = (TextView)vi.findViewById(R.id.description_list); 
            vHT.txtIncome = (TextView)vi.findViewById(R.id.debet_list);
            vHT.txtExpense = (TextView)vi.findViewById(R.id.credit_list);
            vi.setTag(vHT);
        }
     
        ExpenseTracker transaction = new ExpenseTracker();
        transaction = data.get(position);
        ViewHolderTransaction vHTItem = (ViewHolderTransaction) vi.getTag();
         // Setting all values in listview
        vHTItem.txtCategory.setText(FormatHelper.formatDateForDisplay(transaction.getDateInput()));
        vHTItem.txtDescription.setText(transaction.getDescription());
        String sign =  transaction.getType().equals(DEBET)?"":"-";
        String kreditStr = "0";
        String debitStr = "0";
		if(transaction.getType().equals(DEBET)){
			
			debitStr = FormatHelper.getBalanceInCurrency(transaction.getAmount());
        }
        if(transaction.getType().equals(CREDIT)){
        	kreditStr = FormatHelper.getBalanceInCurrency(transaction.getAmount());
        }
        vHTItem.txtIncome.setText(debitStr);
        vHTItem.txtExpense.setText(kreditStr);
        return vi;
	}

}
