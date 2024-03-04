package com.learningmachine.android.app.data;

import com.learningmachine.android.test.stubs.StubIssuerService;
import com.learningmachine.android.test.helpers.FileHelpers;
import com.learningmachine.android.test.helpers.BlockCertHelpers;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;


import com.google.gson.Gson;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

public class IssuerManagerTest {
    private IssuerManager instance;
    private IssuerResponse expectedIssuerProfile;

    @Before
    public void setup () {
        IssuerStore mockIssuerStore = mock(IssuerStore.class);
        doNothing().when(mockIssuerStore).saveIssuerResponse(any(IssuerResponse.class), anyString());
        instance = new IssuerManager(mockIssuerStore, new StubIssuerService());
        expectedIssuerProfile = FileHelpers
                .readFileAsClass("/src/test/resources/issuer/issuer-blockcerts.json", IssuerResponse.class);
    }

    @Test
    public void fetchIssuerWithDidReturnsIssuerProfile () {
        instance.fetchIssuer("did:ion:EiA_Z6LQILbB2zj_eVrqfQ2xDm4HNqeJUw5Kj2Z7bFOOeQ")
                .subscribe(issuerResponse -> assertThat(expectedIssuerProfile, jsonEquals(issuerResponse)));
    }

    @Test
    public void fetchIssuerWithUrlReturnsIssuerProfile () {
        instance.fetchIssuer("https://www.blockcerts.org/samples/3.0/issuer-blockcerts.json")
                .subscribe(issuerResponse -> assertThat(expectedIssuerProfile, jsonEquals(issuerResponse)));
    }

    @Test
    public void fetchAndSaveIssuerOfDidBlockcertReturnsIssuerId () {
        final BlockCert fixture = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
        instance.fetchAndSaveIssuerOf(fixture)
                .subscribe(issuerId -> assertEquals("https://www.blockcerts.org/samples/3.0/issuer-blockcerts.json", issuerId));
    }
}
