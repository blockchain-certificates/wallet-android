package com.learningmachine.android.app.data.store.cursor;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.LMDatabaseHelper;

public class CertificateCursorWrapper extends CursorWrapper {

    public CertificateCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public CertificateRecord getCertificate() {
        String certUuid = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.UUID));
        String issuerUuid = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.ISSUER_UUID));
        String name = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.NAME));
        String description = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.DESCRIPTION));
        String issueDate = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.ISSUE_DATE));
        String expirationDate = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.EXPIRATION_DATE));
        String urlString = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.URL));
        String metadata = getString(getColumnIndex(LMDatabaseHelper.Column.Certificate.METADATA));

        return new CertificateRecord(certUuid, issuerUuid, name, description, issueDate, urlString, metadata, expirationDate);
    }
}
