package com.learningmachine.android.app.data.store;

import android.content.Context;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.model.Certificate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class CertificateStoreTest {

    private CertificateStore mCertificateStore;

    @Before
    public void setup() {
        Context context = RuntimeEnvironment.application;
        LMDatabaseHelper databaseHelper = new LMDatabaseHelper(context);
        ImageStore imageStore = mock(ImageStore.class);
        mCertificateStore = new CertificateStore(databaseHelper, imageStore);
    }

    @Test
    public void testCertificate_save_andLoad() {
        String certUuid = "certUuid";
        String issuerUuid = "issuerUuid";
        String name = "Sample Certificate 1";
        String description = "Welcome to the sample certificate!";

        Certificate certificate = new Certificate(certUuid, issuerUuid, name, description);
        mCertificateStore.saveCertificate(certificate);

        Certificate actualCertificate = mCertificateStore.loadCertificate(certUuid, issuerUuid);

        assertNotNull(actualCertificate);
        assertEquals(certUuid, actualCertificate.getUuid());
        assertEquals(issuerUuid, actualCertificate.getIssuerUuid());
        assertEquals(name, actualCertificate.getName());
        assertEquals(description, actualCertificate.getDescription());
    }
}
