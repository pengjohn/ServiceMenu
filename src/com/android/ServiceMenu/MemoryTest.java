/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to test the memory.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import android.servicemenu.CITTest;

public class MemoryTest extends Activity implements OnClickListener {
	private EditText mEdit;
	private Button mBtn;
	private TextView mText,mSize,mMaker;
	//private CITTest mMemory; 
	public static long testSiz = 0;
	private byte [] buff = new byte[120];
	private static final String TAG = "MemoryTest";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memory_layout);
        
        mEdit = (EditText)findViewById(R.id.input_num);
        mBtn = (Button)findViewById(R.id.ddr_test);
        mText = (TextView)findViewById(R.id.test_show);
        mSize = (TextView)findViewById(R.id.test_size);
        mMaker = (TextView)findViewById(R.id.nand_maker);
        
        mBtn.setOnClickListener(this);
        mSize.setText(getString(R.string.memory_use) + String.valueOf(testSiz) + getString(R.string.memory_space));
    }
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch(id){
		case R.id.ddr_test:
			if(mEdit.getText().toString().length() == 0) {
				Toast.makeText(MemoryTest.this,R.string.ddr_hint,Toast.LENGTH_SHORT).show();

			} else {
				long  totle = Integer.parseInt(mEdit.getText().toString());
				long time = 0;  
				//long time = mMemory.onMemoryDDRTest(totle*1024*1024);
				if(time >= 0) {
					testSiz += totle;
					//Log.e(TAG,"the time is : " + mMemory.onGetMemoryTestTime());
					//Log.e(TAG,"the time is : " + String.valueOf(mMemory.onGetMemoryTestTime()));
					mText.setText(getString(R.string.suc_result) + String.valueOf(time) + getString(R.string.microsecond));
					//	mText.setText("The memory is good !\nIt costs " + String.valueOf(mMemory.onGetMemoryTestTime()) + " microseconds");
				} else {
					mText.setText(R.string.fal_result);
				}
				mSize.setText(getString(R.string.memory_use) + String.valueOf(testSiz) + getString(R.string.memory_space));
			}
			break;
			default:
				break;
		}
	}
	  @Override
  public void onResume() {
    super.onResume();
    //mMemory = new CITTest();
    //mMemory.CITTestInit();
    //add for nand maker
    /*
    if(0 == mMemory.onGetNandMaker(buff)){
      mMaker.setText(R.string.unknown);
    } else {
      String maker = new String(buff);
      mMaker.setText(maker);
    }
    */
  }
  @Override
  protected void onPause() {        
    //if(mMemory != null){       
    //  mMemory.CITTestFini();
    //  mMemory = null;
    //}
    super.onPause();
  }
}
