package com.learningmachine.android.app.data.url;

import org.junit.Test;

import static com.learningmachine.android.app.data.url.LaunchType.*;
import static junit.framework.Assert.assertEquals;

public class SplashUrlDecoderTest {

    private static final String ADD_ISSUER_URL = "https://certificates.learningmachine.com/#/introduce-recipient/https:%2F%2Fcertificates.learningmachine.com%2Fissuer%2Fasdf.json/a*bcd";
    private static final String ADD_CERTIFICATE_URL = "https://certificates.learningmachine.com/#/import-certificate/https:%2F%2Fcertificates.learningmachine.com%2Fcert%2Fasdf.json";
    private static final String BLOCKCERTS_ADD_ISSUER_URL = "https://wallet.blockcerts.org/#/introduce-recipient/https:%2F%2Fcertificates.learningmachine.com%2Fissuer%2Fasdf.json/a*bcd";
    private static final String BLOCKCERTS_ADD_CERTIFICATE_URL = "https://wallet.blockcerts.org/#/import-certificate/https:%2F%2Fcertificates.learningmachine.com%2Fcert%2Fasdf.json";
    private static final String BLOCKCERTS_DIRECT_ADD_ISSUER_URL = "https://wallet.blockcerts.org/introduce-recipient/https:%2F%2Fcertificates.learningmachine.com%2Fissuer%2Fasdf.json/a*bcd";
    private static final String BLOCKCERTS_DIRECT_ADD_CERTIFICATE_URL = "https://wallet.blockcerts.org/import-certificate/https:%2F%2Fcertificates.learningmachine.com%2Fcert%2Fasdf.json";

    @Test
    public void emptyLaunchUrlLaunchesOnboarding() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType("");

        assertEquals(ONBOARDING, launchData.getLaunchType());
    }

    @Test
    public void nullLaunchUrlLaunchesOnboarding() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(null);

        assertEquals(ONBOARDING, launchData.getLaunchType());
    }

    @Test
    public void issuerUrlLaunchesIssuerFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(ADD_ISSUER_URL);

        assertEquals(ADD_ISSUER, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/issuer/asdf.json", launchData.getIntroUrl());
        assertEquals("a*bcd", launchData.getNonce());
    }

    @Test
    public void shortIssuerUrlLaunchesDefault() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType("https://a.com/#/introduce-recipient/https:%2F%2Fcertificates.learningmachine.com%2Fissuer%2Fasdf.json");

        assertEquals(ONBOARDING, launchData.getLaunchType());
    }

    @Test
    public void longIssuerUrlLaunchesIssuerFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(ADD_ISSUER_URL + "/extra");

        assertEquals(ADD_ISSUER, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/issuer/asdf.json", launchData.getIntroUrl());
        assertEquals("a*bcd", launchData.getNonce());
    }

    @Test
    public void certificateUrlLaunchesCertificateFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(ADD_CERTIFICATE_URL);

        assertEquals(ADD_CERTIFICATE, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/cert/asdf.json", launchData.getCertUrl());
    }

    @Test
    public void shortCertificateUrlLaunchesDefault() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType("https://certificates.learningmachine.com/#/import-certificate/");

        assertEquals(ONBOARDING, launchData.getLaunchType());
    }

    @Test
    public void blockcertsAddIssuerUrlLaunchesIssuerFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(BLOCKCERTS_ADD_ISSUER_URL);

        assertEquals(ADD_ISSUER, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/issuer/asdf.json", launchData.getIntroUrl());
        assertEquals("a*bcd", launchData.getNonce());
    }

    @Test
    public void blockcertsCertificateUrlLaunchesCertificateFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(BLOCKCERTS_ADD_CERTIFICATE_URL);

        assertEquals(ADD_CERTIFICATE, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/cert/asdf.json", launchData.getCertUrl());
    }

    @Test
    public void blockcertsDirectAddIssuerUrlLaunchesIssuerFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(BLOCKCERTS_DIRECT_ADD_ISSUER_URL);

        assertEquals(ADD_ISSUER, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/issuer/asdf.json", launchData.getIntroUrl());
        assertEquals("a*bcd", launchData.getNonce());
    }

    @Test
    public void blockcertsDirectAddCertificateUrlLaunchesCertificateFlow() {
        LaunchData launchData = SplashUrlDecoder.getLaunchType(BLOCKCERTS_DIRECT_ADD_CERTIFICATE_URL);

        assertEquals(ADD_CERTIFICATE, launchData.getLaunchType());
        assertEquals("https://certificates.learningmachine.com/cert/asdf.json", launchData.getCertUrl());
    }

}
