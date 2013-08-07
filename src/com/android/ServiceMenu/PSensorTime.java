/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to  psensor adujust.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PSensorTime extends Activity implements OnClickListener,SensorEventListener {

  private Button startBtn;
  private EditText mEdit;
  private TextView mText;
  private SensorManager sm;
  private Sensor proSeneor;
  private static int value = 0;
  private static final String DOCK_STATE_PATH = "/sys/class/proximity/apds9120/polltime";
  private static final String TAG = "PSensorTime";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.psen_time);

    mEdit = (EditText)findViewById(R.id.input_time);
    mText = (TextView)findViewById(R.id.psen_value);
    startBtn = (Button)findViewById(R.id.psen_time_btn);
    startBtn.setOnClickListener(this);
    
    mText.setText("Proximity Sensor value is: 1");
    sm = (SensorManager)getSystemService(SENSOR_SERVICE);
    proSeneor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
  }
	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.psen_time_btn:
      if(mEdit.getText().toString().length() == 0) {
				Toast.makeText(PSensorTime.this,R.string.ddr_hint,Toast.LENGTH_SHORT).show();

			} else {
			  value = Integer.parseInt(mEdit.getText().toString());
			  setValue(value);
		  }
			break;
		default:
			Log.e(TAG,"Error!");
			break;
		}
	}
  private void setValue(int num){
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(DOCK_STATE_PATH));
      String mDockPin = String.format("%04d", num);
      out.write(mDockPin);
    } catch (FileNotFoundException e) {
      Log.e(TAG,"FileNotFoundException start");
    } catch (IOException e) {
      Log.e(TAG,"IOException");
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // Ignore
        }
      }
    }
  }
  private int getValue(){
    char[] buffer = new char[10];
    int result = 1;
    FileReader file = null;
    try {
      file = new FileReader(DOCK_STATE_PATH);
      int len = file.read(buffer, 0, 10);
      result = Integer.valueOf((new String(buffer, 0, len)).trim());

     } catch (FileNotFoundException e) {
       Log.e(TAG,"FileNotFoundException get");
     } catch (Exception e) {

     } finally {
       if (file != null) {
         try {
           file.close();
         } catch (IOException e) {
           // Ignore
         }
       }
    }
    return result;
  }
  //	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
//	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
			mText.setText("Proximity Sensor value is: " + event.values[0]);
		}
	}
  @Override
	protected void onResume(){
		super.onResume();
		if (proSeneor != null)
			sm.registerListener(this, proSeneor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	protected void onPause(){
		super.onPause();
		if (proSeneor != null)
			sm.unregisterListener(this, proSeneor);		
	}
}
