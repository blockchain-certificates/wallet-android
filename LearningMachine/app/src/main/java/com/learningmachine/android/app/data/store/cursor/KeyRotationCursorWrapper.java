package com.learningmachine.android.app.data.store.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.LMDatabase;

public class KeyRotationCursorWrapper extends CursorWrapper {

    public KeyRotationCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public KeyRotation getKeyRotation() {
        String createdDate = getString(getColumnIndex(LMDatabase.Column.KeyRotation.CREATED_DATE));
        String key = getString(getColumnIndex(LMDatabase.Column.KeyRotation.KEY));

        return new KeyRotation(createdDate, key);
    }
}
