package com.mwiacek.apn;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {	
	@Override
    public void onReceive(Context context, Intent intent) {
		final Context cx=context;
		
    	if (!PreferenceManager.getDefaultSharedPreferences(cx).getBoolean("Auto", false) &&
    		!PreferenceManager.getDefaultSharedPreferences(cx).getBoolean("ApndroidLTE", false)) {
    	    return;
    	}
    	final TelephonyManager tm=(TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
    	
		tm.listen(new PhoneStateListener(){
			boolean first=true;
			
			@Override
			public void onServiceStateChanged(ServiceState serviceState) {
				super.onServiceStateChanged(serviceState);
		    	if (Build.VERSION.RELEASE.charAt(0)=='4') {
		    		if ((cx.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
		    			tm.listen(this, PhoneStateListener.LISTEN_NONE);
		    			return;
		    		}
		    	}
				
		    	if (tm.getSimState ()!=TelephonyManager.SIM_STATE_READY) {
		    		return;
		    	}

			    APNClass APN=new APNClass();
			    //13
		    	if (PreferenceManager.getDefaultSharedPreferences(cx).getBoolean("ApndroidLTE", false)) {
		    		if (first) {
		    			APN.Ustaw(cx,tm.getSimOperator(),tm.getSimOperatorName (),tm.getNetworkType()!=13);
		    			first = false;
		    		}
	    			
			    	Cursor c = cx.getContentResolver().query(APN.APN_TABLE_URI, null, "type not like \"%mms%\" and numeric=\""+tm.getSimOperator()+"\"", null, null);

			        if (c != null) {
			            if (c.moveToFirst()) {
			            	if (c.getCount()==1) {
				            	String[] Columns = c.getColumnNames();
				            	ContentValues values = new ContentValues();
					        	int l=Columns.length,idnum=-1;
					        	for (int i=0;i<l;i++) {
					        		if (Columns[i].equals("apn")) {
					        			if (tm.getNetworkType()==13) {
			        	        	        values.put("apn",c.getString(c.getColumnIndex(Columns[i])).replace("apndroid", ""));    			        			
					        			} else {
			        	        	        values.put("apn",c.getString(c.getColumnIndex(Columns[i])).replace("apndroid", "")+"apndroid");
					        			}
					        		} else if (Columns[i].equals("type")) {
					        			if (tm.getNetworkType()==13) {
			        	        	        values.put("type",c.getString(c.getColumnIndex(Columns[i])).replace("apndroid", ""));    			        			
					        			} else {
			        	        	        values.put("type",c.getString(c.getColumnIndex(Columns[i])));
					        			}
					        		} else if (Columns[i].equals("_id")) {
					        			idnum=c.getInt(c.getColumnIndex(Columns[i]));
					        		} else {
			    	        	        values.put(Columns[i],c.getString(c.getColumnIndex(Columns[i])));
					        		}
					        		
					        	}	
			        	        try {
			        	        	if (idnum!=-1) {
			        	        		cx.getContentResolver().update(APN.APN_TABLE_URI, values, "_id="+idnum, null);
			        	        	}
			        	        } catch (Exception e) {
			        	        }			            		
			            	}
			            }
			        }
	    			
		    	} else {
	    			APN.Ustaw(cx,tm.getSimOperator(),tm.getSimOperatorName (),!PreferenceManager.getDefaultSharedPreferences(cx).getBoolean("Apndroid", false));
	    			tm.listen(this, PhoneStateListener.LISTEN_NONE);		    		
		    	}		    	
			}
		},PhoneStateListener.LISTEN_SERVICE_STATE);    	  	    		
    }
}
