package com.learningmachine.android.app.data.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.data.store.cursor.KeyRotationCursorWrapper;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.GsonUtil;
import com.learningmachine.android.app.util.ListUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IssuerStore implements DataStore {

    private SQLiteDatabase mDatabase;
    private ImageStore mImageStore;

    public IssuerStore(LMDatabaseHelper databaseHelper, ImageStore imageStore) {
        mDatabase = databaseHelper.getWritableDatabase();
        mImageStore = imageStore;
    }

    public void saveIssuerResponse(IssuerResponse issuerResponse) {
        if (issuerResponse == null) {
            return;
        }

        String uuid = issuerResponse.getUuid();
        String imageData = issuerResponse.getImageData();
        mImageStore.saveImage(uuid, imageData);

        saveIssuer(issuerResponse);
    }

    private void saveIssuer(Issuer issuer) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.Issuer.NAME, issuer.getName());
        contentValues.put(LMDatabaseHelper.Column.Issuer.EMAIL, issuer.getEmail());
        contentValues.put(LMDatabaseHelper.Column.Issuer.UUID, issuer.getUuid());
        contentValues.put(LMDatabaseHelper.Column.Issuer.CERTS_URL, issuer.getCertsUrl());
        contentValues.put(LMDatabaseHelper.Column.Issuer.INTRO_URL, issuer.getIntroUrl());

        saveIssuerKeys(issuer.getIssuerKeys(), issuer.getUuid());
        saveRevocationKeys(issuer.getRevocationKeys(), issuer.getUuid());

        if (loadIssuer(issuer.getUuid()) == null) {
            mDatabase.insert(LMDatabaseHelper.Table.ISSUER,
                    null,
                    contentValues);
        } else {
            mDatabase.update(LMDatabaseHelper.Table.ISSUER,
                    contentValues, LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                    new String[] { issuer.getUuid() });
        }
    }

    public List<Issuer> loadIssuers() {
        List<Issuer> issuerList = new ArrayList<>();

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
                Issuer issuer = cursorWrapper.getIssuer();

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

    public Issuer loadIssuer(String uuid) {
        Issuer issuer = null;
        Cursor cursor = mDatabase.query(
                LMDatabaseHelper.Table.ISSUER,
                null,
                LMDatabaseHelper.Column.Issuer.UUID + " = ?",
                new String[] { uuid },
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

    private void saveIssuerKeys(List<KeyRotation> keyRotationList, String issuerUuid) {
        saveKeyRotations(keyRotationList, issuerUuid, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private void saveRevocationKeys(List<KeyRotation> keyRotationList, String issuerUuid) {
        saveKeyRotations(keyRotationList, issuerUuid, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    private void saveKeyRotations(List<KeyRotation> keyRotationList, String issuerUuid, String tableName) {
        for (KeyRotation keyRotation : keyRotationList) {
            saveKeyRotation(keyRotation, issuerUuid, tableName);
        }
    }

    @VisibleForTesting
    void saveKeyRotation(KeyRotation keyRotation, String issuerUuid, String tableName) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(LMDatabaseHelper.Column.KeyRotation.KEY, keyRotation.getKey());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.CREATED_DATE, keyRotation.getCreatedDate());
        contentValues.put(LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID, issuerUuid);

        if (ListUtils.isEmpty(loadKeyRotations(issuerUuid, tableName))) {
            mDatabase.insert(tableName,
                    null,
                    contentValues);
        } else {
            mDatabase.update(tableName,
                    contentValues,
                    LMDatabaseHelper.Column.KeyRotation.KEY + " = ? "
                    + " AND " + LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID + " = ?",
                    new String[] { keyRotation.getKey(), issuerUuid });
        }
    }

    private List<KeyRotation> loadIssuerKeys(String issuerUuid) {
        return loadKeyRotations(issuerUuid, LMDatabaseHelper.Table.ISSUER_KEY);
    }

    private List<KeyRotation> loadRevocationKeys(String issuerUuid) {
        return loadKeyRotations(issuerUuid, LMDatabaseHelper.Table.REVOCATION_KEY);
    }

    @VisibleForTesting
    List<KeyRotation> loadKeyRotations(String issuerUuid, String tableName) {
        List<KeyRotation> keyRotationList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                tableName,
                null,
                LMDatabaseHelper.Column.KeyRotation.ISSUER_UUID + " = ?",
                new String[] { issuerUuid },
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            KeyRotationCursorWrapper cursorWrapper = new KeyRotationCursorWrapper(cursor);
            while (!cursorWrapper.isAfterLast()) {
                KeyRotation keyRotation = cursorWrapper.getKeyRotation();
                keyRotationList.add(keyRotation);
                cursorWrapper.moveToNext();
            }
        }

        cursor.close();

        return keyRotationList;
    }

    @Override
    public void reset() {
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.ISSUER_KEY, null, null);
        mDatabase.delete(LMDatabaseHelper.Table.REVOCATION_KEY, null, null);
        mImageStore.reset();
    }
}
