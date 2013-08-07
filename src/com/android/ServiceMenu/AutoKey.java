/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110530  PengZhiXiong   Initial to auto test key.

===========================================================================*/
package com.android.ServiceMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;

public class AutoKey extends AutoItemActivity implements OnClickListener{

  private TextView keyText;
  private Button falBtn;
  private StringBuffer resText = new StringBuffer();
  private boolean bUp,bDn,bBc,bMen,bLef,bCap,bCaf;

  private int []nkeyCode;
  private int []nkeyResId;
  private boolean []bPressFlag;
  private int nKeyNum = 0;
  private static final int MAX_KEY_NUM = 50;

  private static final String TAG = "AutoKey";
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.auto_key);
    
    keyText = (TextView)findViewById(R.id.key_text);
    falBtn = (Button)findViewById(R.id.key_fail);
    falBtn.setOnClickListener(this);

    nkeyCode = new int[MAX_KEY_NUM];
    nkeyResId = new int[MAX_KEY_NUM];
    bPressFlag = new boolean[MAX_KEY_NUM];
    nKeyNum = 0;
    
    InsetKeyItem(KeyEvent.KEYCODE_VOLUME_UP);
    InsetKeyItem(KeyEvent.KEYCODE_VOLUME_DOWN);

    InsetKeyItem(KeyEvent.KEYCODE_MENU);
    //InsetKeyItem(KeyEvent.KEYCODE_HOME);
    InsetKeyItem(KeyEvent.KEYCODE_BACK);
    //InsetKeyItem(KeyEvent.KEYCODE_SEARCH);
        
    updateText();
  }

  private void InsetKeyItem(int nCode)
  {
     if(nKeyNum >= MAX_KEY_NUM)
				return;

     nkeyCode[nKeyNum] = nCode;
     nkeyResId[nKeyNum] = getKeyResID(nCode);
     bPressFlag[nKeyNum] = false;
     nKeyNum ++;
  }
  
  public void onClick(View v) {
    // TODO Auto-generated method stub
    if(R.id.key_fail == v.getId()){
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
    }		
  }
  public boolean onKeyDown(int keyCode, KeyEvent event) {
	for(int i=0; i<nKeyNum; i++)
	{
		if(keyCode == nkeyCode[i])
		{
			bPressFlag[i] = true;
			break;
		}
	}
    updateText();
    return true;	
  }

  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return true;	
  }
    
  private void updateText(){
    resText.delete(0, resText.length());
    for(int i=0; i<nKeyNum; i++)
    {
			if(bPressFlag[i] == false)
			{
	    	resText.append("[" + getResources().getString(nkeyResId[i]) + "] \n");
			}
    }
    
    keyText.setText(resText);
    if(resText.length() == 0){
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
    }
  }

  private int getKeyResID(int nKeyCode)
  {
  	int nResID = 0;
  	switch(nKeyCode)
  	{
  	    case KeyEvent.KEYCODE_VOLUME_UP:
  	       nResID = R.string.auto_key_vup;
  	       break;
  	    case KeyEvent.KEYCODE_VOLUME_DOWN:
  	       nResID = R.string.auto_key_vdn;
  	       break;
  	    case KeyEvent.KEYCODE_MENU:
  	       nResID = R.string.auto_key_menu;
  	       break;
  	    case KeyEvent.KEYCODE_HOME:
  	       nResID = R.string.auto_key_home;
  	       break;
  	    case KeyEvent.KEYCODE_BACK:
  	       nResID = R.string.auto_key_back;
  	       break;
  	    case KeyEvent.KEYCODE_SEARCH:
  	       nResID = R.string.auto_key_search;
  	       break;
  	    default:
  	       break;
    }
    return nResID;
  }  
}
