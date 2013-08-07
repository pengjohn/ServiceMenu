/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test 

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;
import java.lang.String;
import java.io.File;
import java.io.InputStream;   
import java.io.InputStreamReader;
import android.content.SharedPreferences;
import java.lang.Character;

import com.android.qualcomm.qcnvitems.*;

public class AutoItemActivity extends Activity {
  public static final int TEST_START = 0;


  public static final int TEST_WAIT_TIME = 5000;
  //delay TEST_INIT_TIME, then show success/fail button
  public static final int WAIT_INIT_TIME = 2000;
  public static final int WAIT_INIT_EVENT = 0xa1;
  
  public static final int TEST_ITEMS_MAX = 1000;
  public static boolean mIsPcba = false;
 
  //for dev
  public static int TagforDev = 0;
  public static int Testnum = 0;
  public static int Testid = 2;
    
  public static int falTotle = 0;
  public static boolean bWaitInitTime = true;
  public static boolean bFlagAutoTest = false;
  public static byte []autoMask = new byte[TEST_ITEMS_MAX];
  public static StringBuffer falText = new StringBuffer();
  public static StringBuffer nameBuf = new StringBuffer();
  public static StringBuffer idBuf = new StringBuffer();
  public static StringBuffer mSharedPreferencesBuf = new StringBuffer();
  private static final String TAG = "AutoItemActivity";

  public void onAttachedToWindow()
  {
  	//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
  	//this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NEEDS_HOME_KEY);
  	super.onAttachedToWindow();
  } 
    
  private Intent getIntent(int id, boolean wait){
    nameBuf.delete(0, nameBuf.length());
    idBuf.delete(0, idBuf.length());
    Intent intent = new Intent();

    intent.putExtra("TEST_ID", id);
    nameBuf.append(getTestItemTitle(id) + " " + getResources().getText(R.string.auto_wait_hint));
    idBuf.append("(" + String.valueOf(id) + "/" + String.valueOf(this.getTestItemActivityCount()) + ")" + "\n");

    if(wait){
      intent.setClass(AutoItemActivity.this, AutoWaitActivity.class);
    } else {
      intent.setClass(AutoItemActivity.this, AutoFailActivity.class);
    }		
    return intent;
  }
  public void openActivity(int id){
		Log.i(TAG, "openActivity:id is[" + id + "] ; total:" + this.getTestItemActivityCount());
		if (id > -1 && id < this.getTestItemActivityCount())
			setTestSuccessed(id);
		
		Intent intent = new Intent();
		if (id == -1) {
			intent.setClass(AutoItemActivity.this, Auto.class);
		}else if (getTestItemActivityCount() == id) {
			intent.putExtra("AUTO_FINAL", autoMask);
			intent.setClass(AutoItemActivity.this, AutoFinish.class);
			
			Log.i(TAG, "openActivity: All finished, output the result");
			
		}else {
			intent.setClassName(AutoItemActivity.this, getTestItemActivityName(id));
			Log.i(TAG, "openActivity: Begin to test:" + getTestItemActivityName(id));
		}
        
    startActivity(intent);
  }
  public void openWaitActivity(int id){
    Intent mIntent = getIntent(id,true);
    if(mIntent != null){
      startActivity(mIntent);
    }		
  }
  public void openFailActivity(int id){
    Intent mIntent = getIntent(id,false);
    if(mIntent != null){
      startActivity(mIntent);
    }
  }
  public void initTestFailed(){
    falTotle = 0;
    falText.delete(0, falText.length());

    //Get sharedata
    SharedPreferences sharedata = getSharedPreferences("data", 0);   
    String data;
    if(mIsPcba)
        data = sharedata.getString("item_pcb", null);
    else
        data = sharedata.getString("item_normal", null);
        
    if (data != null && data.length() != this.getTestItemActivityCount()) {
			data = null;
    }  
    
    if(data == null)
    { 
    	Log.i(TAG,"initTestFailed, SharedPreferences is null");
      for(int i = 0;i < this.getTestItemActivityCount() ;i++)
        autoMask[i] = 0;
    }
    else
    {
    	int nSharePrefLen = data.length();
    	Log.i(TAG,"initTestFailed, SharedPreferences is["+data+"]");
      for(int i = 0;i < this.getTestItemActivityCount() ;i++)
      {
      	 char c = data.charAt(i);
      	 if(c == '1') 
           autoMask[i] = 1;
         else
           autoMask[i] = 0;
    	}
    }
  }
  
