package com.learningmachine.android.app.data.store;

import android.content.Context;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.cert.v12.Assertion;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.cert.v12.Certificate;
import com.learningmachine.android.app.data.cert.v12.Document;
import com.learningmachine.android.app.data.cert.v12.Issuer;
import com.learningmachine.android.app.data.model.CertificateRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class CertificateStoreTest {

    private CertificateStore mCertificateStore;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        LMDatabaseHelper databaseHelper = new LMDatabaseHelper(context);
        mCertificateStore = new CertificateStore(databaseHelper);
    }

    @Test
    public void testCertificate_save_andLoad() throws URISyntaxException {
        String certUuid = "certUuid";
        String issuerUuid = "issuerUuid";
        String name = "Sample Certificate 1";
        String description = "Welcome to the sample certificate!";
        String issuedDate = "2017-05-11T18:28:27.415+00:00";
        String urlString = "https://certificates.learningmachine.com/certificate/sampelcertificate";

        BlockCertV12 blockchainCertificate = new BlockCertV12();
        Assertion assertion = new Assertion();
        assertion.setUid(certUuid);
        assertion.setIssuedOn(issuedDate);
        assertion.setId(new URI(urlString));
        Issuer issuer = new Issuer();
        issuer.setId(new URI(issuerUuid));

        Certificate certificate = new com.learningmachine.android.app.data.cert.v12.Certificate();
        certificate.setIssuer(issuer);
        certificate.setName(name);
        certificate.setDescription(description);

        Document document = new Document();
        document.setAssertion(assertion);
        document.setCertificate(certificate);
        blockchainCertificate.setDocument(document);

        mCertificateStore.saveBlockchainCertificate(blockchainCertificate);

        CertificateRecord actualCertificate = mCertificateStore.loadCertificate(certUuid);

        assertNotNull(actualCertificate);
        assertEquals(certUuid, actualCertificate.getUuid());
        assertEquals(issuerUuid, actualCertificate.getIssuerUuid());
        assertEquals(name, actualCertificate.getName());
        assertEquals(description, actualCertificate.getDescription());
        assertEquals(issuedDate, actualCertificate.getIssuedOn());
        assertEquals(urlString, actualCertificate.getUrlString());
    }
}
