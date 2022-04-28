package com.learningmachine.android.app.data;

import com.learningmachine.android.test.stubs.StubIssuerService;
import com.learningmachine.android.test.helpers.FileHelpers;

import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;


import com.google.gson.Gson;
import org.junit.Test;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

public class IssuerManagerTest {
    private IssuerManager instance;
    private IssuerResponse expectedIssuerProfile;

    @Before
    public void setup () {
        IssuerStore mockIssuerStore = mock(IssuerStore.class);
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
}
