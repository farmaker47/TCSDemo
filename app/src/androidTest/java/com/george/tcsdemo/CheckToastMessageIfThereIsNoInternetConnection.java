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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CheckToastMessageIfThereIsNoInternetConnection {

    //////// CLOSE INTERNET TO USE THIS TEST

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void testInternetConnection() {

        //First check internet connection
        assertFalse(isConnected(InstrumentationRegistry.getContext()));

        //Perform click on button
        onView(withId(R.id.buttonMaps)).perform(click());

        //Check if spesific toast is displayed
        onView(withText(R.string.connectToInternet)).inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


