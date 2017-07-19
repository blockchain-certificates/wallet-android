package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {
    int getFromVersion();
    int getToVersion();
    void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
}
