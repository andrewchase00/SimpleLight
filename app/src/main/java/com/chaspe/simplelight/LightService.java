package com.chaspe.simplelight;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Service used to start LightControl Thread and set LightControl
                                                                                                    * parameters. Also, takes Intents to start/stop and update parameters,
                                                                                                    * and sets up a Notification to let the user know the service is running
                                                                                                    * and open the app by tapping the notification or end the service by
                                                                                                    * dismissing the notification.
 */
public class LightService extends Service {

    private final IBinder mBinder = new LightBinder();                                              // Binder used to initialize LightFragment

    private FirstReceiver firstReceiver;                                                            // Broadcast Receiver used to end service on Close Intent

    private NotificationManager mNotificationManager;                                               // Notification Manager used to create Notification

    public boolean mOn;                                                                             // Light parameters
    public boolean mStrobe;
    public int mFrequency;

    LightControl lc;                                                                                // LightControl instance used to set light parameters before starting thread
    Thread t;                                                                                       // Thread to start LightControl in

    public int onStartCommand (Intent intent, int flags, int startId) {

        if(intent != null) {                                                                        // Get light parameters from Intent Extras
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

            boolean change = intent.getBooleanExtra(LightFragment.EXTRA_CHANGE, true);              // Turn light on/off if change Extra is true
            if (change) {
                onOff();
            }

            if(!mOn){                                                                               // End Service if light is off
                if (t != null) {
                    t.interrupt();
                    t = null;
                }
                stopSelf();
            }
        }

        return START_STICKY;                                                                        // Set Service as Sticky
    }

    @Override
    public IBinder onBind(Intent intent) {                                                          // Return Binder for LightFragment Initialization
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {                                                        // Default on Unbind function
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        NotificationCompat.Builder mBuilder =                                                       // Create Notification builder with desired settings
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.lightlogo)
                        .setContentTitle("Simple Light")
                        .setContentText("Tap to open");

        Intent resultIntent = new Intent(this, LightActivity.class);                                // Create intent to open LightActivity (main app)

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);                              // Create stack to ensure proper back button function on exiting app
        stackBuilder.addParentStack(LightActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =                                                         // Set Pending Intent to open LightActivity when Notification is tapped
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Intent deleteIntent = new Intent(LightActivity.ACTION_CLOSE);                               // Set Pending Intent to close LightActivity and LightService when
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, 0);   // Notification is dismissed
        mBuilder.setDeleteIntent(deletePendingIntent);

        mNotificationManager =                                                                      // Get Notification Manager and place Notification
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(69, mBuilder.build());

        mOn = false;                                                                                // Initialize light parameters
        mStrobe = false;
        mFrequency = 0;

        lc = new LightControl();                                                                    // Initialize LightControl instance

        IntentFilter filter = new IntentFilter(LightActivity.ACTION_CLOSE);                         // Set up Broadcast Receiver to receive Close Intent
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (t != null) {                                                                            // Stop LightControl Thread
            lc.stopRunning = true;
            t = null;
        }

        unregisterReceiver(firstReceiver);                                                          // Destroy Broadcast Receiver

        mNotificationManager.cancelAll();                                                           // Remove Notification
    }

    public void onOff(){                                                                            // Switches light on or off depending on current state
        if (!mOn) {
            mOn = true;
            if (t != null) {                                                                        // Stop existing LightControl Thread
                t.interrupt();
                t = null;
            }
            t = new Thread(lc);                                                                     // Start new LightControl Thread
            t.start();
        }else if(mOn){
            mOn = false;
            if (t != null) {                                                                        // End LightControl Thread
                t.interrupt();
                t = null;
            }
            stopSelf();                                                                             // End LightService
        }
    }

    public class LightBinder extends Binder {                                                       // Custom Binder Definition
        LightService getService() {
            return LightService.this;                                                               // Return this instance of LocalService so LightFragment can extract parameters
        }
    }

    class FirstReceiver extends BroadcastReceiver {                                                 // Broadcast Receiver Definition
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LightActivity.ACTION_CLOSE)) {
                mNotificationManager.cancelAll();                                                   // Remove Notification
                stopSelf();                                                                         // End LightService
            }
        }
    }
}
