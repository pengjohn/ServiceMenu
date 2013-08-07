/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test music player.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.List;

public class AutoWiFi extends AutoItemActivity implements OnClickListener {
	
    private Button sucBtn,falBtn;
    private TextView statText,devText;
    private IntentFilter mIntentFilter;
    private WifiManager wifiManager ;
    private boolean serFin;
    private StringBuffer devName = new StringBuffer();
    private static final String TAG = "AutoWiFi";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	  String action = intent.getAction();
        	  if(action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
              int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
              handleWifiStateChanged(state);
            } else if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
              if(serFin)return;
              List<ScanResult> netList = wifiManager.getScanResults();
              if ((netList == null)||(netList.size() == 0)){
              	statText.setText(R.string.auto_wifi_not_find);
              } else {
                for (int i = 0; i < netList.size(); i++) {
                  ScanResult sr= netList.get(i);
                  devName.append(sr.SSID + " (" + sr.level + ")\n");
                }
                statText.setText(R.string.auto_wifi_find);
                devText.setText(devName);
                sucBtn.setEnabled(true);
                serFin = true;
                
                //test success
              }
            } 
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_wifi);
        
        statText = (TextView)findViewById(R.id.wifi_hint);
        devText = (TextView)findViewById(R.id.wifi_name);
        sucBtn = (Button)findViewById(R.id.wifi_success);
        falBtn = (Button)findViewById(R.id.wifi_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mIntentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.wifi_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.wifi_fail:
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
      break;
    default:
      Log.e(TAG,"Error!");
      break;
    }
  }
  private void handleWifiStateChanged(int state) {
    switch (state) {
      case WifiManager.WIFI_STATE_ENABLING:
        statText.setText(R.string.auto_wifi_open);
        sucBtn.setEnabled(false);
        break;
      case WifiManager.WIFI_STATE_ENABLED:
      statText.setText(R.string.auto_wifi_search);
        sucBtn.setEnabled(false);
        if(!wifiManager.startScan())
        	statText.setText(R.string.auto_error);
        break;
      case WifiManager.WIFI_STATE_DISABLING:
        statText.setText(R.string.auto_error);
        sucBtn.setEnabled(false);
        break;
      case WifiManager.WIFI_STATE_DISABLED:
        sucBtn.setEnabled(false);
        wifiManager.setWifiEnabled(true);
        break;
      default:
        statText.setText(R.string.auto_error);
        sucBtn.setEnabled(false);
        break;
    }
  }
  @Override
  public void onResume() {
    super.onResume();
    serFin = false;
    registerReceiver(mReceiver, mIntentFilter);
    if(wifiManager != null)
      handleWifiStateChanged(wifiManager.getWifiState());
    else
      statText.setText(R.string.auto_error);
  }
  @Override
  protected void onPause() {
  	unregisterReceiver(mReceiver);
    devName.delete(0, devName.length());
    devText.setText(devName);
    super.onPause();
  }  


  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	switch(keyCode)
  	{
  		//disable the key
      case KeyEvent.KEYCODE_HOME:
      case KeyEvent.KEYCODE_BACK:
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }
  
}
