package com.alamsz.inc.expensetracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FormatHelper;

public class InputTabFragment extends Fragment {
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
	        
	        View layout = (View)inflater.inflate(R.layout.input, container, false);
			initializeTabContent(layout);
			return layout;
	    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		/*AutomaticScalingLayout at = new AutomaticScalingLayout();
		at.scaleContents(view);*/
	}

	private void initializeTabContent(View layout) {
		FormatHelper.setCurrentDateOnView((EditText) layout.findViewById(R.id.dateInputText));
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.category_input,R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity().getApplicationContext(), R.array.type_input,R.layout.spinner_item);
		adapter2.setDropDownViewResource(R.layout.spinner_dropdown_item);
		Spinner categorySpinner = (Spinner) layout.findViewById(R.id.categorySpinner);
		categorySpinner.setAdapter(adapter);
		Spinner typeSpinner = (Spinner) layout.findViewById(R.id.typeSpinner);
		typeSpinner.setAdapter(adapter2);
		
	}
	
}
