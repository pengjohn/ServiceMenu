/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to make the NV test.

===========================================================================*/

package com.android.ServiceMenu;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.Phone;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import android.os.AsyncResult;

public class NVTest extends Activity implements OnClickListener {
  private TextView statusShow;
  private Button backupBtn,recoverBtn,earseBtn;
  private Handler mHandler;
  private final int BACKUP_SUCCESS = 1;
  private final int BACKUP_FAIL = 2;
  private final int RECOVER_SUCCESS = 3;
  private final int RECOVER_FAIL = 4;
  private final int EARSE_SUCCESS = 5;
  private final int EARSE_FAIL = 6;
  private final static String TAG = "NVTest";
	private Phone phone = null;
	static final String NV_BACKUP	= "NVTestbackup";
	static final String NV_RECOVER	= "NVTestrecover";
	static final String NV_EARSE	= "NVTestearse";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.nv_layout);
	phone = PhoneFactory.getDefaultPhone();
    statusShow = (TextView)findViewById(R.id.NV_status);
    backupBtn = (Button)findViewById(R.id.NV_backup);
    recoverBtn = (Button)findViewById(R.id.NV_resume);
    earseBtn = (Button)findViewById(R.id.NV_earse);
        
    backupBtn.setOnClickListener(this);
    recoverBtn.setOnClickListener(this);
    earseBtn.setOnClickListener(this);
        
    mHandler = new Handler(){
      public	void handleMessage(Message msg){
/* Original code
        switch(msg.what){
*/
	  	AsyncResult ar = (AsyncResult)msg.obj;
/* Original code
		byte bResult = ((byte[])ar.result)[0];
*/
		byte bResult = 0;
		if (ar.exception == null)
		{
			bResult = ((byte[])ar.result)[0];
		}
        switch(bResult){
          case BACKUP_SUCCESS:
            statusShow.setText(R.string.backup_suc);        			
            break;
          case BACKUP_FAIL:
            statusShow.setText(R.string.backup_fail);        			
            break;
          case RECOVER_SUCCESS:
            statusShow.setText(R.string.resume_suc);        			
            break;
          case RECOVER_FAIL:
            statusShow.setText(R.string.resume_fail);        			
            break;
          case EARSE_SUCCESS:
            statusShow.setText(R.string.earse_suc);        			
            break;
          case EARSE_FAIL:
            statusShow.setText(R.string.earse_fail);        			
            break;
          default:
		  	statusShow.setText("failed");
            break;
        }
        backupBtn.setEnabled(true);
        recoverBtn.setEnabled(true);
        earseBtn.setEnabled(true);
      }
    };
  }
  public void onClick(View arg0) {
    // TODO Auto-generated method stub
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        String strTag = null;

    final Message msg = new Message();
    final boolean result = false;
    backupBtn.setEnabled(false);
    recoverBtn.setEnabled(false);
    earseBtn.setEnabled(false);
    switch(arg0.getId()){
      case R.id.NV_backup:
        statusShow.setText(R.string.backup_ing);
/* Original code
        new Thread(){
          public void run()
          {	        		
            try {	        			
              sleep(2000);
						
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if(result)
              msg.what = BACKUP_SUCCESS;
            else msg.what = BACKUP_FAIL;
            NVTest.this.mHandler.sendMessage(msg);					
          }
        }.start();
*/
	strTag = NV_BACKUP;
        break;
      case R.id.NV_resume:
        statusShow.setText(R.string.resume_ing);
/* Original code
        new Thread(){
          public void run()
          {	        		
            try {	        			
              sleep(2000);
						
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if(result)
              msg.what = RECOVER_SUCCESS;
            else msg.what = RECOVER_FAIL;
            NVTest.this.mHandler.sendMessage(msg);					
          }
        }.start();
*/
	strTag = NV_RECOVER;
        break;
      case R.id.NV_earse:
        statusShow.setText(R.string.earse_ing);
/* Original code
        new Thread(){
          public void run()
          {	        		
            try {	        			
              sleep(2000);
						
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if(result)
              msg.what = EARSE_SUCCESS;
            else msg.what = EARSE_FAIL;
            NVTest.this.mHandler.sendMessage(msg);					
          }
        }.start();
*/
	strTag = NV_EARSE;
        break;
      default:
        statusShow.setText(R.string.nv_errror);
        break;
				
    }
	try {
		dos.writeBytes(strTag);
	} catch (IOException e) {
		return;
	}
	phone.invokeOemRilRequestRaw(bos.toByteArray(), mHandler.obtainMessage());

  }
}
