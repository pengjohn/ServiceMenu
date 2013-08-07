/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to receive the Message of SN.

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


public class ServiceMenuSNReceiver extends BroadcastReceiver {

  public ServiceMenuSNReceiver() {
		
  }
  @Override
  public void onReceive(Context arg0, Intent arg1) {
    // TODO Auto-generated method stub
	  if (arg1.getAction().equals(SECRET_CODE_ACTION)) 
    {
      Intent i = new Intent(Intent.ACTION_MAIN);
      i.setClass(arg0, SetSN.class);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      arg0.startActivity(i);
    }
  }
}
