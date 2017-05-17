package com.learningmachine.android.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.util.StringUtils;

import timber.log.Timber;

public class UrlLaunchActivity extends LMActivity {

    private static final String ADD_ISSUER_PATH = "introduce-recipient/";
    private static final String ADD_CERT_PATH = "import-certificate/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        String uriString = data.toString();
        if (StringUtils.isEmpty(uriString)) {
            Timber.e("Launch uri is empty");
            return;
        }

        if (uriString.contains(ADD_ISSUER_PATH)) {
            handleAddIssuerUri(uriString);
        } else if (uriString.contains(ADD_CERT_PATH)) {
            handleAddCertificateUri(uriString);
        } else {
            Timber.e("Launch uri has unknown issuer / certificate format");
        }
        finish();
    }

    private void handleAddIssuerUri(String uriString) {
        String pathSuffix = getPathSuffix(uriString, ADD_ISSUER_PATH);
        if (StringUtils.isEmpty(pathSuffix)) {
            Timber.e("Launch uri missing the issuer path suffix");
            return;
        }

        String[] issuerParts = pathSuffix.split("/");
        if (issuerParts.length < 2) {
            Timber.e("Launch uri missing issuer path parts");
            return;
        }
        Intent intent = AddIssuerActivity.newIntent(this, issuerParts[0], issuerParts[1]);
        startActivity(intent);
    }

    private void handleAddCertificateUri(String uriString) {
        String pathSuffix = getPathSuffix(uriString, ADD_CERT_PATH);
        if (StringUtils.isEmpty(pathSuffix)) {
            Timber.e("Launch uri missing the cert path suffix");
            return;
        }

        Intent intent = AddCertificateActivity.newIntent(this, pathSuffix);
        startActivity(intent);
    }

    private String getPathSuffix(String uriString, String delimeter) {
        String[] uriSplit = uriString.split(delimeter);
        if (uriSplit.length < 1) {
            return null;
        }
        return uriSplit[1];
    }
}
