package com.learningmachine.android.app.ui.onboarding;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.ACCOUNT_CHOOSER;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.GENERATE_PASSPHRASE;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.VIEW_PASSPHRASE;
import static com.learningmachine.android.app.ui.onboarding.OnboardingScreen.PASTE_PASSPHRASE;

public class OnboardingFlow implements Serializable {

    private int mPosition;
    private List<OnboardingScreen> mScreens;

    public enum FlowType {
        UNKNOWN(Arrays.asList(ACCOUNT_CHOOSER)),
        NEW_ACCOUNT(Arrays.asList(ACCOUNT_CHOOSER, GENERATE_PASSPHRASE, VIEW_PASSPHRASE)),
        EXISTING_ACCOUNT(Arrays.asList(ACCOUNT_CHOOSER, PASTE_PASSPHRASE));

        private List<OnboardingScreen> mScreens;

        FlowType(List<OnboardingScreen> mScreens) {
            this.mScreens = mScreens;
        }

        public List<OnboardingScreen> getScreens() {
            return mScreens;
        }
    }

    public OnboardingFlow(FlowType flowType) {
        mScreens = flowType.getScreens();
    }

    public List<OnboardingScreen> getScreens() {
        return mScreens;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

}
