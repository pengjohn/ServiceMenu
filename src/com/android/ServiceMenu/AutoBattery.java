/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to  auto test battery.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileInputStream;
import java.io.IOException;

import com.android.internal.app.IBatteryStats;

public class AutoBattery extends AutoItemActivity implements OnClickListener {

    private Button sucBtn,falBtn;
    private TextView mStatus;
    private TextView mPower;
    private TextView mLevel;
    private TextView mScale;
    private TextView mHealth;
    private TextView mVoltage;
    private TextView mTemperature;
    private TextView mTechnology;
    private TextView mUptime;
    private IBatteryStats mBatteryStats;
    private IPowerManager mScreenStats;    
    //Gozone start BID 11668 qiuchangping:for berry current
    private TextView mCurrent;
	Timer timer = null;
	//Gozone end BID 11668

    private static final int EVENT_TICK = 16;
    private static final String TAG = "AutoBattery";
    /**
     * Format a number of tenths-units as a decimal string without using a
     * conversion to float.  E.g. 347 -> "34.7"
     */
    private final String tenthsToFixedString(int x) {
        int tens = x / 10;
        return Integer.toString(tens) + "." + (x - 10 * tens);
    }

   /**
    *Listens for intent broadcasts
    */
    private IntentFilter   mIntentFilter;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int plugType = intent.getIntExtra("plugged", 0);

                mLevel.setText("" + intent.getIntExtra("level", 0));
                mScale.setText("" + intent.getIntExtra("scale", 0));
                mVoltage.setText("" + intent.getIntExtra("voltage", 0) + " "
                        + getString(R.string.battery_info_voltage_units));
                mTemperature.setText("" + tenthsToFixedString(intent.getIntExtra("temperature", 0))
                        + getString(R.string.battery_info_temperature_units));
                mTechnology.setText("" + intent.getStringExtra("technology"));
				
                //Gozone start BID 11668 qiuchangping:for berry current
				mCurrent.setText("" + intent.getIntExtra("current", 0) + " "
                        + getString(R.string.battery_info_current_units));
				//Gozone end BID 11668
				        
				        //not display Current for QRD
				        mCurrent.setVisibility(View.GONE);
				        
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                String statusString;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    statusString = getString(R.string.battery_info_status_charging);
                    if (plugType > 0) {
                        statusString = statusString + " " + getString(
                                (plugType == BatteryManager.BATTERY_PLUGGED_AC)
                                        ? R.string.battery_info_status_charging_ac
                                        : R.string.battery_info_status_charging_usb);
                    }
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    statusString = getString(R.string.battery_info_status_discharging);
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    statusString = getString(R.string.battery_info_status_not_charging);
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    statusString = getString(R.string.battery_info_status_full);
                } else {
                    statusString = getString(R.string.battery_info_status_unknown);
                }
                mStatus.setText(statusString);

                switch (plugType) {
                    case 0:
                        mPower.setText(getString(R.string.battery_info_power_unplugged));
                        break;
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        mPower.setText(getString(R.string.battery_info_power_ac));
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        mPower.setText(getString(R.string.battery_info_power_usb));
                        break;
                    case (BatteryManager.BATTERY_PLUGGED_AC|BatteryManager.BATTERY_PLUGGED_USB):
                        mPower.setText(getString(R.string.battery_info_power_ac_usb));
                        break;
                    default:
                        mPower.setText(getString(R.string.battery_info_power_unknown));
                        break;
                }
                
                int health = intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
                String healthString;
                if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
                    healthString = getString(R.string.battery_info_health_good);
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                    healthString = getString(R.string.battery_info_health_overheat);
                } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                    healthString = getString(R.string.battery_info_health_dead);
                } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                    healthString = getString(R.string.battery_info_health_over_voltage);
                } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                    healthString = getString(R.string.battery_info_health_unspecified_failure);
                } else {
                    healthString = getString(R.string.battery_info_health_unknown);
                }
                mHealth.setText(healthString);
            }
        }
    };
	
