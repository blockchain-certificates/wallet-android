package com.learningmachine.android.app.ui.issuer;

import android.support.test.rule.ActivityTestRule;

import com.learningmachine.android.app.R;

import org.junit.Rule;
import org.junit.Test;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by chris on 1/4/18.
 */

public class AddAcceptingIssuerTest {
    public static final String WIREMOCK_SERVER = "http://10.0.2.2:1234";

    @Rule
    public ActivityTestRule<AddIssuerActivity> mActivityTestRule = new ActivityTestRule<>(AddIssuerActivity.class);

    @Test
    public void addAcceptingIssuerTest() {
        onView(withId(R.id.add_issuer_url_edit_text)).perform(replaceText(WIREMOCK_SERVER + "/issuer/accepting-estimate-unsigned"), closeSoftKeyboard());
        onView(withId(R.id.add_issuer_nonce_edit_text)).perform(replaceText("1234"), closeSoftKeyboard());
        Timber.i("Testing this out");
        onView(withId(R.id.fragment_add_issuer_verify)).perform(click());
    }
}
