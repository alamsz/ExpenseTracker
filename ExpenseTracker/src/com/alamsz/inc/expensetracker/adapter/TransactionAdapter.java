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
import com.alamsz.inc.expensetracker.dao.FinanceHelper;
import com.alamsz.inc.expensetracker.utility.FormatHelper;




public class TransactionAdapter extends BaseAdapter {
	private Activity activity;
    private List<FinanceHelper>data;
    private static LayoutInflater inflater=null;
    //public ImageLoader imageLoader;
 
    public TransactionAdapter(Activity a,List<FinanceHelper> d) {
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
        }
        TextView category = (TextView)vi.findViewById(R.id.category_list); // title
        TextView description = (TextView)vi.findViewById(R.id.description_list); // artist name
        TextView debet = (TextView)vi.findViewById(R.id.debet_list); // duration
        TextView credit = (TextView)vi.findViewById(R.id.credit_list);
        
        FinanceHelper transaction = new FinanceHelper();
        transaction = data.get(position);
        
        // Setting all values in listview
        category.setText(FinanceHelper.getCategoryDescription(transaction.getCategory()));
        description.setText(FormatHelper.formatDateForDisplay(transaction.getDateInput()));
        String sign =  transaction.getType().equals("D")?"":"-";
        if(transaction.getType().equals("D")){
        	debet.setText(transaction.getAmountInCurrency());
        }
        if(transaction.getType().equals("K")){
        	credit.setText(transaction.getAmountInCurrency());
        }
        
        return vi;
	}

}
