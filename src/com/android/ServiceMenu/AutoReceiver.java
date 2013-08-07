/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20120710  PengZhiXiong   Initial to auto receiver.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
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
import android.media.AudioManager; 

public class AutoReceiver extends AutoItemActivity implements OnClickListener {
    private Button sucBtn,falBtn;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private static final String TAG = "AutoReceiver";
    //private static final String testMusicFile = "/system/";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_receiver);
        
        sucBtn = (Button)findViewById(R.id.success);
        falBtn = (Button)findViewById(R.id.fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }

        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);    
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
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
    mMediaPlayer = MediaPlayer.create(this, R.raw.test_music);
    if(mMediaPlayer != null){
      mMediaPlayer.setVolume(1, 1);
      mMediaPlayer.start();
      mMediaPlayer.setLooping(true);
    }
    mAudioManager.setMode(AudioManager.MODE_IN_CALL);
     
    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);  
        

  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);
  	
  	mAudioManager.setMode(AudioManager.MODE_NORMAL);
  	
     if(mMediaPlayer != null){
      mMediaPlayer.stop();
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
    
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
