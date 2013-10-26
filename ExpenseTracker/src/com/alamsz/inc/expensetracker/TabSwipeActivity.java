package com.alamsz.inc.expensetracker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.fragment.HomeFragment;
 
public abstract class TabSwipeActivity extends SherlockFragmentActivity {
 
    private ViewPager mViewPager;
    private TabsAdapter adapter;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*
         * Create the ViewPager and our custom adapter
         */
        mViewPager = new ViewPager(this);
        adapter = new TabsAdapter( this, mViewPager );
        mViewPager.setAdapter( adapter );
        mViewPager.setOnPageChangeListener( adapter );
        
        /*
         * We need to provide an ID for the ViewPager, otherwise we will get an exception like:
         *
         * java.lang.IllegalArgumentException: No view found for id 0xffffffff for fragment TestFragment{40de5b90 #0 id=0xffffffff android:switcher:-1:0}
         * at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:864)
         *
         * The ID 0x7F04FFF0 is large enough to probably never be used for anything else
         */
        mViewPager.setId( 0x7F04FFF0 );
 
        super.onCreate(savedInstanceState);
 
        /*
         * Set the ViewPager as the content view
         */
        setContentView(mViewPager);
        
       
    }
 
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string resource pointing to the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected void addTab(int titleRes, Class fragmentClass, Bundle args, Drawable drawable ) {
        adapter.addTab( getString( titleRes ), fragmentClass, args, drawable );
    }
    /**
     * Add a tab with a backing Fragment to the action bar
     * @param titleRes A string to be used as the title for the tab
     * @param fragmentClass The class of the Fragment to instantiate for this tab
     * @param args An optional Bundle to pass along to the Fragment (may be null)
     */
    protected Tab addTab(CharSequence title, Class fragmentClass, Bundle args, Drawable drawable ) {
        return adapter.addTab( title, fragmentClass, args, drawable );
    }
 
    private class TabsAdapter extends FragmentStatePagerAdapter implements TabListener, ViewPager.OnPageChangeListener {
 
        private final SherlockFragmentActivity mActivity;
        private final ActionBar mActionBar;
        private final ViewPager mPager;
 
        /**
         * @param fm
         * @param fragments
         */
        public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            this.mActivity = activity;
            this.mActionBar = activity.getSupportActionBar();
           //this.mActionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.bluishbackground));	
				
            this.mPager = pager;
            this.mPager.setBackgroundResource(R.drawable.bluishbackground);
            mActionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
        }
 
        private  class TabInfo {
            public final Class fragmentClass;
            public final Bundle args;
            public TabInfo(Class fragmentClass,
                    Bundle args) {
                this.fragmentClass = fragmentClass;
                this.args = args;
            }
        }
 
        
		private List mTabs = new ArrayList();
 
        public Tab addTab( CharSequence title, Class fragmentClass, Bundle args, Drawable drawable ) {
            final TabInfo tabInfo = new TabInfo( fragmentClass, args );
 
            Tab tab = mActionBar.newTab();
          
            tab.setText( title );
            tab.setTabListener( this );
            tab.setTag( tabInfo );
            tab.setIcon(drawable);
            mTabs.add( tabInfo );
            mActionBar.addTab( tab );
            notifyDataSetChanged();
            
            return tab;
        }
 
        @Override
        public Fragment getItem(int position) {
            final TabInfo tabInfo = (TabInfo) mTabs.get( position );
            return (Fragment) Fragment.instantiate( mActivity, tabInfo.fragmentClass.getName(), tabInfo.args );
        }
 
        @Override
        public int getCount() {
            return mTabs.size();
        }
 
        public void onPageScrollStateChanged(int arg0) {
        	//mActionBar.setSelectedNavigationItem( arg0 );
        }
 
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        	// mActionBar.setSelectedNavigationItem( arg0 );
        }
 
        public void onPageSelected(int position) {
        	final TabInfo tabInfo = (TabInfo) mTabs.get( position );
            mActionBar.setSelectedNavigationItem( position );
            selectInSpinnerIfPresent(position, true);
            ExpenseTrackerActivity.positionTab=position;
            
             if(getItem(position) instanceof HomeFragment){
            	HomeFragment homeFragment = (HomeFragment)getItem(position);
            	TextView txtSaldoOther = (TextView) findViewById(R.id.saldoTabunganViewValue);
            	TextView txtSaldoCash = (TextView) findViewById(R.id.saldoCashViewValue);
            	TextView txtSaldo = (TextView) findViewById(R.id.saldoViewValue);
            	TextView finTips = (TextView) findViewById(R.id.tipsValue);
            	homeFragment.onRefresh(txtSaldoOther,txtSaldoCash,txtSaldo,finTips);
            	mPager.getAdapter().notifyDataSetChanged();
            	
            }
            
         
           
            
        }
 
      public void onTabSelected(Tab tab, FragmentTransaction ft) {
            TabInfo tabInfo = (TabInfo) tab.getTag();
            for ( int i = 0; i < mTabs.size(); i++ ) {
                if ( mTabs.get( i ) == tabInfo ) {
                    mPager.setCurrentItem( i );
                    
                }
            }
        }
 
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
 
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
       
        
    }
    /**
	* Hack that takes advantage of interface parity between ActionBarSherlock and the native interface to reach inside
	* the classes to manually select the appropriate tab spinner position if the overflow tab spinner is showing.
	*
	* Related issues: https://github.com/JakeWharton/ActionBarSherlock/issues/240 and
	* https://android-review.googlesource.com/#/c/32492/
	*
	* @author toulouse@crunchyroll.com
	*/
	private  void selectInSpinnerIfPresent(int position, boolean animate) {
		try {
			View actionBarView = findViewById(R.id.abs__action_bar);
			if (actionBarView == null) {
				int id = getResources().getIdentifier("action_bar", "id",
						"android");
				actionBarView = findViewById(id);
			}

			Class<?> actionBarViewClass = actionBarView.getClass();
			Field mTabScrollViewField = actionBarViewClass
					.getDeclaredField("mTabScrollView");
			mTabScrollViewField.setAccessible(true);

			Object mTabScrollView = mTabScrollViewField.get(actionBarView);
			if (mTabScrollView == null) {
				return;
			}

			Field mTabSpinnerField = mTabScrollView.getClass()
					.getDeclaredField("mTabSpinner");
			mTabSpinnerField.setAccessible(true);

			Object mTabSpinner = mTabSpinnerField.get(mTabScrollView);
			if (mTabSpinner == null) {
				return;
			}

			Method setSelectionMethod = mTabSpinner
					.getClass()
					.getSuperclass()
					.getDeclaredMethod("setSelection", Integer.TYPE,
							Boolean.TYPE);
			setSelectionMethod.invoke(mTabSpinner, position, animate);

			Method requestLayoutMethod = mTabSpinner.getClass().getSuperclass()
					.getDeclaredMethod("requestLayout");
			requestLayoutMethod.invoke(mTabSpinner);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public void setTabId(int position){
		mViewPager.setCurrentItem(position);
		
	}
	
	
}
