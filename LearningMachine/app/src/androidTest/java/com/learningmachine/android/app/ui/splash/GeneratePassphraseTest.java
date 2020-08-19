package com.learningmachine.android.app.ui.splash;


import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;

import androidx.test.filters.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.learningmachine.android.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;

import timber.log.Timber;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

//
// This is a multi-Activity test that requires the app to be in a known clean state. This is
// difficult to achieve programmatically (and reliably). So I'm leaving this test in so that once
// I put in the effort to reliably set app state before running tests, then I'll be able to use
// this test
//
// To re-enable, just uncomment the @LargeTest, @RunWith and @Test annotations.
//
// --Chris
//


//@LargeTest
//@RunWith(AndroidJUnit4.class)
public class GeneratePassphraseTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

//    @Test
    public void generatePassphraseTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.new_account_button), withText("New Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction onboardingViewPager = onView(
                allOf(withId(R.id.onboarding_view_pager),
                        childAtPosition(
                                allOf(withId(android.R.id.content),
                                        childAtPosition(
                                                withId(R.id.action_bar_root),
                                                1)),
                                0),
                        isDisplayed()));
        onboardingViewPager.perform(swipeLeft());

        ViewInteraction textView = onView(
                allOf(withId(R.id.onboarding_passphrase_content),
                        childAtPosition(
                                withParent(withId(R.id.onboarding_view_pager)),
                                3),
                        isDisplayed()));
        final String[] generatedPassphraseArray = { null };
        textView.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting the text from a text view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view;
                generatedPassphraseArray[0] = tv.getText().toString();
            }
        });
        final String generatedPassphrase = generatedPassphraseArray[0];
        Timber.i("Generated passphrase: " + generatedPassphrase);
//        textView.check(matches(withText("crater pioneer also space seven unhappy rare tail glove zero great noodle armed modify woman point guilt dry yellow inherit note reduce ticket brick")));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.onboarding_done_button), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.fragment_home_settings_menu_item), withContentDescription("Settings"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.settings_reveal_passphrase_text_view), withText("Reveal Passphrase"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                1)));
        appCompatTextView.perform(scrollTo(), click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.onboarding_passphrase_content),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                1),
                        isDisplayed()));

        final String[] displayedPassphraseArray = { null };
        textView2.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "Getting text from a text view";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view;
                displayedPassphraseArray[0] = tv.getText().toString();
            }
        });
        final String displayedPassphrase = displayedPassphraseArray[0];
        textView2.check(matches(withText(displayedPassphrase)));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
