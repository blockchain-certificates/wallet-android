package com.learningmachine.android.app.data.store;

import android.content.Context;
import android.content.res.AssetManager;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        context = mock(Context.class);
        AssetManager assetManager = mock(AssetManager.class);
        when(context.getAssets()).thenReturn(assetManager);
        when(assetManager.open(any())).thenReturn(getClass().getClassLoader()
                .getResourceAsStream("sample-issuer.json"));

        mIssuerStore = new IssuerStore(context, database, imageStore);
    }

    @Test
    public void testIssuer_save_andLoad() throws Exception {
        String uuid = "https://www.learningmachine.com/sample-issuer/issuer.json";
        String certsUrl = "https://www.learningmachine.com/sample-issuer";
        String introUrl = "https://www.learningmachine.com/sample-issuer/intro/";
        String name = "Sample Issuer";
        String email = "sample-certificate@learningmachine.com";

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

        String tableName = LMDatabaseHelper.Table.ISSUER_KEY;
        mIssuerStore.saveKeyRotation(keyRotation, issuerUuid, tableName);
        List<KeyRotation> keyRotationList = mIssuerStore.loadKeyRotations(issuerUuid, tableName);

        assertFalse(ListUtils.isEmpty(keyRotationList));

        KeyRotation actualKeyRotation = keyRotationList.get(0);

        assertEquals(createdDate, actualKeyRotation.getCreatedDate());
        assertEquals(key, actualKeyRotation.getKey());
    }
}
