package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class TrackIssuerURL implements Migration {
    public static final int BASE_VERSION = 1;
    public static final int SECOND_VERSION = 2;

    @Override
    public int getBaseVersion() {
        return BASE_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion) {
        database.execSQL("alter table " + LMDatabaseHelper.Table.ISSUER
                + " add column " + LMDatabaseHelper.Column.Issuer.ISSUERURL + " text");
    }
}
