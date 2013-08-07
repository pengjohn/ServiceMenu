/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test version.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoVersion extends AutoItemActivity implements OnClickListener {

    private Button sucBtn,falBtn;
    private TextView modText,firText,basText,kerText,budText;
    private static final String TAG = "AutoVersion";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_version);
        
        modText = (TextView)findViewById(R.id.auto_mod);
        firText = (TextView)findViewById(R.id.auto_fir);
        basText = (TextView)findViewById(R.id.auto_bas);
        kerText = (TextView)findViewById(R.id.auto_ker);
        budText = (TextView)findViewById(R.id.auto_bud);        
        sucBtn = (Button)findViewById(R.id.version_success);
        falBtn = (Button)findViewById(R.id.version_fail);
        
        modText.setText(Build.MODEL);
        firText.setText(Build.VERSION.RELEASE);
        basText.setText(SystemProperties.get("persist.sys.mpversion",getResources().getString(R.string.device_info_default)));

        kerText.setText(getFormattedKernelVersion());
        budText.setText(Build.DISPLAY);
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
        sucBtn.setEnabled(true);
        falBtn.setEnabled(true);        
        break;    			
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };
	private String getFormattedKernelVersion() {
    String procVersionStr;

    try {
      BufferedReader reader = new BufferedReader(new FileReader("/proc/version"), 256);
      try {
        procVersionStr = reader.readLine();
      } finally {
        reader.close();
      }

      final String PROC_VERSION_REGEX =
                "\\w+\\s+" + /* ignore: Linux */
                "\\w+\\s+" + /* ignore: version */
                "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                "\\([^)]+\\)\\s+" + /* ignore: (gcc ..) */
                "([^\\s]+)\\s+" + /* group 3: #26 */
                "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                "(.+)"; /* group 4: date */

      Pattern p = Pattern.compile(PROC_VERSION_REGEX);
      Matcher m = p.matcher(procVersionStr);

      if (!m.matches()) 
      {
        Log.e(TAG, "Regex did not match on /proc/version: " + procVersionStr);
        return "Unavailable";
      } else if (m.groupCount() < 4) {
        Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                        + " groups");
        return "Unavailable";
      } else {
        /* Original code
        return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
        */
        //only display group 1]
        return (new StringBuilder(m.group(1)).toString());
      }
    } catch (IOException e) {  
      Log.e(TAG,"IO Exception when getting kernel version for Device Info screen",
                e);

      return "Unavailable";
    }
  }
  @Override
  public void onResume() {
    super.onResume();
    Message msg = new Message();
    msg.what = WAIT_INIT_EVENT;
    myHandler.sendMessageDelayed(msg, WAIT_INIT_TIME);
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


}
