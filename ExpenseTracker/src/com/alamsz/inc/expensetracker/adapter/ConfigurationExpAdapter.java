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
import com.alamsz.inc.expensetracker.dao.ConfigurationDAO;
import com.alamsz.inc.expensetracker.dao.ConfigurationExpTracker;
import com.alamsz.inc.expensetracker.utility.StaticVariables;

public class ConfigurationExpAdapter extends BaseAdapter {

	private static final String CREDIT = "K";
	private static final String DEBET = "D";
	private Activity activity;
	private List<ConfigurationExpTracker> data;
	private static LayoutInflater inflater = null;

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

		}

		ConfigurationExpTracker transaction = new ConfigurationExpTracker();
		transaction = data.get(position);

		// Setting all values in listview
		Button btnCode = (Button) vi.findViewById(R.id.btnCode);
		TextView txtDesc = (TextView) vi.findViewById(R.id.description);
		btnCode.setText(transaction.getTableCode());
		txtDesc.setText(transaction.getLocDesc());
		Switch onOffSwitch = (Switch) vi.findViewById(R.id.customSwitchOnOff);
		onOffSwitch.setChecked(transaction.getStatus() == 1);
		onOffSwitch.setTag(transaction.getTableCode() + "_"
				+ transaction.getLocDesc());

		int r = Character.getNumericValue(transaction.getTableCode().charAt(0)) * 5 + 50;
		int g = Character.getNumericValue(transaction.getTableCode().charAt(1)) * 3 + 50;
		int b = Character.getNumericValue(transaction.getTableCode().charAt(2)) * 2 + 50;
		btnCode.setBackgroundColor(Color.rgb(r, g, b));
		// btnCode.setOnClickListener();
		return vi;
	}

}
