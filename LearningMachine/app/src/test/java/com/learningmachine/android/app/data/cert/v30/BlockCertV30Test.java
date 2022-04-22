package com.learningmachine.android.app.data.cert.v30;

import com.learningmachine.android.app.data.cert.BlockCert;

import com.learningmachine.android.test.helpers.BlockCertHelpers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockCertV30Test {
  @Test
  public void testGetCertUid () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
    assertEquals("urn:uuid:13172c8c-efa5-49e1-9f69-a67ba6bd9937", blockCert.getCertUid());
  }

  @Test
  public void testGetCertName () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
    assertEquals("V3 issuance", blockCert.getCertName());
  }

  @Test
  public void testGetUrl () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
    // if the certificate is hosted, then its id should be the URL
    assertEquals(null, blockCert.getUrl());
  }

  @Test
  public void testGetCertDescription () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
    assertEquals("A v3 working example for test purposes", blockCert.getCertDescription());
  }

  @Test
  public void testGetIssuerIdIssuerProfileIsObject () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-valid.json");
    assertEquals("did:ion:EiA_Z6LQILbB2zj_eVrqfQ2xDm4HNqeJUw5Kj2Z7bFOOeQ", blockCert.getIssuerId());
  }

  @Test
  public void testGetIssuerIdIssuerProfileIsString () {
    final BlockCert blockCert = BlockCertHelpers.fileToBlockCertInstance("/src/test/resources/v3/testnet-display-png.json");
    assertEquals("https://raw.githubusercontent.com/blockchain-certificates/cert-issuer/master/examples/issuer/profile.json", blockCert.getIssuerId());
  }
}
