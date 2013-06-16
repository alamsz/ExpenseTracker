package com.alamsz.inc.expensetracker.fragment;

import java.io.File;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FileArrayAdapter;
import com.google.ads.AdView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class DirectoryChooserFragment extends Fragment {
	private boolean scalingComplete = false;
	private static final String SLASH = "/";

	private static final int REQUEST_PATH = 1;

	String curFileName;

	EditText edittext;
	File currentDir;
	FileArrayAdapter adapter;
	AdView mAdView;
	
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
        
        View layout = (View)inflater.inflate(R.layout.home, container, false);
		
		return layout;
    }
}
