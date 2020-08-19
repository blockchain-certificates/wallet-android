package com.learningmachine.android.app.ui.issuer;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import com.learningmachine.android.app.R;

import org.junit.Rule;
import org.junit.Test;

import timber.log.Timber;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by chris on 1/4/18.
 */

public class AddAcceptingIssuerTest {
    public static final String WIREMOCK_SERVER = "http://10.0.2.2:1234";

    @Rule
    public IntentsTestRule<AddIssuerActivity> mActivityTestRule = new IntentsTestRule<AddIssuerActivity>(AddIssuerActivity.class);

    @Test
    public void addAcceptingIssuerTest() {
        onView(withId(R.id.add_issuer_url_edit_text)).perform(replaceText(WIREMOCK_SERVER + "/issuer/accepting-estimate-unsigned"), closeSoftKeyboard());
        onView(withId(R.id.add_issuer_nonce_edit_text)).perform(replaceText("1234"), closeSoftKeyboard());
        onView(withId(R.id.import_button)).perform(click());

//        intended(hasComponent(IssuerActivity.class.getName()));
//        intended(toPackage("com.learningmachine.android.app.ui.issuer.IssuerActivity"));
        Timber.i("Did it work??");
    }
}
