package com.learningmachine.android.app.data.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.ListUtils;
import com.learningmachine.android.app.util.StringUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SQLiteIssuerStore implements IssuerStore {

    private final SQLiteDatabase mDatabase;
    private final ImageStore mImageStore;

    @Inject
    public SQLiteIssuerStore(SQLiteDatabase database, ImageStore imageStore) {
        mDatabase = database;
        mImageStore = imageStore;
    }

    @Override
    public void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey) {
        if (issuerResponse == null) {
            return;
        }

        String uuid = issuerResponse.getUuid();
        String imageData = issuerResponse.getImageData();
        mImageStore.saveImage(uuid, imageData);

        String introducedOn = DateTime.now().toString();
        issuerResponse.setIntroducedOn(introducedOn);

        saveIssuer(issuerResponse, recipientPubKey);
    }

    @Override
    public void saveIssuer(IssuerRecord issuer, String recipientPubKey) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Issuer.NAME, issuer.getName());
        contentValues.put(LMDatabaseHelper.Column.Issuer.EMAIL, issuer.getEmail());
        contentValues.put(LMDatabaseHelper.Column.Issuer.ISSUERURL, issuer.getIssuerURL());
        contentValues.put(LMDatabaseHelper.Column.Issuer.INTRODUCED_ON, issuer.getIntroducedOn());
        contentValues.put(LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY, recipientPubKey);

        // Issuers in certificates are incomplete, do not overwrite data if it was there before
        String certsUrl = issuer.getCertsUrl();
        if (!StringUtils.isEmpty(certsUrl)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.CERTS_URL, certsUrl);
        }
        String introUrl = issuer.getIntroUrl();
        if (!StringUtils.isEmpty(introUrl)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.INTRO_URL, introUrl);
        }
        String analyticsUrlString = issuer.getAnalyticsUrlString();
        if (!StringUtils.isEmpty(analyticsUrlString)) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.ANALYTICS, analyticsUrlString);
        }
        String issuerId = issuer.getUuid();
        if (!ListUtils.isEmpty(issuer.getIssuerKeys())) {
            saveIssuerKeys(issuer.getIssuerKeys(), issuerId);
        }
        if (!ListUtils.isEmpty(issuer.getRevocationKeys())) {
            saveRevocationKeys(issuer.getRevocationKeys(), issuerId);
        }

        if (loadIssuer(issuerId) == null) {
            contentValues.put(LMDatabaseHelper.Column.Issuer.UUID, issuerId);
            mDatabase.insert(LMDatabaseHelper.Table.ISSUER,
                    null,
                    contentValues);
        } else {
            mDatabase.update(LMDatabaseHelper.Table.ISSUER,
                    contentValues, LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                    new String[]{issuerId});
        }
    }

    @Override
    public List<IssuerRecord> loadIssuers() {

        List<IssuerRecord> issuerList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.ISSUER,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            while (!cursorWrapper.isAfterLast()) {
                IssuerRecord issuer = cursorWrapper.getIssuer();

                List<KeyRotation> issuerKeys = loadIssuerKeys(issuer.getUuid());
                issuer.setIssuerKeys(issuerKeys);
                List<KeyRotation> revocationKeys = loadRevocationKeys(issuer.getUuid());
                issuer.setRevocationKeys(revocationKeys);

                issuerList.add(issuer);
                cursorWrapper.moveToNext();
            }
        }

        cursor.close();

        return issuerList;
    }

    @Override
    public IssuerRecord loadIssuer(String issuerId) {
        IssuerRecord issuer = null;
        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.ISSUER,
                null,
                LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                new String[]{issuerId},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            issuer = cursorWrapper.getIssuer();
            List<KeyRotation> issuerKeys = loadIssuerKeys(issuer.getUuid());
            issuer.setIssuerKeys(issuerKeys);
            List<KeyRotation> revocationKeys = loadRevocationKeys(issuer.getUuid());
            issuer.setRevocationKeys(revocationKeys);
        }

        cursor.close();

        return issuer;
    }

    @Override
    public IssuerRecord loadIssuerForCertificate(String certId) {
        IssuerRecord issuer = null;

        String selectQuery = "SELECT "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ID + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.NAME + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.EMAIL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ISSUERURL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.UUID + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.CERTS_URL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.INTRO_URL + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.INTRODUCED_ON + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.ANALYTICS + ", "
                + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.RECIPIENT_PUB_KEY
                + " FROM "
                + LMDatabaseHelper.Table.ISSUER
                + " INNER JOIN " + LMDatabaseHelper.Table.CERTIFICATE
                + " ON " + LMDatabaseHelper.Table.ISSUER + "." + LMDatabaseHelper.Column.Issuer.UUID
                + " = " + LMDatabaseHelper.Table.CERTIFICATE + "." + LMDatabaseHelper.Column.Certificate.ISSUER_UUID
                + " WHERE " + LMDatabaseHelper.Table.CERTIFICATE + "." + LMDatabaseHelper.Column.Certificate.UUID
                + " = ?";

        Cursor cursor = mDatabase.rawQuery(selectQuery, new String[]{certId});

        if (cursor.moveToFirst()) {
            IssuerCursorWrapper cursorWrapper = new IssuerCursorWrapper(cursor);
            issuer = cursorWrapper.getIssuer();
            List<KeyRotation> issuerKeys = loadIssuerKeys(issuer.getUuid());
            issuer.setIssuerKeys(issuerKeys);
            List<KeyRotation> revocationKeys = loadRevocationKeys(issuer.getUuid());
            issuer.setRevocationKeys(revocationKeys);
        }

        cursor.close();

        return issuer;
    }

    @Override
    public void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.KeyRotation.KEY, keyRotation.getKey());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.CREATED_DATE, keyRotation.getCreatedDate());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID, issuerId);

        if (ListUtils.isEmpty(loadKeyRotations(issuerId, tableName))) {
            mDatabase.insert(tableName,
                    null,
                    contentValues);
        } else {
            mDatabase.update(tableName,
                    contentValues,
                    LMDatabaseHelper.Column.KeyRotation.KEY + " = ? "
                            + " AND " + LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID + " = ?",
                    new String[]{keyRotation.getKey(), issuerId});
        }
    }

    @Override
    public List<KeyRotation> loadKeyRotations(String issuerId, String tableName) {
        return null;
    }

    @Override
    public void reset() {
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER_KEY, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.REVOCATION_KEY, null, null);
        mImageStore.reset();
    }

    private List<KeyRotation> loadIssuerKeys(String issuerId) {
        return loadKeyRotations(issuerId, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private List<KeyRotation> loadRevocationKeys(String issuerId) {
        return loadKeyRotations(issuerId, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    private void saveIssuerKeys(Iterable<KeyRotation> keyRotations, String issuerId) {
        saveKeyRotations(keyRotations, issuerId, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private void saveRevocationKeys(Iterable<KeyRotation> keyRotations, String issuerId) {
        saveKeyRotations(keyRotations, issuerId, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    private void saveKeyRotations(Iterable<KeyRotation> keyRotations, String issuerId, String tableName) {
        for (KeyRotation keyRotation : keyRotations) {
            saveKeyRotation(keyRotation, issuerId, tableName);
        }
    }
}
