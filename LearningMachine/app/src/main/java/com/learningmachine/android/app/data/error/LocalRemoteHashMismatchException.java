package com.learningmachine.android.app.data.error;

import android.content.Context;

import com.learningmachine.android.app.R;

class LocalRemoteHashMismatchException extends ExceptionWithResourceString {
    private final String mLocalHash;
    private final String mRemoteHash;

    public LocalRemoteHashMismatchException(String localHash, String remoteHash) {
        super(R.string.error_remote_and_local_hash_mismatch);
        mLocalHash = localHash;
        mRemoteHash = remoteHash;
    }

    public String getString(Context context) {
        return context.getString(getErrorMessageResId(), mLocalHash, mRemoteHash);
    }
}
