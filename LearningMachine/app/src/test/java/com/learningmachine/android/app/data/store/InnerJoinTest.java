package com.learningmachine.android.app.data.store;

import android.content.Context;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.model.IssuerRecord;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 27)
public class InnerJoinTest {

    private IssuerStore mIssuerStore;
    private CertificateStore mCertificateStore;

    @Before
    public void setup() throws Exception {
        ImageStore imageStore = mock(ImageStore.class);
        Context context = RuntimeEnvironment.application;
        LMDatabaseHelper database = new LMDatabaseHelper(context);

        mIssuerStore = new IssuerStore(database, imageStore);
        mCertificateStore = new SQLiteCertificateStore(database);
    }

    @Test
    public void testIssuer_save_andLoad() throws Exception {
        String issuerUrl = "https://www.blockcerts.org/mockissuer/issuer/got-issuer.json";
        String issuerUuid = "http://www.blockcerts.org/mockissuer/issuer/got-issuer.json";
        String certsUrl = "http://www.blockcerts.org/mockissuer/certificates/";
        String introUrl = "http://www.blockcerts.org/mockissuer/intro/";
        String name = "Game of thrones issuer on testnet";
        String email = "org@org.org";
        String introducedOn = "2017-05-11T18:28:27.415+00:00";
        String analytics = "https://www.learningmachine.com/analytics";
        String recipientPubKey = "aaaabbbbcccc";

	//    public IssuerRecord(String name, String email, String issuerURL, String uuid, String certsUrl, String introUrl, String introducedOn, String analyticsUrlString, String recipientPubKey) 
        IssuerRecord issuerOrig = new IssuerRecord(name, email, issuerUrl, issuerUuid, certsUrl, introUrl, introducedOn, analytics, recipientPubKey);
        issuerOrig.setRevocationKeys(new ArrayList<>());
        issuerOrig.setIssuerKeys(new ArrayList<>());

        mIssuerStore.saveIssuer(issuerOrig, recipientPubKey);

        String certUuid = "certUuid";
        String certName = "Sample Certificate 1";
        String description = "Welcome to the sample certificate!";
        String issuedDate = "2017-05-11T18:28:27.415+00:00";
        String urlString = "https://certificates.learningmachine.com/certificate/samplecertificate";

        BlockCert blockCert = BlockCertV12.createInstance(certUuid, issuerUuid, certName, description, issuedDate, urlString);

        mCertificateStore.save(blockCert);

        IssuerRecord issuerLoaded = mIssuerStore.loadIssuerForCertificate(certUuid);

        assertNotNull(issuerLoaded);
        assertEquals(name, issuerLoaded.getName());
        assertEquals(email, issuerLoaded.getEmail());
        assertEquals(issuerUuid, issuerLoaded.getUuid());
        assertEquals(certsUrl, issuerLoaded.getCertsUrl());
        assertEquals(introUrl, issuerLoaded.getIntroUrl());
        assertEquals(introducedOn, issuerLoaded.getIntroducedOn());
    }

}
