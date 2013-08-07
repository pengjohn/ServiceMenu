/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to receive the Message of CIT Debug.

===========================================================================*/

package com.android.ServiceMenu;

import android.provider.Telephony;
import static android.provider.Telephony.Intents.SECRET_CODE_ACTION;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.util.Config;
import android.util.Log;
import android.view.KeyEvent;

import android.os.SystemProperties;

public class ServiceMenuDebugReceiver extends BroadcastReceiver {

  private static final String TAG = "ServiceMenuDebugReceiver";

  public ServiceMenuDebugReceiver() {
		
  }
  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
	  if (intent.getAction().equals(SECRET_CODE_ACTION)) 
    {
      Intent i = new Intent(Intent.ACTION_MAIN);
      i.setClass(context, ForDebug.class);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
    else if(intent.getAction().equals("com.qualcomm.otg.open")) 
    {
    	Log.i(TAG, "receive com.qualcomm.otg.open");
    	SystemProperties.set("ctl.start", "otg_open");
    }
    else if(intent.getAction().equals("com.qualcomm.otg.close")) 
    {
    	Log.i(TAG, "receive com.qualcomm.otg.close");
    	SystemProperties.set("ctl.start", "otg_close");
    }
    
  }
}
