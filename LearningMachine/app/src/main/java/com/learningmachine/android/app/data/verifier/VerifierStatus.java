package com.learningmachine.android.app.data.verifier;

import com.google.gson.Gson;

public class VerifierStatus {
    public String code;
    public String label;
    public String status;
    public String errorMessage;

    public static VerifierStatus getFromString(String status) {
        return new Gson().fromJson(status, VerifierStatus.class);
    }

    public boolean isSuccess() {
        return status.equals("success");
    }

    public boolean isFailure() {
        return status.equals("failure");
    }

    public boolean isStarting() {
        return status.equals("starting");
    }
}
