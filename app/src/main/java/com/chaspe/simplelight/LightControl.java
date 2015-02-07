package com.chaspe.simplelight;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Bears on 2/3/2015.
 */

class LightControl implements Runnable {
    public static final String TAG = "LightControl";

    int frequency;
    boolean stopRunning = false;
    private Camera mCamera;
    private Camera.Parameters mPOn;
    private Camera.Parameters mPOff;

    @Override
    public void run() {
        try {
            if(mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            mCamera = Camera.open();
        } catch(Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "Camera Init Error");
            mCamera = null;
        }

        if(mCamera != null) {
            mPOn = mCamera.getParameters();
            mPOff = mCamera.getParameters();
            mPOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mPOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            try {
                while (!stopRunning) {
                    if (frequency == 0) {
                        mCamera.setParameters(mPOn);
                    } else {
                        mCamera.setParameters(mPOn);
                        Thread.sleep((frequency * frequency) / 20);

                        mCamera.setParameters(mPOff);
                        Thread.sleep((frequency * frequency) / 20);
                    }
                    if (Thread.currentThread().isInterrupted()){
                        stopRunning = true;
                        mCamera.setParameters(mPOff);
                        mCamera.release();
                        mCamera = null;
                    }
                }
                if(mCamera!=null) {
                    mCamera.setParameters(mPOff);
                    mCamera.release();
                    mCamera = null;
                }

            }catch (InterruptedException e) {
                stopRunning = true;
                if(mCamera!=null) {
                    mCamera.setParameters(mPOff);
                    mCamera.release();
                    mCamera = null;
                }


            }catch (Throwable t2) {
                Log.e(TAG, "light end");
                stopRunning = true;
                if(mCamera!=null) {
                    mCamera.setParameters(mPOff);
                    mCamera.release();
                    mCamera = null;
                }
            }

        }
    }
}
