package com.learningmachine.android.app.data.store;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.store.cursor.IssuerCursorWrapper;
import com.learningmachine.android.app.data.store.cursor.KeyRotationCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class IssuerStore implements DataStore {

    private SQLiteDatabase mDatabase;

    public IssuerStore(LMDatabase database) {
        mDatabase = database.getWritableDatabase();
    }

    public List<Issuer> loadIssuers() {
        List<Issuer> issuerList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                LMDatabase.Table.ISSUER,
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

    private List<KeyRotation> loadIssuerKeys(String uuid) {
        return loadKeyRotation(uuid, LMDatabase.Table.ISSUER_KEY);
    }

    private List<KeyRotation> loadRevocationKeys(String uuid) {
        return loadKeyRotation(uuid, LMDatabase.Table.REVOCATION_KEY);
    }

    private List<KeyRotation> loadKeyRotation(String uuid, String tableName) {
        List<KeyRotation> keyRotationList = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                tableName,
                null,
                LMDatabase.Column.KeyRotation.ISSUER_UUID + " = ?",
                new String[] { uuid },
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

    }
}
