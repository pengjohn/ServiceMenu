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

import android.app.AlertDialog;
import android.os.SystemProperties;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class ServiceMenuVersionReceiver extends BroadcastReceiver {

  public ServiceMenuVersionReceiver() {
		
  }
  @Override
  public void onReceive(Context context, Intent intent) {
    // TODO Auto-generated method stub
	  if (intent.getAction().equals(SECRET_CODE_ACTION)) 
    {
      Intent i = new Intent(Intent.ACTION_MAIN);
      i.setClass(context, VersionShow.class);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
      
      
      //String datetimeOld = "10:23:07 2013";//Fri Apr 19 10:23:07 2013
      //String datetimeOld = SystemProperties.get("ro.build.date","");
      //String datetimeNew;
			//SimpleDateFormat sdfold = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			/*
			SimpleDateFormat sdfold = new SimpleDateFormat("hh:mm:ss yyyy");
			SimpleDateFormat sdfnew = new SimpleDateFormat("yyyyMMdd_HHmmss");
			try
  		{
				Date date = sdfold.parse(datetimeOld);
				datetimeNew = sdfnew.format(date);

	  	  new AlertDialog.Builder(context)
	  	      .setTitle("SW version")
	  	      .setMessage(datetimeNew)
	  	      .setPositiveButton("OK", null)
	  	      .show();
			}
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }			
			*/
    }
  }
}
