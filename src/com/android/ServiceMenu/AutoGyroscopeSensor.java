/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test g sensor.

===========================================================================*/
package com.android.ServiceMenu;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoGyroscopeSensor extends AutoItemActivity implements OnClickListener,SensorEventListener {

    private Button falBtn, successBtn;
    private TextView gText1, gText2, gText3, gText4, gText5, gText6;
    private boolean bStepFlag1 = false;
    private boolean bStepFlag2 = false;
    private boolean bStepFlag3 = false;
    private boolean bStepFlag4 = false;
    private boolean bStepFlag5 = false;
    private boolean bStepFlag6 = false;
    private SensorManager sm ;
    private Sensor accSensor;

    // Indices into SensorEvent.values
    private static final int _DATA_X = 0;
    private static final int _DATA_Y = 1;
    private static final int _DATA_Z = 2;

    private static final String TAG = "AutoGyroscopeSensor";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_gyroscope_sensor);
        
        gText1 = (TextView)findViewById(R.id.auto_gsensor_step1);
        gText2 = (TextView)findViewById(R.id.auto_gsensor_step2);
        gText3 = (TextView)findViewById(R.id.auto_gsensor_step3);
        gText4 = (TextView)findViewById(R.id.auto_gsensor_step4);
        gText5 = (TextView)findViewById(R.id.auto_gsensor_step5);
        gText6 = (TextView)findViewById(R.id.auto_gsensor_step6);
        falBtn = (Button)findViewById(R.id.fail);
        successBtn = (Button)findViewById(R.id.success);
        falBtn.setOnClickListener(this);
        successBtn.setOnClickListener(this);

        gText4.setVisibility(View.GONE);
        gText5.setVisibility(View.GONE);
        gText6.setVisibility(View.GONE);
        
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        if(sm != null){
        	accSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }
	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.success:  
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.fail:
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

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		    // TODO Auto-generated method stub
		
	  }
	  public void onSensorChanged(SensorEvent event) {
		    // TODO Auto-generated method stub
		    if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			      float x = event.values[_DATA_X];
            float y = event.values[_DATA_Y];
            float z = event.values[_DATA_Z];

			      gText1.setText("X: " + x + " m/s^2");
			      gText2.setText("Y: " + y + " m/s^2");
			      gText3.setText("Z: " + z + " m/s^2");
/*            
            if(y > 9)
            {
            	gText1.setTextColor(Color.RED);
            	bStepFlag1 = true;
            }
            else if(y < -9)	
            {
            	gText2.setTextColor(Color.RED);
            	bStepFlag2 = true;
            }
            else if(x > 9)
            {
            	gText3.setTextColor(Color.RED);
            	bStepFlag3 = true;
            }
            else if(x < -9)
            {
            	gText4.setTextColor(Color.RED);
            	bStepFlag4 = true;
            }
            else if(z > 9)
            {
            	gText5.setTextColor(Color.RED);
            	bStepFlag5 = true;
            }
            else if(z < -9)
            {
            	gText6.setTextColor(Color.RED);
            	bStepFlag6 = true;
            }
            
            if(bStepFlag1 && bStepFlag2 && bStepFlag3 && bStepFlag4 && bStepFlag5 && bStepFlag6)
            {
      				openActivity(getTestItemActivityIdByClass(this)+1);
      				finish();            	
            }
*/            
		   }
	}
	
  @Override
  public void onResume() {
    super.onResume();
    if (accSensor != null)
      sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
  }
  @Override
  protected void onPause() {
    if (accSensor != null)
      sm.unregisterListener(this, accSensor);
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
