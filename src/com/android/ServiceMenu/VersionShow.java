/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to show the version.

===========================================================================*/

package com.android.ServiceMenu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.PreferenceActivity;
import android.util.Log;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.os.SystemProperties;
import java.text.SimpleDateFormat;
import java.util.Date;


public class VersionShow extends AutoItemActivity implements OnClickListener{
  /** Called when the activity is first created. */
  private static final String TAG = "versionShow";
  private TextView versionText;
  private Button sucBtn;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.version);
	
		String datetimeUTC = SystemProperties.get("ro.build.date.utc","");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String datetime = sdf.format(new Date((Long.parseLong(datetimeUTC)+8*60*60)*1000));
		
		versionText = (TextView)findViewById(R.id.versionText);
		versionText.setText(datetime);
		
		sucBtn = (Button)findViewById(R.id.bt_success);
		sucBtn.setOnClickListener(this);
  }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.bt_success:
      finish();
      break;
    default:
      Log.e(TAG,"Error!");
      break;
    }
  }    
}
