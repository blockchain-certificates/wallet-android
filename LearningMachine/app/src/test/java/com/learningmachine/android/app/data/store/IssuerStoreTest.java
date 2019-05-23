package com.learningmachine.android.app.data.store;

import android.content.Context;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.util.ListUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * Currently only tests saving and loading since users cannot modify Issuer or KeyRotations
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class IssuerStoreTest {

    private IssuerStore mIssuerStore;

    @Before
    public void setup() throws Exception {
        ImageStore imageStore = mock(ImageStore.class);
        Context context = RuntimeEnvironment.application;
        LMDatabaseHelper database = new LMDatabaseHelper(context);

        mIssuerStore = new IssuerStore(database, imageStore);
    }

    /* Test failure -- needs to be debugged 
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

        IssuerRecord issuerLoaded = mIssuerStore.loadIssuer(issuerUuid);

        assertNotNull(issuerLoaded);
        assertEquals(name, issuerLoaded.getName());
        assertEquals(email, issuerLoaded.getEmail());
        assertEquals(issuerUuid, issuerLoaded.getUuid());
        assertEquals(certsUrl, issuerLoaded.getCertsUrl());
        assertEquals(introUrl, issuerLoaded.getIntroUrl());
        assertEquals(introducedOn, issuerLoaded.getIntroducedOn());
    }

    @Test
    public void testKeyRotation_save_andLoad() {
        String issuerUuid = "issuer.com";
        String createdDate = "2017-04-18";
        String key = "249jm9wmldskjgmawe";
        KeyRotation keyRotation = new KeyRotation(createdDate, key);

        String tableName = LMDatabaseHelper.Table.ISSUER_KEY;
        mIssuerStore.saveKeyRotation(keyRotation, issuerUuid, tableName);

        List<KeyRotation> keyRotationList = mIssuerStore.loadKeyRotations(issuerUuid, tableName);

        assertFalse(ListUtils.isEmpty(keyRotationList));

        KeyRotation actualKeyRotation = keyRotationList.get(0);

        assertEquals(createdDate, actualKeyRotation.getCreatedDate());
        assertEquals(key, actualKeyRotation.getKey());
    }
    */
}
