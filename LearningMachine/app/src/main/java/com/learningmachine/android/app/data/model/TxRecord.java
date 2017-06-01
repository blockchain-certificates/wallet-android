package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.util.ListUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxRecord {
    private static final String OP_RETURN_PREFIX = "6a20";

    @SerializedName("ver")
    private int mVersion;
    @SerializedName("inputs")
    private List<TxRecordInput> mInputs;
    @SerializedName("out")
    private List<TxRecordOut> mOut;
    @SerializedName("time")
    private long mTimestamp;

    public TxRecordOut getInputsPreviousOut() {
        return ListUtils.isEmpty(mInputs) ? null : mInputs.get(0).getPreviousOut();
    }

    public TxRecordOut getLastOut() {
        return ListUtils.isEmpty(mOut) ? null : mOut.get(mOut.size() - 1);
    }

    public List<String> getRevoked() {
        if (ListUtils.isEmpty(mOut)) {
            return Collections.emptyList();
        }

        List<String> revoked = new ArrayList<>();
        for (TxRecordOut out : mOut) {
            if (out.isSpent()) {
                revoked.add(out.getAddress());
            }
        }
        return revoked;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public DateTime getDateTime() {
        // mTimestamp is in seconds, DateTime expects milliseconds
        return new DateTime(mTimestamp * 1000);
    }

    public String getRemoteHash() {
        TxRecordOut txRecordOut = getLastOut();
        if (txRecordOut == null) {
            return null;
        }
        if (txRecordOut.getValue() != 0) {
            return null;
        }
        String remoteHash = txRecordOut.getScript();
        // strip out 6a20 prefix, if present
        remoteHash = remoteHash.startsWith(OP_RETURN_PREFIX) ? remoteHash.substring(4) : remoteHash;
        return remoteHash;
    }
}
