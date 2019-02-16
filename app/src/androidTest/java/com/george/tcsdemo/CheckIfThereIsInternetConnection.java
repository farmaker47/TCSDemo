package com.george.tcsdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CheckIfThereIsInternetConnection {

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void testInternetConnection() {

        //Simple test for internet connection
        assertTrue(isConnected(InstrumentationRegistry.getContext()));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


