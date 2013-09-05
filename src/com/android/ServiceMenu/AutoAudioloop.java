/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhixiong   Initial to autotest audioloop.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.view.KeyEvent;
import android.content.Context;
import android.app.Activity;  
import android.media.AudioFormat;  
import android.media.AudioManager;  
import android.media.AudioRecord;  
import android.media.AudioTrack;  
import android.media.MediaRecorder;  
import android.os.Bundle;  
import android.view.View;  
import android.widget.Button;  
import android.widget.SeekBar;  
import android.widget.Toast; 
import android.view.WindowManager;

import android.widget.TextView;

public class AutoAudioloop extends AutoItemActivity implements OnClickListener {

    private Button sucBtn,falBtn;
    private TextView micAmplitudeTxt;
    private AudioManager mAudioManager;
    private static final String TAG = "AutoAudioloop";
    public static final int AUDIOLOOP_MIC_AMPLITUDE = 0xc1;

	boolean isRecording = false;
	boolean mMicTestOK = false;
	static final int frequency = 44100;  
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;  
	int recBufSize,playBufSize;  
	AudioRecord audioRecord;  
	AudioTrack audioTrack; 
    int mMicAmplitudeCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_audioloop);
        
        sucBtn = (Button)findViewById(R.id.audio_success);
        falBtn = (Button)findViewById(R.id.audio_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        micAmplitudeTxt = (TextView)findViewById(R.id.mic_amplitude);

        sucBtn.setEnabled(false);
        if(bWaitInitTime)
        {
          falBtn.setEnabled(false);
        }          
    }
  
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.audio_success:
    	isRecording = false;
     if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.audio_fail:
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
        falBtn.setEnabled(true);
        break;
      case AUDIOLOOP_MIC_AMPLITUDE:
       if(msg.arg1 > 2800)
       {
            mMicAmplitudeCount ++;
            if(mMicAmplitudeCount >= 20)
            {
                mMicAmplitudeCount = 20;
                sucBtn.setEnabled(true);
            }
       }
        micAmplitudeTxt.setText(getResources().getText(R.string.auto_audioloop_mic_amplitude)+":"+msg.arg1+"["+mMicAmplitudeCount+"/20]");
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
	mMicAmplitudeCount = 0;
    new RecordPlayThread().start();
  }
  @Override
  protected void onPause() {
  	myHandler.removeMessages(WAIT_INIT_EVENT);
  	
	isRecording = false;  
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

                  if(mMicTestOK == false)
                  {
                   int volume = 0;
                   for (int i = 0; i < buffer.length; i++) 
                   { 
                        volume += (buffer[i] * buffer[i]); 
                   }
                   Message message = new Message();
                   message.what = AutoAudioloop.AUDIOLOOP_MIC_AMPLITUDE;
                   message.arg1 = volume/bufferReadResult;
                   AutoAudioloop.this.myHandler.sendMessage(message);
                  }
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
              Toast.makeText(AutoAudioloop.this, t.getMessage(), 1000);  
          }  
      }  
  };     
}
