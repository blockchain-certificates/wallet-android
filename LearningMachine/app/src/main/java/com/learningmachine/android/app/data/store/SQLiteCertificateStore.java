package com.learningmachine.android.app.data.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.cursor.CertificateCursorWrapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SQLiteCertificateStore implements CertificateStore {

    private final SQLiteDatabase mDatabase;

    @Inject
    public SQLiteCertificateStore(LMDatabaseHelper databaseHelper) {
        mDatabase = databaseHelper.getWritableDatabase();
    }

    public CertificateRecord load(String certId) {
        CertificateRecord certificate = null;
        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.CERTIFICATE,
                null,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[]{certId},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            CertificateCursorWrapper cursorWrapper = new CertificateCursorWrapper(cursor);
            certificate = cursorWrapper.getCertificate();
        }

        cursor.close();

        return certificate;
    }

    public List<CertificateRecord> loadForIssuer(String issuerId) {
        List<CertificateRecord> certificateList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.CERTIFICATE,
                null,
                LMDatabaseHelper.Column.Certificate.ISSUER_UUID + " = ?",
                new String[]{issuerId},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            CertificateCursorWrapper cursorWrapper = new CertificateCursorWrapper(cursor);
            while (!cursorWrapper.isAfterLast()) {
                CertificateRecord certificate = cursorWrapper.getCertificate();
                certificateList.add(certificate);
                cursorWrapper.moveToNext();
            }
        }

        cursor.close();

        return certificateList;
    }

    public void save(BlockCert cert) {
        String certUid = cert.getCertUid();
        String urlString = cert.getUrl();
        String issuerId = cert.getIssuerId();
        String certName = cert.getCertName();
        String certDescription = cert.getCertDescription();
        String issueDate = cert.getIssueDate();
        String metadata = cert.getMetadata();
        String expirationDate = cert.getExpirationDate();

        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Certificate.UUID, certUid);
        contentValues.put(LMDatabaseHelper.Column.Certificate.NAME, certName);
        contentValues.put(LMDatabaseHelper.Column.Certificate.DESCRIPTION, certDescription);
        contentValues.put(LMDatabaseHelper.Column.Certificate.ISSUER_UUID, issuerId);
        contentValues.put(LMDatabaseHelper.Column.Certificate.ISSUE_DATE, issueDate);
        contentValues.put(LMDatabaseHelper.Column.Certificate.URL, urlString);
        contentValues.put(LMDatabaseHelper.Column.Certificate.EXPIRATION_DATE, expirationDate);
        contentValues.put(LMDatabaseHelper.Column.Certificate.METADATA, metadata);

        if (load(certUid) == null) {
            mDatabase.insert(LMDatabaseHelper.Table.CERTIFICATE,
                    null,
                    contentValues);
        } else {
            mDatabase.update(LMDatabaseHelper.Table.CERTIFICATE,
                    contentValues,
                    LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                    new String[]{certUid});
        }
    }

    public boolean delete(String certId) {
        // the delete operation should remove 1 row from the table
        return 1 == mDatabase.delete(LMDatabaseHelper.Table.CERTIFICATE,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[]{certId});
    }

    @Override
    public void reset() {
        mDatabase.delete(LMDatabaseHelper.Table.CERTIFICATE, null, null);
    }
}
