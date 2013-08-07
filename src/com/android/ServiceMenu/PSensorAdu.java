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
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import java.lang.Integer;
import android.os.AsyncResult;

public class PSensorAdu extends AutoItemActivity implements OnClickListener,SensorEventListener {

  private Button startBtn;
  private TextView pText;
  private TextView pTextValue;
  private TextView pTextValueOld;
  private TextView pTextValueMin;
  private TextView pTextValueMax;
  private boolean mExit = false;
  private SensorManager sm ;
  private Sensor proSeneor;
  private String mAduValue;
  private float mAduValueMin = 10.0f;
  private float mAduValueMax = 0.0f;
  private int mPdata = 0;
  private int mPdataMin = 1024;
  private int mPdataMax = 0;
  
  private static final String FILE_PSEN_ADU = "/data/psensoradu";
  private static final String FILE_PSEN_ADU_STATE = "/sys/devices/virtual/optical_sensors/proximity/adu_enable";
  private static final String FILE_PSEN_PDATA = "/sys/devices/virtual/optical_sensors/proximity/pdata";
  private Toast mToast = null; 
  
  private Handler myHandler = new Handler(){
    public	void handleMessage(Message msg){
    switch(msg.what){
      case PSEN_GET_PDATA:
      {
        mAduValue = readFile(FILE_PSEN_PDATA);
        Log.e(TAG,"mAduValue = "+mAduValue);
    		mPdata = Integer.valueOf(mAduValue);
    		if(mPdata > mPdataMax)
    		    mPdataMax = mPdata;
    		if(mPdata < mPdataMin)
    		    mPdataMin = mPdata;      	
      	
        pTextValue.setText(getResources().getText(R.string.psensor_value) + String.valueOf(mPdata));
        pTextValueMin.setText(getResources().getText(R.string.psensor_value_min) + String.valueOf( mPdataMin));
        pTextValueMax.setText(getResources().getText(R.string.psensor_value_max) + String.valueOf( mPdataMax));
              	
	      Message msg1 = new Message();
	      msg1.what = PSEN_GET_PDATA;
	      myHandler.sendMessageDelayed(msg1, 500);      	
      	break;
      }
      default:
        break;
      }
    }
  };
//  private static final int MSG_FIN = 1;
  private final int PSEN_GET_PDATA = 1;
  private static final String TAG = "AutoSensor";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.psen);
        
    pText = (TextView)findViewById(R.id.p_sen);
    pTextValue = (TextView)findViewById(R.id.p_sen_value);
    pTextValueOld = (TextView)findViewById(R.id.p_sen_value_old);
    pTextValueMin = (TextView)findViewById(R.id.p_sen_value_min);
    pTextValueMax = (TextView)findViewById(R.id.p_sen_value_max);

    startBtn = (Button)findViewById(R.id.psen_btn);
    startBtn.setOnClickListener(this);
    pText.setText(getResources().getText(R.string.auto_psensoradu_info));
    pTextValue.setText(getResources().getText(R.string.auto_psensor_hint));

    pTextValueOld.setText(getResources().getText(R.string.psensor_value_old) + readFile(FILE_PSEN_ADU) );

    sm = (SensorManager)getSystemService(SENSOR_SERVICE);
    if(sm != null){
      proSeneor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }    
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO Auto-generated method stub
    
  }
  
  public void onSensorChanged(SensorEvent event) {
    // TODO Auto-generated method stub
    if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
    	{
    		mAduValue = String.valueOf(event.values[0]);
    		float f = Float.valueOf(mAduValue).floatValue();
    		if(f > mAduValueMax)
    		    mAduValueMax = f;
    		if(f < mAduValueMin)
    		    mAduValueMin = f;

        pTextValue.setText(getResources().getText(R.string.psensor_value) + String.valueOf(event.values[0]));
        pTextValueMin.setText(getResources().getText(R.string.psensor_value_min) + String.valueOf( mAduValueMin));
        pTextValueMax.setText(getResources().getText(R.string.psensor_value_max) + String.valueOf( mAduValueMax));
        
      }
    }
  }
  	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.psen_btn:
      startAdjust();
      pTextValueOld.setText(getResources().getText(R.string.psensor_value_old) + readFile(FILE_PSEN_ADU) );
			break;
		default:
			Log.e(TAG,"Error!");
			break;
		}
	}
  private void startAdjust(){
  	    writeFile(FILE_PSEN_ADU, mAduValue);
			  ProcessShellCommand("/data/atx_test/psensoradu.sh");
			  
			  Toast.makeText(this, R.string.psensor_adu_success, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onResume() {
    super.onResume();
    mExit = false;
    writeFile( FILE_PSEN_ADU_STATE, "1");
    
    if (proSeneor != null)
      sm.registerListener(this, proSeneor, SensorManager.SENSOR_DELAY_FASTEST);    

    //Message msg = new Message();
    //msg.what = PSEN_GET_PDATA;
    //myHandler.sendMessageDelayed(msg, 1000); 
  }
  @Override
  
  protected void onPause() {
  	writeFile( FILE_PSEN_ADU_STATE, "0");
    if (proSeneor != null)
      sm.unregisterListener(this, proSeneor);
        	
    mExit = true;

    //myHandler.removeMessages(PSEN_GET_PDATA);
    super.onPause();
  }
}
