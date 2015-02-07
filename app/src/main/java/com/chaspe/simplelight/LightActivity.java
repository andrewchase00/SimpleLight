package com.chaspe.simplelight;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;


public class LightActivity extends SingleFragmentActivity {

    public static final String ACTION_CLOSE = "com.chaspe.simplelight.LightActivity.ACTION_CLOSE";

    private FirstReceiver firstReceiver;

    @Override
    public Fragment createFragment() {


        return new LightFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);



        IntentFilter filter = new IntentFilter(ACTION_CLOSE);
        firstReceiver = new FirstReceiver();
        registerReceiver(firstReceiver, filter);

    }

    @Override
    public void onStart(){
        super.onStart();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(firstReceiver);
    }

    class FirstReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("FirstReceiver", "FirstReceiver");
            if (intent.getAction().equals(ACTION_CLOSE)) {

                finish();
            }
        }
    }

}

