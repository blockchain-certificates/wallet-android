package com.hyland.android.app.data.webservice.response;

import com.learningmachine.android.test.helpers.FileHelpers;

import com.google.gson.Gson;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class DidResponseTest {
    @Test
    public void getIssuerProfileUrlTest () {
        try {
            final String didResponseFixtureString = FileHelpers.readFileAsString("/src/test/resources/did/didUniversalResolverResponse.json");
            Gson gson = new Gson();
            final DidResponse parsedResponse = gson.fromJson(didResponseFixtureString, DidResponse.class);
            assertEquals("https://www.blockcerts.org/samples/3.0/issuer-blockcerts.json", parsedResponse.getIssuerProfileUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
