/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test compass.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MSensorTest extends AutoItemActivity  {
	
    private SensorManager mSensorManager;
    private SampleView mView;
    private float[] mValues;
    
    private final SensorListener mListener = new SensorListener() {
    
        public void onSensorChanged(int sensor, float[] values) {            
            mValues = values;
						Log.e(TAG,"Msensor, onSensorChanged:["+values[0]+"]["+values[1]+"]["+values[2]+"]");
            if (mView != null) {
                mView.invalidate();
            }
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
            // TODO Auto-generated method stub
            
        }
    };
    private static final int MENU_SUC = Menu.FIRST+1;
    private static final int MENU_FAL = Menu.FIRST+2;
	  private static final String TAG = "AutoMusic";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mView = new SampleView(this);
        setContentView(mView);

        
    }
    private class SampleView extends View {
        private Paint   mPaint = new Paint();
        private Path    mPath = new Path();
        private boolean mAnimate;
        private long    mNextTime;

        public SampleView(Context context) {
            super(context);

            // Construct a wedge-shaped path
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
        }
    
        protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;

            canvas.drawColor(Color.WHITE);
            
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.translate(cx, cy);
            if (mValues != null) {            
                canvas.rotate(-mValues[0]);
            }
            canvas.drawPath(mPath, mPaint);
            openOptionsMenu();
        }
    
        @Override
        protected void onAttachedToWindow() {
            mAnimate = true;
            super.onAttachedToWindow();
        }
        
        @Override
        protected void onDetachedFromWindow() {
            mAnimate = false;
            super.onDetachedFromWindow();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, MENU_SUC, 0, R.string.auto_exit);
    	//menu.add(0, MENU_FAL, 0, R.string.auto_fail);
    	
    	return true;
       
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_SUC:
            //openActivity(TEST_MSENSOR+1);
			      finish();
            return true;
        case MENU_FAL:
            //openFailActivity(TEST_MSENSOR);
			      finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(mListener, 
        		SensorManager.SENSOR_ORIENTATION,
        		SensorManager.SENSOR_DELAY_GAME);
    }
    
    @Override
    protected void onStop()
    {
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	switch(keyCode)
  	{
  		//disable the key
      case KeyEvent.KEYCODE_HOME:
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  public void onAttachedToWindow()
  {
  	//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
  	super.onAttachedToWindow();
  }     
}
