/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test camera.

===========================================================================*/
package com.android.ServiceMenu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.app.Activity;
import android.view.Window;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.view.Surface;

public class AutoCamera extends AutoItemActivity implements OnClickListener,Callback {

  private Button sucBtn,falBtn,switchBtn,takeBtn;
  private Camera mCamera = null;
  private SurfaceView mSurfaceView;
  private boolean bIfPreview = false;
  private boolean bIfPhoto = false;
  private SurfaceHolder mSurfaceHolder;
  private String strCaptureFilePath = "/data/camera_snap.jpg";
  private static final String TAG = "AutoCamera";
  private int camerID = CameraInfo.CAMERA_FACING_FRONT;//CAMERA_FACING_BACK;
  private boolean bHaveTakeBack = false;
  private boolean bHaveTakeFront = false;
  
  private int mDisplayRotation;
  private int mDisplayOrientation;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_camera);
        
        sucBtn = (Button)findViewById(R.id.camera_success);
        falBtn = (Button)findViewById(R.id.camera_fail);
        switchBtn = (Button)findViewById(R.id.camera_switch);
        takeBtn = (Button)findViewById(R.id.camera_take);        

        sucBtn.setEnabled(false);
        
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        switchBtn.setOnClickListener(this);
        takeBtn.setOnClickListener(this);
        
        takeBtn.setVisibility(View.GONE);
        
        /*
        if(mIsPcba)
        {
        	bHaveTakeBack = true;
        	switchBtn.setVisibility(View.GONE);
        }
        */
        
        mSurfaceView = (SurfaceView)findViewById(R.id.camera_surface);
        mSurfaceHolder = mSurfaceView.getHolder();

        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
	
  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.camera_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.camera_fail:
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.camera_switch:
      switchCamera();
      //sucBtn.setEnabled(true);
    	break;
    case R.id.camera_take:
      onTakePictures();
    	break;
    default:
      Log.e(TAG,"Error!");
      break;
    }
  }
  public void startPreview() {   	    	

	  bIfPhoto = false;
	
    if (mCamera == null) {
    	try
    	{
      	mCamera = Camera.open(camerID);
      }catch(Throwable ex)
			{
			  Log.e(TAG, "Camera.open fail!");
			}
    }

    if(bIfPreview) 
    	stopPreview();

    if (mCamera != null && !bIfPreview) {
      Log.i(TAG, "inside the camera");

      setCameraParameters();
      setPreviewDisplay(mSurfaceHolder);
      try {
        mCamera.startPreview();
        Log.i(TAG, "start preview");
      } catch (Throwable ex) {
        closeCamera();
			  // TODO Auto-generated catch block
              //e.printStackTrace();
      }            
      bIfPreview = true;

      //Preview ok -> Success      
      if(camerID == CameraInfo.CAMERA_FACING_FRONT)
      	bHaveTakeFront = true;
      if(camerID == CameraInfo.CAMERA_FACING_BACK)
      	bHaveTakeBack = true;
      if(bHaveTakeFront && bHaveTakeBack)
      {
      	sucBtn.setEnabled(true);
      }      	
    } 
  }

  public void switchCamera() {
  	if(camerID == CameraInfo.CAMERA_FACING_BACK)
  	 	camerID = CameraInfo.CAMERA_FACING_FRONT;
  	else
  		camerID = CameraInfo.CAMERA_FACING_BACK;
  		
    stopPreview();
    closeCamera();
    startPreview();  	
  	}
  	
  private void setPreviewDisplay(SurfaceHolder holder) {
    try {
      mCamera.setPreviewDisplay(holder);
	    //if(camerID == CameraInfo.CAMERA_FACING_FRONT)
		  //  mCamera.setDisplayOrientation(180);
      mDisplayRotation = getDisplayRotation(this);
      mDisplayOrientation = getDisplayOrientation(mDisplayRotation, camerID);
      mCamera.setDisplayOrientation(mDisplayOrientation);
        		  
    } catch (IOException e) {
      closeCamera();
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  private void setCameraParameters() {
    Camera.Parameters mParameters = mCamera.getParameters();
		
    // Reset preview frame rate to the maximum because it may be lowered by
    // video camera application.
    List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
    if (frameRates != null) {
      Integer max = Collections.max(frameRates);
      mParameters.setPreviewFrameRate(max);
    }
    
    // Set picture size.

    mParameters.setPictureSize(640,480);
    // Set the preview frame aspect ratio according to the picture size.
    
    Size size = mParameters.getPictureSize();

    // Set a preview size that is closest to the viewfinder height and has
    // the right aspect ratio.
    List<Size> sizes = mParameters.getSupportedPreviewSizes();
    Size optimalSize = getOptimalPreviewSize(
					   sizes, (double) size.width / size.height);
    if (optimalSize != null) {
      mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
    }

//pengzhixiong@gozone
//    mCamera.setParameters(mParameters);	   

  }
  
  private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
    final double ASPECT_TOLERANCE = 0.05;
    if (sizes == null) return null;
	
    Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;
	
    // Because of bugs of overlay and layout, we sometimes will try to
    // layout the viewfinder in the portrait orientation and thus get the
    // wrong size of mSurfaceView. When we change the preview size, the
    // new overlay will be created before the old one closed, which causes
    // an exception. For now, just get the screen size
	
    Display display = getWindowManager().getDefaultDisplay();
    int targetHeight = Math.min(display.getHeight(), display.getWidth());
	
    if (targetHeight <= 0) {
      // We don't know the size of SurefaceView, use screen height
      WindowManager windowManager = (WindowManager)
      getSystemService(Context.WINDOW_SERVICE);
      targetHeight = windowManager.getDefaultDisplay().getHeight();
    }
	
    // Try to find an size match aspect ratio and size
    for (Size size : sizes) {
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
      if (Math.abs(size.height - targetHeight) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }
	
    // Cannot find the one match the aspect ratio, ignore the requirement
    if (optimalSize == null) {
      Log.v(TAG, "No preview size match the aspect ratio");
      minDiff = Double.MAX_VALUE;
      for (Size size : sizes) {
        if (Math.abs(size.height - targetHeight) < minDiff) {
          optimalSize = size;
          minDiff = Math.abs(size.height - targetHeight);
        }
      }
    }
    Log.i(TAG, String.format(
				   "Optimal preview size is %sx%s",
				   optimalSize.width, optimalSize.height));
    return optimalSize;
  }
  public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {
    // TODO Auto-generated method stub
    mSurfaceHolder = surfaceholder;
    if(mCamera == null) return;
    if(bIfPreview && surfaceholder.isCreating()) setPreviewDisplay(surfaceholder); 
    else  startPreview();
    Log.i(TAG, "Surface Changed");
  }

  public void surfaceCreated(SurfaceHolder surfaceholder) {
    // TODO Auto-generated method stub
    Log.i(TAG, "Surface Changed");
  }

  public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    // TODO Auto-generated method stub
    Log.i(TAG, "Surface Destroyed");
  }
  private void stopPreview() {
    if (mCamera != null && bIfPreview) {
      Log.v(TAG, "stopPreview");
      mCamera.stopPreview();
    }
    bIfPreview = false;
  }
  //function to release the camera 
  private void closeCamera(){
    if(mCamera != null){
      mCamera.release();
      mCamera = null;
			bIfPreview = false;
    }		
  }

  private void onTakePictures() { 
    if (mCamera != null && bIfPreview) {
    	if(bIfPhoto == false)
    	{
	      bIfPhoto = true;
        mCamera.autoFocus(autofocusCallback);
      }
    }
  }
private AutoFocusCallback autofocusCallback = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
            // Shutter has closed
        }
    };

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            // Shutter has closed
        }
    };

    private PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            // TODO Handle RAW image data
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {

            Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);

            // mSurfaceView01.setBackgroundColor(Color.BLACK);
            File myCaptureFile = new File(strCaptureFilePath);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                        myCaptureFile));

                bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bm.recycle();
                bos.flush();
                bos.close();
                stopPreview();
                startPreview();

                //Take Phone ok->Success
                if(camerID == CameraInfo.CAMERA_FACING_FRONT)
        	         bHaveTakeFront = true;
                if(camerID == CameraInfo.CAMERA_FACING_BACK)
                   bHaveTakeBack = true;
                if(bHaveTakeFront && bHaveTakeBack)
                {
                	sucBtn.setEnabled(true);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };
  @Override
  public void onResume() {
    super.onResume();

    startPreview();

  }
  @Override
  protected void onPause() {
    stopPreview();
    closeCamera();
    super.onPause();
  }

  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	switch(keyCode)
  	{
  		//disable the key
      case KeyEvent.KEYCODE_HOME:
      case KeyEvent.KEYCODE_BACK:
        return true;
      case KeyEvent.KEYCODE_MENU:
//        if(!bIfPhoto)
//          onTakePictures();
        return true;        
    }
    return super.onKeyDown(keyCode, event);
  }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
 
    public static int getDisplayOrientation(int degrees, int cameraId) {
        // See android.hardware.Camera.setDisplayOrientation for documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }
    

}
