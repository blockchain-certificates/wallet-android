package com.learningmachine.android.app.ui.onboarding;


import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.learningmachine.android.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GoodExistingPassphraseTest {
    private static final String passphrase = "view virtual ice oven upon material humor vague vessel jacket aim clarify moral gesture canvas wing shoot average charge section issue inmate waste large";

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(OnboardingActivity.class);

    @Test
    public void badExistingPassphraseTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.existing_account_button), withText("I already have one"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        0),
                                2),
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

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.paste_passphrase_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                2),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.paste_passphrase_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(passphrase), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.doneButton), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.onboarding_view_pager),
                                        1),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        onView(withText("Invalid passphrase")).check(doesNotExist());
        onView(withText("Issuers")).check(matches(isDisplayed()));
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
