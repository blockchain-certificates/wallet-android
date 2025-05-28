package com.hyland.android.app.data.error;

public class CertificateOwnershipException extends Exception {
    public CertificateOwnershipException() {
        super("Unable to add certificate, you are not the owner.");
    }
}
