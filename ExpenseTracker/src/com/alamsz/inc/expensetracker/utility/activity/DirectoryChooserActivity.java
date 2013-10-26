package com.alamsz.inc.expensetracker.utility.activity;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.alamsz.inc.expensetracker.R;
import com.alamsz.inc.expensetracker.utility.FileArrayAdapter;
import com.alamsz.inc.expensetracker.utility.FileItem;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

public class DirectoryChooserActivity extends SherlockFragmentActivity {

	private static final String SLASH = "/";

	private static final int REQUEST_PATH = 1;

	String curFileName;

	EditText edittext;
	File currentDir;
	FileArrayAdapter adapter;
	AdView mAdView;
	Class callingActivity;
	Boolean fileClickable = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directory_chooser);
		currentDir = Environment.getExternalStorageDirectory();
		Intent intent = getIntent();
		fileClickable = intent.getBooleanExtra("fileClickable", false);
		generateFolderView(currentDir);
		
		Button saveOrOpenButton = (Button) findViewById(R.id.buttonSave);
		if(fileClickable){
			saveOrOpenButton.setText("Open");
			saveOrOpenButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clickOpen(v);
					
				}
			});
		}else{
			saveOrOpenButton.setText("Save");
			saveOrOpenButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					clickSave(v);
					
				}
			});
		}
	    
		
		mAdView = (AdView) this.findViewById(R.id.ad);
	    mAdView.setAdListener(new AdListener() {
			
			@Override
			public void onReceiveAd(Ad arg0) {
				Log.d(getApplicationInfo().name, "Receiving ad : "+arg0.toString());
				
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
				Log.d(getApplicationInfo().name, "Failed to receive ad : "+arg1.toString());
				
			}
			
			@Override
			public void onDismissScreen(Ad arg0) {
				// TODO Auto-generated method stub
				
			}
		});

	    AdRequest adRequest = new AdRequest();
	    adRequest.addKeyword("sporting goods");
	    mAdView.loadAd(adRequest);
	    getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		View v =  super.onCreateView(name, context, attrs);
		
	    
	    return v;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		super.onWindowFocusChanged(hasFocus);
	}

	private void generateFolderView(File f) {
		File[] dirs = f.listFiles();
		this.setTitle(getString(R.string.cur_dir) + f.getName());
		List<FileItem> dir = new ArrayList<FileItem>();
		List<FileItem> fls = new ArrayList<FileItem>();
		try {
			for (File ff : dirs) {
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if (ff.isDirectory()) {

					File[] fbuf = ff.listFiles();
					int buf = 0;
					if (fbuf != null) {
						buf = fbuf.length;
					} else
						buf = 0;
					String num_item = String.valueOf(buf);
					if (buf == 0)
						num_item = num_item + " FileItem";
					else
						num_item = num_item + " items";

					// String formated = lastModDate.toString();
					dir.add(new FileItem(ff.getName(), num_item, date_modify,
							ff.getAbsolutePath(), "directory_icon"));
				} else {

					fls.add(new FileItem(ff.getName(), ff.length() + " Byte",
							date_modify, ff.getAbsolutePath(), "file_icon"));
				}
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"no external storage found", Toast.LENGTH_SHORT).show();
		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new FileItem("..", "Parent Directory", "",
					f.getParent(), "directory_up"));
		ListView listFolder = (ListView) findViewById(R.id.listDir);
		adapter = new FileArrayAdapter(this, R.layout.file_view, dir);
		listFolder.setAdapter(adapter);
		listFolder.setOnItemClickListener(listClickListener);
	}

	OnItemClickListener listClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			FileItem o = adapter.getItem(position);
			if (o.getImage().equalsIgnoreCase("directory_icon")
					|| o.getImage().equalsIgnoreCase("directory_up")) {
				if(o.getPath() != null){
					currentDir = new File(o.getPath());
					generateFolderView(currentDir);
				}
				
			}else {
				if(fileClickable){
					currentDir = new File(o.getPath());
					EditText fileNameEdit = (EditText) findViewById(R.id.fileNameText);
					fileNameEdit.setText(currentDir.getPath());
				}
			}

		}
	};

	public void clickSave(View view) {
		EditText fileNameEdit = (EditText) findViewById(R.id.fileNameText);
		String fileName = fileNameEdit.getText().toString();
		if (fileName.equals("")) {
			fileNameEdit.setError(getString(R.string.mustNotEmpty));
		} else {
			this.getIntent().putExtra("getFullPathName",
					currentDir +SLASH+ fileName);
			setResult(RESULT_OK, this.getIntent());
			finish();
		}
	}

	public void clickOpen(View view){
		
		//EditText fileNameEdit = (EditText) findViewById(R.id.fileNameText);
		//String fileName = fileNameEdit.getText().toString();
		//if (fileName.equals("")) {
		//	fileNameEdit.setError(getString(R.string.mustNotEmpty));
		//} else {
			this.getIntent().putExtra("getFullPathName",
					currentDir.getPath());
			setResult(RESULT_OK, this.getIntent());
			finish();
		//}
	}
	
	public void clickCancel(View view) {
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		 switch (item.getItemId()) {
		    case android.R.id.home:
		        // app icon in action bar clicked; go home
		    	
		    	//finish();
		    	Intent intent = new Intent(this, getCallingActivity().getClass());
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
		        break;
		    }
		    return true;
	}
}
