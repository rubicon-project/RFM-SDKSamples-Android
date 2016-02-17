/*
~  * Copyright (C) 2012 Rubicon Project. All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */


package com.rubicon.rfmsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * RFMAdSample is a demo application that simulates the typical application scenarios
 * in order to assist you with integrating the RFM SDK.
 * 
 * This activity is the main activity for the sample application, displaying a list of
 * various demo test cases. Each demo simulates a typical scenario of requesting and displaying
 * ads fetched from RFM server.
 * SDK Version: RFM SDK 4.0.0
 */
public class RFMSample extends Activity {

	ListView lview;
	ListViewAdapter lviewAdapter;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        // Entries on this string-array must match activity names
		final String[] actArray = getResources().getStringArray(R.array.testcase_activities);
		final String[] testcaseTitles = getResources().getStringArray(R.array.testcase_titles);
		final String[] testcaseDescriptions = getResources().getStringArray(R.array.testcase_descriptions);

		lview = (ListView) findViewById(R.id.list);
		lviewAdapter = new ListViewAdapter(this, testcaseTitles, testcaseDescriptions);
		lview.setAdapter(lviewAdapter);

		lview.setOnItemClickListener(new OnItemClickListener() {
			
			// Launch the correct activity when list view item is clicked
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String activityName = actArray[position];
				Intent i;
				try {
					i = new Intent(getApplicationContext(),	Class.forName(activityName));
					startActivity(i);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});        
    }

    /** Options/Preferences menu launches view for setting RFMAd 
     * parameters applicable to all demo cases **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		Intent prefsIntent = new Intent(getApplicationContext(), SamplePreferences.class);

		MenuItem preferences = menu.findItem(R.id.settings_option_item);
		preferences.setIntent(prefsIntent);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.settings_option_item:
				startActivity(item.getIntent());
				break;
		}
		return true;
	}    
}
