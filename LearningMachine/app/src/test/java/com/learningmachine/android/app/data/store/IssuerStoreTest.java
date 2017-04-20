package com.learningmachine.android.app.data.store;

import android.content.Context;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.util.ListUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
        LMDatabase database = new LMDatabase(context);
        mIssuerStore = new IssuerStore(context, database, imageStore);
    }

    @Test
    public void testIssuer_save_andLoad() throws Exception {
        String uuid = "https://issuer.stark.com/issuer/lm-issuer.json";
        String certsUrl = "https://issuer.stark.com/";
        String introUrl = "https://issuer.stark.com/intro/";
        String name = "House Stark";
        String email = "contact@stark.com";

        Issuer issuer = mIssuerStore.loadIssuer(uuid);

        assertNotNull(issuer);
        assertEquals(name, issuer.getName());
        assertEquals(email, issuer.getEmail());
        assertEquals(uuid, issuer.getUuid());
        assertEquals(certsUrl, issuer.getCertsUrl());
        assertEquals(introUrl, issuer.getIntroUrl());
    }

    @Test
    public void testKeyRotation_save_andLoad() {
        String issuerUuid = "issuer.com";
        String createdDate = "2017-04-18";
        String key = "249jm9wmldskjgmawe";
        KeyRotation keyRotation = new KeyRotation(createdDate, key);

        String tableName = LMDatabase.Table.ISSUER_KEY;
        mIssuerStore.saveKeyRotation(keyRotation, issuerUuid, tableName);
        List<KeyRotation> keyRotationList = mIssuerStore.loadKeyRotations(issuerUuid, tableName);

        assertFalse(ListUtils.isEmpty(keyRotationList));

        KeyRotation actualKeyRotation = keyRotationList.get(0);

        assertEquals(createdDate, actualKeyRotation.getCreatedDate());
        assertEquals(key, actualKeyRotation.getKey());
    }
}
