/*===========================================================================

EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test version.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.graphics.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile; 


public class AutoTraceInfo extends AutoItemActivity implements OnClickListener {

	private Button sucBtn,falBtn;

	private TextView infoPT, infoFT, infoBT, infoWIFI, infoGPS;
	private static final String TAG = "AutoTraceInfo";

	private static final int TEST_FLAG_OFFSET_PT	= 147;
	private static final int TEST_FLAG_OFFSET_FT	= 148;
	private static final int TEST_FLAG_OFFSET_BT	= 149;
	private static final int TEST_FLAG_OFFSET_WIFI	= 150;
	private static final int TEST_FLAG_OFFSET_GPS	= 151;
	private static final int TEST_FLAG_OFFSET_ANT	= 152;
	private static final int TEST_FLAG_OFFSET_MMI	= 153;
	
	private static final String GOZONE_TK_MSG_PATH =  "/data/tktestmsg.txt";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.auto_trace_info);

	    infoPT   = (TextView)findViewById(R.id.trace_info_pt);
	    infoFT   = (TextView)findViewById(R.id.trace_info_ft);
	    infoBT   = (TextView)findViewById(R.id.trace_info_bt);
		infoWIFI = (TextView)findViewById(R.id.trace_info_wifi);
		infoGPS  = (TextView)findViewById(R.id.trace_info_gps);
		
	    sucBtn = (Button)findViewById(R.id.version_success);
	    falBtn = (Button)findViewById(R.id.version_fail);
	    
	    if(bFlagAutoTest)
	    {
	    	sucBtn.setText(R.string.test_start);
	    	falBtn.setText(R.string.auto_exit);
	    }
	    
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
	        case R.id.version_success:
	           if(bFlagAutoTest)
	              openActivity(getTestItemActivityIdByClass(this)+1);
	           else
	              setTestSuccessed(getTestItemActivityIdByClass(this));
	           finish();
	           break;
	        case R.id.version_fail:
	           if(bFlagAutoTest){
	              // openFailActivity(getTestItemActivityIdByClass(this));
	           }
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
	    Message msg = new Message();
	    msg.what = WAIT_INIT_EVENT;
	    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);

		getTraceInfo();
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
	    }
	    return super.onKeyDown(keyCode, event);
	}  

	public void onAttachedToWindow()
	{
	    //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	    super.onAttachedToWindow();
	} 

	
	private void  getTraceInfo(){

		try {
				int brRead;
				RandomAccessFile file = new RandomAccessFile(GOZONE_TK_MSG_PATH, "r");


				// PT
				file.seek(TEST_FLAG_OFFSET_PT);
				brRead = file.read();
				if(brRead == 'A'){
					infoPT.setText("  PT: " + "PASS");
					infoPT.setTextColor(android.graphics.Color.GREEN);
				}
				else if (brRead == 'F') {
					infoPT.setText("  PT: " + "NG");
					infoPT.setTextColor(android.graphics.Color.RED);
				}
				else {
					infoPT.setText("  PT: " + "UNKNOWN");
					infoPT.setTextColor(android.graphics.Color.GRAY);
				}

				Log.i(TAG, "getTraceInfo = "+brRead);	

				// FT
				file.seek(TEST_FLAG_OFFSET_FT);
				brRead = file.read();
				if(brRead == 'A'){
					infoFT.setText("  FT: " + "PASS");
					infoFT.setTextColor(android.graphics.Color.GREEN);
				}
				else if (brRead == 'F') {
					infoFT.setText("  FT: " + "NG");
					infoFT.setTextColor(android.graphics.Color.RED);
				}
				else {
					infoFT.setText("  FT: " + "UNKNOWN");
					infoFT.setTextColor(android.graphics.Color.GRAY);
				}
				Log.i(TAG, "getTraceInfo = "+brRead);	

				// BT
				file.seek(TEST_FLAG_OFFSET_BT);
				brRead = file.read();
				if(brRead == 'A'){
					infoBT.setText("  BT: " + "PASS");
					infoBT.setTextColor(android.graphics.Color.GREEN);
				}
				else if (brRead == 'F') {
					infoBT.setText("  BT: " + "NG");
					infoBT.setTextColor(android.graphics.Color.RED);
				}
				else {
					infoBT.setText("  BT: " + "UNKNOWN");
					infoBT.setTextColor(android.graphics.Color.GRAY);
				}
				Log.i(TAG, "getTraceInfo = "+brRead);	

				// WIFI
				file.seek(TEST_FLAG_OFFSET_WIFI);
				brRead = file.read();
				if(brRead == 'A'){
					infoWIFI.setText("  WIFI: " + "PASS");
					infoWIFI.setTextColor(android.graphics.Color.GREEN);
				}
				else if (brRead == 'F') {
					infoWIFI.setText("  WIFI: " + "NG");
					infoWIFI.setTextColor(android.graphics.Color.RED);
				}
				else {
					infoWIFI.setText("  WIFI: " + "UNKNOWN");
					infoWIFI.setTextColor(android.graphics.Color.GRAY);
				}
				Log.i(TAG, "getTraceInfo = "+brRead);	

				// GPS
				file.seek(TEST_FLAG_OFFSET_GPS);
				brRead = file.read();
				if(brRead == 'A'){
					infoGPS.setText("  GPS: " + "PASS");
					infoGPS.setTextColor(android.graphics.Color.GREEN);
				}
				else if (brRead == 'F') {
					infoGPS.setText("  GPS: " + "NG");
					infoGPS.setTextColor(android.graphics.Color.RED);
				}
				else {
					infoGPS.setText("  GPS: " + "UNKNOWN");
					infoGPS.setTextColor(android.graphics.Color.GRAY);
				}
				Log.i(TAG, "getTraceInfo = "+brRead);	
		    
				file.close();
	    	}catch(IOException e){		
	    		Log.i(TAG, "RandomAccessFile failed!");		
			}
	    
	}
}
