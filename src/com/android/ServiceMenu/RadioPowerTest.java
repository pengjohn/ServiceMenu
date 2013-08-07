/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to turn on/off the RF.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import android.telephony.ServiceState;

public class RadioPowerTest extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String KEY_TOGGLE_RF = "toggle_RF";

    
    private CheckBoxPreference  mRFPreference;

  private Phone phone;

  private static final String TAG = "ElectricityTest";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.radio_preference);
        mRFPreference = (CheckBoxPreference) findPreference(KEY_TOGGLE_RF);
        mRFPreference.setOnPreferenceChangeListener(this);
        phone = PhoneFactory.getDefaultPhone();		
    }

	public boolean onPreferenceChange(Preference preference, Object newValue) {
    // TODO Auto-generated method stub
    if(preference == mRFPreference){
      if((Boolean)newValue == true){
        phone.setRadioPower(true);
      } else {
        phone.setRadioPower(false);
      }
    }  
    return true;
	}
   private void initToggleState(){
	   if(phone. getServiceState().getState() == ServiceState.STATE_POWER_OFF){
           mRFPreference.setChecked(false);
       } else {
           mRFPreference.setChecked(true);
       	}
   }
  @Override
  public void onResume() {
    super.onResume();	
    initToggleState();

  }

}
