package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.BuildConfig;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.util.GsonParserUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.learningmachine.android.app.util.GsonParserUtil.ISSUER_STARK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class IssuerStoreTest {

    private IssuerStore mIssuerStore;

    @Before
    public void setup() {
        ImageStore imageStore = mock(ImageStore.class);
        LMDatabase database = new LMDatabase(RuntimeEnvironment.application);
        mIssuerStore = new IssuerStore(database, imageStore);
    }

    @Test
    public void testIssuerCanBeSaved_andLoaded() throws Exception {
        GsonParserUtil gsonParserUtil = new GsonParserUtil();
        IssuerResponse issuerResponse = (IssuerResponse) gsonParserUtil.loadModelObject(ISSUER_STARK,
                IssuerResponse.class);

        assertNotNull(issuerResponse);
        String uuid = issuerResponse.getUuid();

        mIssuerStore.saveIssuerResponse(issuerResponse);
        Issuer issuer = mIssuerStore.loadIssuer(uuid);

        assertNotNull(issuer);
        assertEquals(issuerResponse.getName(), issuer.getName());
        assertEquals(issuerResponse.getEmail(), issuer.getEmail());
        assertEquals(uuid, issuer.getUuid());
        assertEquals(issuerResponse.getCertsUrl(), issuer.getCertsUrl());
        assertEquals(issuerResponse.getIntroUrl(), issuer.getIntroUrl());
    }


}
