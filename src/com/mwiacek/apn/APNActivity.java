package com.mwiacek.apn;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class APNActivity extends Activity {
		
		final Activity MyActivity5 = this;	
    	PhoneStateListener phoneStateListener=null;		
	    public static Spinner spinner1;
	    public static Spinner spinner2;
	    public static TextView textView,textView3;
	    public static Button button1,button2;	    
	    public static CheckBox checkbox1;
	    int i,j,numidglobal;
	    SharedPreferences sp;	    
        TelephonyManager tm;	    
	    StringBuilder s= new StringBuilder();
	    APNClass APN=new APNClass();
	    ArrayAdapter<CharSequence> adapter2;
	    boolean started=false;
	    
	    void Alert(String s) {
	    	AlertDialog alertDialog = new AlertDialog.Builder(MyActivity5).create();
    		alertDialog.setTitle("APN");
    		alertDialog.setMessage(s);
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    			}
    		});
    		alertDialog.show();		    	
	    }
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.sysmenu:
	        	startActivityForResult(new Intent(Settings.ACTION_APN_SETTINGS ), 0);
	            return true;	        	
	        case R.id.sett:
	        	//SharedPreferences.Editor ed = sp.edit();
	        	//ed.putBoolean("Auto", sp.getBoolean("Auto", false));
	        	//ed.putBoolean("SysApp", ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)!=0));
	        	//ed.commit();
	        	
	        	StringBuilder body = new StringBuilder();

	        	body.append(Info2());
	    		if (body.length()==0) {
		        	body.append("Sieæ SIM/SIM network: "+tm.getSimOperator());		        		
		        	if (tm.getSimOperatorName ().length()!=0) {
		        		body.append(" (operator: '"+tm.getSimOperatorName ()+"')");
		        	}
		        	s.delete(0,s.length());
		        	PokazPhoneDataAndGetNetAPNNum(tm.getSimOperator(),tm.getSimOperatorName());
		    	    Pokaz2(tm.getSimOperator(),tm.getSimOperatorName());
		        	body.append("\n\n"+s);		    	    
	    		}
	    		
	    		Intent i = new Intent(this,PreferencesActivity.class);
	        	i.putExtra("values", body.toString() );
	        	
	        	startActivity(i);
	        		        	  
	            return true;     
	        case R.id.inf:
	    	    StringBuilder s= new StringBuilder();
	        	/*if (tm.getDeviceId ()!=null) {
	            	s.append("ID telefonu: "+tm.getDeviceId ()+"\n");        		
	        	}
	        	if (tm.getDeviceSoftwareVersion()!=null) {
	            	s.append("Wersja oprogramowania telefonu: "+tm.getDeviceSoftwareVersion ()+"\n");        		
	        	}
	        	if (tm.getLine1Number ()!=null) {
	            	s.append("Numer 1 linii: "+tm.getLine1Number ()+"\n");        		
	        	}*/

	    		s.append(Info2());
	    		if (s.length()==0) {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
			    		//zalogowana siec
			        	s.append("Sieæ zalogowana: "+tm.getNetworkOperator());
		        	} else {
			    		//zalogowana siec
			        	s.append("Logged network: "+tm.getNetworkOperator());		        		
		        	}
		        	switch (tm.getNetworkType()) {
		        	case 7: s.append(" 1xRTT"); break;
		        	case 4: s.append(" CDMA IS95A/IS95B"); break;
		        	case 2: s.append(" EDGE"); break;
		        	case 14: s.append(" eHRPD"); break;
		        	case 5: s.append(" EVDO 0"); break;
		        	case 6: s.append(" EDVO A"); break;
		        	case 12: s.append(" EDVO B"); break;
		        	case 1: s.append(" GPRS"); break;
		        	case 8: s.append(" HSDPA"); break;
		        	case 10: s.append(" HSPA"); break;
		        	case 15: s.append(" HSPA+"); break;
		        	case 9: s.append(" HSUPA"); break;
		        	case 11: s.append(" iDen"); break;
		        	case 13: s.append(" LTE"); break;
		        	case 3: s.append(" UMTS"); break;
		        	}
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {				    	
			        	if (tm.getNetworkOperatorName ().length()!=0) {
			        		s.append(" (operator: "+tm.getNetworkOperatorName ()+")");	        
			        	}
			        	if (tm.getSimSerialNumber ()!=null) {
			            	s.append("\nNr seryjny karty SIM: "+tm.getSimSerialNumber ());        		
			        	}
			        	if (tm.getSubscriberId ()!=null) {
			            	s.append("\nIMSI karty SIM: "+tm.getSubscriberId ());        		
			        	}	    		
			        	//siec sim
			        	s.append("\nSieæ karty SIM: "+tm.getSimOperator()+" (");		        		
			        	if (tm.getSimOperatorName ().length()!=0) {
			        		s.append("operator: "+tm.getSimOperatorName ()+")");
			        	} else {
			        		s.append("bez nazwy operatora)");        		
			        	}
		        	} else {
			        	if (tm.getNetworkOperatorName ().length()!=0) {
			        		s.append(" (operator: "+tm.getNetworkOperatorName ()+")");	        
			        	}
			        	if (tm.getSimSerialNumber ()!=null) {
			            	s.append("\nSIM card serial number: "+tm.getSimSerialNumber ());        		
			        	}		        		
			        	if (tm.getSubscriberId ()!=null) {
			            	s.append("\nSIM card IMSI: "+tm.getSubscriberId ());        		
			        	}	    		
			        	//siec sim
			        	s.append("\nSIM card network: "+tm.getSimOperator()+" (");		        		
			        	if (tm.getSimOperatorName ().length()!=0) {
			        		s.append("operator: "+tm.getSimOperatorName ()+")");
			        	} else {
			        		s.append("without operator name)");        		
			        	}
		        	}
		        	
		        	try {
		        		s.append("\n");
		        		Class<?> cx = Class.forName("android.telephony.MSimTelephonyManager");
		        		Object obj =MyActivity5.getSystemService("phone_msim");
		        		
			        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
			        		//zalogowana siec
			        		Method m = cx.getMethod("getNetworkOperator",int.class);
			        		String sss=(String)m.invoke(obj, 1);
			        		if (sss.length()!=0) {
			        			s.append("\nSieæ zalogowana: "+sss);
			        		}
				        	Method m2=cx.getMethod("getNetworkOperatorName",int.class);
				        	sss=(String)m2.invoke(obj, 1);
				        	if (sss.length()!=0) {
				        		s.append(" (operator: "+sss+")");	        
				        	}
				        	m = cx.getMethod("getSimSerialNumber",int.class);
				        	if ((String)m.invoke(obj, 1)!=null) {
				            	s.append("\nNr seryjny karty SIM: "+(String)m.invoke(obj, 1));        		
				        	}		        		
			        		m = cx.getMethod("getSubscriberId",int.class);
			        	  	if ((String)m.invoke(obj, 1)!=null) {
				            	s.append("\nIMSI karty SIM: "+(String)m.invoke(obj, 1));        		
				        	}	
				        	//siec sim
				        	m = cx.getMethod("getSimOperator",int.class);
				        	s.append("\nSieæ karty SIM: "+(String)m.invoke(obj,1));
				        	m2=cx.getMethod("getSimOperatorName",int.class);
				        	sss=(String)m2.invoke(obj,1);
				        	if (sss.length()!=0) {
				        		s.append(" (operator: "+sss+")");	        
				        	} else {
				        		s.append("bez nazwy operatora)");        		
				        	}	
				        } else {
			        		//zalogowana siec
			        		Method m = cx.getMethod("getNetworkOperator",int.class);
			        		String sss=(String)m.invoke(obj, 1);
			        		if (sss.length()!=0) {
			        			s.append("\nLogged network: "+sss);
			        		}
				        	Method m2=cx.getMethod("getNetworkOperatorName",int.class);
				        	sss=(String)m2.invoke(obj, 1);
				        	if (sss.length()!=0) {
				        		s.append(" (operator: "+sss+")");	        
				        	}
				        	m = cx.getMethod("getSimSerialNumber",int.class);
				        	if ((String)m.invoke(obj, 1)!=null) {
				            	s.append("\nSIM card serial number: "+(String)m.invoke(obj, 1));        		
				        	}		        		
			        		m = cx.getMethod("getSubscriberId",int.class);
			        	  	if ((String)m.invoke(obj, 1)!=null) {
				            	s.append("\nSIM card IMSI: "+(String)m.invoke(obj, 1));        		
				        	}	
				        	//siec sim
				        	m = cx.getMethod("getSimOperator",int.class);
				        	s.append("\nSIM card network: "+(String)m.invoke(obj,1));
				        	m2=cx.getMethod("getSimOperatorName",int.class);
				        	sss=(String)m2.invoke(obj,1);
				        	if (sss.length()!=0) {
				        		s.append(" (operator: "+sss+")");	        
				        	} else {
				        		s.append("without operator name)");        		
				        	}				        	    		
			        	}		        		 
		        	} catch (Exception e) {
		        	}
		    	}	    
	    		
	    		Alert(s.toString());
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {	        	        
	        super.onCreate(savedInstanceState);
        	setContentView(R.layout.main);
    		
        	try {
          	  PackageManager manager = getPackageManager();
          	  PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

          	  setTitle("APN "+info.versionName);
        	} catch (Exception e) {          	  
        	}
        	
	        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	        sp=PreferenceManager.getDefaultSharedPreferences(this);
        	        
        	textView = (TextView) findViewById(R.id.textView1);
        	textView3 = (TextView) findViewById(R.id.textView3);
        	button1 = (Button) findViewById(R.id.button1);
        	button2 = (Button) findViewById(R.id.button2);
        	spinner1 = (Spinner) findViewById(R.id.spinner1);
        	spinner2 = (Spinner) findViewById(R.id.spinner2);
        	checkbox1 = (CheckBox) findViewById(R.id.checkBox1);
        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		checkbox1.setText("Internet wy³¹czony ? (z 'apndroid')");
        	} else {
        		checkbox1.setText("Internet off ? (with 'apndroid')");
        	}

        	ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter <CharSequence>
            (this, android.R.layout.simple_spinner_item);
        	
        	//get networks from phone db
        	boolean wrong=false;
	    	if (Build.VERSION.RELEASE.charAt(0)=='4' && Build.VERSION.RELEASE.charAt(2)>'1') {
	    		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
	    			wrong=true;
	    		}
	    	}
	    	if (!wrong) {
	            Cursor c = getContentResolver().query(APN.APN_TABLE_URI, new String[]{"numeric"}, null, null,"numeric asc");
	 	        if (c != null) {
	 		        try {
	 		            if (c.moveToFirst()) {
	 		            	StringBuilder a=new StringBuilder();
	 		            	do {
	 		            		if (!a.toString().equals(c.getString(0))) {
	 		            			a.delete(0, a.length());
	 		            			a.append(c.getString(0));
	 		            			adapter1.add(c.getString(0));
	 		            		}
	 		                } while (c.moveToNext());	                
	 		            }
	 		        } catch(SQLException e) {
	 		        	s.append(e.getMessage());
	 		        }
	 		        c.close();
	 	        }  	    		
	    	}
        	 	        
 	        //add new networks from file
	    	StringBuilder s2 = new StringBuilder();
        	XmlPullParserFactory factory;
			try {
				factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            int i;
	    		InputStream stream;
	    		try {
	    			stream = APN.GetDBFile(MyActivity5);
					xpp.setInput(stream,"utf-8");
			        int eventType = xpp.getEventType();			            
			        while (eventType != XmlPullParser.END_DOCUMENT) {			            	
			        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
			        		s2.delete(0, s2.length());
			            	   
			            	for(i=0;i<xpp.getAttributeCount();i++) {
			            		if (xpp.getAttributeName(i).equals("mcc")) {
			                	   	s2.insert(0, xpp.getAttributeValue(i));
			                	}
			                	if (xpp.getAttributeName(i).equals("mnc")) {
			                		s2.append(xpp.getAttributeValue(i));
			                		if (adapter1.getPosition(s2.toString())<0) {
			                			adapter1.add(s2.toString());
			               			}
			                	}
			            	}
			        	}			               
			            eventType = xpp.next();
			        }
			        stream.close();			            
				} catch (IOException e) {
					Alert(e.getMessage());
				} catch(IllegalArgumentException e2) {
					Alert(e2.getMessage());				
				}				
			} catch (XmlPullParserException e1) {
				Alert(e1.getMessage());				
			}

			//sort network names
	        adapter1.sort(new Comparator<CharSequence>() {
	            public int compare(CharSequence s1, CharSequence s2) {
	            	if (s1==null) {
	            		return 1;
	            	}
	            	if (s2==null) {
	            		return -1;
	            	}
	                return s1.toString().compareTo(s2.toString());
	            }
	        });
			
            spinner1.setAdapter(adapter1);        
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                	button1.setEnabled(false);
                	button2.setEnabled(false);
                	adapter2 = new ArrayAdapter <CharSequence>
                    	(MyActivity5, android.R.layout.simple_spinner_item);
    				spinner2.setEnabled(false);
    				textView3.setEnabled(false);

    	 	        //add operators from file
    		    	StringBuilder s2 = new StringBuilder();
    	        	XmlPullParserFactory factory;
    				try {
    					factory = XmlPullParserFactory.newInstance();
    		            factory.setNamespaceAware(true);
    		            XmlPullParser xpp = factory.newPullParser();
    		            
    		    		InputStream stream;
    		    		try {
    		    			stream = APN.GetDBFile(MyActivity5);
    						xpp.setInput(stream,"utf-8");
    				        int eventType = xpp.getEventType();			            
    				        while (eventType != XmlPullParser.END_DOCUMENT) {			            	
    				        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
    				        		s2.delete(0, s2.length());
    				            	   
    				            	for(i=0;i<xpp.getAttributeCount();i++) {
    				            		if (xpp.getAttributeName(i).equals("mcc")) {
    				            			s2.insert(0, xpp.getAttributeValue(i));
    				            		}
    				                	if (xpp.getAttributeName(i).equals("mnc")) {    				                		   
    				                		s2.append(xpp.getAttributeValue(i));
    				                		if (!s2.toString().equals(spinner1.getSelectedItem().toString())) {
    				                			break;
    				                		}
    				                	}
    				                	if (xpp.getAttributeName(i).equals("op")) {
    				                		if (adapter2.getPosition(xpp.getAttributeValue(i))<0) {
    				                			adapter2.add(xpp.getAttributeValue(i));
    					            			spinner2.setEnabled(true);
    					            			textView3.setEnabled(true);                    				
    				                		}    				                		   
    				                	}
    				            	}
    				            	   
    				        	}
    				            eventType = xpp.next();
    				        }
    				        stream.close();
    				            
    					} catch (IOException e) {
    						Alert(e.getMessage());
    					} catch(IllegalArgumentException e2) {
    						Alert(e2.getMessage());				
    					}	    					
    				} catch (XmlPullParserException e1) {
    					Alert(e1.getMessage());
    				}
    				
    	        	spinner2.setAdapter(adapter2);
    	        	if (spinner2.isEnabled()) {    	        		
        		    	if (tm.getSimState ()==TelephonyManager.SIM_STATE_READY) {
        		    		if (textView.length()==0) {
        		    			int spinnerPosition = adapter2.getPosition(tm.getSimOperatorName());
        		    			if (spinnerPosition>=0) {
        		    				spinner2.setSelection(spinnerPosition);        		    				
        		    			}
        		    		}
        	        	}        	        			
        	        	
        	        	spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	        		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        	        			Pokaz(null);
        	        		} 
        	        		public void onNothingSelected(AdapterView<?> adapterView) {
        	        			return;
        	        		} 
        	        	});
        	        } else { 
            			Pokaz(null);        	        	
        	        }                   
                } 
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                } 
            }); 
            
        	button1.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			if (CheckSystem()) {
        		    	StringBuilder s2 = new StringBuilder();
        		    	boolean found,found2,mms=false;
        		    	
        	        	XmlPullParserFactory factory;
        				try {
        					factory = XmlPullParserFactory.newInstance();
        		            factory.setNamespaceAware(true);
        		            XmlPullParser xpp = factory.newPullParser();
        		            int i;
        		    		InputStream stream;
        		    		try {
        						stream = APN.GetDBFile(MyActivity5);
        						xpp.setInput(stream,"utf-8");
        				        int eventType = xpp.getEventType();
        				            
        				        while (eventType != XmlPullParser.END_DOCUMENT) {        				            	
        				        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
        				        		s2.delete(0, s2.length());
        				            	found=false;
        				            	found2=false;
        				            	mms=false;
        				                   
        				                for(i=0;i<xpp.getAttributeCount();i++) {
        				                	if (xpp.getAttributeName(i).equals("mcc")) {
        				                		s2.insert(0, xpp.getAttributeValue(i));
        				                	}
        				                	if (xpp.getAttributeName(i).equals("mnc")) {
        				                		s2.append(xpp.getAttributeValue(i));

        				                		if (!s2.toString().equals(spinner1.getSelectedItem().toString())) {
        				                			break;
        				                		}
        				                		found2=true;
        				                	}
        				                	if (xpp.getAttributeName(i).equals("type")) {
        				                		if (xpp.getAttributeValue(i).contains("mms")) {
        				                			mms=true;
        				                		}
        				                	}
        				                	if (xpp.getAttributeName(i).equals("op")) {
        				                		found2=false;
        				                		if (xpp.getAttributeValue(i).equals(spinner2.getSelectedItem().toString())) {
        				                			found = true;
        				                		}
        				                	}
        				                }
        				                if (found || found2) {
        				                	ContentResolver resolver = getContentResolver();	                    
        				                	ContentValues values = new ContentValues();
        				                	values.put("numeric",s2.toString());
        				                	for(i=0;i<xpp.getAttributeCount();i++) {
        				                		if (xpp.getAttributeName(i).equals("op")) {        				                			   
        				                		} else if (xpp.getAttributeName(i).equals("apn")) {
        				                			if (mms) {
        				                				values.put(xpp.getAttributeName(i),xpp.getAttributeValue(i));        				                				   
        				                			} else {
        				                				if (PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("Apndroid", false)) {
        				                					values.put(xpp.getAttributeName(i),xpp.getAttributeValue(i)+"apndroid");
        				                				} else {
        				                					values.put(xpp.getAttributeName(i),xpp.getAttributeValue(i));        				                					
        				                				}
        				                			}        	        					        					        		        				                			   
        				                		} else if (xpp.getAttributeName(i).equals("carrier")) {
        				                			values.put("name",xpp.getAttributeValue(i)+" ");
        				                		} else {
        				                			values.put(xpp.getAttributeName(i),xpp.getAttributeValue(i));
        				                		}
        				                	}
        				                	try {
        		            					resolver.insert(APN.APN_TABLE_URI, values);
        		            				} catch(SQLException e) {
        		            					textView.setText(e.getMessage());
        		            					return;
        		            				}			        				                	
        				                }
        				                   
        				        	}
        				            eventType = xpp.next();
        				        }
        				        stream.close();
        				            
        					} catch (IOException e) {
        						Alert(e.getMessage());				
        					} catch(IllegalArgumentException e2) {
        						Alert(e2.getMessage());				
        					}	    					        					
        				} catch (XmlPullParserException e1) {
    						Alert(e1.getMessage());				
        				}

        				Pokaz(null);
        			}
        		}
        	});

        	button2.setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			if (CheckSystem()) { 
        				Pokaz(APN.Ustaw(MyActivity5,spinner1.getSelectedItem().toString(),spinner2.getSelectedItem().toString(),PreferenceManager.getDefaultSharedPreferences(MyActivity5.getApplicationContext()).getBoolean("Apndroid", !PreferenceManager.getDefaultSharedPreferences(MyActivity5).getBoolean("Apndroid", false))));
        			}
        		}
        	});	  
            
	    	if (tm.getSimState ()==TelephonyManager.SIM_STATE_READY) {
	    		int spinnerPosition = adapter1.getPosition(tm.getSimOperator());
	    		if (spinnerPosition>=0) {
	    			spinner1.setSelection(spinnerPosition);
	    		}
	    	}
	    	
	    	checkbox1.setOnClickListener(new OnClickListener() {
	    		public void onClick(View v) {
	    	    	Cursor c = getContentResolver().query(APN.APN_TABLE_URI, null, "_id="+numidglobal, null, null);
	    	        if (c != null) {
    		            if (c.moveToFirst()) {
    		            	String[] Columns = c.getColumnNames();
    		            	ContentValues values = new ContentValues();
    			        	int l=Columns.length;
    			        	for (i=0;i<l;i++) {
    			        		if (Columns[i].equals("apn")) {
    			        			if (!checkbox1.isChecked()) {
                	        	        values.put("apn",c.getString(c.getColumnIndex(Columns[i])).replace("apndroid", ""));    			        			
    			        			} else {
                	        	        values.put("apn",c.getString(c.getColumnIndex(Columns[i]))+"apndroid");
    			        			}
    			        		} else if (Columns[i].equals("type")) {
    			        			if (!checkbox1.isChecked()) {
                	        	        values.put("type",c.getString(c.getColumnIndex(Columns[i])).replace("apndroid", ""));    			        			
    			        			} else {
                	        	        values.put("type",c.getString(c.getColumnIndex(Columns[i])));
    			        			}
    			        		} else {
            	        	        values.put(Columns[i],c.getString(c.getColumnIndex(Columns[i])));
    			        		}
    			        		
    			        	}	
    	        	        try {
    	        	        	MyActivity5.getContentResolver().update(APN.APN_TABLE_URI, values, "_id="+numidglobal, null);
    	        	        	Pokaz(null);
    	        	        } catch (Exception e) {
    	    					Alert(e.getMessage());				
    	        	        }
    		            }
	    	        }
	    	    }
	    	});
	    }
	   	    
	    private void PokazPhoneDataAndGetNetAPNNum(String NetworkCode, Object OperatorName) {
			checkbox1.setVisibility(View.INVISIBLE);
	    	s.delete(0, s.length());

	    	if (Build.VERSION.RELEASE.charAt(0)=='4' && Build.VERSION.RELEASE.charAt(2)>'1') {
	    		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
	    	    		s.append("Aplikacja nie jest systemowa (nie jest w /system/app). W Androidzie 4.2 i póŸniejszych jest to wymagane do odczytu APN.\n\nProszê u¿yæ np. '/system/app mover' z Google Play w celu naprawy tego.");
		        	} else {
	    	    		s.append("Application is not system (not in /system/app). It's required in Android 4.2 and later for getting APN.\n\nPlease use for example '/system/app mover' from Google Play to fix it.");
		        	}
	    			return;
	    		}
	    	}
			
	    	
	    	Cursor c = getContentResolver().query(APN.APN_TABLE_URI, null, "numeric='"+NetworkCode+"'", null, null);
        	numidglobal=-1;
	        try {
	        	if (c==null || !c.moveToFirst()) {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
		        		s.append("Brak danych w urz¹dzeniu dla sieci "+NetworkCode);	        		
		        	} else {
		        		s.append("No data in the device for the network "+NetworkCode);
		        	}		        		
	        	} else {
		        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
		        		s.append("Dane w urz¹dzeniu dla sieci "+NetworkCode+"\n===================================");
		        	} else {
		        		s.append("Data in the device for the network "+NetworkCode+"\n===================================");
		        	}
	            	String[] Columns = c.getColumnNames();
		        	int l=Columns.length;
		        	int[] ColumnIndexes = new int[l];
		        	for (i=0;i<l;i++) {
		        		ColumnIndexes[i] = c.getColumnIndex(Columns[i]);
		        	}			        	
		        	int numapn,numname,numtype,numid;
		        	int id=-1;
	                do {
			        	numid=-1;
	                	numapn=-1;
	                	numname=-1;
	                	numtype=-1;
	                	for (i=0;i<l;i++) {
	                		if (Columns[i].equals("_id")) {
	                			numid=i;			                    		
		                    }
	                		if (Columns[i].equals("name")) {
	                			numname=i;			                    		
		                    }
		                    if (Columns[i].equals("type")) {
		                    	if (!c.isNull(ColumnIndexes[i])) {
			                    	if (!c.getString(ColumnIndexes[i]).contains("mms")) {
			                    		numtype=i;
			                    	}		                    		
		                    	}
		                    }
		                    if (Columns[i].equals("apn")) {
		                    	numapn=i;
		                    }
		                }
		                if (numid!=-1 && numtype!=-1 && numname!=-1 && numapn!=-1) {
		        	    	StringBuilder s2 = new StringBuilder();
		        	    	boolean found;	    	
		                	XmlPullParserFactory factory;
		        			try {
		        				factory = XmlPullParserFactory.newInstance();
		        	            factory.setNamespaceAware(true);
		        	            XmlPullParser xpp = factory.newPullParser();
		        	            int i;
		        	    		InputStream stream;
		        	    		try {
		        	    			stream = APN.GetDBFile(MyActivity5);
		        					xpp.setInput(stream,"utf-8");
		        			        int eventType = xpp.getEventType();			            
		        			        while (eventType != XmlPullParser.END_DOCUMENT) {			            	
		        			        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
		        			        		s2.delete(0, s2.length());
		        			            	found=false;
		        			            	   
		        			            	for(i=0;i<xpp.getAttributeCount();i++) {
		        			            		if (xpp.getAttributeName(i).equals("mcc")) {
		        			                	   	s2.insert(0, xpp.getAttributeValue(i));
		        			                	}
		        			                	if (xpp.getAttributeName(i).equals("mnc")) {
		        			                		s2.append(xpp.getAttributeValue(i));
		        			                		if (s2.toString().equals(NetworkCode)) {
		        			                			found=true;
		        			               			}
		        			                		if (!found) {
	        				                			break;
	        				                		}
		        			                	}
		        			                	if (xpp.getAttributeName(i).equals("mnc")) {
		        			                		if (OperatorName!=null && xpp.getAttributeValue(i).equals(OperatorName.toString())) {
		        			                			found=true;
		        			                		}
		        			                	}
		        			            	}
		        			            	if (found) {
		        			            		if (id==-1) {
	    	    			    		        	checkbox1.setVisibility(View.VISIBLE);
	    	    			    		        	checkbox1.setChecked(c.getString(ColumnIndexes[numapn]).contains("apndroid"));
	    	    			    		        	numidglobal=c.getInt(ColumnIndexes[numid]);
		        			            			id=1;
		        			            		} else {
	    	    			    		        	checkbox1.setVisibility(View.INVISIBLE);
		        			            		}
    	    			    		        	break;			        			            		
		        			            	}
		        			        	}			               
		        			            eventType = xpp.next();
		        			        }
		        			        stream.close();			            
		        				} catch (IOException e) {
	        						Alert(e.getMessage());				
	        					} catch(IllegalArgumentException e2) {
	        						Alert(e2.getMessage());				
		        				}		        				
		        			} catch (XmlPullParserException e1) {
		        				Alert(e1.getMessage());
		        			}			                	
		                }
		                s.append("\n");
	                    for (i=0;i<l;i++) {
	                        s.append(Columns[i]+"="+c.getString(ColumnIndexes[i])+" ");
			            }
                    	s.append("\n");
	                } while (c.moveToNext());	                
			        c.close();
			    }       	    	
	        } catch(SQLException e) {
	        	s.append(e.getMessage());
	        }
	    }
	    
	    private boolean Pokaz2(String NetworkCode, String OperatorName) {
	    	StringBuilder s2 = new StringBuilder();
	    	boolean found,found2,f1=false;
	    	
        	XmlPullParserFactory factory;
			try {
				factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	            int i;
	    		InputStream stream;
	    		try {
					stream = APN.GetDBFile(MyActivity5);
					 xpp.setInput(stream,"utf-8");
			            int eventType = xpp.getEventType();
			            
			            while (eventType != XmlPullParser.END_DOCUMENT) {
			            	
			               if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
			            	   s2.delete(0, s2.length());
			            	   found=false;
			            	   found2=false;
			                   for(i=0;i<xpp.getAttributeCount();i++) {
			                	   if (xpp.getAttributeName(i).equals("mcc")) {
			                		   s2.insert(0, xpp.getAttributeValue(i));
			                	   }
			                	   if (xpp.getAttributeName(i).equals("mnc")) {
			                		   s2.append(xpp.getAttributeValue(i));

			                		   if (!s2.toString().equals(NetworkCode)) {
			                			   break;
			                		   }
			                		   found2=true;
			                	   }
			                	   if (xpp.getAttributeName(i).equals("op")) {
			                		   found2=false;
			                		   if (xpp.getAttributeValue(i).equals(OperatorName)) {
			                			   found = true;
			                		   }
			                	   }
			                   }
			                   if (found || found2) {
			                	   if (!f1) {
			                		   if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
			                			   s.append("\n\nDane APN dla operatora '"+OperatorName+"'\n===================================");
			                       		} else {
			                       			s.append("\n\nAPN data for the operator '"+OperatorName+"'\n===================================");	                    		
			                       		}
			                       		f1=true;
			                       		s.append("\n");
			                   		} else {
			                   			s.append("\n\n");
			                   		}
			                	   
			                	   	s.append("numeric="+s2.toString()+" ");
			                	   
			                	   	for(i=0;i<xpp.getAttributeCount();i++) {
			                	   		if (xpp.getAttributeName(i).equals("carrier")) {
			                	   			s.append("name="+xpp.getAttributeValue(i)+" ");	
			                	   		} else if (xpp.getAttributeName(i).equals("op")) {
			                	   		} else {
			                	   			s.append(xpp.getAttributeName(i)+"="+xpp.getAttributeValue(i)+" ");
			                	   		}
			                	   	}   			                	   
			                	   
			                	   	f1=true;
			                   }
			                   
			               }
			               
			               eventType = xpp.next();
			            }
			            stream.close();
			            
				} catch (IOException e) {
					Alert(e.getMessage());				
				} catch(IllegalArgumentException e2) {
					Alert(e2.getMessage());				
				}		        				
			} catch (XmlPullParserException e1) {
				Alert(e1.getMessage());
			}
			return f1;
	    }
	    
	    private Boolean CheckSystem() {
	    	if (Build.VERSION.RELEASE.charAt(0)=='4') {
	    		if ((MyActivity5.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
	        		if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
    					Alert("Aplikacja nie jest systemowa (nie jest w /system/app). W Androidzie 4.x jest to wymagane do zmiany APN.\n\nProszê u¿yæ np. '/system/app mover' z Google Play w celu naprawy tego.");				
	            	} else {
    					Alert("Application is not system (not in /system/app). It's required in Android 4.x for changing APN.\n\nPlease use for example '/system/app mover' from Google Play to fix it.");				
	            	}
	        		return false;
	    		}	    		
	    	}
	    	return true;
	    }
	    
	    private void Pokaz(StringBuilder s2) {
	    	if (s.length()==0) {
	    		s.append(Info2());
		    	if (s.length()!=0) {
		    		Alert(s.toString());
		    	}
	    	}
	    	
	    	PokazPhoneDataAndGetNetAPNNum(spinner1.getSelectedItem().toString(),spinner2.getSelectedItem());
	        
        	button1.setEnabled(false);
        	button2.setEnabled(false);

        	if (s2!=null) {
	        	if (s2.length()!=0) {
	        		s.insert(0, button2.getText()+" - "+s2+"\n\n\n");
	        	}
	        }
	        
	        if (spinner2.isEnabled()) {
	        	if (Pokaz2(spinner1.getSelectedItem().toString(),spinner2.getSelectedItem().toString())) {
	        		button1.setEnabled(true);
	        		button2.setEnabled(true);
	        	}
	        }
	        	        
        	textView.setText(s);
    	    
	    	started=true;
        	
	    }	
	    
	    public String Info2() {
        	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
		    	switch (tm.getSimState ()) {
		    	case TelephonyManager.SIM_STATE_UNKNOWN:
		    		return "Nieznany status karty SIM";
		    	case TelephonyManager.SIM_STATE_ABSENT:
		    		return "Brak karty SIM";
		    	case TelephonyManager.SIM_STATE_PIN_REQUIRED:
		    		return "Oczekiwanie na PIN karty SIM";
		    	case TelephonyManager.SIM_STATE_PUK_REQUIRED:
		    		return "Oczekiwanie na PUK karty SIM";
		    	case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
		    		return "Oczekiwanie na PIN sieciowy";
		    	case TelephonyManager.SIM_STATE_READY:
		    		return "";
		    	}
        	} else {
		    	switch (tm.getSimState ()) {
		    	case TelephonyManager.SIM_STATE_UNKNOWN:
		    		return "Unknown SIM card status";
		    	case TelephonyManager.SIM_STATE_ABSENT:
		    		return "SIM card absent";
		    	case TelephonyManager.SIM_STATE_PIN_REQUIRED:
		    		return "Waiting for the SIM card PIN";
		    	case TelephonyManager.SIM_STATE_PUK_REQUIRED:
		    		return "Waiting for the SIM card PUK";
		    	case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
		    		return "Waiting for the network PIN";
		    	case TelephonyManager.SIM_STATE_READY:
		    		return "";
		    	}        		
        	}	    	
        	return "";
	    }	
	    
	    @Override
	    public void onResume() {
	        super.onResume();
	        if (started) {
	        	Pokaz(null);
	        }
	    }
}