  public void setTestFailed(int id){
    autoMask[id] = 0;
    updateReportText();
  }

  public void setTestSuccessed(int id){
    autoMask[id] = 1;
    updateReportText();
  }
  
  public void updateReportText()
  {
    falText.delete(0, falText.length());
    falTotle = 0;
    
    mSharedPreferencesBuf.delete(0, mSharedPreferencesBuf.length());
    for(int i = 0;i < this.getTestItemActivityCount() ;i++)
  {
    if(autoMask[i] != 0)
    {
       mSharedPreferencesBuf.append("1");
       continue;
    }
    mSharedPreferencesBuf.append("0");
    falTotle += 1;
    
    falText.append(String.valueOf(falTotle) + " - " 
         + getTestItemTitle(i).toString() + "\n");
  }

   SharedPreferences settings = getSharedPreferences("data", 0);
   SharedPreferences.Editor editor = settings.edit();

   Log.i(TAG,"updateReportText, SharedPreferences is["+mSharedPreferencesBuf+"]");
   if(mIsPcba)
       editor.putString("item_pcb",mSharedPreferencesBuf.toString());
   else
       editor.putString("item_normal",mSharedPreferencesBuf.toString());
           
   editor.commit();
  }
  
  public void openReportActivity(){
    Intent intent = new Intent();
    intent.setClass(AutoItemActivity.this, AutoReport.class);
    startActivity(intent);
  }
    
  	
  public void writeFile(String strFile, String strBuf){
  	   Log.i(TAG,"writeFile "+strFile+" ["+strBuf+"]");
       BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(strFile));
            out.write(strBuf);
        } catch (FileNotFoundException e) {
            Log.e(TAG,"FileNotFoundException start");
        } catch (IOException e) {
            Log.e(TAG,"IOException");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
	}

  public void writeFile(String strFile,int nValue){
       String strBuf = String.format("%01d", nValue);
       writeFile(strFile, strBuf);
	}

  public String readFile(String strFile){
       BufferedReader in = null;
        try {
        	  char[] buf = new char[32];
            in = new BufferedReader(new FileReader(strFile));
            int len = in.read(buf);
            Log.i(TAG,"readFile "+strFile+",len="+len+", ["+buf+"]");
            return String.valueOf(buf);
        } catch (FileNotFoundException e) {
            Log.e(TAG,"FileNotFoundException start");
        } catch (IOException e) {
            Log.e(TAG,"IOException");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
	      return "";
	}

	public synchronized void ProcessShellCommand(String ADBCmd)
	{
		Runtime runtime = Runtime.getRuntime();
		Process process ;
		try
		{
			Log.i(TAG, "runtime.exec ....");
			process = runtime.exec(ADBCmd); 
			try{
				 	final InputStream is1 = process.getInputStream();   
  					final InputStream is2 = process.getErrorStream();   
    				new Thread() {   
				     	public void run() 
						{   
					       	BufferedReader br = new BufferedReader(new InputStreamReader(is1));   
					         
					     	try {     
								String Linebr = null;

					     		while ((Linebr = br.readLine())!= null )
									{
										Log.i(TAG, "Linebr = "+Linebr);
									}			
					       	} catch (IOException e) 
				       		{   
				        		e.printStackTrace();   
				       		}   
				      }   
     				}.start();   
					
   					new Thread() {   
						public void run() 
						{   
      					 	BufferedReader br2 = new BufferedReader( new InputStreamReader(is2));   
					       try {   
							String Linebr2 = null;
					       	while ( (Linebr2 = br2.readLine()) != null)
							{
								Log.i(TAG, "Linebr2 = "+Linebr2);
							} 
					       } catch (IOException e) {   
					        e.printStackTrace();   
					       }   
         
      					}   
     				}.start();  

				Log.i(TAG, "process.waitFor ....");	
				process.waitFor();
			}catch(InterruptedException e)
			{
				Log.i(TAG, "process.waitFor failed!");
			}
		}catch(IOException e)
		  {
		    Log.i(TAG, "runtime.exec failed! errormsg ="+e.getMessage());
		  }
	
		return;
	}
	
	// Get the count of the test item
	public int getTestItemActivityCount(){
		//Log.v(TAG, "getTestItemActivityCount");
        
		String[] activityList = getResources().getStringArray(getActivityListId());
		//Log.v(TAG, "getTestItemActivityCount:" + activityList.length);
        
		return activityList.length;
	}
	
	// Get test list
	private int getActivityListId() {
		//Log.i(TAG, "getActivityListId: " + mIsPcba);
		if (mIsPcba) {
			return R.array.activity_list_pcba;
		}else {
			return R.array.activity_list_normal;
		}
 	}
  
  // Get the title of the test list
	private int getActivityListTitleId() {
		//Log.i(TAG, "getActivityListTitleId: " + mIsPcba);
		if (mIsPcba) {
			return R.array.activity_list_pcba_title;
		} else {
			return R.array.activity_list_normal_title;
		}
	}
	
	// Get TITLE based the test id
	public String getTestItemTitle(int nTestItem) {
		//Log.v(TAG, "getTestItemTitle:" + nTestItem);
		if (nTestItem < 0) {
			return null;
		}
        
		String[] activityList = getResources().getStringArray(getActivityListTitleId());
		if (nTestItem < activityList.length) {
			return activityList[nTestItem];
		}
		return null;
	}
	
	// Get activity name base the test id
	public String getTestItemActivityName(int nTestItem) {
		//Log.v(TAG, "getTestItemActivityName:" + nTestItem);
		if (nTestItem < 0) {
			return null;
		}
		
		int nArrayID = getActivityListId();
	
		String[] activityList = getResources().getStringArray(nArrayID);
	
		if (nTestItem < activityList.length) {
			return activityList[nTestItem];
		}
		return null;
	}
	
	// Get test id based the activity name
	public int getTestItemActivityIdByClass(AutoItemActivity activity) {
		String className = activity.getClass().getName();
		//Log.v(TAG, "getTestItemActivityIdByClass:" + className);
		return getTestItemActivityIdByName(className);
	}
	
	// Get test id based the title
	public int getTestItemActivityIdByName(String name) {
		/*
		StackTraceElement st[] = Thread.currentThread().getStackTrace();
		if (st != null) {
			for (int i = 0; i < st.length; i++) {
				Log.v(TAG, "MYTRACE:" + i + ":" + st[i]);
			}
		}
		*/
		Log.v(TAG, "getTestItemActivityIdByName:" + name);
		if (name == null || name.length() < 0) {
			return -2;
		}
	
		String[] activityList = getResources().getStringArray(getActivityListId());
		for (int i = 0; i < activityList.length; i++) {
			if (name.equals(activityList[i])) {
				return i;
			}
		}
		return -2;
	}

  public void SetTraceInfo(boolean bSuccess)
  {
  	  QcNvItems mQcNvItems;
  		mQcNvItems = new QcNvItems();
      try {
      	  String factoryData;
          String factoryDataFromNV = mQcNvItems.getFactoryData3();   	
          Log.w(TAG, "factoryDataFromNV = "+factoryDataFromNV);
          if (!factoryDataFromNV.isEmpty()) 
          {
          	if(bSuccess)
          	{
          	  factoryData = factoryDataFromNV.substring(0,4) + "P" + factoryDataFromNV.substring(5);
          	}
          	else
          	{
							factoryData = factoryDataFromNV.substring(0,4) + "F" + factoryDataFromNV.substring(5);
						}
            
            Log.w(TAG, "factoryData = "+factoryData);
          	mQcNvItems.setFactoryData3(factoryData);
          } 
          else 
          {
          }
      } catch (IOException e) {
          // TODO Show error notification window
          //Toast.makeText(IMEITest.this, getString(R.string.getfactoryData_fail), Toast.LENGTH_SHORT).show();
          Log.d(TAG, "Cannot read NV Settings");
      }   	
  }

}
