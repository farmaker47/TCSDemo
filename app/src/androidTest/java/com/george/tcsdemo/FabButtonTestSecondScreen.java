package com.george.tcsdemo;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FabButtonTestSecondScreen {

    @Rule
    public ActivityTestRule<WeatherActivity> mActivityTestRule = new ActivityTestRule<>(WeatherActivity.class);


    /////**********************THIS TEST HANDLES IDLING STATE AND WAIT UNTIL RECYCLER VIEW POPS UP


    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void clickRecyclerViewPosition() {

        //Perform click on FAB button
        onView(withId(R.id.fab)).check(matches((isDisplayed())));
        onView(withId(R.id.fab)).perform(click());

        //find the main recycler view and check it
        onView(withId(R.id.recyclerViewSearchFragment)).check(matches((isDisplayed())));


    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
