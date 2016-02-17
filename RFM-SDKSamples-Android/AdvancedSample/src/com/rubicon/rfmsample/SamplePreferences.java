/*
 * Copyright (C) 2011 Rubicon Project . All rights reserved
 * 
 * @author: Rubicon Project.
 * 
 */

package com.rubicon.rfmsample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rfm.sdk.RFMConstants;
import com.rfm.util.RFMLog;

/**
 * PreferenceActivity class for displaying the MBS Ad settings common to all demo cases
 */
public class SamplePreferences extends PreferenceActivity {
	
	SharedPreferences settings;
	public static String ADSIZE_FILLPARENT_50 = "fillparent x 50dp";
	public static String ADSIZE_320_50 = "320dp x 50dp";
	public static String ADSIZE_300_100 = "300dp x 100dp";
	public static String LOCATION_FIXED = "Fixed";
	public static String LOCATION_IP = "IP";
	public static String LOCATION_GPS = "GPS";
	public static String ORIENTATION_ALL = "All";
	public static String ORIENTATION_PORTRAIT = "Portrait";
	public static String ORIENTATION_LANDSCAPE = "Landscape";
	public static String DEFAULT_APP_ID = "281844F0497A0130031D123139244773";
	public static String DEFAULT_AD_ID = "0";
	public static String DEFAULT_PUB_ID = "111008";

	public static String PREF_SERVERNAME = "rfmserver_name";
	public static String PREF_RFMAPPID = "rfmapp_id";
	public static String PREF_RFMADID = "rfmad_id";
	public static String PREF_RFMPUBID= "rfmpub_id";
	public static String PREF_LOCATION = "location";
	public static String PREF_ORIENTATION = "orientation";
	public static String PREF_ADSIZE = "adsize";
	public static String PREF_LOCATION_LAT = "rfmlocation_lat";
	public static String PREF_LOCATION_LONG = "rfmlocation_long";
	public static String PREF_ADIDSET = "adidsset";
	public static String PREF_APPSET = "appidsset";
	public static String PREF_SDK_VER = "rfm_sdk_ver";
	public static String PREF_RFMTEST_MODE = "rfmtest_mode";

	private static String LOG_TAG = "SamplePreferences";

	@SuppressWarnings("deprecation")
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
		 addPreferencesFromResource(R.xml.preferences);
		 sharedPrefs = getPreferenceScreen().getSharedPreferences();
		 // setPreferenceScreen(generatePreferences());
		 //Set summary of all EditTextPreferences to the value
		 setPreferenceItems();
		 setTextSummaries();
	 }
	
	@Override
	protected void onResume(){
		super.onResume();
		//setTextSummaries();
	}
	
	@Override
	protected void onPause(){
		super.onPause();

	}
	
	//public class PrefsFragment extends PreferenceFragment {
		private EditTextPreference serverName;
		private EditTextPreference rfmPubId;
		private Preference rfmAppId;
		private Preference rfmAdId;
		private EditTextPreference locLat;
		private EditTextPreference locLng;
		private Preference sdkVersion;
		private ListPreference adSizePreference; 
		private ListPreference orientationPreference; 
		private ListPreference locationPreference; 
		private Set<String> appIdsSet;
		private Set<String> adIdsSet;
		private CheckBoxPreference testModePreferemce;
		SharedPreferences sharedPrefs;
		protected LinkedList<String> adIdsList = null;
		protected LinkedList<String> appIdsList = null;
		private final int MAX_HISTORY = 5;
		AlertDialog customDialog;
		EditText editAppIdText;
		EditText editAdIdText;

		PreferenceManager pm = getPreferenceManager();
		
