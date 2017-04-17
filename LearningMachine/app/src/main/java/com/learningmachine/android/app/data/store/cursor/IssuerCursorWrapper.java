package com.learningmachine.android.app.data.store.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.store.LMDatabase;

public class IssuerCursorWrapper extends CursorWrapper {

    public IssuerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Issuer getIssuer() {
        String name = getString(getColumnIndex(LMDatabase.Column.Issuer.NAME));
        String email = getString(getColumnIndex(LMDatabase.Column.Issuer.EMAIL));
        String uuid = getString(getColumnIndex(LMDatabase.Column.Issuer.UUID));
        String certsUrl = getString(getColumnIndex(LMDatabase.Column.Issuer.CERTS_URL));
        String introUrl = getString(getColumnIndex(LMDatabase.Column.Issuer.INTRO_URL));

        return new Issuer(name, email, uuid, certsUrl, introUrl);
    }
}
