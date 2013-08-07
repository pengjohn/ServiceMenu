/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 * Gozone BID 10951 zhangjinguo:add NfcTester
 */

package com.android.ServiceMenu;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AutoNfc extends AutoItemActivity implements OnClickListener{
    private static final String TAG = "AutoNFC";
    private TextView mText;
    private Button falBtn;
    
    private int mCount = 0;
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.auto_nfc);
        mText = (TextView) findViewById(R.id.text);
        falBtn = (Button)findViewById(R.id.fail);
        falBtn.setOnClickListener(this);
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mAdapter == null) { 
            mText.setText(R.string.auto_nfc_not_support);
            
        return; 
        }
		
		    if (!mAdapter.isEnabled()){
			     mAdapter.enable();
		    }
		
		    mText.setText(R.string.auto_nfc_info);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all support tags
        mTechLists = new String[][] { new String[] { IsoDep.class.getName()},{Ndef.class.getName()},
        		{MifareClassic.class.getName()},{MifareUltralight.class.getName()},
        		{NfcA.class.getName()},{NfcB.class.getName()},{NfcF.class.getName()},
        		{NfcV.class.getName()}};
        
    }

    @Override
    public void onResume() {
       super.onResume();
	   if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
	   /*
       //detect ACTION_TECH_DISCOVERED
       if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())
    		   ||NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
    	   mText.setText("Discovered tag " + ++mCount + " with intent: ");
       }
       else{
    	   mText.setText("Scan resume a tag");
       }
       */
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        //mText.setText("Discovered tag ,Result succeed");
        //mText.setText("Discovered tag " + ++mCount + " with intent: "+intent);
        if(bFlagAutoTest)
           openActivity(getTestItemActivityIdByClass(this)+1);
        else
           setTestSuccessed(getTestItemActivityIdByClass(this));
        finish();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	if (mAdapter != null){
			//mAdapter.disable();
		  }
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.fail:
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
}
