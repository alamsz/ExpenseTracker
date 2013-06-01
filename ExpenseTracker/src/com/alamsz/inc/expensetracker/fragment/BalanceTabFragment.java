package com.alamsz.inc.expensetracker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.dao.FinanceHelperDAO;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;



public class BalanceTabFragment extends Fragment {
	  private FinanceHelperDAO daoFinHelper;
	  private AdView mAdView;
	  
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
	        View layout = (View)inflater.inflate(R.layout.balance, container, false);
	        //mAdStatus = (TextView) v.findViewById(R.id.status);
	        mAdView = (AdView) layout.findViewById(R.id.ad);
	        //mAdView.loadAd(new AdRequest());
	        mAdView.setAdListener(new AdListener() {
				
				@Override
				public void onReceiveAd(Ad arg0) {
					Log.d(getActivity().getApplicationInfo().name, "Receiving ad : "+arg0.toString());
					
				}
				
				@Override
				public void onPresentScreen(Ad arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLeaveApplication(Ad arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
					Log.d(getActivity().getApplicationInfo().name, "failed to revceive ad : "+arg1.toString());
					
				}
				
				@Override
				public void onDismissScreen(Ad arg0) {
					// TODO Auto-generated method stub
					
				}
			});

	        AdRequest adRequest = new AdRequest();
	        adRequest.addKeyword("sporting goods");
	        mAdView.loadAd(adRequest);
	        
	        return layout;
	    }
	 
}
