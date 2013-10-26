package com.alamsz.inc.expensetracker.utility;

import android.util.Log;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

public class AdUtility {
	public static void displayAd(AdView mAdView) {
		// only show ad if the view not null
		if (mAdView != null) {
			mAdView.setAdListener(new AdListener() {

				@Override
				public void onReceiveAd(Ad arg0) {
					if (arg0 != null)
						Log.d("balanceTab", "Receiving ad : " + arg0.toString());

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
					if (arg1 != null)
						Log.d("balanceTab",
								"failed to revceive ad : " + arg1.toString());

				}

				@Override
				public void onDismissScreen(Ad arg0) {
					// TODO Auto-generated method stub

				}
			});
			if(StaticVariables.mAdRequest == null){
				StaticVariables.mAdRequest = new AdRequest();
			}
			
			mAdView.loadAd(StaticVariables.mAdRequest);
		}
	}

}
