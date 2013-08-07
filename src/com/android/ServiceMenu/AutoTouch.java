/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test touch.

===========================================================================*/
package com.android.ServiceMenu;
import com.android.ServiceMenu.LockPatternView.Cell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.List;
import android.widget.Button;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
/**
 * If the user has a lock pattern set already, makes them confirm the existing one.
 *
 * Then, prompts the user to choose a lock pattern:
 * - prompts for initial pattern
 * - asks for confirmation / restart
 * - saves chosen password when confirmed
 */
public class AutoTouch extends AutoItemActivity implements OnClickListener{
    /**
     * Used by the choose lock pattern wizard to indicate the wizard is
     * finished, and each activity in the wizard should finish.
     * <p>
     * Previously, each activity in the wizard would finish itself after
     * starting the next activity. However, this leads to broken 'Back'
     * behavior. So, now an activity does not finish itself until it gets this
     * result.
     */
    static final int RESULT_FINISHED = RESULT_FIRST_USER;

    private static final String TAG = "TouchPanel";
    private Button falBtn;
    protected LockPatternView mLockPatternView;
    protected List<LockPatternView.Cell> mChosenPattern = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN	, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.auto_touch, null);
        setContentView(view);
        mLockPatternView = (LockPatternView) view.findViewById(R.id.lockPattern);
        mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);

        falBtn = (Button) view.findViewById(R.id.failBtn);
        falBtn.setOnClickListener(this);
    }

    protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() 
    {
        public void onPatternStart() 
        {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternCleared() 
        {
            mLockPatternView.removeCallbacks(mClearPatternRunnable);
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) 
        {
        }

        public void onPatternCellAdded(List<Cell> pattern) 
        {
        	mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern);
        	int size=mChosenPattern.size(); 
        	if(size == 9)
          {
             if(bFlagAutoTest)
                openActivity(getTestItemActivityIdByClass(AutoTouch.this)+1);
             else
                 setTestSuccessed(getTestItemActivityIdByClass(AutoTouch.this));
             finish(); 	
          }                	
        	//Log.i(TAG,"onPatternCellAdded size = "+size);                	
        }

        private void patternInProgress() 
        {
        }
     };

    private Runnable mClearPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };         

    public void onClick(View v) {
    if(R.id.failBtn == v.getId()){
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
    }		
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
