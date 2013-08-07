/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20110901  PengZhiXiong   Initial to auto test music player.

===========================================================================*/
package com.android.ServiceMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.Iterator;

public class AutoGPS extends AutoItemActivity implements OnClickListener {
	
    private Button sucBtn,falBtn;
    private TextView mtext, stitletextView, lngAndlatTextView;
    private boolean mGPSTestStatus = false;
    private LocationManager mLocationGps;
    private String mGPSprovider = LocationManager.GPS_PROVIDER;
    private GpsStatus gpsStatus;
    private GPSInfo currentGPSinfo = new GPSInfo();
    private Calendar c;
    public class GPSInfo {
    
    double mLongitude;
    double mLatitude;
    double mAltitude;
    float mSpeed;
    }
    private static final String TAG = "AutoGPS";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_gps);
        
        mtext = (TextView)findViewById(R.id.text);
        lngAndlatTextView = (TextView)findViewById(R.id.currentgpsinfo);
        stitletextView = (TextView)findViewById(R.id.currentgpstitle);
        
        sucBtn = (Button)findViewById(R.id.gps_success);
        falBtn = (Button)findViewById(R.id.gps_fail);
        sucBtn.setOnClickListener(this);
        falBtn.setOnClickListener(this);
        
        mLocationGps = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
    }

  public void onClick(View v) {
    // TODO Auto-generated method stub
    int id = v.getId();
    switch(id){
    case R.id.gps_success:
      if(bFlagAutoTest)
         openActivity(getTestItemActivityIdByClass(this)+1);
      else
         setTestSuccessed(getTestItemActivityIdByClass(this));
      finish();
      break;
    case R.id.gps_fail:
      if(bFlagAutoTest)
         openFailActivity(getTestItemActivityIdByClass(this));
      else
         setTestFailed(getTestItemActivityIdByClass(this));
      finish();
      break;
    default:
      Log.e(TAG,"Error!");
      break;
    }
  }
  private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    // gpsStatus.getTimeToFirstFix();
                    break;

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    gpsStatus = mLocationGps.getGpsStatus(null);
                    Iterable<GpsSatellite> allSatellites;
                    allSatellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> it = allSatellites.iterator();
                    int iPos = 0;
                    String sSatellite = "";
                    while (it.hasNext()) {
                        GpsSatellite gpsS = (GpsSatellite)it.next();
                        iPos++;
                        sSatellite += getResources().getString(R.string.gps_satellite)
                                + String.valueOf(iPos) + ": ";
                        sSatellite += getResources().getString(R.string.gps_prn) + "="
                                + String.valueOf(gpsS.getPrn()) + ", ";
                        sSatellite += getResources().getString(R.string.gps_snr) + "="
                                + String.valueOf(gpsS.getSnr()) + "\n";
                    }
                    
                    if(iPos > 0)
                    {
                        //test success
                        sucBtn.setEnabled(true);
                    }
                                        	                    
                    if (sSatellite != null) {
                        stitletextView.setText(sSatellite);
                    }
                    break;

                case GpsStatus.GPS_EVENT_STARTED:
                    // Event sent when the GPS system has started.
                    break;

                case GpsStatus.GPS_EVENT_STOPPED:
                    // Event sent when the GPS system has stopped.
                    break;

                default:
                    break;
            }
        }

    };
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateLatAndLng(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void updateLatAndLng(Location currentLocation) {
        String latLongString;

        if (currentLocation != null) {
            currentGPSinfo.mLongitude = currentLocation.getLongitude();
            currentGPSinfo.mLatitude = currentLocation.getLatitude();
            currentGPSinfo.mAltitude = currentLocation.getAltitude();
            currentGPSinfo.mSpeed = currentLocation.getSpeed();
            latLongString = getResources().getString(R.string.gps_longitude)
                    + currentGPSinfo.mLongitude + "\n"
                    + getResources().getString(R.string.gps_latitude) + currentGPSinfo.mLatitude
                    + "\n" + getResources().getString(R.string.gps_altitude)
                    + currentGPSinfo.mAltitude;
            Calendar cnow = Calendar.getInstance();

            long timewhile = cnow.getTimeInMillis() - c.getTimeInMillis();
            if (timewhile > 0) {
                latLongString += "\n" + getResources().getString(R.string.gps_time) + timewhile
                        / 1000;
            }
          //  Toast.makeText(Gpslocation.this, latLongString, Toast.LENGTH_SHORT).show();
            if (latLongString != null) {
                lngAndlatTextView.setText(latLongString);
                sucBtn.setEnabled(true);
            } else {
                // lngAndlatTextView.setText("searching...");
            }
        }
    }
  @Override
  public void onResume() {
    super.onResume();
    sucBtn.setEnabled(false);
    //the function can not work, if build as common APK
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.NETWORK_PROVIDER, false);
    Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
    
    if (mLocationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    	mtext.setText(R.string.gps_search);
      lngAndlatTextView.setText("");
      mLocationGps.requestLocationUpdates(mGPSprovider, 2000, 2, locationListener);
      mLocationGps.addGpsStatusListener(statusListener);
      mGPSTestStatus = true;
      c = Calendar.getInstance();
    } else {
      mtext.setText(R.string.auto_error);
    }                
 
  }
  @Override
  protected void onPause() {
  	mtext.setText("");
    lngAndlatTextView.setText("");
    if (mGPSTestStatus) {
      mLocationGps.removeUpdates(locationListener);
      mLocationGps.removeGpsStatusListener(statusListener);
      mGPSTestStatus = false;
    }
    
    super.onPause();
  }  
  
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
