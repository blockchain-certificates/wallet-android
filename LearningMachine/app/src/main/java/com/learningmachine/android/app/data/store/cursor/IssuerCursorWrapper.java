package com.learningmachine.android.app.data.store.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class IssuerCursorWrapper extends CursorWrapper {

    public IssuerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Issuer getIssuer() {
        String name = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.NAME));
        String email = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.EMAIL));
        String uuid = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.UUID));
        String certsUrl = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.CERTS_URL));
        String introUrl = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.INTRO_URL));

        return new Issuer(name, email, uuid, certsUrl, introUrl);
    }
}
