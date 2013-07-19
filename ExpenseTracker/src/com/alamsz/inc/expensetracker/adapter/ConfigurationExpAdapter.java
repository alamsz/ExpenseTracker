package com.alamsz.inc.expensetracker.adapter;

import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.dao.DatabaseHandler;

public class ConfigurationExpAdapter extends BaseAdapter {

	private static final String CREDIT = "K";
	private static final String DEBET = "D";
	private Activity activity;
	private List<ConfigurationExpTracker> data;
	private static LayoutInflater inflater = null;

	static class ViewHolder
	{
		public Button btnCode;
		public TextView txtDesc;
		public Switch swtStatus;
		
	}
	// public ImageLoader imageLoader;
	public ConfigurationExpAdapter(Activity a, List<ConfigurationExpTracker> d) {
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
			vi = inflater.inflate(R.layout.trans_category_list, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.btnCode = (Button) vi.findViewById(R.id.btnCode);
			viewHolder.txtDesc = (TextView) vi.findViewById(R.id.description);
			viewHolder.swtStatus = (Switch) vi.findViewById(R.id.customSwitchOnOff);
			vi.setTag(viewHolder);
		}
		ViewHolder vHold = (ViewHolder) vi.getTag();
		ConfigurationExpTracker transaction = new ConfigurationExpTracker();
		transaction = data.get(position);
		vHold.swtStatus.setVisibility(Button.VISIBLE);
		String[] keyword = DatabaseHandler.persistentConfig;
		// to make sure config persistent not erased
		for (int i = 0; i < keyword.length; i++) {
			String value = keyword[i];
			String confValue = transaction.getTableType()+transaction.getTableCode();
			if(value.equals(confValue)){
				vHold.swtStatus.setVisibility(Button.INVISIBLE);
				break;
			}
			
		}
		// Setting all values in listview
		
		vHold.btnCode.setText(transaction.getTableCode());
		vHold.txtDesc.setText(transaction.getLocDesc());
		vHold.swtStatus.setChecked(transaction.getStatus() == 1);
		vHold.swtStatus.setTag(transaction.getTableCode() + "_"
				+ transaction.getLocDesc());
		
		int r = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 5 + 50;
		int g = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 50 + 150;
		int b = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 30 + 100;
		vHold.btnCode.setBackgroundColor(Color.rgb(r, g, b));
		// btnCode.setOnClickListener();
		return vi;
	}

}
