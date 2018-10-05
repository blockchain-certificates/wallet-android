package com.learningmachine.android.app.data.verifier;

import com.google.gson.Gson;

public class VerificationSteps {
    public String label;
    public String labelPending;
    public String code;
    public SubSteps[] subSteps;

    public class SubSteps {
        public String code;
        public String label;
        public String labelPending;
        public String parentStep;
    }

    public static VerificationSteps[] getFromString(String verificationSteps) {
        return new Gson().fromJson(verificationSteps, VerificationSteps[].class);
    }
}
