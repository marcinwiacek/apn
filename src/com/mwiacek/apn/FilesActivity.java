package com.mwiacek.apn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FilesActivity extends ListActivity {
    final Activity MyActivity5 = this;
    APNClass APN=new APNClass();
	public final static String EXTRA_MESSAGE = "com.mwiacek.apn.MESSAGE1";
	public final static String EXTRA_MESSAGE2 = "com.mwiacek.apn.MESSAGE2";
	public final static String EXTRA_MESSAGE3 = "com.mwiacek.apn.MESSAGE3";
	private List<String> item = null;
	private List<String> path = null;
	private TextView myPath,myFile;
	Button filesbutton1;

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
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.files);
        myPath = (TextView)findViewById(R.id.path);
        myFile = (TextView)findViewById(R.id.file);

        getDir(getIntent().getStringExtra(EXTRA_MESSAGE));
        myFile.setText(getIntent().getStringExtra(EXTRA_MESSAGE2));

    	try {
      	  PackageManager manager = getPackageManager();
      	  PackageInfo info = manager.getPackageInfo(getPackageName(), 0);

      	  if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("open")) {
      		  if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
      			  setTitle("APN "+info.versionName+" (zewnêtrzny plik)");	
      		  } else {
      			  setTitle("APN "+info.versionName+" (external file)");
      		  }
      	  } else if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("save")) {
      		  if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
      			  setTitle("APN "+info.versionName+" (eksport wewnêtrznej bazy)");	
      		  } else {
      			  setTitle("APN "+info.versionName+" (export internal database)");
      		  }	    	
      	  } else if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("save2")) {
      		  if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
      			  setTitle("APN "+info.versionName+" (eksport bazy urz¹dzenia)");	
      		  } else {
      			  setTitle("APN "+info.versionName+" (export device database)");
      		  }
      	  }
    	} catch (Exception e) {      	  
    	}        

        filesbutton1 = (Button) findViewById(R.id.filesbutton2);
  		if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
    		filesbutton1.setText("  Cofnij  ");
  		} else {
  			filesbutton1.setText("  Back  ");
  		}	    	
        filesbutton1.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {	
    		    MyActivity5.setResult(RESULT_CANCELED);
    		    MyActivity5.finish();
    		}
    	});	
    	
        filesbutton1 = (Button) findViewById(R.id.filesbutton1);
        filesbutton1.setText("  OK  ");        
        filesbutton1.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {	
    		    if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("open")) {
    	        	XmlPullParserFactory factory;
    	        	try {
        				try {
        					factory = XmlPullParserFactory.newInstance();
        		            factory.setNamespaceAware(true);
        		            XmlPullParser xpp = factory.newPullParser();
        		    		InputStream stream;
        		    		try {
        		    			File f = new File(myPath.getText().toString()+"/"+myFile.getText().toString());
        		    			
        		    			stream = new FileInputStream(f);
        						xpp.setInput(stream,"utf-8");
        				        stream.close();			            
        					} catch (IOException e) {
        						Alert(e.getMessage());
        						return;
        					}    					
        				} catch (XmlPullParserException e1) {
        					Alert(e1.getMessage());
        					return;
        				}    		    	    	        		
    	        	} catch (IllegalArgumentException e2) {
    	        		Alert(e2.getMessage());
    					return;    	        		
    	        	}

    	        	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyActivity5).edit();
    	        	editor.putString("ExtFilePath2",myPath.getText().toString());
    	        	editor.putString("ExtFileName2",myFile.getText().toString());
    	        	editor.commit();
    		    } else if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("save")) {        		    
    		    	AlertDialog alertDialog;
    		    	
    		    	File f = new File(myPath.getText().toString()+"/"+myFile.getText().toString());
    		    	if (f.exists()) {    		    		
						alertDialog = new AlertDialog.Builder(MyActivity5).create();
    		    		alertDialog.setTitle("APN");
    		      		if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		    		alertDialog.setMessage("Plik ju¿ istnieje. Zast¹piæ ?");
    		      		} else {
        		    		alertDialog.setMessage("File already exists. Replace ?");
    		      		}	    	
    		    		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    				File f = new File(myPath.getText().toString()+"/"+myFile.getText().toString());
    			    			try {						    			    				
    								FileOutputStream fos = new FileOutputStream(f,false);
    			        	    	InputStream stream;
    			        	    	stream = getAssets().open("apns-full-conf.xml");
    			        	    	byte[] input = new byte[stream.available()];
    			        	    	stream.read(input);		        					
    			        		    fos.write(input);
    			        		    fos.close();
    							} catch (IOException e) {
    		    	        		Alert(e.getMessage());
    					    		return;
    							}
    			    			MyActivity5.finish();
    		    			}	    		    			
    		    		});
    		    		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    			}	    		    			
    		    		});
    		    		alertDialog.show();	 								
    		    		return;
    		    	}
	    			try {						
						FileOutputStream fos = new FileOutputStream(f,false);
	        	    	InputStream stream;
	        	    	stream = getAssets().open("apns-full-conf.xml");
	        	    	byte[] input = new byte[stream.available()];
	        	    	stream.read(input);		        					
	        		    fos.write(input);
	        		    fos.close();
					} catch (IOException e) {
						Alert(e.getMessage());
			    		return;
					} 	    			
    		    } else if (getIntent().getStringExtra(EXTRA_MESSAGE3).equals("save2")) {        		    
    		    	AlertDialog alertDialog;
    		    	
    		    	File f = new File(myPath.getText().toString()+"/"+myFile.getText().toString());
    		    	if (f.exists()) {    		    		
						alertDialog = new AlertDialog.Builder(MyActivity5).create();
    		    		alertDialog.setTitle("APN");
    		      		if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
        		    		alertDialog.setMessage("Plik ju¿ istnieje. Zast¹piæ ?");
    		      		} else {
        		    		alertDialog.setMessage("File already exists. Replace ?");
    		      		}	    	
    		    		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    				File f = new File(myPath.getText().toString()+"/"+myFile.getText().toString());
    			    			try {						    			    				
        		    				FileOutputStream fos = new FileOutputStream(f,false);

        							
        			    	    	Cursor c = getContentResolver().query(APN.APN_TABLE_URI, null, null, null, null);
        			    	        if (c != null) {
        			    	        	StringBuilder s=new StringBuilder();
        				    	        s.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        				    	        s.append("\n<!-- device internal database -->");
        				    	        s.append("\n<apns version=\"8\">");
        		    		            if (c.moveToFirst()) {
        		    		            	do {
        			    		            	s.append("\n <apn ");
        			    		            	String[] Columns = c.getColumnNames();
        			    			        	int l=Columns.length;
        			    	                    for (int i=0;i<l;i++) {
        			    	                    	if (Columns[i].equals("_id")) {
        			    	                    		continue;
        			    	                    	}
        			    	                        s.append(" "+Columns[i]+"=\""+c.getString(c.getColumnIndex(Columns[i]))+"\" ");
        			    			            }
        			                        	s.append(" />\n");	    		            	
        		    		            		
        		    		            	} while (c.moveToNext());	                
        		    				        c.close();
        		    				        
        		    		            }
        		    		            
        		    		            s.append("\n</apns>");
        		    		            byte buf[] = s.toString().getBytes("UTF-8");
        		    		            Log.d("com.mwiacek.apn",buf.length+" "+s.toString());
        		    		            fos.write(buf);
        			    	        }

        			    	        
        		        		    fos.close();    		    				
    							} catch (IOException e) {
    		    	        		Alert(e.getMessage());
    					    		return;
    							}
    			    			MyActivity5.finish();
    		    			}	    		    			
    		    		});
    		    		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
    		    			public void onClick(DialogInterface dialog, int which) {
    		    			}	    		    			
    		    		});
    		    		alertDialog.show();	 								
    		    		return;
    		    	}
	    			try {						
						FileOutputStream fos = new FileOutputStream(f,false);

						
						Cursor c = getContentResolver().query(APN.APN_TABLE_URI, null, null, null, null);
		    	        if (c != null) {
		    	        	StringBuilder s=new StringBuilder();
			    	        s.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			    	        s.append("\n<!-- device internal database -->");
			    	        s.append("\n<apns version=\"8\">");
	    		            if (c.moveToFirst()) {
	    		            	do {
		    		            	s.append("\n <apn ");
		    		            	String[] Columns = c.getColumnNames();
		    			        	int l=Columns.length;
		    	                    for (int i=0;i<l;i++) {
		    	                    	if (Columns[i].equals("_id")) {
		    	                    		continue;
		    	                    	}
		    	                        s.append(" "+Columns[i]+"=\""+c.getString(c.getColumnIndex(Columns[i]))+"\" ");
		    			            }
		                        	s.append(" />\n");	    		            	
	    		            		
	    		            	} while (c.moveToNext());	                
	    				        c.close();
	    				        
	    		            }
	    		            
	    		            s.append("\n</apns>");
	    		            byte buf[] = s.toString().getBytes("UTF-8");
	    		            Log.d("com.mwiacek.apn",buf.length+" "+s.toString());
	    		            fos.write(buf);
		    	        }

		    	        
	        		    fos.close();  
	    			} catch (IOException e) {
						Alert(e.getMessage());
			    		return;
					} 	    			
    		    }

    		    
    		    MyActivity5.setResult(RESULT_OK);
    		    MyActivity5.finish();
    		}
    	});	
    }

    private void getDir(String dirPath) {
    	myPath.setText(dirPath);
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	File f = new File(dirPath);
    	File[] files = f.listFiles();

    	if(!dirPath.equals("/")) {
    	      //item.add("/");
    	      //path.add("/");
    	      item.add("../");
    	      path.add(f.getParent());
    	}

    	for(int i=0; i < files.length; i++) {
    		File file = files[i];
    		path.add(file.getPath());

    		if(file.isDirectory()) {
    			item.add(file.getName() + "/");    			
    		} else {
    	        item.add(file.getName());    			
    		}
    	}

    	ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);

    	setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	File file = new File(path.get(position));
    	
    	if (file.isDirectory()) {
    		if(file.canRead()) {
    			getDir(path.get(position));
    		} else {
	    		if (Locale.getDefault ().getDisplayLanguage ().equals("polski")) {
	    			Alert("B³¹d odczytu folderu "+file.getName());
	      		} else {
	    			Alert("Error reading folder "+file.getName());
	      		}	    	
    		}
    	} else {
    		myFile.setText(file.getName().toString());
		}
	}
   
}