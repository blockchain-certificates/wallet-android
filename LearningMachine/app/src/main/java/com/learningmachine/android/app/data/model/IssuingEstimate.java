package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;

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
    public String getEstimateDescription() {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        DateTime estimateDate = dateTimeFormatter.parseDateTime(mWillIssueOn);

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        return "📅 Scheduled for " + format.format(estimateDate.toDate());
    }

}
