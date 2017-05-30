package com.learningmachine.android.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.learningmachine.android.app.ui.cert.AddCertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;

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
            finish();
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

        try {
            String introUrl = URLDecoder.decode(issuerParts[0], "UTF-8");
            String nonce = URLDecoder.decode(issuerParts[1], "UTF-8");
            Intent intent = AddIssuerActivity.newIntent(this, introUrl, nonce);
            startActivity(intent);
        } catch (IOException e) {
            Timber.e(e, "Unable to decode Urls.");
        }
    }

    private void handleAddCertificateUri(String uriString) {
        String pathSuffix = getPathSuffix(uriString, ADD_CERT_PATH);
        if (StringUtils.isEmpty(pathSuffix)) {
            Timber.e("Launch uri missing the cert path suffix");
            return;
        }
        try {
            String certUrl = URLDecoder.decode(pathSuffix, "UTF-8");
            Intent intent = AddCertificateActivity.newIntent(this, certUrl);
            startActivity(intent);
        } catch (IOException e) {
            Timber.e(e, "Unable to decode Urls.");
        }
    }

    private String getPathSuffix(String uriString, String delimiter) {
        String[] uriSplit = uriString.split(delimiter);
        if (uriSplit.length < 2) {
            return null;
        }
        return uriSplit[1];
    }
}
