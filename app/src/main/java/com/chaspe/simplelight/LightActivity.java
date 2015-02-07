package com.chaspe.simplelight;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Single Fragment Activity used to launch LightFragment. Also contains a Broadcast Receiver
                                                                                                    * to close the app when the Notification is cleared or the close Button is pressed.
 */

public class LightActivity extends SingleFragmentActivity {

    public static final String ACTION_CLOSE = "com.chaspe.simplelight.LightActivity.ACTION_CLOSE";  // Tag used to identify Broadcast Intents used to close the app

    private FirstReceiver firstReceiver;                                                            // Intent receiver

    @Override
    public Fragment createFragment() {                                                              // Launches LightFragment
        return new LightFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(ACTION_CLOSE);                                       // Set up Broadcast Receiver
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(firstReceiver);                                                          // Destroy Broadcast Receiver
    }

    class FirstReceiver extends BroadcastReceiver {                                                 // Broadcast Receiver definition

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CLOSE)) {
                finish();                                                                           // Close app
            }
        }
    }
}

