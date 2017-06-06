package com.learningmachine.android.app.data.error;

public class CertificateFileImportException extends Exception {
    public CertificateFileImportException() {
        super("Unable to import certificate from file");
    }
}
