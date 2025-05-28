package com.hyland.android.app.util;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {
    @Test
    public void isDidTrue () {
        final String validDid = "did:example:1234567890";
        assertTrue(StringUtils.isDid(validDid));
    }

    @Test
    public void isDidMalformedTooLongFalse () {
        final String validDid = "did:example:1234567890:xyz";
        assertFalse(StringUtils.isDid(validDid));
    }

    @Test
    public void isDidMalformedTooShortFalse () {
        final String validDid = "did:example";
        assertFalse(StringUtils.isDid(validDid));
    }

    @Test
    public void isDidFalse () {
        final String invalidDid = "https://www.did.com";
        assertFalse(StringUtils.isDid(invalidDid));
    }
}