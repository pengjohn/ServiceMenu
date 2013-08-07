/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to set SN.
===========================================================================*/

package com.android.ServiceMenu;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.servicemenu.CITTest;

import android.os.Handler;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.Phone;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import android.os.AsyncResult;


public class SetSN extends Activity {
  
  private Button snBtn;
  
  private Phone phone = null;
  private Handler mHandler;
  static final String NV_SETSN = "SetSN";
  
  private static final String TAG = "SetSN";
  /** Called when the activity is first created. */
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sn_preference);
    
    phone = PhoneFactory.getDefaultPhone();
    snBtn = (Button)findViewById(R.id.vibrationBtn);
    snBtn.setOnClickListener(new Button.OnClickListener(){
      public void onClick(View arg0) 
      {
          // TODO Auto-generated method stub
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          DataOutputStream dos = new DataOutputStream(bos);

          try {
		  dos.writeBytes(NV_SETSN);
          } catch (IOException e) {
               return;
          }

	    phone.invokeOemRilRequestRaw(bos.toByteArray(), mHandler.obtainMessage());
      }        	
    });

        mHandler = new Handler()
        {
		public void handleMessage(Message msg)
		{
			AsyncResult ar = (AsyncResult)msg.obj;
			if (null == ar.exception)
			{
				Toast.makeText(SetSN.this, "Succeed to set SN", Toast.LENGTH_LONG).show();
			}
		}
        };	
  }

}
