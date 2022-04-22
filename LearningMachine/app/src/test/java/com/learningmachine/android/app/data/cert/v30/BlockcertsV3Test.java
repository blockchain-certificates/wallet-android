package com.learningmachine.android.app.data.cert.v30;

import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.cert.BlockCert;

import com.learningmachine.android.test.helpers.FileHelpers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockcertsV3Test {
  @Test
  public void testGetCertUid () {
    try {
      final String jsonV3String = FileHelpers.readFileAsString("/src/test/resources/v3/testnet-valid.json");
      final BlockCertParser blockCertParser = new BlockCertParser();
      final BlockCert blockCert = blockCertParser.fromJson(jsonV3String);
      assertEquals("urn:uuid:13172c8c-efa5-49e1-9f69-a67ba6bd9937", blockCert.getCertUid());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testGetIssuerIdIssuerProfileIsObject () {
    try {
      final String jsonV3String = FileHelpers.readFileAsString("/src/test/resources/v3/testnet-valid.json");
      final BlockCertParser blockCertParser = new BlockCertParser();
      final BlockCert blockCert = blockCertParser.fromJson(jsonV3String);
      assertEquals("did:ion:EiA_Z6LQILbB2zj_eVrqfQ2xDm4HNqeJUw5Kj2Z7bFOOeQ", blockCert.getIssuerId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testGetIssuerIdIssuerProfileIsString () {
    try {
      final String jsonV3String = FileHelpers.readFileAsString("/src/test/resources/v3/testnet-display-png.json");
      final BlockCertParser blockCertParser = new BlockCertParser();
      final BlockCert blockCert = blockCertParser.fromJson(jsonV3String);
      assertEquals("https://raw.githubusercontent.com/blockchain-certificates/cert-issuer/master/examples/issuer/profile.json", blockCert.getIssuerId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
