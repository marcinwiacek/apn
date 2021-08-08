package com.mwiacek.apn;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;

public class PreferencesActivity extends PreferenceActivity {
    final Activity MyActivity5 = this;
	String line="";
	Process p;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sett2);

    	try {
        	  PackageManager manager = getPackageManager();
        	  PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

        	  if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		  setTitle("APN "+info.versionName+" (opcje)");  
        	  } else {
        		  setTitle("APN "+info.versionName+" (options)");
        	  }
        	  
        } catch (Exception e) {
        	  
        }

    	Preference customPref = (Preference) findPreference("Czyszczenie");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		public boolean onPreferenceClick(Preference preference) {
        			Intent intent = new Intent(Intent.ACTION_VIEW);
                	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {        			
                		intent.setData(Uri.parse("http://mwiacek.com/www/?q=node/121"));
                	} else {
                		intent.setData(Uri.parse("http://mwiacek.com/www/?q=node/98"));
                		
                	}
        			MyActivity5.startActivity(intent);
        			
        			return true;
        		}
        });

        customPref = (Preference) findPreference("ExtFileExport");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		public boolean onPreferenceClick(Preference preference) {
	    		    Intent intent = new Intent(MyActivity5, FilesActivity.class);
	    		    intent.putExtra(FilesActivity.EXTRA_MESSAGE, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFilePath2", MyActivity5.getExternalFilesDir(null).toString()));
	    	    	try {
	    	    		PackageManager manager = getPackageManager();
	    	    		PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
	        		    intent.putExtra(FilesActivity.EXTRA_MESSAGE2, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
	    	    	} catch (Exception e) {          	  
	  	      		}
	    	        intent.putExtra(FilesActivity.EXTRA_MESSAGE3, "save");
	    	        startActivityForResult(intent, 1);
        	        return true;        	        
        		}
        });
    	
        customPref = (Preference) findPreference("ExtFileExport2");
        if (Build.VERSION.RELEASE.charAt(0)=='4' && Build.VERSION.RELEASE.charAt(2)>'1') {
    		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
    			customPref.setEnabled(false);
    		}
    	}
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		public boolean onPreferenceClick(Preference preference) {
	    		    Intent intent = new Intent(MyActivity5, FilesActivity.class);
	    		    intent.putExtra(FilesActivity.EXTRA_MESSAGE, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFilePath2", MyActivity5.getExternalFilesDir(null).toString()));
	    	    	try {
	    	    		PackageManager manager = getPackageManager();
	    	    		PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
	        		    intent.putExtra(FilesActivity.EXTRA_MESSAGE2, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
	    	    	} catch (Exception e) {          	  
	  	      		}
	    	        intent.putExtra(FilesActivity.EXTRA_MESSAGE3, "save2");
	    	        startActivityForResult(intent, 1);
        	        return true;        	        
        		}
        });
        customPref = (Preference) findPreference("Czyszczenie2");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		public boolean onPreferenceClick(Preference preference) {    	        
    	        	Intent emailIntent=new Intent(Intent.ACTION_SEND);
    	        	String subject = "";
    	        	try {
    	            	  PackageManager manager = getPackageManager();
    	            	  PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

    	            	  subject="APN "+info.versionName+" / Android "+Build.VERSION.RELEASE;
    	          	} catch (Exception e) {          	  
    	          	}
    	        	
    	        	String[] extra = new String[]{"marcin@mwiacek.com"};
    	        	emailIntent.putExtra(Intent.EXTRA_EMAIL, extra);
    	        	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    	        	emailIntent.putExtra(Intent.EXTRA_TEXT, getIntent().getExtras().getString("values"));
    	        	emailIntent.setType("message/rfc822");
    	        	try {
    	        		startActivity(emailIntent);
    	        	} catch (Exception e) {
    		    		AlertDialog alertDialog = new AlertDialog.Builder(MyActivity5).create();
    		    		alertDialog.setTitle("APN");
    		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
    		        		alertDialog.setMessage("B³¹d stworzenia maila");
    		        	} else {
    		        		alertDialog.setMessage("Error creating email");		        		
    		        	}
    	    	   		alertDialog.show();   	   		        			        		
    	        	}
    	            return true;     	             	        
        		}
        });
        
        customPref = (Preference) findPreference("SysApp");
        SharedPreferences.Editor prefs = customPref.getEditor();
		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
			prefs.putBoolean("SysApp", false);    		            			
		} else {
	    	prefs.putBoolean("SysApp", true);    	    	     		    				
		}
    	prefs.commit();
  		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    		public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=de.j4velin.systemappmover"));
				MyActivity5.startActivity(intent);

				return true;
    		}
		});
		customPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
    		public boolean onPreferenceChange (Preference preference, Object newValue) {
    			return false;
    		}
		});
		    	    	
		customPref = (Preference) findPreference("Apndroid");
		if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("ApndroidLTE", false)) {
        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		customPref.setSummary("Dodawanie 'apndroid' na koñcu APN do Internetu przy zmianach przyciskami");		        		
        	} else {
        		customPref.setSummary("Adding 'apndroid' on the Internet APN end on button changes.");		        		
        	}					
		} else {
        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		customPref.setSummary("Dodawanie 'apndroid' na koñcu APN do Internetu przy starcie/zmianach przyciskami");		        		
        	} else {
        		customPref.setSummary("Adding 'apndroid' on the Internet APN end on start/button changes.");		        		
        	}					
		}		
		if (Build.VERSION.RELEASE.charAt(0)=='4') {
    		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
    	        customPref = (Preference) findPreference("Auto");
    	        customPref.setEnabled(false);
    	        customPref = (Preference) findPreference("ApndroidLTE");
    	        customPref.setEnabled(false);    	            	        
    		}
    	}
		
		customPref = (Preference) findPreference("ApndroidLTE");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    		public boolean onPreferenceClick(Preference preference) {
				Preference customPref = (Preference) findPreference("Apndroid");
				if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("ApndroidLTE", false)) {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
		        		customPref.setSummary("Dodawanie 'apndroid' na koñcu APN do Internetu przy zmianach przyciskami");		        		
		        	} else {
		        		customPref.setSummary("Adding 'apndroid' on the Internet APN end on button changes.");		        		
		        	}					
				} else {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
		        		customPref.setSummary("Dodawanie 'apndroid' na koñcu APN do Internetu przy starcie/zmianach przyciskami");		        		
		        	} else {
		        		customPref.setSummary("Adding 'apndroid' on the Internet APN end on start/button changes.");		        		
		        	}					
				}
	    		
	    		AlertDialog alertDialog = new AlertDialog.Builder(MyActivity5).create();
	    		alertDialog.setTitle("APN");
	        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
	        		alertDialog.setMessage("Proszê zrestartowaæ urz¹dzenie.");
	        	} else {
	        		alertDialog.setMessage("Please restart device.");		        		
	        	}
	    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    			}
	    		});	        	
    	   		alertDialog.show();   	   		        			        		
    			return true;
    		}

		});

	    customPref = (Preference) findPreference("ExtFile");
	    customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
    		public boolean onPreferenceClick(Preference preference) {
    		    Preference customPref = (Preference) findPreference("ExtFileName0");
    			customPref.setEnabled(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("ExtFile", false));
    		    return true;        	        
    		}	
	    });
		
	    customPref = (Preference) findPreference("ExtFileName0");
    	try {
    		PackageManager manager = getPackageManager();
    		PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
    		customPref.setSummary(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFilePath2", MyActivity5.getExternalFilesDir(null).toString())+"/"+PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
    	} catch (Exception e) {          	  
    	}
    	customPref.setEnabled(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("ExtFile", false));
	    customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    		public boolean onPreferenceClick(Preference preference) {
	    		    Intent intent = new Intent(MyActivity5, FilesActivity.class);
	    		    intent.putExtra(FilesActivity.EXTRA_MESSAGE, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFilePath2", MyActivity5.getExternalFilesDir(null).toString()));
	    	    	try {
	    	    		PackageManager manager = getPackageManager();
	    	    		PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
	        		    intent.putExtra(FilesActivity.EXTRA_MESSAGE2, PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
	    	    	} catch (Exception e) {          	  
	  	      		}
	    	        intent.putExtra(FilesActivity.EXTRA_MESSAGE3, "open");		    	    	    
	    	        startActivityForResult(intent, 1);

	    		    return true;        	        
	    		}		
	    });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if (resultCode == RESULT_OK) {
	        	Preference customPref = (Preference) findPreference("ExtFileName0");
	        	try {
	        		PackageManager manager = getPackageManager();
	        		PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
	        		customPref.setSummary(PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFilePath2", MyActivity5.getExternalFilesDir(null).toString())+"/"+PreferenceManager.getDefaultSharedPreferences(MyActivity5).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
	        	} catch (Exception e) {          	  
	        	}	        	
	        }
	    }
	}	
}	
    	