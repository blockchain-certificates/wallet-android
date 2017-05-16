package com.learningmachine.android.app.data.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.VisibleForTesting;

public class LMDatabaseHelper extends SQLiteOpenHelper {

    @VisibleForTesting static final String DB_NAME = "com.learningmachine.android.app.sqlite";

    private static final int DB_VERSION = 1;

    public LMDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createIssuerTable(sqLiteDatabase);
        createIssuerKeyTable(sqLiteDatabase);
        createRevocationKeyTable(sqLiteDatabase);
        createCertificateTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    static class Table {
        static final String ISSUER = "Issuer";
        static final String ISSUER_KEY = "IssuerKey";
        static final String REVOCATION_KEY = "RevocationKey";
        static final String CERTIFICATE = "Certificate";
    }

    public static class Column {
        public static class Issuer {
            public static final String ID = "id";
            public static final String NAME = "name";
            public static final String EMAIL = "email";
            public static final String UUID = "uuid";
            public static final String CERTS_URL = "certs_url";
            public static final String INTRO_URL = "intro_url";
        }

        public static class KeyRotation {
            public static final String ID = "id";
            public static final String ISSUER_UUID = "issuer_uuid";
            public static final String KEY = "key";
            public static final String CREATED_DATE = "created_date";
        }

        public static class Certificate {
            public static final String ID = "id";
            public static final String UUID = "uuid";
            public static final String ISSUER_UUID = "issuer_id";
            public static final String NAME = "name";
            public static final String DESCRIPTION = "description";
            public static final String ISSUE_DATE = "issue_date";
        }
    }

    private void createIssuerTable(SQLiteDatabase sqLiteDatabase) {
        final String createTable = "CREATE TABLE "
                + Table.ISSUER
                + " ( " + Column.Issuer.ID + " TEXT PRIMARY KEY "
                + ", " + Column.Issuer.NAME + " TEXT"
                + ", " + Column.Issuer.EMAIL + " TEXT"
                + ", " + Column.Issuer.UUID + " TEXT"
                + ", " + Column.Issuer.CERTS_URL + " TEXT"
                + ", " + Column.Issuer.INTRO_URL + " TEXT"
                + ");";
        sqLiteDatabase.execSQL(createTable);
    }

    private void createIssuerKeyTable(SQLiteDatabase sqLiteDatabase) {
        createKeyRotationTable(sqLiteDatabase, Table.ISSUER_KEY);
    }

    private void createRevocationKeyTable(SQLiteDatabase sqLiteDatabase) {
        createKeyRotationTable(sqLiteDatabase, Table.REVOCATION_KEY);
    }

    private void createKeyRotationTable(SQLiteDatabase sqLiteDatabase, String tableName) {
        final String createTable = "CREATE TABLE "
                + tableName
                + " ( " + Column.KeyRotation.ID + " TEXT PRIMARY KEY "
                + ", " + Column.KeyRotation.ISSUER_UUID + " TEXT"
                + ", " + Column.KeyRotation.KEY + " TEXT"
                + ", " + Column.KeyRotation.CREATED_DATE + " TEXT"
                + ");";
        sqLiteDatabase.execSQL(createTable);
    }

    private void createCertificateTable(SQLiteDatabase sqLiteDatabase) {
        final String createTable = "CREATE TABLE "
                + Table.CERTIFICATE
                + " ( " + Column.Certificate.ID + " TEXT PRIMARY KEY "
                + ", " + Column.Certificate.UUID + " TEXT"
                + ", " + Column.Certificate.ISSUER_UUID + " TEXT"
                + ", " + Column.Certificate.NAME + " TEXT"
                + ", " + Column.Certificate.DESCRIPTION + " TEXT"
                + ", " + Column.Certificate.ISSUE_DATE + " TEXT"
                + ");";
        sqLiteDatabase.execSQL(createTable);
    }
}
