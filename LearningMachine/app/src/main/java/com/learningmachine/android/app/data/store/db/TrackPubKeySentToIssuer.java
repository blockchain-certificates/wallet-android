package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class TrackPubKeySentToIssuer implements Migration {
    public static final int BASE_VERSION = 1;

    @Override
    public int getBaseVersion() {
        return BASE_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion) {
        assert oldVersion == BASE_VERSION;
        database.execSQL("alter table " + LMDatabaseHelper.Table.ISSUER
                + " add column " + LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY + " text");
    }
}
