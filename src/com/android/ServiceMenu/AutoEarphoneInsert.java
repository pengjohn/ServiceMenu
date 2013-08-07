/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test earphone.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.view.WindowManager;


public class AutoEarphoneInsert extends AutoItemActivity implements OnClickListener {

  private Button falBtn;
  private TextView earText;
  private AudioManager audioManager;
  private boolean mIsHeadsetPlugged = false;
  private static final String TAG = "AutoEarphoneInsert";
  private static final int EARPHONE_TIMER = 1000;
  private boolean bInitTime = false;  
  
  private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver(){
  @Override			
  public void onReceive(Context context, Intent intent) {				
    // TODO Auto-generated method stub	
    String action = intent.getAction();				
    if(action.equals(Intent.ACTION_HEADSET_PLUG)){
      mIsHeadsetPlugged = (intent.getIntExtra("state", 0) == 1);
      if(mIsHeadsetPlugged )
      {
      	if(bInitTime)
      	{
           if(bFlagAutoTest)
              openActivity(getTestItemActivityIdByClass(AutoEarphoneInsert.this)+1);
           else
              setTestSuccessed(getTestItemActivityIdByClass(AutoEarphoneInsert.this));
           finish();
         }
      } 
      else 
      {
      }
    }
  }
  };
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_earphone_insert);
    
    earText = (TextView)findViewById(R.id.earphone_insert);

    falBtn = (Button)findViewById(R.id.earphone_fail);
    falBtn.setOnClickListener(this);    
    
  }
  
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.earphone_fail:
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
        bInitTime = true;    
        break;   			  
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
    
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // TODO Auto-generated method stub		
    switch(keyCode){
    case KeyEvent.KEYCODE_HEADSETHOOK:
    		break;
  	//disable the key
    case KeyEvent.KEYCODE_HOME:
    case KeyEvent.KEYCODE_BACK:
        return true;    		
    default:
      Log.e(TAG,"Error!");
      return super.onKeyDown(keyCode, event);
    }
    return true;	
  }

  @Override
  public void onResume() {
    super.onResume();
    registerReceiver(mHeadsetReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));

    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, EARPHONE_TIMER); 
  }
  @Override
  protected void onPause() {
    unregisterReceiver(mHeadsetReceiver);	  	
    super.onPause();
  }
}
