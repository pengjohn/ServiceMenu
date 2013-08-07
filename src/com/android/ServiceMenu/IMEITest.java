package com.android.ServiceMenu;


import com.android.qualcomm.qcnvitems.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;

public class IMEITest extends AutoItemActivity {

    private Button updateImeiButton;
    private Button readImeiButton;
    private EditText IMEIText;
    private TextView IMEITextView;
    
    private Button updateImei2Button;
    private Button readImei2Button;
    private EditText IMEI2Text;
    private TextView IMEI2TextView;
    
    private Button updateMeidButton;
    private Button readMeidButton;
    private EditText MEIDText;
    private TextView MEIDTextView;
    
    //ProgressDialog mPdialog;
    private Button updateImeiMeidBtn;
    private Handler mHandler = null;
    
    private boolean updateImeiSuccess = false;
    private boolean updateMeidSuccess = false;

    private QcNvItems mQcNvItems;
    private String mImeiStr = "";
    private String mImei2Str = "";
    private String mMeidStr = "";
    private static final String TAG = "IMEITest";
    
    
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);    
     
        setContentView(R.layout.imei_edit_view);
    	updateImeiButton = (Button)findViewById(R.id.update);
        readImeiButton = (Button)findViewById(R.id.read);
    	IMEIText =  (EditText) findViewById(R.id.IMEIEdit); 
    	IMEITextView =  (TextView) findViewById(R.id.IMEIView);
    	
    	updateImei2Button = (Button)findViewById(R.id.update2);
        readImei2Button = (Button)findViewById(R.id.read2);
    	IMEI2Text =  (EditText) findViewById(R.id.IMEI2Edit); 
    	IMEI2TextView =  (TextView) findViewById(R.id.IMEI2View);
    	
    	updateMeidButton = (Button)findViewById(R.id.meidupdate);
        readMeidButton = (Button)findViewById(R.id.meidread);
    	MEIDText =  (EditText) findViewById(R.id.MEIDEdit); 
    	MEIDTextView =  (TextView) findViewById(R.id.MEIDView);
    	updateImeiMeidBtn = (Button)findViewById(R.id.IMEI_MEID_update);
    	
    	updateImeiMeidBtn.setVisibility(View.GONE);    	
    	
    	mHandler = new Handler();
    	
