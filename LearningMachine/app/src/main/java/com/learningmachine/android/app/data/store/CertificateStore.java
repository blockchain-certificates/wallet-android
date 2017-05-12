package com.learningmachine.android.app.data.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.model.LMAssertion;
import com.learningmachine.android.app.data.model.LMDocument;
import com.learningmachine.android.app.data.store.cursor.CertificateCursorWrapper;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.data.webservice.response.AddCertificateResponse;
import com.learningmachine.android.app.data.webservice.response.CertificateResponse;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.ArrayList;
import java.util.List;

public class CertificateStore implements DataStore {

    private SQLiteDatabase mDatabase;

    public CertificateStore(LMDatabaseHelper databaseHelper) {
        mDatabase = databaseHelper.getWritableDatabase();
    }

    public Certificate loadCertificate(String certUuid) {

        Certificate certificate = null;
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

    public List<Certificate> loadCertificatesForIssuer(String issuerUuid) {
        List<Certificate> certificateList = new ArrayList<>();

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
                Certificate certificate = cursorWrapper.getCertificate();
                certificateList.add(certificate);
                cursorWrapper.moveToNext();
            }
        }

        cursor.close();

        return certificateList;
    }

    public void saveAddCertificateResponse(AddCertificateResponse response) {
        LMDocument document = response.getDocument();
        CertificateResponse certificateResponse = document.getCertificateResponse();

        LMAssertion assertion = document.getLMAssertion();
        String uuid = assertion.getUuid();
        certificateResponse.setUuid(uuid);

        IssuerResponse issuerResponse = certificateResponse.getIssuerResponse();
        String issuerUuid = issuerResponse.getUuid();
        certificateResponse.setIssuerUuid(issuerUuid);

        saveCertificate(certificateResponse);
    }

    public void saveCertificate(Certificate certificate) {
        ContentValues contentValues = new ContentValues();

        String certUuid = certificate.getUuid();
        String issuerUuid = certificate.getIssuerUuid();

        contentValues.put(LMDatabaseHelper.Column.Certificate.UUID, certUuid);
        contentValues.put(LMDatabaseHelper.Column.Certificate.NAME, certificate.getName());
        contentValues.put(LMDatabaseHelper.Column.Certificate.DESCRIPTION, certificate.getDescription());
        contentValues.put(LMDatabaseHelper.Column.Certificate.ISSUER_UUID, issuerUuid);

        if (loadCertificate(certUuid) == null) {
            mDatabase.insert(LMDatabaseHelper.Table.CERTIFICATE,
                    null,
                    contentValues);
        } else {
            mDatabase.update(LMDatabaseHelper.Table.CERTIFICATE,
                    contentValues,
                    LMDatabaseHelper.Column.Certificate.UUID + " = ? ",
                    new String[] { certUuid });
        }
    }

    @Override
    public void reset() {
        mDatabase.delete(LMDatabaseHelper.Table.CERTIFICATE, null, null);
    }
}
