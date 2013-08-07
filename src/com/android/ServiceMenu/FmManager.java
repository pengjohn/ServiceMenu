/*
 * Copyright (c) 2011-2012 Qualcomm Technologies, Inc.  All Rights Reserved.
 * Qualcomm Technologies Proprietary and Confidential.
 */
package com.android.ServiceMenu;

import android.content.Context;
import android.content.Intent;
import android.hardware.fmradio.FmConfig;
import android.hardware.fmradio.FmReceiver;
import android.hardware.fmradio.FmRxEvCallbacksAdaptor;
import android.os.Handler;
import android.util.Log;

public class FmManager {

    public static FmReceiver mReceiver = null;
    private static final String RADIO_DEVICE = "/dev/radio0";
    private static boolean mFmOn = false;
    static String TAG = "FM";
    private int frequency = 87500;
    Context mContext = null;
    Handler mHandler;

    public FmManager(Context context, Handler handler) {

        mContext = context;
        mHandler = handler;
    }
    
    public int getFrequency() {
        return frequency;
    }

    FmConfig getFmDefConfig() {

        FmConfig mFmConfig = new FmConfig();
        mFmConfig.setRadioBand(0);
        mFmConfig.setEmphasis(0);
        mFmConfig.setChSpacing(0);
        mFmConfig.setRdsStd(0);
        mFmConfig.setLowerLimit(87500);
        mFmConfig.setUpperLimit(1079000);
        return mFmConfig;
    }

    FmRxEvCallbacksAdaptor mFmRxEvCallbacksAdaptor = new FmRxEvCallbacksAdaptor() {
        public void FmRxEvSearchComplete(int freq) {
            FmManager.this.frequency = freq;
            logd(freq);
            mHandler.sendEmptyMessage(0);
        }
        
        public void FmRxEvRadioTuneStatus(int freq) {
            FmManager.this.frequency = freq;
            logd(freq);
            mHandler.sendEmptyMessage(0);
        };
    };

    public boolean openFM() {

        boolean ret = false;

        if (mReceiver == null) {
            try {
                mReceiver = new FmReceiver(RADIO_DEVICE, mFmRxEvCallbacksAdaptor);				
            } catch (InstantiationException e) {
                loge(e);
            }
        }

        if (mReceiver != null) {
            if (isFmOn()) {
                ret = true;
            } else {
                logd("to enable");
                FmConfig mFmConfig = getFmDefConfig();
                ret = mReceiver.enable(mFmConfig);
                mReceiver.setAnalogMode(true); 

                if (ret) {
                    mFmOn = true;
                    logd("heare");
                    Intent mIntent = new Intent("android.intent.action.FM");
                    mIntent.putExtra("state", 1);
                    mContext.sendBroadcast(mIntent);
                }
            }
        }
        logd(ret);
        return ret;
    }

    public boolean closeFM() {

        boolean ret = false;

        if (mReceiver != null) {
            mReceiver.setAnalogMode(false); 
            ret = mReceiver.disable();

            Intent mIntent = new Intent("android.intent.action.FM");
            mIntent.putExtra("state", 0);
            mContext.sendBroadcast(mIntent);
            mReceiver = null;
        }
        logd(ret);
        if (ret) {
            mFmOn = false;
        }
        return ret;
    }

    public boolean isFmOn() {

        return mFmOn;
    }

    public int searchUP() {

        mReceiver.searchStations(FmReceiver.FM_RX_SRCH_MODE_SEEK, FmReceiver.FM_RX_DWELL_PERIOD_1S,
                FmReceiver.FM_RX_SEARCHDIR_UP);
        logd("searchUP: " + getFrequency());
        return getFrequency();
    }

    public int searchDown() {

        mReceiver.searchStations(FmReceiver.FM_RX_SRCH_MODE_SEEK, FmReceiver.FM_RX_DWELL_PERIOD_1S,
                FmReceiver.FM_RX_SEARCHDIR_DOWN);
        logd("searchDown: " + getFrequency());
        return getFrequency();
    }

    private void loge(Object e) {

        if (e == null)
            return;
        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();
        e = "[" + mMethodName + "] " + e;
        Log.e(TAG, e + "");
    }

    private void logd(Object s) {

        Thread mThread = Thread.currentThread();
        StackTraceElement[] mStackTrace = mThread.getStackTrace();
        String mMethodName = mStackTrace[3].getMethodName();

        s = "[" + mMethodName + "] " + s;
        Log.d(TAG, s + "");
    }

}