//    	mPdialog = new ProgressDialog(this);
//    	mPdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//    	mPdialog.setMessage(getString(R.string.update_waiting_msg));
//    	mPdialog.setIndeterminate(false);
//    	mPdialog.setCancelable(false);
    	    	
    	mQcNvItems = new QcNvItems();
        try {
        		mImeiStr = mQcNvItems.getImei(0);

                if (!mImeiStr.isEmpty()) {
                    //IMEIText.setText(mImeiStr);
                    IMEITextView.setText(mImeiStr);
                    Toast.makeText(IMEITest.this, getString(R.string.getIMEI_success), Toast.LENGTH_SHORT)
            		.show();
                } else {
                    Toast.makeText(IMEITest.this, getString(R.string.getIMEI_fail), Toast.LENGTH_SHORT)
    	   			.show();
                }
            } catch (IOException e) {
                // TODO Show error notification window
                Toast.makeText(IMEITest.this, getString(R.string.getIMEI_fail), Toast.LENGTH_SHORT)
    	   			.show();
                Log.d(TAG, "Cannot read NV Settings");
            }
        
        try {
    		mImei2Str = mQcNvItems.getImei(1);

            if (!mImei2Str.isEmpty()) {
                //IMEIText.setText(mImeiStr);
                IMEI2TextView.setText(mImei2Str);
                Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_success), Toast.LENGTH_SHORT)
        		.show();
            } else {
                Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_fail), Toast.LENGTH_SHORT)
	   			.show();
            }
        } catch (IOException e) {
            // TODO Show error notification window
            Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_fail), Toast.LENGTH_SHORT)
	   			.show();
            Log.d(TAG, "Cannot read NV Settings");
        }
            
    	readImeiButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
                try {
                    mImeiStr = mQcNvItems.getImei(0);                	
                    Log.w(TAG, "mImeiStr = "+mImeiStr);
                    if (!mImeiStr.isEmpty()) {
                        //IMEIText.setText(mImeiStr);
                        IMEITextView.setText(mImeiStr);
                        Toast.makeText(IMEITest.this, getString(R.string.getIMEI_success), Toast.LENGTH_SHORT)
                		.show();
                    } else {
                        Toast.makeText(IMEITest.this, getString(R.string.getIMEI_fail), Toast.LENGTH_SHORT)
                    		.show();
                    }
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.getIMEI_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot read NV Settings");
                }        		
        		
        		return;
        	}	
        	});
    	
    	readImei2Button.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
                try {
                    mImei2Str = mQcNvItems.getImei(1);                	
                    Log.w(TAG, "mImei2Str = "+mImei2Str);
                    if (!mImei2Str.isEmpty()) {
                        //IMEIText.setText(mImeiStr);
                        IMEI2TextView.setText(mImei2Str);
                        Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_success), Toast.LENGTH_SHORT)
                		.show();
                    } else {
                        Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_fail), Toast.LENGTH_SHORT)
                    		.show();
                    }
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.getIMEI2_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot read NV Settings");
                }        		
        		
        		return;
        	}	
        	});
    	
    	updateImeiButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		try {
                    mImeiStr = IMEIText.getText().toString();
                   
                    if(mImeiStr.length() == 14){
                    	
                        mQcNvItems.setImei(mImeiStr,0);
                        Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_success), Toast.LENGTH_SHORT)
        	   			.show();                        
            		    
            		}else{
                		Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                }         		
        		
        		return;
        	}	
        	});
    	
    	updateImei2Button.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		try {
                    mImei2Str = IMEI2Text.getText().toString();
                   
                    if(mImei2Str.length() == 14){
                    	
                        mQcNvItems.setImei(mImei2Str,1);
                        Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_success), Toast.LENGTH_SHORT)
        	   			.show();                        
            		    
            		}else{
                		Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                }         		
        		
        		return;
        	}	
        	});
        //meid
        try {
        		mMeidStr = mQcNvItems.getMeid();

                if (!mMeidStr.isEmpty()) {                    
                    MEIDTextView.setText(mMeidStr);
                    Toast.makeText(IMEITest.this, getString(R.string.getMEID_success), Toast.LENGTH_SHORT)
            		.show();
                } else {
                    Toast.makeText(IMEITest.this, getString(R.string.getMEID_fail), Toast.LENGTH_SHORT)
    	   			.show();
                }
            } catch (IOException e) {
                // TODO Show error notification window
                Toast.makeText(IMEITest.this, getString(R.string.getMEID_fail), Toast.LENGTH_SHORT)
    	   			.show();
                Log.d(TAG, "Cannot read NV Settings");
            }
            
    	readMeidButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
                try {
                    mMeidStr = mQcNvItems.getMeid();                	
                    Log.w(TAG, "mMeidStr = "+mMeidStr);
                    if (!mMeidStr.isEmpty()) {
                        //IMEIText.setText(mImeiStr);
                        MEIDTextView.setText(mMeidStr);
                        Toast.makeText(IMEITest.this, getString(R.string.getMEID_success), Toast.LENGTH_SHORT)
                		.show();
                    } else {
                        Toast.makeText(IMEITest.this, getString(R.string.getMEID_fail), Toast.LENGTH_SHORT)
                    		.show();
                    }
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.getMEID_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot read NV Settings");
                }        		
        		
        		return;
        	}	
        	});
    	updateMeidButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		try {
                    mMeidStr = MEIDText.getText().toString();
                   
                    if(mMeidStr.length() == 14){
                    	
                        mQcNvItems.setMeid(mMeidStr);
                        Toast.makeText(IMEITest.this, getString(R.string.updateMEID_success), Toast.LENGTH_SHORT)
        	   			.show();                        
            		    
            		}else{
                		Toast.makeText(IMEITest.this, getString(R.string.updateMEID_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}
                } catch (IOException e) {
                    // TODO Show error notification window
                    Toast.makeText(IMEITest.this, getString(R.string.updateMEID_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                }         		
        		
        		return;
        	}	
        	});
    	
    	updateImeiMeidBtn.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v){
        		//mPdialog.show();
                //mHandler.postDelayed(mShowResultRunnable, 1000);
                
        		try {                  
                    mImeiStr = IMEIText.getText().toString();
                   
                    if(mImeiStr.length() == 14){                    	
                        mQcNvItems.setImei(mImeiStr,0);
                        Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_success), Toast.LENGTH_SHORT)
        	   			.show();
                        updateImeiSuccess = true;
            		}else{
            			updateImeiSuccess = false;
                		Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}                    
                } catch (IOException e) {
                    // TODO Show error notification window
                	updateImeiSuccess = false;
                    Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                }  
        		try {                  
                    mImei2Str = IMEI2Text.getText().toString();
                   
                    if(mImei2Str.length() == 14){                    	
                        mQcNvItems.setImei(mImei2Str,1);
                        Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_success), Toast.LENGTH_SHORT)
        	   			.show();                        
            		}else{            			
                		Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}                    
                } catch (IOException e) {
                    // TODO Show error notification window
                	updateImeiSuccess = false;
                    Toast.makeText(IMEITest.this, getString(R.string.updateIMEI2_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                }
        		//meid
        		try {
                    mMeidStr = MEIDText.getText().toString();
                   
                    if(mMeidStr.length() == 14){                    	
                        mQcNvItems.setMeid(mMeidStr);
                        Toast.makeText(IMEITest.this, getString(R.string.updateMEID_success), Toast.LENGTH_SHORT)
        	   			.show();
                        updateMeidSuccess = true;
            		}else{
            			updateMeidSuccess = false;
                		Toast.makeText(IMEITest.this, getString(R.string.updateMEID_fail), Toast.LENGTH_SHORT)
            	   			.show();	
            		}
                } catch (IOException e) {
                    // TODO Show error notification window
                	updateMeidSuccess = false;
                    Toast.makeText(IMEITest.this, getString(R.string.updateMEID_fail), Toast.LENGTH_SHORT)
                    		.show();
                    Log.d(TAG, "Cannot write NV Settings");
                } 
        		
//        		if(!updateImeiSuccess && !updateMeidSuccess){
//        			mPdialog.cancel();
//        		}
        		
        		return;
        	}	
        	});

    }
    
    private Runnable mShowResultRunnable = new Runnable() {
		public void run() {
			//mPdialog.cancel();
			if(updateImeiSuccess){
				Toast.makeText(IMEITest.this, getString(R.string.updateIMEI_success), Toast.LENGTH_SHORT)
	   			.show();
    		}
			if(updateMeidSuccess){
				Toast.makeText(IMEITest.this, getString(R.string.updateMEID_success), Toast.LENGTH_SHORT)
	   			.show();
    		}
		}
	};
        
    protected void onResume() {
        super.onResume();
    }
}
