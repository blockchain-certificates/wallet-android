package com.learningmachine.android.app;

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
    public void addIssuer() {
        Timber.i("And we're running this test!");
    }
}
