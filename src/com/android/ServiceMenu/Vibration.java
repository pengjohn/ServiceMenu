/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to test the vibration.

===========================================================================*/

package com.android.ServiceMenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import android.os.Handler;


public class Vibration extends Activity {
  /** Called when the activity is first created. */
  private Button vibBtn;
  private boolean isVibrator;
  private Vibrator mVibrator = null;
  private static final String TAG = "Vibration";

  static final String NV_VIBRATOR = "FactoryData4_Write";

  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.vibrator_preference);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    isVibrator = false;
    vibBtn = (Button)findViewById(R.id.vibrationBtn);
    vibBtn.setOnClickListener(new Button.OnClickListener(){
      public void onClick(View arg0) 
      {
        // TODO Auto-generated method stub
        if(!isVibrator)
        {
        	long [] pattern = {100,10000,100,10000};   //stop start stop start
          vibBtn.setText(R.string.vibration_off);
          
          mVibrator.vibrate(pattern, 0);
          isVibrator = true;
        } else {
          vibBtn.setText(R.string.vibration_on);
          mVibrator.cancel();
          isVibrator = false;
        }
      }        	
    });
  }
  @Override
  public void onResume() {
    super.onResume();
  }
  @Override
  protected void onPause() {        
    if(mVibrator != null)
    {
      if(isVibrator)
      {
        vibBtn.setText(R.string.vibration_on);
        mVibrator.cancel();
        isVibrator = false;
      }
    }

    super.onPause();
  }
}