//	    @Override
//	    public void onCreate(Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);
//	        // Load the preferences from an XML resource
//	        addPreferencesFromResource(R.xml.preferences);
//	        sharedPrefs = getPreferenceScreen().getSharedPreferences();
//	        setPreferenceItems();
//			setTextSummaries();
//	    }
//	    
//		@Override
//		public void onResume(){
//			super.onResume();
//			//setTextSummaries();
//		}
	    
		protected void setTextSummaries(){
			if(sharedPrefs!=null){		
				serverName.setSummary(sharedPrefs.getString(PREF_SERVERNAME, "Server Name"));
				rfmPubId.setSummary(sharedPrefs.getString(PREF_RFMPUBID, DEFAULT_PUB_ID));
				String str = sharedPrefs.getString(PREF_RFMAPPID, DEFAULT_APP_ID);
				rfmAppId.setSummary(str);
				rfmAdId.setSummary(sharedPrefs.getString(PREF_RFMADID, DEFAULT_AD_ID));
				locLat.setSummary(sharedPrefs.getString(PREF_LOCATION_LAT, "0.0"));
				locLng.setSummary(sharedPrefs.getString(PREF_LOCATION_LONG, "0.0"));
				String adSizeStr = sharedPrefs.getString(PREF_ADSIZE, SamplePreferences.ADSIZE_FILLPARENT_50);
				adSizePreference.setSummary(adSizeStr);
				String adOrientStr = sharedPrefs.getString(PREF_ORIENTATION, SamplePreferences.ORIENTATION_ALL);
				orientationPreference.setSummary(adOrientStr);
				String locStr = sharedPrefs.getString(PREF_LOCATION, SamplePreferences.LOCATION_GPS);
				locationPreference.setSummary(locStr);
				boolean testMode =sharedPrefs.getBoolean(PREF_RFMTEST_MODE, true);
				testModePreferemce.setChecked(testMode);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					adIdsSet = sharedPrefs.getStringSet(PREF_ADIDSET, null);
					appIdsSet = sharedPrefs.getStringSet(PREF_APPSET, null);
				} else {
					String adIdsSetStr = sharedPrefs.getString(PREF_ADIDSET, null);
					if(adIdsSetStr != null) {
						String[] adArray = adIdsSetStr.split(",");
						adIdsSet = new TreeSet<String>();
						for(int i=0;i<adArray.length;i++) {
							adIdsSet.add(adArray[i]);
						}
						
					}
					String appIdsSetStr = sharedPrefs.getString(PREF_APPSET, null);
					if(appIdsSetStr != null) {
						String[] appArray = appIdsSetStr.split(",");
						appIdsSet = new TreeSet<String>();
						for(int i=0;i<appArray.length;i++) {
							appIdsSet.add(appArray[i]);
						}
					}
				}
				
				if(adIdsSet != null)  {
					adIdsList = new LinkedList<String>();
					adIdsList.addAll(adIdsSet);
				} else {
					adIdsList = new LinkedList<String>();
				}
				
				
				if(appIdsSet != null)  {
					appIdsList = new LinkedList<String>();
					appIdsList.addAll(appIdsSet);
				} else {
					appIdsList = new LinkedList<String>();
					appIdsList.add(DEFAULT_APP_ID);
				}
			}

		}

		protected void setPreferenceItems(){
			pm = getPreferenceManager();
			
			if(pm!=null){
					serverName = (EditTextPreference) pm.findPreference(PREF_SERVERNAME);
					if(serverName!=null){
						serverName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
							public boolean onPreferenceChange(Preference preference, Object newValue) {
								preference.setSummary((String) newValue);	
								return true;
				        	}
						});
					}
					rfmPubId = (EditTextPreference) pm.findPreference(PREF_RFMPUBID);
					if(rfmPubId !=null){
						rfmPubId.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
							@Override
							public boolean onPreferenceChange(Preference preference, Object newValue) {
								preference.setSummary((String) newValue);
								return true;
							}
						});
					}
					
					rfmAppId = (Preference) pm.findPreference(PREF_RFMAPPID);
					if(rfmAppId != null){
						Log.v(LOG_TAG, " RFM APP ID = "+rfmAppId.getSummary());
						rfmAppId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
							@Override
							public boolean onPreferenceClick(Preference preference) {
								// TODO Auto-generated method stub
								showDialog();
								return true;
							}
						});
					}
	
					
					rfmAdId = pm.findPreference(PREF_RFMADID);
					if(rfmAdId != null){
						rfmAdId.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

							@Override
							public boolean onPreferenceClick(Preference preference) {
								// TODO Auto-generated method stub
								showDialog();
								return true;
							}
						});
					}
	
					
					locLat = (EditTextPreference) pm.findPreference(PREF_LOCATION_LAT);
					if(locLat != null){
						locLat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
						       public boolean onPreferenceChange(Preference preference, Object newValue) {
					            preference.setSummary((String) newValue);	
					            return true;
					        }
						});
					}
	
					
					locLng = (EditTextPreference) pm.findPreference(PREF_LOCATION_LONG);
					if(locLng!=null){
						locLng.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
							@Override
							public boolean onPreferenceChange(Preference preference, Object newValue) {
								preference.setSummary((String) newValue);
								return true;
							}
						});
					}
	
					
					sdkVersion = pm.findPreference(PREF_SDK_VER);
					if(sdkVersion !=null){
						sdkVersion.setSummary(RFMConstants.getRFMSDKVersion());
					}
					
					adSizePreference = (ListPreference) pm.findPreference(PREF_ADSIZE);
					if(adSizePreference != null) {
						adSizePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
						       public boolean onPreferenceChange(Preference preference, Object newValue) {
					            preference.setSummary((String) newValue);	
					            return true;
					        }
						});
					}
				 
					orientationPreference=(ListPreference)pm.findPreference(PREF_ORIENTATION);
					if(orientationPreference != null){
						orientationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
						       public boolean onPreferenceChange(Preference preference, Object newValue) {
					            preference.setSummary((String) newValue);	
					            return true;
					        }
						});
					}
					
					
					locationPreference=(ListPreference)pm.findPreference(PREF_LOCATION);
					if(locationPreference != null){
						locationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
						       public boolean onPreferenceChange(Preference preference, Object newValue) {
					            preference.setSummary((String) newValue);	
					            return true;
					        }
						});
					}

					testModePreferemce=(CheckBoxPreference)pm.findPreference(PREF_RFMTEST_MODE);
					if(testModePreferemce != null) {
						testModePreferemce.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
							@Override
							public boolean onPreferenceChange(Preference preference, Object newValue) {
								if(newValue instanceof Boolean){
									Boolean boolVal = (Boolean)newValue;
								}
								return true;
							}
						});
					}
			}
		}
		
		private void handleSelection() {
			handleAdIdSelection();
			handleAppIdSelection();
		}
		
		private void handleAdIdSelection() {
			String selectedAd = "";
			if(editAdIdText != null && editAdIdText.getText().toString().length()>0) {
				selectedAd = editAdIdText.getText().toString().toString();
				//Check if its there in list and add it
				if (!this.adIdsList.contains(selectedAd)) {
					// if it does not, check for size
					if (this.adIdsList.size() >= MAX_HISTORY) {
						this.adIdsList.removeFirst();
					}
					this.adIdsList.addLast(selectedAd);
				}
			}

			SharedPreferences.Editor editor  = sharedPrefs.edit();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Set<String> saveSet = new TreeSet<String>();
				saveSet.addAll(this.adIdsList);
				editor.putStringSet(PREF_ADIDSET, saveSet);
			} else {
				// StringSet is not available in 2.3
				StringBuilder adIdsSetStr = new StringBuilder();
				for(String temp:adIdsList) {
					if(adIdsSetStr.length()>0) {
						adIdsSetStr.append(",");
					}
					adIdsSetStr.append(temp);
				}
				editor.putString(PREF_ADIDSET, adIdsSetStr.toString());
			}
			editor.putString(PREF_RFMADID, selectedAd);
			editor.commit();
			if(this.rfmAdId !=null) {
				this.rfmAdId.setSummary(selectedAd);
			}
		}
		
		private void handleAppIdSelection() {
			String selectedApp = "";
			if(editAppIdText != null && editAppIdText.getText().toString().length()>0) {
				selectedApp = editAppIdText.getText().toString().toString();
				//Check if its there in list and add it
				if (!this.appIdsList.contains(selectedApp)) {
					// if it does not, check for size
					if (this.appIdsList.size() >= MAX_HISTORY) {
						this.appIdsList.removeFirst();
					}
					this.appIdsList.addLast(selectedApp);
				}
			}
			SharedPreferences.Editor editor  = sharedPrefs.edit();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Set<String> saveSet = new TreeSet<String>();
				saveSet.addAll(this.appIdsList);
				editor.putStringSet(PREF_APPSET, saveSet);
			} else {
				// StringSet is not available on 2.3
				StringBuilder appIdsSetStr = new StringBuilder();
				for(String temp:appIdsList) {
					if(appIdsSetStr.length()>0) {
						appIdsSetStr.append(",");
					}
					appIdsSetStr.append(temp);
				}
				editor.putString(PREF_APPSET, appIdsSetStr.toString());
			}

			editor.putString(PREF_RFMAPPID, selectedApp);
			editor.commit();

			if(this.rfmAppId !=null) {
				this.rfmAppId.setSummary(selectedApp);
			}
		}
		
		//@@ Unused code , need to be refactored for for custom dialog
		@SuppressLint({ "NewApi", "InflateParams" })
		private void showDialog() {
			try {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("RFM");
				LayoutInflater inflater = (this).getLayoutInflater();
				View customView=inflater.inflate(R.layout.appadhistory, null);
				builder.setView(customView);
				// Add the buttons
				builder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								handleSelection();
								finish();
								startActivity(getIntent());
								dialog.dismiss();
							}
						});
				builder.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
								dialog.dismiss();
							}
						});

	            // Create the AlertDialog
				customDialog = builder.create();
				editAppIdText = (EditText) customView.findViewById(R.id.appIdEntry);
				editAdIdText = (EditText) customView.findViewById(R.id.testAdIdEntry);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
					// This is because for 2.3, Theme.Light does not work for AlertDialogs
					TextView appIdTitleObj = (TextView) customView.findViewById(R.id.appIdTitle);
					if(appIdTitleObj != null) {
						appIdTitleObj.setTextColor(Color.WHITE);
					}
					
					TextView appIdHistoryTitle = (TextView) customView.findViewById(R.id.appIdHistoryTitle);
					if(appIdHistoryTitle != null) {
						appIdHistoryTitle.setTextColor(Color.WHITE);
					}
					
					TextView testAdIdTitle = (TextView) customView.findViewById(R.id.testAdIdTitle);
					if(testAdIdTitle != null) {
						testAdIdTitle.setTextColor(Color.WHITE);
					}
					
					TextView testAdHistoryTitle = (TextView) customView.findViewById(R.id.testAdHistoryTitle);
					if(testAdHistoryTitle != null) {
						testAdHistoryTitle.setTextColor(Color.WHITE);
					}				
				}
				//prefDialog.setContentView(R.layout.history);
				addItemsOnAppSpinner(customView);
				addItemsOnAdSpinner(customView);
				
				if(this.rfmAppId.getSummary().toString().equalsIgnoreCase("")) {
					editAppIdText.setText("");
				} else {
					editAppIdText.setText(this.rfmAppId.getSummary());
				}
				editAdIdText.setText(this.rfmAdId.getSummary());
				
	
				customDialog.show();
			} catch(Exception e) {
				Log.v("SamplePreferences", "Mraid Could not show confirmation dialog for Store Picture "+e.toString());
				if(RFMLog.canLogVerbose()) {
					e.printStackTrace();
				}
				// show alert/toast
				Log.v("SamplePreferences", "Mraid, store image unsuccessful");
			}

		}

		// add items into spinner dynamically
		 public void addItemsOnAppSpinner(View customView) {
			if(this.customDialog == null) {
				 return;
			}
			Spinner appIdSpinner = (Spinner) customView.findViewById(R.id.appIdHistory);
			List<String> list = new ArrayList<String>();
			if(this.appIdsList != null && this.appIdsList.size()>0) {
				list.addAll(this.appIdsList);
			}
			list.add("None");
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			appIdSpinner.setAdapter(dataAdapter);
			appIdSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String selectedText = parent.getItemAtPosition(position).toString();
					if(editAppIdText != null) {
						if(selectedText != null && selectedText.equalsIgnoreCase("None")) {
							editAppIdText.setText("");
						} else {
							editAppIdText.setText(selectedText);
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					if(editAppIdText != null) {
						editAppIdText.setText("");
					}
				}
			
			});
			int i=0;
			for(String str:list) {
				if(this.rfmAppId != null && str.equalsIgnoreCase(this.rfmAppId.getSummary().toString())) {
					appIdSpinner.setSelection(i);
					i=-1;
					break;
				}
				i++;
			}

			 if(i>-1 && list.size()>0) {
				 appIdSpinner.setSelection(i-1);
			 }
		  			
		  }
		 
			// add items into spinner dynamically
		 public void addItemsOnAdSpinner(View customView) {
			if(this.customDialog == null) {
				 return;
			}
			Spinner adIdSpinner = (Spinner) customView.findViewById(R.id.testAdIdHistory);
			List<String> list = new ArrayList<String>();
			if(this.adIdsList != null && this.adIdsList.size()>0) {
				list.addAll(this.adIdsList);
			}
			list.add("None");
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adIdSpinner.setAdapter(dataAdapter);
			adIdSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					String selectedText = parent.getItemAtPosition(position).toString();
					if(editAdIdText != null) {
						if(selectedText != null && selectedText.equalsIgnoreCase("None")) {
							editAdIdText.setText("");
						} else {
							editAdIdText.setText(selectedText);
						}
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					if(editAdIdText != null) {
						editAdIdText.setText("");
					}
				}
			
			});
			// Check for the AdId in SharedPreferences and set Spinner with selection
			int i=0;
			for(String str:list) {
				if(this.rfmAdId != null && str.equalsIgnoreCase(this.rfmAdId.getSummary().toString())) {
					adIdSpinner.setSelection(i);
					i=-1;
					break;
				} else {
					i++;
				}
			}
			if(i>-1 && list.size()>0) {
				// Could not find a match, set it to "None" which is the last entry in the list
				adIdSpinner.setSelection(i-1);
			}
		  }
	//}
}

