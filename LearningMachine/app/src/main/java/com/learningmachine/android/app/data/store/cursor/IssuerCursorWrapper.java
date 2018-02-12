package com.learningmachine.android.app.data.store.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class IssuerCursorWrapper extends CursorWrapper {

    public IssuerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public IssuerRecord getIssuer() {
        String name = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.NAME));
        String email = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.EMAIL));
        String issuerURL = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.ISSUERURL));
        String uuid = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.UUID));
        String certsUrl = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.CERTS_URL));
        String introUrl = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.INTRO_URL));
        String introducedOn = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.INTRODUCED_ON));
        String analytics = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.ANALYTICS));
        String recipientPubKey = getString(getColumnIndex(LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY));

        return new IssuerRecord(name, email, issuerURL, uuid, certsUrl, introUrl, introducedOn, analytics, recipientPubKey);
    }
}
