package com.chaspe.simplelight;

import android.hardware.Camera;

/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Runnable Thread that opens the camera, and controls the flash mode
                                                                                                    * based on parameters set up by LightService.
 */

class LightControl implements Runnable {

    int frequency;                                                                                  // Sets frequency of flashes in Strobe setting. 0 is a normal light

    boolean stopRunning = false;                                                                    // Set to true to end this Thread

    private Camera mCamera;                                                                         // Camera Object
    private Camera.Parameters mPOn;                                                                 // Preset Camera Parameters to set flash mode to TORCH
    private Camera.Parameters mPOff;                                                                // Preset Camera Parameters to set flash mode to OFF

    @Override
    public void run() {                                                                             // Main Thread
        try {                                                                                       // Try opening Camera
            if(mCamera != null) {                                                                   // Release existing Camera
                mCamera.release();
                mCamera = null;
            }
            mCamera = Camera.open();                                                                // Open Camera Object
        } catch(Throwable t) {                                                                      // Set Camera to null on Error
            mCamera = null;
        }

        if(mCamera != null) {                                                                       // If Camera successfully opens
            mPOn = mCamera.getParameters();                                                         // Setup pre-made parameters
            mPOff = mCamera.getParameters();
            mPOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mPOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            try {
                while (!stopRunning) {                                                              // Main Thread Loop. Ended by setting stopRunning to false
                    if (frequency == 0) {                                                           // Turn on light with no flashing if frequency == 0
                        mCamera.setParameters(mPOn);
                    } else {                                                                        // Turns light on and off with a sleep of (frequency^2)/20 between each switch
                        mCamera.setParameters(mPOn);
                        Thread.sleep((frequency * frequency) / 20);

                        mCamera.setParameters(mPOff);
                        Thread.sleep((frequency * frequency) / 20);
                    }
                    if (Thread.currentThread().isInterrupted()){                                    // On interrupt exit loop, turn off flash, and release Camera
                        stopRunning = true;
                        mCamera.setParameters(mPOff);
                        mCamera.release();
                        mCamera = null;
                    }
                }
                if(mCamera!=null) {                                                                 // Release Camera after ending loop
                    mCamera.setParameters(mPOff);
                    mCamera.release();
                    mCamera = null;
                }

            }catch (InterruptedException e) {                                                       // Catch interrupts to end loop and release Camera
                stopRunning = true;
                if(mCamera!=null) {
                    mCamera.setParameters(mPOff);
                    mCamera.release();
                    mCamera = null;
                }


            }catch (Throwable t2) {                                                                 // Catch other errors to end loop and release Camera
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