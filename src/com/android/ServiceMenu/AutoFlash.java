/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test backlight.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;



public class AutoFlash extends AutoItemActivity implements OnClickListener{

  private static final int TEST_FLASH_ON = 1;
  private static final int TEST_FLASH_OFF = 2;
  private static final int TEST_MESSAGE_EVENT = 0x150;
  private static final int FLASH_ON_DELAY = 1000;
  private static final int FLASH_OFF_DELAY = 1000;  
  private Button falBtn,sucBtn;
  
  private static final String FILE_FLASH = "/sys/class/leds/flashlight/brightness";
  
  private static final String TAG = "AutoFlash";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_flash);
     
    falBtn = (Button)findViewById(R.id.baclig_fail);
    falBtn.setOnClickListener(this);

    sucBtn = (Button)findViewById(R.id.baclig_success);
    sucBtn.setOnClickListener(this);

    if(bWaitInitTime)
    {
        sucBtn.setEnabled(false);
        falBtn.setEnabled(false);
    }    
  }

  public void onClick(View arg0) {
    // TODO Auto-generated method stub
    int id = arg0.getId();
    switch(id){
    case R.id.baclig_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.baclig_fail:
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

  @Override
  public void onResume() {
    super.onResume();
    writeFile(FILE_FLASH, 100);
    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);  
    
    Message msg2 = new Message();
    msg2.what = TEST_MESSAGE_EVENT;
    msg2.arg1 = TEST_FLASH_OFF;
    myHandler.sendMessageDelayed(msg2, FLASH_ON_DELAY);
  }
  
  @Override
  protected void onPause() {

    writeFile(FILE_FLASH, 0);
    myHandler.removeMessages(WAIT_INIT_EVENT);    
    myHandler.removeMessages(TEST_MESSAGE_EVENT);    
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
  

  Handler myHandler = new Handler(){
    public void handleMessage(Message msg){
      switch(msg.what){
      case WAIT_INIT_EVENT:
        sucBtn.setEnabled(true);
        falBtn.setEnabled(true);        
        break;
      case TEST_MESSAGE_EVENT:
      {
				if(msg.arg1 == TEST_FLASH_ON)      
  	    {
  	    	writeFile(FILE_FLASH, 100);
  	    	
    			Message msg2 = new Message();
    			msg2.what = TEST_MESSAGE_EVENT;
    			msg2.arg1 = TEST_FLASH_OFF;
	    		myHandler.sendMessageDelayed(msg2, FLASH_ON_DELAY); 
  	    }
  	    else
    	  {
    	  	writeFile(FILE_FLASH, 0);
    			Message msg2 = new Message();
    			msg2.what = TEST_MESSAGE_EVENT;
	    		msg2.arg1 = TEST_FLASH_ON;
  	  		myHandler.sendMessageDelayed(msg2, FLASH_OFF_DELAY);
    	  }
    		break;
    }
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
    
}
