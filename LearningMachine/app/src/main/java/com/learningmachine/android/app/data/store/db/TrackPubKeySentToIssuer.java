package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class TrackPubKeySentToIssuer implements Migration {
    public static final int FROM_VERSION = 1;
    public static final int TO_VERSION = 2;

    @Override
    public int getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public int getToVersion() {
        return TO_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        assert oldVersion == FROM_VERSION;
        assert newVersion == TO_VERSION;
        database.execSQL("alter table " + LMDatabaseHelper.Table.ISSUER
                + " add column " + LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY + " text");
    }
}
