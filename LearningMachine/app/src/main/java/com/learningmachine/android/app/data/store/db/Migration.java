package com.learningmachine.android.app.data.store.db;

import android.database.sqlite.SQLiteDatabase;

public interface Migration {
    int getBaseVersion();

    void onUpgrade(SQLiteDatabase database, int oldVersion);
}
