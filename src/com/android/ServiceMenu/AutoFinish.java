/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show finish.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.Intent;
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

//import com.android.internal.telephony.PhoneFactory;
//import com.android.internal.telephony.Phone;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;
import java.io.RandomAccessFile;  

public class AutoFinish extends AutoItemActivity implements OnClickListener {

  private Button finBtn;
  private TextView numText,resText;
  private StringBuffer numStr = new StringBuffer();
  //private Phone phone = null;
  //private static final int NV_OFFSET = 4;
  //private byte[] write = new byte[TEST_TOTEL];
  private static final String NV_VIBRATOR = "FactoryData4_Write";
  private static final String TAG = "AutoFinish";
  /** Called when the activity is first created. */

	private static final int    MMI_FLAG_OFFSET    = 153;
	private static final String GOZONE_TK_MSG_PATH =  "/data/tktestmsg.txt";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_finish);

    numText = (TextView)findViewById(R.id.finish_report_num);
    resText = (TextView)findViewById(R.id.finish_report_show);    
    numStr.append(String.valueOf(falTotle) + " "
          + getResources().getText(R.string.auto_num_hint).toString() + "\n");
    numText.setText(numStr);
    resText.setText(falText);

    //write = getIntent().getByteArrayExtra("AUTO_FINAL");
    //phone = PhoneFactory.getDefaultPhone();
    finBtn = (Button)findViewById(R.id.finish_fi);
    finBtn.setOnClickListener(this);
  }

  public void onClick(View v) {
	// TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.finish_fi:
		if(falTotle == 0){
			
			Log.i(TAG,"Test success, Write trace flag = A");
			SetTraceInfo(true);
			try {
		    	RandomAccessFile file = new RandomAccessFile(GOZONE_TK_MSG_PATH, "rw");
		    
		    	file.seek(MMI_FLAG_OFFSET);
		    
		    	file.write('A');
		    
		    	file.close();
	    	}catch(IOException e){			
	    		Log.i(TAG, "RandomAccessFile failed!");		
			}
		}
		else {
			
      Log.i(TAG,"Test failed, Write trace flag = F");
			SetTraceInfo(false);
			try {
		    	RandomAccessFile file = new RandomAccessFile(GOZONE_TK_MSG_PATH, "rw");
		    
		    	file.seek(MMI_FLAG_OFFSET);
		    
		    	file.write('F');
		    
		    	file.close();
	    	}catch(IOException e)
	    	{			
	    		Log.i(TAG, "RandomAccessFile failed!");		
			}
      	}
		Runtime runtime = Runtime.getRuntime();
		Process process ;
		try {
			process = runtime.exec("/system/etc/atx_test/tktramsgW.sh");
		}catch(IOException e)
		{
			Log.i(TAG, "runtime.exec failed!");
		}
      /*
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      Handler mHandler = new Handler();
      try {
        dos.writeBytes(NV_VIBRATOR);
        dos.writeByte(TEST_TOTEL);
        for(byte i = 0; i < TEST_TOTEL;i++){
          dos.writeByte(NV_OFFSET+i);
          dos.writeByte(write[i]);
        }
      }catch(IOException e){
        return;
      }
      phone.invokeOemRilRequestRaw(bos.toByteArray(), mHandler.obtainMessage());
      */
      finish();
      break;
    default:
      Log.e(TAG,"Error!");
    break;
    }
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

  
}
