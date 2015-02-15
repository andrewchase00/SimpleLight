package com.chaspe.simplelight;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Simple Light's main Fragment. Contains the view and all UI functions
                                                                                                    * based on parameters set up by LightService.
 */


public class LightFragment extends Fragment {

    public static final String EXTRA_CHANGE = "CHANGE";                                             // Tags for extras put onto the Intent used to launch LightService
    public static final String EXTRA_STROBE = "STROBE";
    public static final String EXTRA_FREQUENCY = "FREQUENCY";

    private Intent mIntent;                                                                         // Template LightService Intent that extras can be added to

    private CheckBox mLightButton;                                                                  // UI Elements
    private SeekBar mBar;
    private CheckBox mBox;

    private boolean mStrobe;                                                                        // Light Parameters
    private int mFrequency;

    LightService mService;                                                                          // LightService used to initialize light parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mIntent = new Intent(getActivity().getApplicationContext(), LightService.class);                                                                     // Initializing template LightService Intent
        mIntent.setAction("com.chaspe.simplelight.LightService");

        getActivity().getApplicationContext().bindService(mIntent,                                  // Bind Light Service to initialize light parameters
                mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_light, container, false);                       // Inflate the layout for this fragment

        Button exitButton = (Button) v.findViewById(R.id.exit);                                           // Set up on Click Listener for Exit Button
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightActivity.ACTION_CLOSE);                             // Broadcast Close intent
                getActivity().sendBroadcast(intent);
            }
        });

        mBox = (CheckBox) v.findViewById(R.id.checkbox);                                            // Initialize Check Box to mStrobe and start on Checked Change Listener
        mBox.setChecked(mStrobe);                                                                   // for Strobe Check Box
        mBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mBar.setEnabled(isChecked);                                                         // Enable/disable Frequency Seek Bar

                mStrobe = isChecked;                                                                // Update Strobe parameter

                if(isChecked){                                                                      // Update Strobe Check Box Text Color
                    mBox.setTextColor(getResources().getColor(R.color.a400));
                } else {
                    mBox.setTextColor(getResources().getColor(R.color.t2));
                }

                updateService(false);                                                               // Update LightService with new light parameters
            }
        });

        mLightButton = (CheckBox) v.findViewById(R.id.light_button);                                // Set up on Click Listener for Light Button
        mLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateService(true);                                                                // Tell LightService to switch light on/off
            }
        });

        mBar = (SeekBar) v.findViewById(R.id.brightness);                                           // Initialize frequency Seek Bar based on light parameters
        mBar.setEnabled(mStrobe);                                                                   // and set on Progress Changed Listener
        mBar.setProgress(mFrequency);
        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mFrequency = progress;                                                              // Update Frequency parameter
                updateService(false);                                                               // Update LightService with new light parameters
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        return v;                                                                                   // Submit View
    }

    public void updateService(boolean change){                                                      // Function used to send an intent to LightService with light
            Intent intent = mIntent;                                                                // parameters and whether or not to change flash state
            intent.putExtra(LightFragment.EXTRA_CHANGE, change);
            intent.putExtra(LightFragment.EXTRA_STROBE, mStrobe);
            intent.putExtra(LightFragment.EXTRA_FREQUENCY, mFrequency);
            getActivity().getApplicationContext().startService(mIntent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {                               // Connection used to temporarily bind to LightService
                                                                                                    // to initialize parameters
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            LightService.LightBinder binder = (LightService.LightBinder) service;                   // Get an instance of the bound LightService
            mService = binder.getService();

            mStrobe = mService.mStrobe;                                                             // Update light parameters and UI
            mFrequency = mService.mFrequency;
            mBar.setProgress(mFrequency);
            mBox.setChecked(mStrobe);
            mLightButton.setChecked(mService.mOn);

            getActivity().getApplicationContext().unbindService(mConnection);                       // Unbind service after update
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}

