package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.store.LMDatabaseHelper;

/**
 * Created by chris on 1/16/18.
 */

public class TrackIssuingEstimateData implements Migration {
    public static final int BASE_VERSION = 2;

    @Override
    public int getBaseVersion() {
        return BASE_VERSION;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion) {
        assert oldVersion == BASE_VERSION;
        database.execSQL("alter table " + LMDatabaseHelper.Table.ISSUER
                + " add column " + LMDatabaseHelper.Column.Issuer.ISSUING_ESTIMATE_URL + " text");
        database.execSQL("alter table " + LMDatabaseHelper.Table.ISSUER
                + " add column " + LMDatabaseHelper.Column.Issuer.ISSUING_ESTIMATE_AUTH + " text");

    }
}
