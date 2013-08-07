/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show auto.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.content.Context;

public class Auto extends AutoItemActivity implements OnClickListener {
  private Button startBtn,reportBtn;
  private static final String TAG = "Auto";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bFlagAutoTest = true;
    bWaitInitTime = true;
    initTestFailed();

    setContentView(R.layout.auto);       
    startBtn = (Button)findViewById(R.id.auto_start);
    reportBtn = (Button)findViewById(R.id.auto_report);
    startBtn.setOnClickListener(this);
    reportBtn.setOnClickListener(this);
  }

  public void onClick(View arg0) {
    // TODO Auto-generated method stub
    int id = arg0.getId();
    switch(id){
    case R.id.auto_start:
      openActivity(TEST_START);
      finish();
      break;
    case R.id.auto_report:
      openReportActivity();
      finish();
      break;
    default:
      Log.e(TAG,"ERROR");
      break;			
    }		
  }

@Override
public void onResume() {
    super.onResume();
      openActivity(TEST_START);
      finish();
}

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	switch(keyCode)
  	{
  		//disable the key
      case KeyEvent.KEYCODE_HOME:
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }  

}
