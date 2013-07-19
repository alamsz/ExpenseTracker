package com.alamsz.inc.expensetracker;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;

public class ReportTabListener  implements TabListener {
	 
	private SherlockFragment mFragment;
	private Activity mActivity;
	private String mTag ;
	private Class mClass ;
	public ReportTabListener(Activity mActivity, String mTag, Class mClass) {
		super();
		this.mActivity = mActivity;
		this.mTag = mTag;
		this.mClass = mClass;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (mFragment == null) {
		      // If not, instantiate and add it to the activity
		      mFragment = (SherlockFragment) Fragment.instantiate(
		                        mActivity, mClass.getName());
		     // mFragment.setProviderId(mTag); // id for event provider
		      ft.add(android.R.id.content, mFragment, mTag);
		    } else {
		      // If it exists, simply attach it in order to show it
		      ft.attach(mFragment);
		    }

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		 if (mFragment != null) {
		      // Detach the fragment, because another one is being attached
		      ft.detach(mFragment);
		    }

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

}
