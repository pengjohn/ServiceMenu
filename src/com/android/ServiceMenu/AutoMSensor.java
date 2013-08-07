/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test compass.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.os.Message;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class AutoMSensor extends AutoItemActivity implements OnClickListener, SensorEventListener {
	
	  private static final String TAG = "AutoMSensor";
	  private Button falBtn, sucBtn;
	  private TextView sensorValue;
    private SensorManager mSensorManager;
    Sensor oriSensor;
    private MSensorView mView;
    //private float[] mValues;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.auto_msensor, null);
        setContentView(view);
        
        mView = (MSensorView) view.findViewById(R.id.MSensorPattern);
        
        sucBtn = (Button) view.findViewById(R.id.sucBtn);
        sucBtn.setOnClickListener(this);
        falBtn = (Button) view.findViewById(R.id.failBtn);
        falBtn.setOnClickListener(this);

				sensorValue = (TextView)findViewById(R.id.sensorValue);
				
        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }
        
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        oriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.sucBtn:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.failBtn:
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
      default:
        break;
      }
      super.handleMessage(msg);
    }
   };
 
    @Override
    protected void onResume()
    {
        super.onResume();
				if (oriSensor != null)
					mSensorManager.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
			        

      Message msg = new Message();
      msg.what = WAIT_INIT_EVENT;
      myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);          		
    }
    
    @Override
    protected void onPause()
    {
    	  myHandler.removeMessages(WAIT_INIT_EVENT);
				if (oriSensor != null)
					mSensorManager.unregisterListener(this, oriSensor);        
        super.onStop();
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

//	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
//	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		// TODO Auto-generated method stub
		Log.e(TAG,"smx the sensor is:"+event.sensor.getType()+"["+event.values[0]+"]["+event.values[1]+"]["+event.values[2]+"]");
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
		{
    	if (mView != null) 
    	{
    		sensorValue.setText("Value: " + event.values[0]);
      	mView.setValue(event.values[0]);
        mView.invalidate();
      }
		}
	}   
}
