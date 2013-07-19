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
import com.alamsz.inc.expensetracker.dao.PayRecMaster;
import com.alamsz.inc.expensetracker.dao.PayRecPayment;
import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class PayRecPaymentAdapter extends BaseAdapter {
	private Activity activity;
    private List<PayRecPayment>data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader;
    static class ViewHolderPayRec{
    	 public TextView txtDateInput; 
         public TextView txtDescription ; 
         public TextView txtAmount; 
         
    }
    
    public PayRecPaymentAdapter(Activity a,List<PayRecPayment> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View vi=convertView;
        if(convertView==null){
            vi = inflater.inflate(R.layout.pay_rec_payment_list_content, null);
            ViewHolderPayRec vHT = new ViewHolderPayRec();
            vHT.txtDateInput = (TextView)vi.findViewById(R.id.dateInput);
            vHT.txtDescription = (TextView)vi.findViewById(R.id.description); 
            vHT.txtAmount = (TextView)vi.findViewById(R.id.amount);
            //vHT.txtExpense = (TextView)vi.findViewById(R.id.credit_list);
            vi.setTag(vHT);
        }
     
        PayRecPayment transaction = new PayRecPayment();
        transaction = data.get(position);
        ViewHolderPayRec vHTItem = (ViewHolderPayRec) vi.getTag();
         // Setting all values in listview
        vHTItem.txtDateInput.setText(FormatHelper.formatDateForDisplay(transaction.getDateInput()));
        vHTItem.txtDescription.setText(transaction.getDescription());
       
        String debitStr = "0";
        debitStr = FormatHelper.getBalanceInCurrency(transaction.getAmount());
		
        vHTItem.txtAmount.setText(debitStr);
        vHTItem.txtDateInput.setTag(transaction.getId());
        // vHTItem.txtDueDate.setText(kreditStr);
        return vi;
	}

}
