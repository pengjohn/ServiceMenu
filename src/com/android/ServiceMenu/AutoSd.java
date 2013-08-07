/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test sd.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AutoSd extends AutoItemActivity implements OnClickListener {

  private Button sucBtn,falBtn;
  private TextView mTotle, mAvailable;
  private static final String TAG = "AutoSd";
  private static final int MESSAGE_SD_SUCCESS = 0xf1;
  private static final int MESSAGE_SD_FAIL = 0xf2;
  private boolean bSDInsert = false;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
  @Override
  public void onReceive(Context context, Intent intent) {
    updateMemoryStatus();
    }
  };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_sd);
        
        mTotle = (TextView)findViewById(R.id.auto_sd_totle);        
        mAvailable = (TextView)findViewById(R.id.auto_sd_left);
        sucBtn = (Button)findViewById(R.id.sd_success);
        falBtn = (Button)findViewById(R.id.sd_fail);
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
    case R.id.sd_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.sd_fail:
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
        if(bSDInsert)
        {
            sucBtn.setEnabled(true);
        }
        break;
      case MESSAGE_SD_SUCCESS:
        mTotle.setText(R.string.sd_available);
        mAvailable.setText("");
        bSDInsert = true;   
        break;
      case MESSAGE_SD_FAIL:
        mTotle.setText(R.string.sd_unavailable);
        mAvailable.setText("");
        bSDInsert = false;
        break;
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

    void exec(final String para) {

        new Thread() {
            
            public void run() {
                try {
                    //logd(para);
                    
                    Process mProcess;
                    String paras[] = para.split(",");
                    //for (int i = 0; i < paras.length; i++)
                    //    logd(i + ":" + paras[i]);
                    mProcess = Runtime.getRuntime().exec(paras);
                    mProcess.waitFor();
                    
                    InputStream inStream = mProcess.getInputStream();
                    InputStreamReader inReader = new InputStreamReader(inStream);
                    BufferedReader inBuffer = new BufferedReader(inReader);
                    String s;
                    String data = "";
                    while ((s = inBuffer.readLine()) != null) {
                        data += s + "\n";
                    }
                    //logd(data);
                    int result = mProcess.exitValue();
                    //logd("ExitValue=" + result);
                    Message msg = new Message();
                    if (data.contains("/storage/sdcard0"))
                        msg.what = MESSAGE_SD_SUCCESS;
                    else
                        msg.what = MESSAGE_SD_FAIL;
                    myHandler.sendMessage(msg);
                } catch (Exception e) {
                    //logd(e);
                }
                
            }
        }.start();
        
    }
      
  private void updateMemoryStatus(){

     if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
     {
        exec("mount");
     } 
     else
     {
    	bSDInsert = false;
      mTotle.setText(R.string.sd_unavailable);
      mAvailable.setText("");        
     }
/*  	
    String status = Environment.getExternalStorageState();
    boolean readOnly = false;
    
    if(status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
      status = Environment.MEDIA_MOUNTED;
      readOnly = true;
    }
    if(status.equals(Environment.MEDIA_MOUNTED)){
      try {
        File path = Environment.getExternalStorageDirectory();
        Log.e(TAG,"the path is: " + path.getPath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totleBlocks = stat.getBlockCount();
        long availableBlocks = stat.getAvailableBlocks();
        String TotleSize = formatSize(totleBlocks * blockSize);
        String AvailableSize = formatSize(availableBlocks * blockSize);
        mTotle.setText(getString(R.string.memory_size) + TotleSize);
        mAvailable.setText(getString(R.string.memory_available) + AvailableSize);
        bSDInsert = true;
			//showFiles(path);
      }catch (IllegalArgumentException e) {
        // this can occur if the SD card is removed, but we haven't received the
        // ACTION_MEDIA_REMOVED Intent yet.
        status = Environment.MEDIA_REMOVED;
      }
    } else {
    	bSDInsert = false;
      mTotle.setText(R.string.sd_unavailable);
      mAvailable.setText("");
    }
*/    
  }
  
  private String formatSize(long size){
    String suffix = null;
    if(size >= 1024){
      suffix = "K";
      size /= 1024;
      if(size >= 1024){
        suffix = "M";
        size /= 1024;
      }
    }
    StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
    int commaOffset = resultBuffer.length() - 3;
    while(commaOffset > 0){
      resultBuffer.insert(commaOffset, ',');
      commaOffset -= 3;
    }
    if(suffix != null)
      resultBuffer.append(suffix);
    return resultBuffer.toString();
  }
    @Override
    public void onResume() {
	    super.onResume();
	    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_REMOVED);
      intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
      intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
      intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
      intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
      intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
      intentFilter.addDataScheme("file");
      registerReceiver(mReceiver, intentFilter);

      updateMemoryStatus();

      Message msg = new Message();
      msg.what = WAIT_INIT_EVENT;
      myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);  
    }
    @Override
    protected void onPause() {
    	myHandler.removeMessages(WAIT_INIT_EVENT);
      unregisterReceiver(mReceiver);
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
