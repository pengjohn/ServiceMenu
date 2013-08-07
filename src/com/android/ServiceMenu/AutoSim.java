/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test sim.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.MSimTelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.WindowManager;

public class AutoSim extends AutoItemActivity implements OnClickListener {

    private Button sucBtn,falBtn;
    private boolean bSim1Insert = false;
    private boolean bSim2Insert = false;
    private TextView simText;
    private TextView sim2Text;
    private boolean bDualSIM = true;
    private static final String TAG = "AutoSim";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_sim);
      
        simText = (TextView)findViewById(R.id.auto_sim_text);
        sim2Text = (TextView)findViewById(R.id.auto_sim2_text);
        sucBtn = (Button)findViewById(R.id.sim_success);
        falBtn = (Button)findViewById(R.id.sim_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);

        if(bWaitInitTime)
        {
          sucBtn.setEnabled(false);
          falBtn.setEnabled(false);
        }    

        //for DualSIM
/*
        if(bDualSIM)
        {
            int simState = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE1)).getSimState();
            Log.e(TAG,"TELEPHONY_SERVICE1.getSimState():"+ simState);
            if(simState == TelephonyManager.SIM_STATE_READY)
            {
               simText.setText(R.string.auto_sim1_found);
               bSim1Insert = true;
            } 
            else 
            {
               simText.setText(R.string.auto_sim1_nofound);
               bSim1Insert = false;
          	}

            int sim2State = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE2)).getSimState();
            Log.e(TAG,"TELEPHONY_SERVICE2.getSimState():"+ sim2State);
            if(sim2State == TelephonyManager.SIM_STATE_READY)
            {
               sim2Text.setText(R.string.auto_sim2_found);
               bSim2Insert = true;
            }
            else 
            {
               sim2Text.setText(R.string.auto_sim2_nofound);
               bSim2Insert = false;
       	    }
        }
        //for SingleSIM
        else
*/      
        if(MSimTelephonyManager.getDefault().isMultiSimEnabled())
        {
            int sim1State = MSimTelephonyManager.getDefault().getSimState(0);
            Log.e(TAG,"TELEPHONY_SERVICE.getSimState(0):"+ sim1State);
            if(sim1State == TelephonyManager.SIM_STATE_READY)
            {
                simText.setText(R.string.auto_sim1_found);
                bSim1Insert = true;
            } 
            else 
            {
                simText.setText(R.string.auto_sim1_nofound);
                bSim1Insert = false;
            }
            
            int sim2State = MSimTelephonyManager.getDefault().getSimState(1);
            Log.e(TAG,"TELEPHONY_SERVICE.getSimState(1):"+ sim2State);
            if(sim2State == TelephonyManager.SIM_STATE_READY)
            {
                sim2Text.setText(R.string.auto_sim2_found);
                bSim2Insert = true;
            } 
            else 
            {
                sim2Text.setText(R.string.auto_sim2_nofound);
                bSim2Insert = false;
            }
        }
        else
        {
            int simState = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getSimState();
            Log.e(TAG,"TELEPHONY_SERVICE.getSimState():"+ simState);
            if(simState == TelephonyManager.SIM_STATE_READY)
            {
                simText.setText(R.string.auto_sim_found);
                bSim1Insert = true;
            } 
            else 
            {
                simText.setText(R.string.auto_sim_nofound);
                bSim1Insert = false;
            }
            
            bSim2Insert = true;
        }            
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.sim_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.sim_fail:
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
        if(bSim1Insert && bSim2Insert)
        {
            sucBtn.setEnabled(true);
        }
        falBtn.setEnabled(true);        
        break;    			
      default:
        break;
      }
      super.handleMessage(msg);
    }
  };

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
