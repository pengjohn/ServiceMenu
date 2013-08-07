/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to test the Camera.

===========================================================================*/

package com.android.ServiceMenu;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.widget.Button;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.view.WindowManager;
import android.view.Display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.InterruptedException;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.view.KeyEvent;


import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class CameraPreview extends Activity implements Callback {
  private Camera mCamera = null;

  
  private CountDownLatch devlatch;

  private String TAG = "CameraPreview";

  private SurfaceView mSurfaceView;

  private SurfaceHolder mSurfaceHolder;

  private boolean bIfPreview = false;
//Add to avoid conflict while photo
  private boolean bIfPhoto = false;
    
  private SharedPreferences mPreferences;
    
  private int mViewFinderWidth, mViewFinderHeight;

  private static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
  private static final String KEY_JPEG_QUALITY = "pref_camera_jpegquality_key";
  private static final String PARM_PICTURE_SIZE = "picture-size";
  private static final String PARM_JPEG_QUALITY = "jpeg-quality";

//Add to store the picture
  private String strCaptureFilePath = "/data/camera_snap.jpg";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    devlatch = new CountDownLatch(1);
    Thread startPreviewThread = new Thread(new Runnable() {
      CountDownLatch tlatch = devlatch;
      public void run() {
        // Wait for framework initialization to be complete before
        // starting preview
        try {
          tlatch.await();
        } catch (InterruptedException ie) {
        
        }
        startPreview();
      }
    });
    startPreviewThread.start();
    setContentView(R.layout.camera);
    mSurfaceView = (SurfaceView)findViewById(R.id.camera_surface);
    mSurfaceHolder = mSurfaceView.getHolder();

    mSurfaceHolder.addCallback(this);
    mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    mViewFinderWidth = mSurfaceView.getLayoutParams().width;
    mViewFinderHeight = mSurfaceView.getLayoutParams().height;
    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        
    
    devlatch.countDown();
 
    // Make sure preview is started.
    try {
            startPreviewThread.join();
    } catch (InterruptedException ex) {
      // ignore
    }
    devlatch = null;
  }

  public void startPreview() {   	    	
//Add to avoid conflict while photo
	  bIfPhoto = false;
	
    if (mCamera == null) {
      mCamera = Camera.open();
    }
    if(bIfPreview) stopPreview();
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
    } 
  }

  private void setPreviewDisplay(SurfaceHolder holder) {
    try {
      mCamera.setPreviewDisplay(holder);
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
		/*String pictureSize = mPreferences.getString(
					   CameraSettings.KEY_PICTURE_SIZE, null);
		if (pictureSize == null) {
			CameraSettings.initialCameraPictureSize(this, mParameters);
		} else {
			List<Size> supported = mParameters.getSupportedPictureSizes();
			CameraSettings.setCameraPictureSize(
						   pictureSize, supported, mParameters);
		}*/
                //Add to set the picture size
		mParameters.setPictureSize(2592,1944);
		// Set the preview frame aspect ratio according to the picture size.
		
		Size size = mParameters.getPictureSize();
		/*PreviewFrameLayout frameLayout =
					   (PreviewFrameLayout) findViewById(R.id.frame_layout);
		frameLayout.setAspectRatio((double) size.width / size.height);
		*/
		// Set a preview size that is closest to the viewfinder height and has
		// the right aspect ratio.
		List<Size> sizes = mParameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(
					   sizes, (double) size.width / size.height);
		if (optimalSize != null) {
			mParameters.setPreviewSize(optimalSize.width, optimalSize.height);
		}
		
		// Set JPEG quality.
		/*String jpegQuality = mPreferences.getString(
					   CameraSettings.KEY_JPEG_QUALITY,
					   getString(R.string.pref_camera_jpegquality_default));
		mParameters.setJpegQuality(getQualityNumber(jpegQuality));*/
		mCamera.setParameters(mParameters);	   

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

   @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    // TODO Auto-generated method stub
    if(keyCode == KeyEvent.KEYCODE_MENU) {
    	Intent intent = new Intent();
    	intent.setClass(CameraPreview.this,SensorTest.class);
    	startActivity(intent);
    	finish();
    	return true;
    } else if(keyCode == KeyEvent.KEYCODE_CAMERA) {
    //Add to avoid conflict while photo
      if(!bIfPhoto)
        onTakePictures();
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

//Add for photo
  private void onTakePictures() { 
    if (mCamera != null && bIfPreview) {
    	//Add to avoid conflict while photo
	    bIfPhoto = true;
      mCamera.autoFocus(autofocusCallback);
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
                
                //Add to release the resources
                bm.recycle();
                bos.flush();
                bos.close();
                stopPreview();
                startPreview();

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };
}
