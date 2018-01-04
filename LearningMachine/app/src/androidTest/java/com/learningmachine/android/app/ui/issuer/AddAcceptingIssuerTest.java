package com.learningmachine.android.app.ui.issuer;

import android.support.test.rule.ActivityTestRule;

//import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.learningmachine.android.app.R;

import org.junit.Rule;
import org.junit.Test;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by chris on 1/4/18.
 */

public class AddAcceptingIssuerTest {

    @Rule
    public ActivityTestRule<AddIssuerActivity> mActivityTestRule = new ActivityTestRule<>(AddIssuerActivity.class);

    // TODO: Configure this with options to load the root directory.
//    @Rule
//    public WireMockRule wireMockRule = new WireMockRule();

    @Test
    public void addAcceptingIssuerTest() {
        onView(withId(R.id.add_issuer_url_edit_text)).perform(replaceText("http://localhost:8080/issuer/accepting"), closeSoftKeyboard());
        onView(withId(R.id.add_issuer_nonce_edit_text)).perform(replaceText("1234"), closeSoftKeyboard());
        Timber.i("Testing this out");
//        onView(withId(R.id.fragment_add_issuer_verify)).perform(click())
    }
}
