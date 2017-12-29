package com.learningmachine.android.app;

import android.databinding.adapters.ViewBindingAdapter;
import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.learningmachine.android.app.ui.home.HomeActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by chris on 12/20/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddIssuerTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void beforeEachTest() {
        Timber.i("Initializing valid state");
    }

    @After
    public void afterEachTest() {
        Timber.i("Deinitializing valid state");
    }

    @Test
    public void addIssuer_fromEmptyState() {
        onView(withId(R.id.issuer_add_button)).perform(click());

        onView(withId(R.id.add_issuer_url_edit_text)).check(matches(isDisplayed()));

        Timber.i("And we're running this test!");
    }

//    @Test
//    public void addIssuer_fromRecyclerViewState() {
//        // Click the first issuer tile
//        onView(withId(R.id.issuer_recyclerview))
//                .perform(actionOnItemAtPosition(0, click()));
//
//        // Check that the "Add Certificate" button is displayed
//        onView(withId(R.id.add_certificate_floating_action_button)).check(matches(isDisplayed()));
//
//        Timber.i("And we're running this test!");
//    }


}
