package com.learningmachine.android.app.ui.onboarding;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * ViewPager that disables swiping.
 */
public class OnboardingViewPager extends ViewPager {

    public OnboardingViewPager(Context context) {
        super(context);
    }

    public OnboardingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
