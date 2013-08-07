/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test USB.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.os.BatteryManager;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoUsb extends AutoItemActivity implements OnClickListener {

  private Button falBtn;
  private TextView usbTextStep1,usbTextStep2,usbTextStep3,usbTextStep4;
  private static final String TAG = "AutoUsb";
  
  private static final int TEST_STEP0_START = 0;
  private static final int TEST_STEP1_INSERT_CHARGER = 1;
  private static final int TEST_STEP2_REMOVE_CHANGER = 2;
  private static final int TEST_STEP3_INSERT_USB = 3;
  private static final int TEST_STEP4_REMOVE_USB = 4;
  private int testStep = TEST_STEP0_START;
    
  private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
  
  @Override			
  public void onReceive(Context context, Intent intent) {				
    String action = intent.getAction();				
    if(action.equals(Intent.ACTION_BATTERY_CHANGED))
    {
      int plugType = intent.getIntExtra("plugged", 0);
      Log.e(TAG,"usb plugType = "+plugType);
      switch (plugType) {
          case 0:
          		if(testStep == TEST_STEP1_INSERT_CHARGER)
          		{
          			usbTextStep2.setTextColor(Color.RED);
          			testStep = TEST_STEP2_REMOVE_CHANGER;
          		}
          		else if(testStep == TEST_STEP3_INSERT_USB)
          		{
          			usbTextStep4.setTextColor(Color.RED);
          			testStep = TEST_STEP4_REMOVE_USB;
          			if(bFlagAutoTest)
      					   openActivity(getTestItemActivityIdByClass(AutoUsb.this)+1);
      					else
      					   setTestSuccessed(getTestItemActivityIdByClass(AutoUsb.this));
      					finish();          			
          		}
              break;
          case BatteryManager.BATTERY_PLUGGED_AC:
          		if(testStep == TEST_STEP0_START)
          		{
          			usbTextStep1.setTextColor(Color.RED);
          			testStep = TEST_STEP1_INSERT_CHARGER;
          		}
              break;
          case BatteryManager.BATTERY_PLUGGED_USB:
          		if(testStep == TEST_STEP2_REMOVE_CHANGER)
          		{
          			usbTextStep3.setTextColor(Color.RED);
          			testStep = TEST_STEP3_INSERT_USB;
          		}
              break;
          case (BatteryManager.BATTERY_PLUGGED_AC|BatteryManager.BATTERY_PLUGGED_USB):
              break;
          default:
              break;
      }
    }
  }
  };
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_usb);
    
    usbTextStep1 = (TextView)findViewById(R.id.auto_usb_step1);
    usbTextStep2 = (TextView)findViewById(R.id.auto_usb_step2);
    usbTextStep3 = (TextView)findViewById(R.id.auto_usb_step3);
    usbTextStep4 = (TextView)findViewById(R.id.auto_usb_step4);
    falBtn = (Button)findViewById(R.id.usb_fail);
    falBtn.setOnClickListener(this);    
  }
  
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.usb_fail:
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

  @Override
  public void onResume() {
    super.onResume();
    registerReceiver(mReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
  }
  @Override
  protected void onPause() {
        
    unregisterReceiver(mReceiver);	  	
    super.onPause();
  }

  @Override
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

