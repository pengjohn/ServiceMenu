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
import android.content.IntentFilter;
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
import android.content.BroadcastReceiver;

public class AutoEarphoneMusic extends AutoItemActivity implements OnClickListener {

    private static final int TEST_MUSIC_LEFT = 1;
    private static final int TEST_MUSIC_RIGHT = 2;
    private static final int TEST_MESSAGE_EVENT = 0x150;
    private static final int TEST_AUDIO_TIME = 2000;
    private Button sucBtn,falBtn;
    private TextView audioinfoText;
    private int iAudioChannel = TEST_MUSIC_LEFT;
    
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private static final String TAG = "AutoEarphoneMusic";
    private Boolean bPause = false;
    private AudioManager audioManager;
    private boolean mIsHeadsetPlugged=false;
    private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver(){
    @Override			
    public void onReceive(Context context, Intent intent) {				
	    // TODO Auto-generated method stub	
	    String action = intent.getAction();				
	    if(action.equals(Intent.ACTION_HEADSET_PLUG)){
	      mIsHeadsetPlugged = (intent.getIntExtra("state", 0) == 1);
	      if(mIsHeadsetPlugged )
	      {       	      	
			    //play left
			    if(mMediaPlayer != null){
			      mMediaPlayer.setVolume(1, 0);
			      mMediaPlayer.start();
			      mMediaPlayer.setLooping(true);
			    }	      	
	        sucBtn.setEnabled(true);
	      } 
	      else 
	      {
			    if(mMediaPlayer != null){
			    	if( mMediaPlayer.isPlaying() )
			    	{
			         mMediaPlayer.pause();
			      }
			    }	      	
	        audioinfoText.setText(R.string.auto_ear_input);
	        sucBtn.setEnabled(false);     	
	      }
	    }
	    }
    };
      
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_earphone_music);
        
        sucBtn = (Button)findViewById(R.id.music_success);
        falBtn = (Button)findViewById(R.id.music_fail);
        audioinfoText = (TextView)findViewById(R.id.music_audio_info);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
									mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2,AudioManager.FLAG_PLAY_SOUND);          
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
         if( bPause == true)
         {
         	break;
         }

			   Message msg1 = new Message();
			   msg1.what = TEST_MESSAGE_EVENT;
			   myHandler.sendMessageDelayed(msg1, TEST_AUDIO_TIME);
			             
         if(!mIsHeadsetPlugged)
         {
            break;
         }

         if(iAudioChannel == TEST_MUSIC_LEFT)
         {
           //left -> Right
			    if(mMediaPlayer != null){
			    	mMediaPlayer.seekTo(0);
			      mMediaPlayer.setVolume(0, 1);
			    }
		
		      iAudioChannel = TEST_MUSIC_RIGHT;
		      audioinfoText.setText(R.string.audio_right);
         }
         else
         {
           //Right -> Left
			    if(mMediaPlayer != null)
          {
			    	mMediaPlayer.seekTo(0);
			      mMediaPlayer.setVolume(1, 0);
          }
          iAudioChannel = TEST_MUSIC_LEFT;
	        audioinfoText.setText(R.string.audio_left);
         }
         break;
      case WAIT_INIT_EVENT:
        if(mIsHeadsetPlugged)
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
    // mMediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
    mMediaPlayer = MediaPlayer.create(this, R.raw.test_earphone_music);

    Message msg = new Message();
    msg.what = TEST_MESSAGE_EVENT;
    myHandler.sendMessageDelayed(msg, TEST_AUDIO_TIME);

    Message msg2 = new Message();
    msg2.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg2, WAIT_INIT_TIME);

    registerReceiver(mHeadsetReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    if(audioManager != null){
      mIsHeadsetPlugged = audioManager.isWiredHeadsetOn();
      if(mIsHeadsetPlugged)
      {
		    //play left
		    if(mMediaPlayer != null){
		      mMediaPlayer.setVolume(1, 0);
		      mMediaPlayer.start();
		      mMediaPlayer.setLooping(true);
		    }
		    iAudioChannel = TEST_MUSIC_LEFT;
        audioinfoText.setText(R.string.audio_left);
      } 
      else
      {
        audioinfoText.setText(R.string.auto_ear_input);
        sucBtn.setEnabled(false);      	
      }
    }    
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
    
    unregisterReceiver(mHeadsetReceiver);
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
      /*
      	if( mMediaPlayer.isPlaying() )
      	{
      		mMediaPlayer.pause();
      		bPause = true;
      		audioinfoText.setText(R.string.audio_pause);
      	}
      	else
      	{
      		mMediaPlayer.start();
      		bPause = false;
      		audioinfoText.setText(R.string.audio_left);
          Message msg = new Message();
          msg.what = TEST_MESSAGE_EVENT;
          msg.arg1 = TEST_MUSIC_STEP1;
          myHandler.sendMessage(msg);      		
      	}
      	*/
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }      

}
