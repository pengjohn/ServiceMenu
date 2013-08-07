/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to open wait.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;

public class AutoWaitActivity extends AutoItemActivity implements OnClickListener{
	
    private TextView waitText,waitId;
    private Button sucBtn,falBtn;
    private int currId;
    private static final String TAG = "AutoWaitActivity";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_wait);
        
        currId = getIntent().getIntExtra("TEST_ID", 0);
        waitText = (TextView)findViewById(R.id.wait_text);
        waitId = (TextView)findViewById(R.id.wait_id);
        sucBtn = (Button)findViewById(R.id.wait_success);
        falBtn = (Button)findViewById(R.id.wait_fail);
        waitText.setText(nameBuf);
        waitId.setText(idBuf);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
    }
	
  public void onClick(View arg0) {
    // TODO Auto-generated method stub
    int id = arg0.getId();
    switch(id){
    case R.id.wait_success:
      openActivity(currId+1);
      finish();
      break;
    case R.id.wait_fail:
      openFailActivity(currId);
      finish();
      break;
    default:
      Log.e(TAG,"Error!");
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
