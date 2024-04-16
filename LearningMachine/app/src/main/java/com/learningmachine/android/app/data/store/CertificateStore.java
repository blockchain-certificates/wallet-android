package com.learningmachine.android.app.data.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;
import com.learningmachine.android.app.data.store.cursor.CertificateCursorWrapper;
import com.learningmachine.android.app.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class CertificateStore implements DataStore {

    private SQLiteDatabase mDatabase;

    public CertificateStore(LMDatabaseHelper databaseHelper) {
        mDatabase = databaseHelper.getWritableDatabase();
    }

    public CertificateRecord loadCertificate(String certUuid) {
        CertificateRecord certificate = null;
        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.CERTIFICATE,
                null,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[] { certUuid },
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

    public List<CertificateRecord> loadCertificatesForIssuer(String issuerUuid) {
        List<CertificateRecord> certificateList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.CERTIFICATE,
                null,
                LMDatabaseHelper.Column.Certificate.ISSUER_UUID + " = ?",
                new String[] { issuerUuid },
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

    public void saveBlockchainCertificate(BlockCert blockCert) {
        String certUid = blockCert.getCertUid();
        String urlString = blockCert.getUrl();
        String issuerId = blockCert.getIssuerId();
        String certName = blockCert.getCertName();
        String certDescription = blockCert.getCertDescription();
        String issueDate = blockCert.getIssueDate();
        String metadata = blockCert.getMetadata();
        String expirationDate = blockCert.getExpirationDate();

        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Certificate.UUID, certUid);
        contentValues.put(LMDatabaseHelper.Column.Certificate.NAME, certName);
        contentValues.put(LMDatabaseHelper.Column.Certificate.DESCRIPTION, certDescription);
        contentValues = storeIssuerId(contentValues, issuerId);
        contentValues.put(LMDatabaseHelper.Column.Certificate.ISSUE_DATE, issueDate);
        contentValues.put(LMDatabaseHelper.Column.Certificate.URL, urlString);
        contentValues.put(LMDatabaseHelper.Column.Certificate.EXPIRATION_DATE, expirationDate);
        contentValues.put(LMDatabaseHelper.Column.Certificate.METADATA, metadata);

        Timber.i("Saving certificate with UUID: %s", certUid);
        Timber.i("contentValues: %s", contentValues.toString());

        if (loadCertificate(certUid) == null) {
            mDatabase.insert(LMDatabaseHelper.Table.CERTIFICATE,
                    null,
                    contentValues);
        } else {
            updateCertificateEntry(certUid, contentValues);
        }
    }

    public void updateIssuerUuid (String certUid, String issuerId) {
        // when using DID, we don't know the issuer's issuer profile before fetching the documents
        ContentValues contentValues = new ContentValues();
        contentValues.put(LMDatabaseHelper.Column.Certificate.ISSUER_UUID, issuerId);
        updateCertificateEntry(certUid, contentValues);
    }

    public void updateCertificateIdFromLegacy(String oldId, String newId) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Certificate.UUID, newId);

        mDatabase.update(LMDatabaseHelper.Table.CERTIFICATE,
                contentValues,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[] { oldId });
    }

    public boolean deleteCertificate(String uuid) {
        // the delete operation should remove 1 row from the table
        return 1 == mDatabase.delete(LMDatabaseHelper.Table.CERTIFICATE,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[] { uuid });
    }

    @Override
    public void reset() {
        mDatabase.delete(LMDatabaseHelper.Table.CERTIFICATE, null, null);
    }

    private void updateCertificateEntry (String certUid, ContentValues contentValues) {
        mDatabase.update(LMDatabaseHelper.Table.CERTIFICATE,
                contentValues,
                LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                new String[] { certUid });
    }

    private ContentValues storeIssuerId (ContentValues contentValues, String issuerId) {
        final String didColumn = LMDatabaseHelper.Column.Certificate.ISSUER_DID;
        final String idColumn = LMDatabaseHelper.Column.Certificate.ISSUER_UUID;
        final String targetColumn = StringUtils.isDid(issuerId) ? didColumn : idColumn;

        contentValues.put(targetColumn, issuerId);
        return contentValues;
    }
}
