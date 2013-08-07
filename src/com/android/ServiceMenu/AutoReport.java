/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show report.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;


import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;
import java.io.RandomAccessFile;


public class AutoReport extends AutoItemActivity implements OnClickListener{
	
  private TextView numText,resText;
  private Button exitBtn;
  private StringBuffer numStr = new StringBuffer();
  private static final String TAG = "AutoReport";
  
	private static final int    MMI_FLAG_OFFSET    = 153;	
	private static final String GOZONE_TK_MSG_PATH =  "/data/tktestmsg.txt";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_report);
    
    numText = (TextView)findViewById(R.id.report_num);
    resText = (TextView)findViewById(R.id.report_show);
    exitBtn = (Button)findViewById(R.id.report_back);
    exitBtn.setOnClickListener(this);
    
    updateReportText();
    numStr.append(String.valueOf(falTotle) + " "
          + getResources().getText(R.string.auto_num_hint).toString() + "\n");
    numText.setText(numStr);
    resText.setText(falText);
    
    Log.i(TAG,"onCreate, test msg");	


	if(falTotle == 0) {						
		Log.i(TAG,"Test success, Write trace flag = A");		
		SetTraceInfo(true);				
		try {		    	
			RandomAccessFile file = new RandomAccessFile(GOZONE_TK_MSG_PATH, "rw");		    		    	
			file.seek(MMI_FLAG_OFFSET);		    		    	
			file.write('A');		    		    	
			file.close();	    	
		}catch(IOException e) {				    		
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
		}catch(IOException e){				    		
			Log.i(TAG, "RandomAccessFile failed!");					
		}      	
	}		
	Runtime runtime = Runtime.getRuntime();		
	Process process ;		
	try {			
		process = runtime.exec("/system/etc/atx_test/tktramsgW.sh");		
	}
	catch(IOException e) {			
		Log.i(TAG, "runtime.exec failed!");		
	}
    

  }
	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if(R.id.report_back == v.getId()){
      finish();
    }
  }
}
