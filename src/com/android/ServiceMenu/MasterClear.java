/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ServiceMenu;

import com.android.internal.os.storage.ExternalStorageFormatter;
import com.android.internal.widget.LockPatternUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.os.SystemProperties;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.lang.Runtime;
import java.lang.Process;
import android.util.Log;
import android.provider.Settings;

/**
 * Confirm and execute a reset of the device to a clean "just out of the box"
 * state.  Multiple confirmations are required: first, a general "are you sure
 * you want to do this?" prompt, followed by a keyguard pattern trace if the user
 * has defined one, followed by a final strongly-worded "THIS WILL ERASE EVERYTHING
 * ON THE PHONE" prompt.  If at any time the phone is allowed to go to sleep, is
 * locked, et cetera, then the confirmation sequence is abandoned.
 *
 * This is the confirmation screen.
 */
public class MasterClear extends AutoItemActivity {

    private Button mFinalButton;
    private static final String TAG = "MasterClear";
    /**
     * The user has gone through the multiple confirmation, so now we go ahead
     * and invoke the Checkin Service to reset the device to its factory-default
     * state (rebooting in the process).
     */
    private Button.OnClickListener mFinalClickListener = new Button.OnClickListener() {

        public void onClick(View v) {
        	  
        	
            //sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
           Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
           intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
           //intent.putExtra(ExternalStorageFormatter.EXTRA_ERASE_USBSTORAGE, true);
           startService(intent);
           
		   /*
           //new master reset
           Log.i(TAG, "set Settings.Secure.DEVICE_PROVISIONED=0!");
           Settings.Secure.putInt(getContentResolver(), Settings.Secure.DEVICE_PROVISIONED, 0);
           Log.i(TAG, "start master_reset serives!");
           SystemProperties.set("ctl.start", "master_reset");
           */
           
           final AlertDialog.Builder b = new AlertDialog.Builder(MasterClear.this);
           b.setTitle(R.string.master_clear_title);
           b.setIconAttribute(android.R.attr.alertDialogIcon);
           b.setMessage(R.string.master_clear_info);
           AlertDialog mdialog  = b.create();
           mdialog.setCanceledOnTouchOutside(false);
           mdialog.show();


        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_clear);
        mFinalButton = (Button) findViewById(R.id.execute_master_clear);
        mFinalButton.setOnClickListener(mFinalClickListener);
    }
}
