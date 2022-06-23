package com.learningmachine.android.app.data.verifier;

import com.google.gson.Gson;

public class VerificationSteps {
    public String label;
    public String labelPending;
    public String code;
    public SubSteps[] subSteps;
    public Suites[] suites;

    public class SubSteps {
        public String code;
        public String label;
        public String labelPending;
        public String parentStep;
        public String status;
    }

    public class Suites {
        public String proofType;
        public SubSteps[] subSteps;
    }

    public static VerificationSteps[] getFromString(String verificationSteps) {
        return new Gson().fromJson(verificationSteps, VerificationSteps[].class);
    }
}
