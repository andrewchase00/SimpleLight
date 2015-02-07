package com.chaspe.simplelight;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;


public class LightFragment extends Fragment {
    public static final String TAG = "LightFragment";



    public static final String EXTRA_CHANGE = "CHANGE";
    public static final String EXTRA_STROBE = "STROBE";
    public static final String EXTRA_FREQUENCY = "FREQUENCY";

    private CheckBox mLightButton;
    private Button mExitButton;
    private SeekBar mBar;
    private Toolbar mToolbar;
    private CheckBox mBox;
    private ActionBar mActionBar;


    private boolean mStrobe;
    private int mFrequency;
    private Intent mIntent;


    LightService mService;
    boolean mBound = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

            mIntent = new Intent();
            mIntent.setAction("com.chaspe.simplelight.LightService");

            getActivity().getApplicationContext().bindService(mIntent, mConnection, getActivity().getApplicationContext().BIND_AUTO_CREATE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_light, container, false);



       mToolbar = (Toolbar) v.findViewById(R.id.toolbar);



        mExitButton = (Button) v.findViewById(R.id.exit);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightActivity.ACTION_CLOSE);
                getActivity().sendBroadcast(intent);
            }
        });

        mBox = (CheckBox) v.findViewById(R.id.checkbox);
        mBox.setChecked(mStrobe);
        mBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBar.setEnabled(isChecked);

                mStrobe = isChecked;

                if(isChecked){
                    mBox.setTextColor(getResources().getColor(R.color.a400));
                } else {
                    mBox.setTextColor(getResources().getColor(R.color.t2));
                }

                updateService(false);
            }
        });


        mLightButton = (CheckBox) v.findViewById(R.id.light_button);
        mLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateService(true);
            }
        });

        mBar = (SeekBar) v.findViewById(R.id.brightness);
        mBar.setEnabled(mStrobe);

        mBar.setProgress(mFrequency);
        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "Seek bar at " + progress);
                mFrequency = progress;
                updateService(false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return v;
    }


    public void updateService(boolean change){

            Intent intent = mIntent;
            intent.putExtra(LightFragment.EXTRA_CHANGE, change);
            intent.putExtra(LightFragment.EXTRA_STROBE, mStrobe);
            intent.putExtra(LightFragment.EXTRA_FREQUENCY, mFrequency);
            getActivity().getApplicationContext().startService(mIntent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();



    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LightService.LightBinder binder = (LightService.LightBinder) service;
            mService = binder.getService();

            mStrobe = mService.mStrobe;
            mFrequency = mService.mFrequency;
            boolean lightOn = mService.wasOn;

            mBar.setProgress(mFrequency);
            mBox.setChecked(mStrobe);

            mLightButton.setChecked(mService.mOn);
            getActivity().getApplicationContext().unbindService(mConnection);

            //if(lightOn) {
            //    updateService(true);
            //}
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {


        }
    };


}

