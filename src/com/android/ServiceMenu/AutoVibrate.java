/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test vib.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoVibrate extends AutoItemActivity implements OnClickListener {

    private Button sucBtn,falBtn;
    private Vibrator mVibrator = null;
    private static final int MSG_VIB = 13;
    private static final int TIME = 1000;
    private static final String TAG = "AutoVibrate";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_vibrate);
        
        sucBtn = (Button)findViewById(R.id.vib_success);
        falBtn = (Button)findViewById(R.id.vib_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

	      mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }   	      
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.vib_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.vib_fail:
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
        case MSG_VIB:
          mVibrator.vibrate(10*1000);
          Message msg1 = new Message();
			    msg1.what = WAIT_INIT_EVENT;
			    myHandler.sendMessageDelayed(msg1, TIME);  
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

    mVibrator.vibrate(10*1000);
    
    Message msg = new Message();
    msg.what = MSG_VIB;
    myHandler.sendMessageDelayed(msg, TIME);  
  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);
  	
    myHandler.removeMessages(MSG_VIB);
    mVibrator.cancel();
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


}
