package com.learningmachine.android.app.ui.cert;

import com.learningmachine.android.app.ui.cert.CertificateFragment;
import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.cert.BlockCertParser;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


class CertificateFragmentTest {
	@Test
	public void displayHTMLV2 () {
		InputStream inputStream = getResourceAsStream("mainnet-valid-2.0.json");
		BlockCertParser blockCertParser = new BlockCertParser();
		BlockCert blockCert = blockCertParser.fromJson(inputStream);
		CertificateFragment certificateFragment = new CertificateFragment();

		String output = certificateFragment.displayHTML(blockCert);
		assertEquals(output, "<div></div>");
	}

    private InputStream getResourceAsStream(String name) {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(name);
        return inputStream;
    }
}

