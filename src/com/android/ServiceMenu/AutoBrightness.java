/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhixiong   Initial to  auto test brightness.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;
import android.view.KeyEvent;

import android.os.IPowerManager;

public class AutoBrightness extends AutoItemActivity implements OnClickListener{
	
  private Button falBtn;
  private Button sucBtn;
  private TextView proText;
  private int mCurrCol = 0;
  private int mOldBrightness;
  private static final int MINIMUM_BACKLIGHT = 50;
  private static final int MAXIMUM_BACKLIGHT = 255;
  private static final int STEP_BACKLIGHT = 50;
  private static final int DisplayBrightCount = 5; //the number of the color to test
  private static final int DisplayBrightTime = 800;//the time of every color's show 
  private static final int BRI_SHOW = 6;
  private static final String TAG = "AutoBrightness";
  private static final int []nBacklightLevel={0, 10,50,100,150,200,255};
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_brightness);
    
    proText = (TextView)findViewById(R.id.bri_level);
    falBtn = (Button)findViewById(R.id.bright_fail);
    falBtn.setOnClickListener(this);

    sucBtn = (Button)findViewById(R.id.bright_success);
    sucBtn.setOnClickListener(this);

    try {
      mOldBrightness = Settings.System.getInt(getBaseContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
    } catch (SettingNotFoundException snfe) {
      mOldBrightness = MAXIMUM_BACKLIGHT;
    }

    if(bWaitInitTime)
    {
    	  sucBtn.setVisibility(View.GONE);
    	  falBtn.setVisibility(View.GONE);
        //sucBtn.setEnabled(false);
        //falBtn.setEnabled(false);
    }    
  }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.bright_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.bright_fail:
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

  Handler mMsgHandler = new Handler(){
    public void handleMessage(Message msg){
      switch(msg.what){
      case BRI_SHOW:
        mCurrCol ++;
        if(mCurrCol > DisplayBrightCount)
        {
        	mCurrCol = 1;
    	    sucBtn.setVisibility(View.VISIBLE);
    	    falBtn.setVisibility(View.VISIBLE);
          //sucBtn.setEnabled(true);
          //falBtn.setEnabled(true);             	
        }
        setBrightness(nBacklightLevel[mCurrCol]);
        proText.setText("(" + String.valueOf(mCurrCol) + "/" + String.valueOf(DisplayBrightCount) + ")");

    		Message msg1 = new Message();
    		msg1.what = BRI_SHOW;
    		mMsgHandler.sendMessageDelayed(msg1, DisplayBrightTime);        
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
    stopAutoBrightness();
    Message msg = new Message();
    msg.what = BRI_SHOW;
    mMsgHandler.sendMessageDelayed(msg, DisplayBrightTime);
        
  }
  @Override
  protected void onPause() {
    mMsgHandler.removeMessages(BRI_SHOW);

    setBrightness(mOldBrightness);
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
  
  private void setBrightness(int brightness) {
				//WindowManager.LayoutParams lp = getWindow().getAttributes();
				//lp.screenBrightness = brightness*1.0f /MAXIMUM_BACKLIGHT;
				//getWindow().setAttributes(lp);
				try {
        IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));				
        if (power != null) 
        {
            power.setBacklightBrightness(brightness);
        }
      }catch (RemoteException e) {
            Log.d(TAG, "toggleBrightness: " + e);
        } 
    }

  private void stopAutoBrightness() {
    Settings.System.putInt(getBaseContext().getContentResolver(),
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
}    
}
