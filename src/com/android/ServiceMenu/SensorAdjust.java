/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to adjust the screen LP.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;

public class SensorAdjust extends Activity implements OnClickListener, SensorEventListener {
    private EditText sen_acc,sen_ori,sen_light,sen_mag;
    private Button mSetValue;
    private static float  acc = 0;
    private static float  ori = 0;
    private static float  lig = 0;
    private static float  mag = 0;
	private TextView xViewA,yViewA,zViewA,xViewO,yViewO,zViewO,mLigSen,xViewM,yViewM,zViewM;
	SensorManager sm ;
	Sensor accSensor,oriSensor,ligSensor,magSensor;
    
    private static final String TAG = "ScreenChangeSet";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoradjust);

		sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        
        mSetValue = (Button)findViewById(R.id.setScreenBtn);
        xViewA = (TextView)findViewById(R.id.sen_accx);
        yViewA = (TextView)findViewById(R.id.sen_accy);
        zViewA = (TextView)findViewById(R.id.sen_accz);
        xViewO = (TextView)findViewById(R.id.sen_orix);
        yViewO = (TextView)findViewById(R.id.sen_oriy);
        zViewO = (TextView)findViewById(R.id.sen_oriz);
        mLigSen = (TextView)findViewById(R.id.sen_lig);
		xViewM = (TextView)findViewById(R.id.sen_magx);
        yViewM = (TextView)findViewById(R.id.sen_magy);
        zViewM = (TextView)findViewById(R.id.sen_magz);
		sen_acc = (EditText)findViewById(R.id.sen_acc);
        sen_ori = (EditText)findViewById(R.id.sen_ori);
        sen_light = (EditText)findViewById(R.id.sen_light);
        sen_mag = (EditText)findViewById(R.id.sen_mag);
        
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        oriSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        ligSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
		magSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
        mSetValue.setOnClickListener(this);
    }
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        acc = TextUtils.isEmpty(sen_acc.getText().toString())? 0:Float.parseFloat(sen_acc.getText().toString());
        ori = TextUtils.isEmpty(sen_ori.getText().toString())? 0:Float.parseFloat(sen_ori.getText().toString());
        lig = TextUtils.isEmpty(sen_light.getText().toString())? 0:Float.parseFloat(sen_light.getText().toString());
        mag = TextUtils.isEmpty(sen_mag.getText().toString())? 0:Float.parseFloat(sen_mag.getText().toString());
        //mSensor.onSensorAdjust(acc,ori,lig,mag);
//pengzhixiong@gozone
//		sm.sensorToAdjust(acc,ori,mag,lig);
    }
	@Override
	protected void onResume(){
		super.onResume();
		//sm.registerListener(this, SensorManager.SENSOR_ACCELEROMETER|SensorManager.SENSOR_ORIENTATION|SensorManager.SENSOR_PROXIMITY|SensorManager.SENSOR_LIGHT,SensorManager.SENSOR_DELAY_NORMAL);
		if (accSensor != null)
			sm.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (oriSensor != null)
			sm.registerListener(this, oriSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (ligSensor != null)
			sm.registerListener(this, ligSensor, SensorManager.SENSOR_DELAY_NORMAL);
		if (magSensor != null)
			sm.registerListener(this, magSensor, SensorManager.SENSOR_DELAY_NORMAL);
		sen_acc.setText(String.valueOf(acc));
        sen_ori.setText(String.valueOf(ori));
        sen_light.setText(String.valueOf(lig));
        sen_mag.setText(String.valueOf(mag));
	}
	@Override
	protected void onPause(){
		super.onPause();
		if (accSensor != null)
			sm.unregisterListener(this, accSensor);
		if (oriSensor != null)
			sm.unregisterListener(this, oriSensor);
		if (ligSensor != null)
			sm.unregisterListener(this, ligSensor);
		if (magSensor != null)
			sm.unregisterListener(this, magSensor);
		
	}
//	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
//	@Override
	public void onSensorChanged(SensorEvent event) {
	//Log.e("hahahahhahahahhahaha","the sensor changed in phonewindowmanager changed");
		// TODO Auto-generated method stub
	//	Log.e(TAG,"smx the sensor is:"+event.sensor.getType());
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			xViewA.setText("X: " + event.values[0] + " m/s^2");
			yViewA.setText("Y: " + event.values[1] + " m/s^2");
			zViewA.setText("Z: " + event.values[2] + " m/s^2");
		}
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			xViewO.setText("Azimuth: " + event.values[0]);
			yViewO.setText("Pitch: " + event.values[1]);
			zViewO.setText("Roll: " + event.values[2]);
		}
		if(event.sensor.getType() == Sensor.TYPE_LIGHT){
			mLigSen.setText("Light: " + event.values[0] + " lux");
		}
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			xViewM.setText("X: " + event.values[0] );
			yViewM.setText("Y: " + event.values[1] );
			zViewM.setText("Z: " + event.values[2] );
		}
	}
}
