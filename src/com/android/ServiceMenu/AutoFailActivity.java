/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto show fail.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;
import java.lang.Runtime;
import java.lang.Process;
import java.io.IOException;
public class AutoFailActivity extends AutoItemActivity implements OnClickListener{
	
  private TextView failText,failId;
  private Button continueBtn,repBtn,retestBtn;
  private int currId;
  private static final String TAG = "AutoFailActivity";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_fail);
    
    currId = getIntent().getIntExtra("TEST_ID", 0);
    failText = (TextView)findViewById(R.id.fail_text);
    failId = (TextView)findViewById(R.id.fail_id);
    continueBtn = (Button)findViewById(R.id.fail_continue);
    repBtn = (Button)findViewById(R.id.fail_report);
    retestBtn = (Button)findViewById(R.id.fail_retest);
    failText.setText(nameBuf);
    failId.setText(idBuf);
    continueBtn.setOnClickListener(this);
    repBtn.setOnClickListener(this);
    retestBtn.setOnClickListener(this);
  }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.fail_continue:
      setTestFailed(currId);
      openActivity(currId+1);
      finish();
      break;
    case R.id.fail_retest:
      openActivity(currId);
      finish();
      break;
    case R.id.fail_report:
      Log.i(TAG,"Test fail, Write trace flag = 0");
	    Runtime runtime = Runtime.getRuntime();
			Process process ;
			try
			{
			  	process = runtime.exec("/system/etc/atx_test/testflagF.sh"); 
			}catch(IOException e)
		  {
		    Log.i(TAG, "runtime.exec failed!");
		  }
		          
      setTestFailed(currId);
      openReportActivity();
      finish();
    break;
    default:
      Log.e(TAG,"Error!");
    break;
    }
  }
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

  	switch(keyCode)
  	{
  		//disable the key
      case KeyEvent.KEYCODE_HOME:
      case KeyEvent.KEYCODE_BACK:
        return true;
      
      case KeyEvent.KEYCODE_MENU:
        openActivity(currId);
        finish();
        return true;
    }  	

    return super.onKeyDown(keyCode, event);
  }

  
}
