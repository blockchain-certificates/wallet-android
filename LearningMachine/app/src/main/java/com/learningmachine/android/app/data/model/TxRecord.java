package com.learningmachine.android.app.data.model;

import com.google.gson.annotations.SerializedName;
import com.learningmachine.android.app.util.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxRecord {
    @SerializedName("ver")
    private int mVersion;
    @SerializedName("out")
    private List<TxRecordOut> mOut;

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
}
