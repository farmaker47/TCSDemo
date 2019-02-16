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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DisplayMapAfterAllDialogBoxes {


    //************* TO USE THIS TEST TURN OFF ACCESSIBILITY OF OTHER APPLICATIONS at Settings>Accessibility of your phone


    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void testDialogBoxes() {

        //First check internet connection
        assertTrue(isConnected(InstrumentationRegistry.getContext()));

        //Perform click on button
        onView(withId(R.id.buttonMaps)).perform(click());

        //Check if First dialogBox appears
        onView(withText(R.string.automaticLocation))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        //Click on No button
        onView(withText("NO"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());

        //Check if Second dialogBox appears
        onView(withText(R.string.titleInsertAddress))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));

        //type town name inside edittext
        onView(withId(444))
                .inRoot(isDialog())
                .perform(typeText("kalamata"));

        //Click on OK button
        onView(withText("OK"))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()))
                .perform(click());
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
