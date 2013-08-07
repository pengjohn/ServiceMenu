/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto testlight sensor.

===========================================================================*/
package com.android.ServiceMenu;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoLSensor extends AutoItemActivity implements OnClickListener,SensorEventListener {

    private Button sucBtn,falBtn;
    private TextView lText;
    private SensorManager sm ;
    private Sensor ligSensor;
    private static final int DARK_LIGHT = 10;
    private boolean bLightFlag = false; 
    private boolean bDarkFlag = false; 
    private boolean bInitTime = false;
    private static final String TAG = "AutoLSensor";
    private static final String FILE_LSEN_ADU_STATE = "/sys/devices/virtual/optical_sensors/lightsensor/adu_enable";    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_lsensor);

        lText = (TextView)findViewById(R.id.auto_lsen);
        sucBtn = (Button)findViewById(R.id.lsensor_success);
        falBtn = (Button)findViewById(R.id.lsensor_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        sucBtn.setVisibility(View.GONE);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }  
                
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sm != null){
          ligSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }
	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.lsensor_success:
        if(bFlagAutoTest)
           openActivity(getTestItemActivityIdByClass(this)+1);
        else
           setTestSuccessed(getTestItemActivityIdByClass(this));
        finish();
      break;
    case R.id.lsensor_fail:
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
        bInitTime = true;   
        break;    			
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
  
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO Auto-generated method stub
    
  }
  public void onSensorChanged(SensorEvent event) {
    // TODO Auto-generated method stub
    if(event.sensor.getType() == Sensor.TYPE_LIGHT)
    {
      lText.setText(getResources().getText(R.string.auto_lsensor) + ": " + event.values[0] + " lux");
      
      if(bInitTime == false)
        return;
        
      if(event.values[0] > DARK_LIGHT)
      {
         lText.setText(getResources().getText(R.string.light_sensor_light)+"("+event.values[0]+" lux)");
         bLightFlag = true;
      }
      else
      {
         lText.setText(getResources().getText(R.string.light_sensor_dark)+"("+event.values[0]+" lux)");
         bDarkFlag = true;
      }
      //test success
      if(bLightFlag && bDarkFlag)
      {
        if(bFlagAutoTest)
           openActivity(getTestItemActivityIdByClass(this)+1);
        else
           setTestSuccessed(getTestItemActivityIdByClass(this));
        finish();      	
      }
      
    }
 
  }
  @Override
  public void onResume() {
    super.onResume();
		writeFile( FILE_LSEN_ADU_STATE, "1");
    if (ligSensor != null)
      sm.registerListener(this, ligSensor, SensorManager.SENSOR_DELAY_NORMAL);

    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);       
  }
  @Override
  protected void onPause() {
    writeFile( FILE_LSEN_ADU_STATE, "0");
    if (ligSensor != null)
      sm.unregisterListener(this, ligSensor);
    myHandler.removeMessages(WAIT_INIT_EVENT);
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
