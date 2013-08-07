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
import android.util.AttributeSet;

  public class PaletteView extends View {
  	  public int screen_state = 0;
      private Paint   mPaint = new Paint();

      public PaletteView(Context context) {
          super(context);

          // Construct a wedge-shaped path
      }
  
  		public PaletteView(Context context, AttributeSet attrs) 
		{
    		super(context, attrs);   		
  	}  
      	
      protected void onDraw(Canvas canvas) {
          switch(screen_state){
              case 0:
                  drawFirstScreen(canvas);
                  break;
              case 1:
                  drawSecondScreen(canvas);
                  break;
              case 2:
                  drawThirdScreen(canvas);
                  break;
              case 3:
                  drawFourthScreen(canvas);
                  break;
              case 4:
                  drawFifthScreen(canvas);
                  break;
              default:
                  break;
          }
      }
      private void drawFirstScreen(Canvas canvas){
          Paint paint = mPaint;
          paint.setAntiAlias(true);
          paint.setColor(Color.RED);
          paint.setStyle(Paint.Style.FILL);
        
          int w = canvas.getWidth();
          int h = canvas.getHeight();
          
          canvas.drawRect(new Rect(0, 0, w, h/3), paint);
          paint.setColor(Color.BLACK);
          paint.setTextSize(48);
          canvas.drawText("LCD", w/2-10, h/6, paint);
          
          paint.setColor(Color.GREEN);
          canvas.drawRect(new Rect(0,h/3,w,2*h/3), paint);
          
          paint.setColor(Color.BLUE);
          canvas.drawRect(new Rect(0,2*h/3,w,h), paint);
      }
      
      private void drawSecondScreen(Canvas canvas){
          Paint paint = mPaint;
          paint.setAntiAlias(true);
          paint.setColor(Color.WHITE);
          paint.setStyle(Paint.Style.FILL);
          
          int w = canvas.getWidth();
          int h = canvas.getHeight();
          
          canvas.drawRect(new Rect(0,0,w,h), paint);
      }
      private void drawThirdScreen(Canvas canvas){
          Paint paint = mPaint;
          
          canvas.drawColor(Color.WHITE);
          
          paint.setAntiAlias(true);
          paint.setColor(Color.BLACK);
          paint.setStyle(Paint.Style.FILL);
          
          int w = canvas.getWidth();
          int h = canvas.getHeight();
          if(w < h)
              w = h;
          else
              h = w;
          int d = 4;
          for(int i=0;i<=2*w;i+=d)
          {
              canvas.drawLine(0, i, i, 0, paint);
          }
//          for(int i=0;i<=2*w;i+=d)
//          {
//              canvas.drawLine(i, h, w, i, paint);
//          }
          for(int i=0;i<=2*w;i+=d)
          {
              canvas.drawLine(0, h-i, i, h, paint);
          }
//          for(int i=0;i<=w;i+=d)
//          {
//              canvas.drawLine(i, 0, w, h-i, paint);
//          }
      }
      private void drawFourthScreen(Canvas canvas){
          Paint paint = mPaint;
          
          canvas.drawColor(Color.WHITE);
          
          paint.setAntiAlias(true);
          paint.setColor(Color.BLACK);
          paint.setStyle(Paint.Style.FILL);
          
          int w = canvas.getWidth();
          int h = canvas.getHeight();
          
          int num = 16;
		  int max_num = num + ((w%num == 0)?0:1);
          int startColor = 0x000000;
          int endColor = 0xffffff;
          int r1,r2,g1,g2,b1,b2;
          r1 = (startColor >> 16)&0xff;
          r2 = (endColor >> 16)&0xff;
          g1 = (startColor >> 8)&0xff;
          g2 = (endColor >> 8)&0xff;
          b1 = (startColor)&0xff;
          b2 = (endColor )&0xff;
          int r,g,b;
          for(int i=0;i<max_num;i++){
              r = r1 + ((r2-r1)/num)*i;
              g = g1 + ((g2-g1)/num)*i;
              b = b1 + ((b2-b1)/num)*i;
              paint.setARGB(0xff, r, g, b);
              canvas.drawRect(new Rect(i*(w/num),0,(i+1)*(w/num)+5,h), paint);
          }
      }
      private void drawFifthScreen(Canvas canvas){
          Paint paint = mPaint;
          paint.setAntiAlias(true);
          paint.setColor(Color.BLACK);
          paint.setStyle(Paint.Style.FILL);
          
          int w = canvas.getWidth();
          int h = canvas.getHeight();
          
          canvas.drawRect(new Rect(0,0,w,h), paint);
      }
      @Override
      protected void onAttachedToWindow() {
          super.onAttachedToWindow();
      }
      
      @Override
      protected void onDetachedFromWindow() {
          super.onDetachedFromWindow();
      }
      
      
  }

