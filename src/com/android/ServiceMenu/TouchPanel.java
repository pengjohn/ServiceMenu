/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to test the touch panel.

===========================================================================*/

package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import android.content.Intent;
import android.view.KeyEvent;
import com.android.internal.widget.PointerLocationView;

public class TouchPanel extends Activity {

    private int TOUCH_PANEL_BROKEN_POINT = -1; /*use -1 to mark the key action up */
    private int TOUCH_PANEL_POINT = 1; /*use 1 to mark the key action down or move*/

    ArrayList<Point> pointList = new ArrayList<Point>();/*pointList: save all the point coordinate*/

    View myView = null; /**/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = new MyView(this);
       /*Original Code
        setContentView(myView);
       End*/
       //Add for TP test 
       setContentView(new PointerLocationView(this));
       //End

    }

    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pointList.add(new Point((int)event.getX(), (int)event.getY()));
                myView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pointList.add(new Point(TOUCH_PANEL_BROKEN_POINT,TOUCH_PANEL_BROKEN_POINT));
                myView.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                pointList.add(new Point((int)event.getX(), (int)event.getY()));
                myView.invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    class MyView extends View {
        protected void onDraw(Canvas canvas) {
            Paint mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);

            float[] xList = new float[pointList.size()];
            float[] yList = new float[pointList.size()];

            for (int i = 0; i < pointList.size(); i++) {
                xList[i] = pointList.get(i).x;
                yList[i] = pointList.get(i).y;

                if (i >= TOUCH_PANEL_POINT) {
                    if ((xList[i - 1] < 0) || (xList[i] < 0)) {
                        continue;
                    }
                    canvas.drawLine(xList[i - 1], yList[i - 1], xList[i], yList[i], mPaint);
                }
            }
            super.onDraw(canvas);
        }

        public MyView(Context context) {
            super(context);
        }
    }
    
    @Override
    protected void onPause() {
    	pointList.clear();
    	super.onPause();
    }

     @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // TODO Auto-generated method stub
    if(keyCode == KeyEvent.KEYCODE_MENU) {
    	return true;
    }
    return super.onKeyDown(keyCode, event);
  }

}
