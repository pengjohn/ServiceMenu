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
import android.media.AudioFormat;  
import android.media.AudioManager;
import android.media.AudioRecord;  
import android.media.AudioTrack;  
import android.media.MediaRecorder;  
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
import android.widget.Toast;  


public class AutoEarphone extends AutoItemActivity implements OnClickListener {

  private Button sucBtn,falBtn;
  private TextView earText;
  private AudioManager mAudioManager;
  private boolean mIsHeadsetPlugged = false;
  private static final String TAG = "AutoEarphone";

  boolean isRecording = false;
  static final int frequency = 44100;  
  static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
  static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;  
  int recBufSize,playBufSize;  
  AudioRecord audioRecord;  
  AudioTrack audioTrack; 


/*
  private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver(){
  
  @Override			
  public void onReceive(Context context, Intent intent) {				
    // TODO Auto-generated method stub	
    String action = intent.getAction();				
    if(action.equals(Intent.ACTION_HEADSET_PLUG)){
      mIsHeadsetPlugged = (intent.getIntExtra("state", 0) == 1);
      if(mIsHeadsetPlugged )
      {
        earText.setText(R.string.auto_earphone_audioloop_hint);
        sucBtn.setEnabled(true);
      } 
      else 
      {
        earText.setText(R.string.auto_ear_input);
        sucBtn.setEnabled(false);     	
      }
    }
  }
  };
*/  
  @Override
  public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_earphone);

        earText = (TextView)findViewById(R.id.earphone_audioloop);
        sucBtn = (Button)findViewById(R.id.earphone_success);
        falBtn = (Button)findViewById(R.id.earphone_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

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
    case R.id.earphone_success:
    	isRecording = false;
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.earphone_fail:
    	isRecording = false;
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
//        if(mIsHeadsetPlugged)
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

    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);  
    
    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
    playBufSize=AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
    audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);  
    audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM); 
    
    audioTrack.setStereoVolume(90, 90);
	  isRecording = true;  
    new RecordPlayThread().start();
/*		    		    
    registerReceiver(mHeadsetReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    if(mAudioManager != null){
      mIsHeadsetPlugged = mAudioManager.isWiredHeadsetOn();
      if(mIsHeadsetPlugged)
      {
      } 
      else
      {
        earText.setText(R.string.auto_ear_input);
        sucBtn.setEnabled(false);      	
      }
    }
*/	
  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);
    isRecording = false;  
//    unregisterReceiver(mHeadsetReceiver);	  	
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

      
  class RecordPlayThread extends Thread 
  {  
      public void run() 
      {  
          try 
          {  
              byte[] buffer = new byte[recBufSize];
              int BUFFER_COUNT = 5;
              byte[][] tmpBuf = {null,null,null,null,null};
              int nBufferIndexRecord = 0;
              int nBufferIndexPlay = 0;
              
              audioRecord.startRecording();
              audioTrack.play();
                
              while (isRecording)
              {  
                  //save data from MIC to buffer
                  int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);
                  tmpBuf[nBufferIndexRecord] = new byte[bufferReadResult];
                  System.arraycopy(buffer, 0, tmpBuf[nBufferIndexRecord], 0, bufferReadResult);
                     
                  //write data to play directly
                  nBufferIndexPlay = (nBufferIndexRecord+BUFFER_COUNT-1)%BUFFER_COUNT;
                  if(tmpBuf[nBufferIndexPlay] != null)
                      audioTrack.write(tmpBuf[nBufferIndexPlay], 0, tmpBuf[nBufferIndexPlay].length);
                  
                  //Bufferindex +1
                  nBufferIndexRecord = (nBufferIndexRecord+1)%BUFFER_COUNT;
              } 
              audioTrack.stop();
              audioRecord.stop();
              audioTrack.release();
              audioRecord.release();
          } 
          catch (Throwable t)
          {  
              Toast.makeText(AutoEarphone.this, t.getMessage(), 1000);  
          }  
      }  
  };     
}