//Gozone start BID 11668 qiuchangping:for berry current
	/**
	 * The contents of the "sys/devices/platform/bcmpmu_hwmon/fg_currsmpl" file
	 */
	private String getBatteryCurrent() {
		String BatteryCurrent = "";
		FileInputStream is = null;
		int count = 0;
		try {
			is = new FileInputStream(
					"sys/devices/platform/bcmpmu_hwmon/fg_currsmpl");
			byte[] buffer = new byte[20];
			count = is.read(buffer);
			if (count > 0) {
				BatteryCurrent = new String(buffer, 0, count);
			}
		} catch (IOException e) {
			Log.e(TAG, "No sys/devices/platform/bcmpmu_hwmon/fg_currsmpl =" + e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		Log.e(TAG, "sys/devices/platform/bcmpmu_hwmon/fg_currsmpl ="
				+ BatteryCurrent);
		if (count == 0) {
			return null;
		}
		return BatteryCurrent.substring(0, count - 1);
	}

private Handler handler = new Handler() {
	  @Override
	  public void handleMessage(Message msg) {
		  // TODO Auto-generated method stub
		  super.handleMessage(msg);
		  int msgId = msg.what;
		  switch (msgId) {
		  case 1:
			  mCurrent.setText(" " + getBatteryCurrent() + " mA");
			  break;
		  default:
			  break;

		  }
	  }
  };

  private void setTimerTask() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}

		}, 0, 1000);
	}
  
  @Override
  protected void onDestroy() {
	  // TODO Auto-generated method stub
	  super.onDestroy();
	  if (timer != null) {
		  timer.cancel();
		  timer.purge();
		  timer = null;
	  }
  }
  //Gozone end BID 11668
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_battery);
        
        mStatus = (TextView)findViewById(R.id.status);
        mPower = (TextView)findViewById(R.id.power);
        mLevel = (TextView)findViewById(R.id.level);
        mScale = (TextView)findViewById(R.id.scale);
        mHealth = (TextView)findViewById(R.id.health);
        mTechnology = (TextView)findViewById(R.id.technology);
        mVoltage = (TextView)findViewById(R.id.voltage);
        mTemperature = (TextView)findViewById(R.id.temperature);
        mUptime = (TextView) findViewById(R.id.uptime);
        sucBtn = (Button)findViewById(R.id.battery_success);
        falBtn = (Button)findViewById(R.id.battery_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
		//Gozone start BID 11668 qiuchangping:for berry current
		mCurrent = (TextView)findViewById(R.id.current);
		//Gozone end BID 11668

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }  
		
		//Gozone start BID 11668 qiuchangping:for berry current
        setTimerTask();
		//Gozone end BID 11668
		
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.battery_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.battery_fail:
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
  Handler myHandler = new Handler(){
    public void handleMessage(Message msg){
      switch(msg.what){
      case WAIT_INIT_EVENT:
        sucBtn.setEnabled(true);
        falBtn.setEnabled(true);        
        break; 
      case EVENT_TICK:
        updateBatteryStats();
        sendEmptyMessageDelayed(EVENT_TICK, 1000);                    
      break;		    			
      default:
      break;
      }
      super.handleMessage(msg);
    }
  };
  @Override
  public void onResume() {
    super.onResume();
    // Get awake time plugged in and on battery
    mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batteryinfo"));
    mScreenStats = IPowerManager.Stub.asInterface(ServiceManager.getService(POWER_SERVICE));
    myHandler.sendEmptyMessageDelayed(EVENT_TICK, 1000);

    
    registerReceiver(mIntentReceiver, mIntentFilter);

    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME); 
  }
  @Override
  protected void onPause() {

    myHandler.removeMessages(WAIT_INIT_EVENT);
    myHandler.removeMessages(EVENT_TICK);
    
    // we are no longer on the screen stop the observers
    unregisterReceiver(mIntentReceiver);
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
  
    
  private void updateBatteryStats() {
    long uptime = SystemClock.elapsedRealtime();
    mUptime.setText(DateUtils.formatElapsedTime(uptime / 1000));
    
  }
}
