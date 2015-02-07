package com.chaspe.simplelight;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

/**
                                                                                                    * Created by Andrew Chase on 2/3/2015.
                                                                                                    * Abstract Activity Class that allows an Activity to start
                                                                                                    * a Fragment with a simple Command
 */

public abstract class SingleFragmentActivity extends Activity {

    protected abstract Fragment createFragment();                                                   // Abstract function to be set in Activity such that it returns
                                                                                                    // the Fragment to be started
    protected int getLayoutResId() {
        return R.layout.activity_fragment;                                                          // Get layout with Frame Layer to put fragment in
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());                                                           // Set Frame Layer as active View

        FragmentManager fm = getFragmentManager();                                                  // Use Fragment Manager to put Fragment in Frame Layer
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }
}
