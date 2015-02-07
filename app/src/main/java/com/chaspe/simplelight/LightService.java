package com.chaspe.simplelight;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by Bears on 2/3/2015.
 */
public class LightService extends Service {
    public static final String TAG = "LightService";

    private final IBinder mBinder = new LightBinder();
    private FirstReceiver firstReceiver;

    public boolean mOn;
    public boolean mStrobe;
    public boolean wasOn;
    private NotificationManager mNotificationManager;


    public int mFrequency;
    LightControl lc;
    Thread t;

    public int onStartCommand (Intent intent, int flags, int startId) {
        if(intent != null) {
            if (intent.hasExtra(LightFragment.EXTRA_STROBE)) {
                mStrobe = intent.getBooleanExtra(LightFragment.EXTRA_STROBE, false);
            }
            if (intent.hasExtra(LightFragment.EXTRA_FREQUENCY)) {
                mFrequency = intent.getIntExtra(LightFragment.EXTRA_FREQUENCY, 0);
            }

            if (mStrobe) {
                lc.frequency = mFrequency;
            } else {
                lc.frequency = 0;
            }

            boolean change = intent.getBooleanExtra(LightFragment.EXTRA_CHANGE, true);
            if (change) {

                onOff();
            }

            if(!mOn){
                if (t != null) {
                    t.interrupt();
                    t = null;
                }
                stopSelf();
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        wasOn = mOn;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()

            //stopSelf();

        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Light service started");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.lightlogo)
                        .setContentTitle("Simple Light")
                        .setContentText("Tap to open");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LightActivity.class);

        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(LightActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Intent deleteIntent = new Intent(LightActivity.ACTION_CLOSE);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);

        /*Intent deleteIntent = new Intent();
        deleteIntent.setAction("com.chaspe.simplelight.LightService");
        deleteIntent.putExtra(LightFragment.EXTRA_CHANGE, true);
        //intent.putExtra(LightFragment.EXTRA_STROBE, strobe);
        //intent.putExtra(LightFragment.EXTRA_FREQUENCY, frequency);
        PendingIntent deletePendingIntent = PendingIntent.getService(this, 0, deleteIntent, 0);*/

        mBuilder.setDeleteIntent(deletePendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

        mNotificationManager.notify(69, mBuilder.build());



        mOn = false;
        mStrobe = false;
        mFrequency = 0;
        lc = new LightControl();

        IntentFilter filter = new IntentFilter(LightActivity.ACTION_CLOSE);
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service Ended");
        if (t != null) {
            lc.stopRunning = true;
            t = null;
        }
        unregisterReceiver(firstReceiver);
        mNotificationManager.cancelAll();
    }

    public void onOff(){
        if (!mOn) {
            mOn = true;
            if (t != null) {
                t.interrupt();
                t = null;
            }

            t = new Thread(lc);
            t.start();
        }else if(mOn){
            mOn = false;
            if (t != null) {
                t.interrupt();
                t = null;
            }
            stopSelf();
        }

        //mCamera.setParameters(mParameters);
    }

    public boolean isOn(){
        return mOn;
    }





    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LightBinder extends Binder {
        LightService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LightService.this;
        }
    }

    /** method for clients */
    public int getFrequency() {
            return mFrequency;
        }

    class FirstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ServiceReceiver", "FirstReceiver");
            if (intent.getAction().equals(LightActivity.ACTION_CLOSE)) {
                mNotificationManager.cancelAll();
                stopSelf();
            }
        }
    }

}
