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
import android.media.AudioManager;
import android.content.Context;

public class AutoMusic extends AutoItemActivity implements OnClickListener {
    private static final int TEST_MUSIC_STEP1 = 1;
    private static final int TEST_MUSIC_STEP2 = 2;
    private static final int TEST_MESSAGE_EVENT = 0x150;
    private static final int TEST_AUDIO_TIME = 2000;
    private Button sucBtn,falBtn;
    private TextView leftAudioText, rightAudioText;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private static final String TAG = "AutoMusic";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_music);
        
        sucBtn = (Button)findViewById(R.id.music_success);
        falBtn = (Button)findViewById(R.id.music_fail);
        leftAudioText = (TextView)findViewById(R.id.music_audio_left);
        rightAudioText = (TextView)findViewById(R.id.music_audio_right);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }      
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
									mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_PLAY_SOUND);   
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.music_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.music_fail:
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
      case TEST_MESSAGE_EVENT:
         if(msg.arg1 == TEST_MUSIC_STEP1)
         {
           //left -> Right
			    if(mMediaPlayer != null){
			      mMediaPlayer.setVolume(0, 1);
			    }
		
		      leftAudioText.setVisibility(View.GONE);
		      rightAudioText.setVisibility(View.VISIBLE);
		
			    Message msg1 = new Message();
			    msg1.what = TEST_MESSAGE_EVENT;
			    msg1.arg1 = TEST_MUSIC_STEP2;
			    myHandler.sendMessageDelayed(msg1, TEST_AUDIO_TIME);
         }
         else if(msg.arg1 == TEST_MUSIC_STEP2)
         {
           //Right -> Left
			    if(mMediaPlayer != null){
			      mMediaPlayer.setVolume(1, 0);
			}

	    leftAudioText.setVisibility(View.VISIBLE);
	    rightAudioText.setVisibility(View.GONE);

	    Message msg1 = new Message();
	    msg1.what = TEST_MESSAGE_EVENT;
	    msg1.arg1 = TEST_MUSIC_STEP1;
	    myHandler.sendMessageDelayed(msg1, TEST_AUDIO_TIME);
         }
      break;
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
    //Uri playUri = Uri.parse("/system/media/testmusic.wav");
    //mMediaPlayer = MediaPlayer.create(this, playUri);
    //mMediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
    
    mMediaPlayer = MediaPlayer.create(this, R.raw.test_music);
    if(mMediaPlayer != null){
      mMediaPlayer.setVolume(1, 1);
      mMediaPlayer.start();
      mMediaPlayer.setLooping(true);
    }
    leftAudioText.setVisibility(View.GONE);
    rightAudioText.setVisibility(View.GONE);

    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);  
        
    /*
    //play left
    if(mMediaPlayer != null){
      mMediaPlayer.setVolume(1, 0);
      mMediaPlayer.start();
      mMediaPlayer.setLooping(true);
    }
    leftAudioText.setVisibility(View.VISIBLE);
    rightAudioText.setVisibility(View.GONE);

    Message msg = new Message();
    msg.what = TEST_MESSAGE_EVENT;
    msg.arg1 = TEST_MUSIC_STEP1;
    myHandler.sendMessageDelayed(msg, TEST_AUDIO_TIME);
    */
  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);
  	
    myHandler.removeMessages(TEST_MESSAGE_EVENT);
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
