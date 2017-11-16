package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chris on 11/16/17.
 */

public class IssuingEstimate {
    @SerializedName("title")
    private String mTitle;

    @SerializedName("willIssueOn")
    private String mWillIssueOn;

    public String getTitle() { return mTitle; }
    public String getWillIssueOn() { return mWillIssueOn; }

}
