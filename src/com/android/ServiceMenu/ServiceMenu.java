/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show main list of CIT.

===========================================================================*/

package com.android.ServiceMenu;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.IBinder;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothAdapter;
import android.location.LocationManager;
import android.provider.Settings;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import android.util.Log;

public class ServiceMenu extends PreferenceActivity {
  private static final String KEY_BT_SETTINGS = "bt_settings";
  
  private WifiManager mWifiManager;
  private BluetoothAdapter mBTAdapter;  
  
  private int mScreenOffTimeoutSetting;
  private static final int DEFAULT_SCREEN_OFF_TIMEOUT = 15000;
  
  private boolean mIsPcba = false;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.service_preference);

    //LCD turn on 10 minutes
    Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT, 600000);
                        
    //turn on wifi
    mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    int wifiApState = mWifiManager.getWifiApState();
    if(wifiApState == WifiManager.WIFI_AP_STATE_DISABLED)
       mWifiManager.setWifiEnabled(true);

    //turn on Bluetooth
    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    mBTAdapter.enable();            

    //turn on GPS, build Common APK, and run to crash
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.NETWORK_PROVIDER, false);
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true); 
    
    //turn on ADB
    Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_ENABLED, 1);
    
    mIsPcba = this.getIntent().getBooleanExtra("isPcba", false);
    AutoItemActivity.mIsPcba = mIsPcba;
    Log.v("ServiceMenu", "onReceive mIsPcba:" + mIsPcba);
  }

  @Override
  protected void onDestroy() {       
    //LCD turn on 1 minutes
    Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT, 60000);
                        
    //turn off wifi
    mWifiManager.setWifiEnabled(false);

    //turn off Bluetooth
    mBTAdapter.disable();

    //turn off GPS, build Common APK, and run to crash
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.NETWORK_PROVIDER, false);
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false); 

    super.onDestroy();
  }
      
  @Override
  public void onResume() {
    super.onResume();    

  }
  @Override
  protected void onPause() {
    super.onPause();
  }  
    
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    //preference.setSummary(R.string.tested);
    return false;
  }

}
