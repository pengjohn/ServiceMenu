/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show debug list of CIT.

===========================================================================*/

package com.android.ServiceMenu;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.nfc.NfcAdapter;

import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.String;
import java.io.File;
import java.io.InputStream;   
import java.io.InputStreamReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.os.SystemProperties;

public class ForDebug extends PreferenceActivity implements  
        Preference.OnPreferenceChangeListener   {
  /** Called when the activity is first created. */
  private NfcAdapter mNfcAdapter;
  private static final String TAG = "ForDebug";
  //private ListPreference CrashDumpListPref; 
  private ListPreference BootanimationListPref;
  private ListPreference OtgListPref; 
  private static final String CRASH_DUMP_SWITCH = "/sys/gz_log/crash_dump/dump_on";
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.debug_preference);
    
    BootanimationListPref = (ListPreference)findPreference("BootanimationChange");
    BootanimationListPref.setOnPreferenceChangeListener(this); 

    OtgListPref = (ListPreference)findPreference("otg_switch");
    OtgListPref.setOnPreferenceChangeListener(this); 
/*
    CrashDumpListPref = (ListPreference)findPreference("CrashDump");
    CrashDumpListPref.setOnPreferenceChangeListener(this); 
    String CrashDumpValue=readFile(CRASH_DUMP_SWITCH);
    Log.i(TAG, "CrashDumpValue[ = "+ CrashDumpValue+"]" );
    if(CrashDumpValue != null && CrashDumpValue.contains("on"))
    {
       CrashDumpListPref.setValue("On");
    }
    else
    {
       CrashDumpListPref.setValue("Off");
    }
*/    
    // Remove NFC if its not available
    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    if (mNfcAdapter == null) 
    {
    }    
  }
  // [Add for the sign of tested Item]
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    //preference.setSummary(R.string.tested);
    String key = preference.getKey();
        if( key != null ){  
            if(key.equals("TestApCrash")) 
            {  
							writeFile("/sys/gz_log/crash_dump/bug", "1");
            }
            else if(key.equals("TestCpCrash")) 
            {  
						  Runtime runtime = Runtime.getRuntime();
						  Process process ;
						  try
						  {
						  	process = runtime.exec("/system/etc/atx_test/test_cp_crash.sh"); 
						  }catch(IOException e)
					    {
					  	  Log.i(TAG, "runtime.exec failed!");
					    }
            }
            else if(key.equals("UsbDebug")) 
            {  
		        	 Log.i(TAG, "Usb debug ON!");
		        	 SystemProperties.set("ctl.start", "usbdebug_on");							
            }            
        }  
            
    return false;
  }

    public boolean onPreferenceChange(Preference preference, Object objValue) {  
        /*if (preference == CrashDumpListPref)
        {  
		        if(objValue.equals("On"))  
		        { 
		        	writeFile(CRASH_DUMP_SWITCH, "1");
		        }  
		        else
		        {  
		        	writeFile(CRASH_DUMP_SWITCH, "0");
		        }              
        }  
        else */
        if (preference == BootanimationListPref)
        {  
		        if(objValue.equals("Off"))  
		        { 
		        	 Log.i(TAG, "Clean bootanimation!");
		        	 SystemProperties.set("ctl.start", "poweron_clean");
		        }  
		        else if(objValue.equals("On"))
		        {  
		        	 Log.i(TAG, "Set bootanimation!");
		        	 SystemProperties.set("ctl.start", "poweron_set");
		        }
        } 
        else if (preference == OtgListPref)
        {  
		        if(objValue.equals("Off"))  
		        { 
		        	 Log.i(TAG, "Otg Close!");
		        	 SystemProperties.set("ctl.start", "otg_close");
		        }  
		        else if(objValue.equals("On"))
		        {  
		        	 Log.i(TAG, "Otg Open!");
		        	 SystemProperties.set("ctl.start", "otg_open");
		        }
        } 
        return true;
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

    public int CopyFile(String fromFile, String toFile)
    {
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[10240000];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;
             
        } catch (Exception ex)
        {
            return -1;
        }finally
        {
        }
    }		
    	
}
