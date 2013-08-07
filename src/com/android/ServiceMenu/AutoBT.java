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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoBT extends AutoItemActivity implements OnClickListener {
	
    private Button sucBtn,falBtn;
    private TextView statText,devText;
    private BluetoothAdapter mBTAdapter;
    private IntentFilter mIntentFilter;
    private StringBuffer devName = new StringBuffer();
    private static final String TAG = "AutoBT";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	  String action = intent.getAction();
        	  if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
              int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
              handleStateChanged(state);
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
              statText.setText(R.string.auto_bt_search);
            } else if(action.equals(BluetoothDevice.ACTION_FOUND)) {
              BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
              short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
              devName.append(device.getName() + " (" + rssi + ")\n" );
              statText.setText(R.string.auto_bt_find);
              devText.setText(devName);
              sucBtn.setEnabled(true);        
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
              if(devName.length() == 0){
              	statText.setText(R.string.auto_bt_not_find);
              } else {
                statText.setText(R.string.auto_bt_find);
                devText.setText(devName);
                sucBtn.setEnabled(true);
                //test success
              }
            }
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_bt);
        
        statText = (TextView)findViewById(R.id.bt_hint);
        devText = (TextView)findViewById(R.id.bt_name);
        sucBtn = (Button)findViewById(R.id.bt_success);
        falBtn = (Button)findViewById(R.id.bt_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        
        sucBtn.setEnabled(false);
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.bt_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.bt_fail:
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
  private void handleStateChanged(int state) {
    switch (state) {
      case BluetoothAdapter.STATE_TURNING_ON:
        statText.setText(R.string.auto_bt_open);
        sucBtn.setEnabled(false);
        break;
      case BluetoothAdapter.STATE_ON:        
        sucBtn.setEnabled(false);
        if (mBTAdapter.isDiscovering())
          mBTAdapter.cancelDiscovery();
        if(!mBTAdapter.startDiscovery())
        	statText.setText(R.string.auto_error);        
        break;
      case BluetoothAdapter.STATE_TURNING_OFF:
        statText.setText(R.string.auto_error);
        sucBtn.setEnabled(false);
        break;
      case BluetoothAdapter.STATE_OFF:
        sucBtn.setEnabled(false);        
        mBTAdapter.enable();
        break;
      default:
        statText.setText(R.string.auto_error);
        sucBtn.setEnabled(false);
     }
  }
  @Override
  public void onResume() {
    super.onResume();    
    registerReceiver(mReceiver, mIntentFilter);
    if(mBTAdapter != null)
      handleStateChanged(mBTAdapter.getState());
    else
      statText.setText(R.string.auto_error); 
  }
  @Override
  protected void onPause() {
    unregisterReceiver(mReceiver);
    devName.delete(0, devName.length());
    devText.setText(devName);
    if (mBTAdapter.isDiscovering()) {
      mBTAdapter.cancelDiscovery();
    }
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
