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

import android.util.Log;
import android.view.View;
import android.util.AttributeSet;

    public class MSensorView extends View {
        private Paint   mPaint = new Paint();
        private Path    mPath = new Path();
        private boolean mAnimate;
        private long    mNextTime;
				private float mValues;
				
        public MSensorView(Context context) {
            this(context, null);

            // Construct a wedge-shaped path
            
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
            
            mValues = 0.0f;
        }
    
    		public MSensorView(Context context, AttributeSet attrs) 
    		{
        		super(context, attrs);
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close(); 
            
            mValues = 0.0f;       		
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
            canvas.rotate(-mValues);
            canvas.drawPath(mPath, mPaint);

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
        
        public void setValue(float Values)
        {
        	mValues = Values;
        }
    }

