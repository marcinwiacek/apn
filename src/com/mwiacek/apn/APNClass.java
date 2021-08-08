package com.mwiacek.apn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
 
public class APNClass {
   		final Uri APN_TABLE_URI = Uri.parse("content://telephony/carriers");
   		final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
   			    
   		InputStream GetDBFile(Context context) {
   			if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ExtFile",false)) {
   				try {
   		    		PackageManager manager = context.getPackageManager();
   		    		PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
   	   				File f = new File(PreferenceManager.getDefaultSharedPreferences(context).getString("ExtFilePath2", context.getExternalFilesDir(null).toString())+"/"+PreferenceManager.getDefaultSharedPreferences(context).getString("ExtFileName2", "com_mwiacek_apn_"+info.versionName+".xml"));
   	    			return new FileInputStream(f);   				   	   				
   		    	} catch (Exception e) {          	  
   		    	}   	
   				return null;
   			}
   			try {
				return context.getAssets().open("apns-full-conf.xml");
			} catch (IOException e) {
			}
   			return null;
   		}
   		
	    StringBuilder Ustaw(Context context, String opercode, String opername, Boolean isLTE) {
	    	StringBuilder s= new StringBuilder(), s2 =  new StringBuilder();;
	    	int i;
	    	
	    	if (Build.VERSION.RELEASE.charAt(0)=='4') {
	    		if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)==0) {
	    			return s;
	    		}
	    	}
    		
	    	//do you have APNs in file ?
	    	boolean found=false;
	    	boolean found2=false;
	    	int num=0;
        	XmlPullParserFactory factory;
    		InputStream stream;        	
			try {
				factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	    		try {
	    			stream = GetDBFile(context);
					xpp.setInput(stream,"utf-8");
			        int eventType = xpp.getEventType();			            
			        while (eventType != XmlPullParser.END_DOCUMENT) {			            	
			        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
			        		if (!found) {
				        		s2.delete(0, s2.length());
				            	found2=false;   
				            	for(i=0;i<xpp.getAttributeCount();i++) {
				            		if (xpp.getAttributeName(i).equals("mcc")) {
				                	   	s2.insert(0, xpp.getAttributeValue(i));
				                	}
				                	
				                	if (xpp.getAttributeName(i).equals("mnc")) {    				                		   
				                		s2.append(xpp.getAttributeValue(i));
				                		if (!s2.toString().equals(opercode)) {
				                			break;
				                		}
				                		found2=true;
				                	}
				                	if (xpp.getAttributeName(i).equals("op")) {
				                		found2=false;
				                		if (xpp.getAttributeValue(i).equals(opername)) {
				                			found=true;
				                			break;
				                		}
				                	}
				            	}	
				            	if (found2) {
				            		found=true;
				            	}
			        		}
			        		num++;
			        	}			               
			            eventType = xpp.next();
			        }
			        stream.close();			            
				} catch (IOException e) {
				} catch(IllegalArgumentException e2) {
				}
			} catch (XmlPullParserException e1) {
			}
	    	
	        if (!found) {
	        	return s;
	        }
	        
	    	Cursor c = context.getContentResolver().query(APN_TABLE_URI, null, "numeric='"+opercode+"'", null, null);
	        boolean APNS[] = new boolean[num];
		    for(i=0;i<num;i++) {
		    	APNS[i]=false;
		    }	    	        
	        if (c != null) {
	        	//pierwsze z nasza nazwa zostawiamy
	        	//reszta do usuniecia
    	        StringBuilder wheretodelete= new StringBuilder();   
		        try {
		            if (c.moveToFirst()) {
		    	        int numid=-1,numname=-1;
		            	String[] Columns = c.getColumnNames();
			        	int l=Columns.length;
			        	int[] ColumnIndexes = new int[l];
			        	for (i=0;i<l;i++) {
			        		ColumnIndexes[i] = c.getColumnIndex(Columns[i]);
			        	}
	                    for (i=0;i<l;i++) {
	                    	if (Columns[i].equals("name")) {
	                    		numname=i;
	                    	}
	                    	if (Columns[i].equals("_id")) {
	                    		numid=i;
	                    	}
	                    }
	                    if (numid!=-1 && numname!=-1) {
    			        	Boolean todelete;
    		                do {                	
    			    	        todelete=true;
    			    	        
    			    	    	s2 = new StringBuilder();
    			    	    	num=0;
    			    			try {
    			    				factory = XmlPullParserFactory.newInstance();
    			    	            factory.setNamespaceAware(true);
    			    	            XmlPullParser xpp = factory.newPullParser();
    			    	    		try {
    			    	    			stream = GetDBFile(context);
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
			    				                		if (!s2.toString().equals(opercode)) {
			    				                			break;
			    				                		}
			    				                		found=true;
			    				                	}
			    				                	if (xpp.getAttributeName(i).equals("carrier")) {
			    				                		if (!(xpp.getAttributeValue(i)+" ").equals(c.getString(ColumnIndexes[numname])) || APNS[num]) {
			    				                			break;
			    				                		}
			    				                	}			                	
			    				                	if (xpp.getAttributeName(i).equals("op")) {
			    				                		found=false;
			    				                		if (xpp.getAttributeValue(i).equals(opername)) {
			    				                			todelete=false;			    	    			    		    		
			    		    		                    	APNS[num]=true;
			    				                			break;
			    				                		}
			    				                	}			    				                	
			    				            	}
			    				            	if (found) {
			    				            		todelete=false;			    	    			    		    		
			    				            		APNS[num]=true;
			    				            	}
			    				            	
			    			    		        if (!todelete) {
			    			    		        	break;
			    			    		        }
			    			    		        num++;
    			    			        	}			               
    			    			            eventType = xpp.next();
    			    			        }
    			    			        stream.close();			            
    			    				} catch (IOException e) {
    			    				} catch(IllegalArgumentException e2) {
    			    				}    			    				    			    				
    			    			} catch (XmlPullParserException e1) {
    			    			}

    			    		    if (todelete) {
    			    		    	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
    			    		    		if (s.length()!=0) s.append(", ");
    			    		    		s.append("usuniêto wpis z _id="+c.getString(ColumnIndexes[numid]));
    			    		    	} else {
    			    		    		if (s.length()!=0) s.append(", ");
    			    		    		s.append("removed entry with _id="+c.getString(ColumnIndexes[numid]));    			    		    		
    			    		    	}
    			    		    	if (wheretodelete.length()!=0) {
    			    		    		wheretodelete.append(" or ");
    			    		    	}
    			    		    	wheretodelete.append("_id="+c.getString(ColumnIndexes[numid]));
    			    		    }
    		                } while (c.moveToNext());    		                    	
	                    }
		            }
		        } catch(SQLException e) {
		        }
		        c.close();
		        //usun nieistniejace w aplikacji
		        if (wheretodelete.length()!=0) {
	    	    	context.getContentResolver().delete(APN_TABLE_URI, wheretodelete.toString(),null);
		        }
	        }
	        //dodaj nieistniejace zdefiniowane w aplikacji
	        Boolean mms;	        	   
	    	num=0;
			try {
				factory = XmlPullParserFactory.newInstance();
	            factory.setNamespaceAware(true);
	            XmlPullParser xpp = factory.newPullParser();
	    		try {
	    			stream = GetDBFile(context);
					xpp.setInput(stream,"utf-8");
			        int eventType = xpp.getEventType();			            
			        while (eventType != XmlPullParser.END_DOCUMENT) {			            	
			        	if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("apn")) {
			        		s2.delete(0, s2.length());
			            	   
			        		mms=false;
			        		found=false;
	                		found2=false;
			            	for(i=0;i<xpp.getAttributeCount();i++) {
			            		if (xpp.getAttributeName(i).equals("mcc")) {
			                	   	s2.insert(0, xpp.getAttributeValue(i));
			                	}
			                	
			                	if (xpp.getAttributeName(i).equals("mnc")) {    				                		   
			                		s2.append(xpp.getAttributeValue(i));
			                		if (!s2.toString().equals(opercode)) {
			                			break;
			                		}
			                		if (!APNS[num]) {
			                			found2=true;
			                		}
			                	}
			            		if (xpp.getAttributeName(i).equals("type")) {
			            			if (xpp.getAttributeValue(i).contains("mms")) {
			            				mms=true;
			            			}
			                	}			                	
			                	if (xpp.getAttributeName(i).equals("op")) {
			                		found2=false;
			                		if (xpp.getAttributeValue(i).equals(opername) && !APNS[num]) {
			                			found=true;
			                			break;
			                		}
			                	}			                	
			            	}
			            	if (found || found2) {
			            		ContentValues values = new ContentValues();
			        	        
				        		values.put("numeric",s2.toString());			        			
			            		
				            	for(i=0;i<xpp.getAttributeCount();i++) {
				            		if (xpp.getAttributeName(i).equals("carrier")) {
				                	   	values.put("name", xpp.getAttributeValue(i)+" ");
				            		} else if (xpp.getAttributeName(i).equals("apn")) {
				            			if (!mms) {
				        		           	if (!isLTE) {
				        		           		values.put("apn", xpp.getAttributeValue(i)+"apndroid");
				        		           	} else {
				        		           		values.put("apn", xpp.getAttributeValue(i));
				        		           	}
				            				
				            			} else {
					                	   	values.put("apn", xpp.getAttributeValue(i));
				            				
				            			}
				                	} else if (xpp.getAttributeName(i).equals("op")) {
				                		
				                	} else {
				                		values.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
				                	}
				            	}
					        	try {
					        		Uri Row = context.getContentResolver().insert(APN_TABLE_URI, values);

				        	        c = context.getContentResolver().query(Row, null, null, null, null);
				        	        int index = c.getColumnIndex("_id");
				        	        c.moveToFirst();
					        		
				    		    	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
				    		    		if (s.length()!=0) s.append(", ");
				    		    		s.append("dodano wpis z _id="+c.getShort(index));
				    		    	} else {
				    		    		if (s.length()!=0) s.append(", ");
				    		    		s.append("added entry with _id="+c.getShort(index));    			    		    		
				    		    	}
					        		
						        	if (!mms && Row != null) {
					        	        values = new ContentValues();
					        	        values.put("apn_id", c.getShort(index));
					    		        
					    		    	if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
					    		    		if (s.length()!=0) s.append(", ");
					    		    		s.append("ustawiono jako domyœlny wpis z _id="+c.getShort(index)+" (proszê potwierdziæ w ustawieniach systemu)");
					    		    	} else {
					    		    		if (s.length()!=0) s.append(", ");
					    		    		s.append("entry with _id="+c.getShort(index)+" setup as default (please confirm in system settings)");    			    		    		
					    		    	}

					    		        c.close();		        	        
					    		    	
					        	        try {
					        	        	context.getContentResolver().update(PREFERRED_APN_URI, values, null, null);		        	        		        	     
					        	        } catch (Exception e) {
					        	        }
						        	}
					    	    } catch(SQLException e) {
						        }
			            		
			            	}
			            	
			            	num++;
			        	}			               
			            eventType = xpp.next();
			        }
			        stream.close();			            
				} catch (IOException e) {
				} catch(IllegalArgumentException e2) {
				}				
			} catch (XmlPullParserException e1) {
			}
			return s;	    	        	    	       	    	
	    }	   		
}
