/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test palette.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.LayoutInflater;

public class AutoPalette extends AutoItemActivity implements OnClickListener{
  
  private static final int DisplayColorCount = 5; //the number of the color to test
  private static final int DisplayColorTime = 2000;//the time of every color's show 
  private static final int DISPLAY_NEXT = 8;
  private static final String TAG = "AutoPalette";
  
  private int screen_state = 0;
  private PaletteView mView;
	private Button falBtn, sucBtn;  
  private static final int MENU_SUC = Menu.FIRST+1;
  private static final int MENU_FAL = Menu.FIRST+2;
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN	, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    LayoutInflater inflater = LayoutInflater.from(this);
    View view = inflater.inflate(R.layout.auto_palette, null);
    setContentView(view);
    
    mView = (PaletteView) view.findViewById(R.id.paletteView);

    sucBtn = (Button) view.findViewById(R.id.sucBtn);
    sucBtn.setOnClickListener(this);
    falBtn = (Button) view.findViewById(R.id.failBtn);
    falBtn.setOnClickListener(this);

    sucBtn.setVisibility(View.GONE);
    falBtn.setVisibility(View.GONE);
		          
    //mView = new PaletteView(this);   
    //setContentView(mView);
  }
  

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.sucBtn:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.failBtn:
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
      break;
    default:
      break;
    }
  }
    
  Handler myHandler = new Handler(){
      public void handleMessage(Message msg){
        switch(msg.what){
        case DISPLAY_NEXT:
            if(screen_state < DisplayColorCount - 1){
                screen_state++;
                Message msg2 = new Message();
                msg2.what = DISPLAY_NEXT;
                myHandler.sendMessageDelayed(msg2, DisplayColorTime); 
                if(mView != null)
                {   
                	  mView.screen_state = screen_state;
                    mView.invalidate();
                 }
            }else{
					    sucBtn.setVisibility(View.VISIBLE);
					    falBtn.setVisibility(View.VISIBLE);
            }
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
    msg.what = DISPLAY_NEXT;
    myHandler.sendMessageDelayed(msg, DisplayColorTime); 
  }
  @Override
  protected void onPause() {  
    myHandler.removeMessages(DISPLAY_NEXT);
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
