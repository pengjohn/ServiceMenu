/***************************************************************
* Copyright (C) 2012 GOZONE Inc. All Rights Reserved.
*
* File: AutoFm.java
*
* Purpose:
*		for FM auto test
* who:  pengzhixiong
*
* when: 20120517
***************************************************************/

package com.android.ServiceMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.os.Message;
import android.hardware.fmradio.FmConfig;
import android.hardware.fmradio.FmReceiver;
import android.hardware.fmradio.FmRxEvCallbacksAdaptor;


public class AutoFm extends AutoItemActivity implements OnClickListener {
    static String TAG = "FM";
    Button searchButton, sucBtn, falBtn;
    TextView mTextView;
    AudioManager mAudioManager = null;
    FmManager mFmManager = null;
    Context mContext = null;
    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                mTextView.setText(new Float(mFmManager.getFrequency() / 1000f).toString() + "MHZ");
            }
        };
    };

    void getService() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mFmManager = new FmManager(mContext, mHandler);
    }

    void bindView() {

        searchButton = (Button) findViewById(R.id.fm_search);
        sucBtn = (Button) findViewById(R.id.fm_success);
        falBtn = (Button) findViewById(R.id.fm_fail);
        mTextView = (TextView) findViewById(R.id.fm_frequency);
        mTextView.setText(new Float(mFmManager.getFrequency() / 1000f).toString() + "MHZ");

        searchButton.setOnClickListener(this);
	      sucBtn.setOnClickListener(this);
	      falBtn.setOnClickListener(this);
    }

    public void setAudio() 
    {
		    AudioSystem.setForceUse(AudioSystem.FOR_MEDIA, AudioSystem.FORCE_NONE);
		    mAudioManager.requestAudioFocus(null, AudioManager.STREAM_FM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        float ratio = 0.3f;

        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_DTMF,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_DTMF)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)), 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_FM,
                (int) (ratio * mAudioManager.getStreamMaxVolume(AudioManager.STREAM_FM)), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.auto_fm);
        getService();
        setAudio();
        bindView();
        if (!mAudioManager.isWiredHeadsetOn()) {
            setButtonClickable(false);
            showWarningDialog(getString(R.string.auto_fm_insert_headset));
        }
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.fm_success:
    {
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    }
    case R.id.fm_fail:
    {
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
      break;
    }
    case R.id.fm_search:
    {
      if (mAudioManager.isWiredHeadsetOn()) 
      {
        setButtonClickable(true);
        mFmManager.searchUP();
      } 
      else 
      {         
        setButtonClickable(false);
        showWarningDialog(getString(R.string.auto_fm_insert_headset));
      }
      break;
    }
    default:
      Log.e(TAG,"Error!");
      break;
    }
  }
  
    void showWarningDialog(String title) 
    {
        new AlertDialog.Builder(mContext).setTitle(title)
                .setPositiveButton(getString(R.string.auto_continue), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    void setButtonClickable(boolean cmd)
    {
        sucBtn.setEnabled(cmd);
        //sucBtn.setClickable(cmd);
        //sucBtn.setFocusable(cmd);
        //falBtn.setClickable(cmd);
        //falBtn.setFocusable(cmd);
    }

    @Override
    public void onResume() 
    {
	    super.onResume();
      if(mFmManager != null)
      {
         mFmManager.openFM();
      }
      Message msg = new Message();
      msg.what = WAIT_INIT_EVENT;
      myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);
    }

    @Override
    protected void onPause() 
    {
    	 myHandler.removeMessages(WAIT_INIT_EVENT);
       if(mFmManager != null)
       {
          mFmManager.closeFM();
       }
      super.onPause();
    }

  Handler myHandler = new Handler(){
    public void handleMessage(Message msg){
      switch(msg.what){
      case WAIT_INIT_EVENT:
        //falBtn.setEnabled(true);        
        //sucBtn.setEnabled(true);
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };    
}

