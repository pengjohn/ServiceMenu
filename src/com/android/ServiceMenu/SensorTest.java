/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show the sensors' value.

===========================================================================*/

package com.android.ServiceMenu;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.content.Intent;
import android.view.KeyEvent;

import java.util.List; 

public class SensorTest extends AutoItemActivity implements SensorEventListener{
	/** Called when the activity is first created. */
    private static final String FILE_PSEN_ADU_STATE = "/sys/devices/virtual/optical_sensors/proximity/adu_enable";	
    private static final String FILE_LSEN_ADU_STATE = "/sys/devices/virtual/optical_sensors/lightsensor/adu_enable";

	private TextView xViewA,yViewA,zViewA,xViewO,yViewO,zViewO,mProSen,mLigSen,mAllSensorInfo;
	private TextView GyroscopeValue1, GyroscopeValue2, GyroscopeValue3;
	SensorManager sm ;
	Sensor accSensor,oriSensor,proSeneor,ligSensor,gyrosSensor;
	private String TAG = "gsensorTest";
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_layout);
        
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        xViewA = (TextView)findViewById(R.id.sen_accx);
        yViewA = (TextView)findViewById(R.id.sen_accy);
        zViewA = (TextView)findViewById(R.id.sen_accz);
        xViewO = (TextView)findViewById(R.id.sen_orix);
        yViewO = (TextView)findViewById(R.id.sen_oriy);
        zViewO = (TextView)findViewById(R.id.sen_oriz);
        mProSen = (TextView)findViewById(R.id.sen_proximity);
        mLigSen = (TextView)findViewById(R.id.sen_light);
				mAllSensorInfo = (TextView)findViewById(R.id.sen_all_info);
				
        GyroscopeValue1 = (TextView)findViewById(R.id.sen_gyroscope_value1);
        GyroscopeValue2 = (TextView)findViewById(R.id.sen_gyroscope_value2);
        GyroscopeValue3 = (TextView)findViewById(R.id.sen_gyroscope_value3);
        List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL); 
         Log.e(TAG, allSensors.size() + "sensors:\n");
         mAllSensorInfo.setText("\n\n" + allSensors.size() + "sensors:\n");
         for (Sensor s : allSensors) 
        	{
        		mAllSensorInfo.setText(mAllSensorInfo.getText().toString() + 
        													s.getType() + 
        													getSensorName(s.getType()) + "\n" +
        													"    name:" + s.getName() + "\n" +
        													"    version:" + s.getVersion() + "\n" + 
        													"    vendor:" + s.getVendor() + "\n"
        													);
          }	
  
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        oriSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        proSeneor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        ligSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        gyrosSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);        
	}
	@Override
	protected void onResume(){
		super.onResume();
        writeFile( FILE_PSEN_ADU_STATE, "1");
        writeFile( FILE_LSEN_ADU_STATE, "1");
		if (accSensor != null)
			sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (ligSensor != null)
			sm.registerListener(this, ligSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (oriSensor != null)
			sm.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (proSeneor != null)
			sm.registerListener(this, proSeneor, SensorManager.SENSOR_DELAY_NORMAL);
		if (gyrosSensor != null)
			sm.registerListener(this, gyrosSensor, SensorManager.SENSOR_DELAY_NORMAL);

	}
	@Override
	protected void onPause(){
        writeFile( FILE_PSEN_ADU_STATE, "0");
        writeFile( FILE_LSEN_ADU_STATE, "0");
		if (accSensor != null)
			sm.unregisterListener(this, accSensor);
		if (oriSensor != null)
			sm.unregisterListener(this, oriSensor);
		if (proSeneor != null)
			sm.unregisterListener(this, proSeneor);
		if (ligSensor != null)
			sm.unregisterListener(this, ligSensor);
		if (gyrosSensor != null)
			sm.unregisterListener(this, gyrosSensor);
		super.onPause();		
	}
//	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
//	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG,"smx the sensor is:"+event.sensor.getType()+"["+event.values[0]+"]["+event.values[1]+"]["+event.values[2]+"]");
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			xViewA.setText("  X: " + event.values[0] + " m/s^2");
			yViewA.setText("  Y: " + event.values[1] + " m/s^2");
			zViewA.setText("  Z: " + event.values[2] + " m/s^2");
		}
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			xViewO.setText("  Azimuth: " + event.values[0]);
			yViewO.setText("  Pitch: " + event.values[1]);
			zViewO.setText("  Roll: " + event.values[2]);
		}
		if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
			mProSen.setText("  Proximity:" + event.values[0]);
		}
		
		if(event.sensor.getType() == Sensor.TYPE_LIGHT){
			mLigSen.setText("  Light: " + event.values[0] + " lux");
		}

		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			GyroscopeValue1.setText("Gyroscope: " + event.values[0]);
			GyroscopeValue2.setText("Gyroscope: " + event.values[1]);
			GyroscopeValue3.setText("Gyroscope: " + event.values[2]);
		}
		
	}

	@Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if(keyCode == KeyEvent.KEYCODE_MENU) {
    	return true;
    }
    return super.onKeyDown(keyCode, event);
  }

	
	private String getSensorName(int type)
	{
	  switch(type) 
	  { 
	   case Sensor.TYPE_ACCELEROMETER: 
	        return "Accelerometer";
	    case Sensor.TYPE_GYROSCOPE: 
	        return "Gyroscope";
	    case Sensor.TYPE_LIGHT: 
	        return "Light";
	    case Sensor.TYPE_MAGNETIC_FIELD: 
	        return "Magnetic field";
	    case Sensor.TYPE_ORIENTATION: 
	        return "Orientation";
	    case Sensor.TYPE_PRESSURE: 
	        return "Pressure";
	    case Sensor.TYPE_PROXIMITY: 
	        return "Proximity"; 
	    case Sensor.TYPE_TEMPERATURE: 
	        return "Temperature"; 
	    default: 
	        return "Unknown"; 
	  }
	}
	
}
