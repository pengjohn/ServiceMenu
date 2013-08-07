/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110530  PengZhiXiong   Initial to auto test music player.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoEarphoneKey extends AutoItemActivity implements OnClickListener {
	
    private Button sucBtn,falBtn;
    private static final String TAG = "AutoEarphoneKey";
    private Boolean bPause = false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_earphone_key);
        
        sucBtn = (Button)findViewById(R.id.success);
        falBtn = (Button)findViewById(R.id.fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        sucBtn.setVisibility(View.GONE);

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
  public void onResume() {
    super.onResume();
        
    Message msg2 = new Message();
    msg2.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg2, WAIT_INIT_TIME);      
  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);

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
      
      case KeyEvent.KEYCODE_HEADSETHOOK:
      {
        if(bFlagAutoTest)
           openActivity(getTestItemActivityIdByClass(this)+1);
        else
           setTestSuccessed(getTestItemActivityIdByClass(this));
        finish();      	
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }      

}
