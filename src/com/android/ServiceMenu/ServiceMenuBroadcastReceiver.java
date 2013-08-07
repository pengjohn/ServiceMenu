/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to receive the Message of CIT.

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


public class ServiceMenuBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = "ServiceMenuBroadcastReceiver";
  
  public ServiceMenuBroadcastReceiver() {
		
  }
  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
	  if (intent.getAction().equals(SECRET_CODE_ACTION)) 
    { 
    	Log.e(TAG,"SECRET_CODE_ACTION!");
      Intent i = new Intent(Intent.ACTION_MAIN);
      i.setClass(context, ServiceMenu.class);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }
  }
}
