package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.cert.BlockCert;
import com.learningmachine.android.app.data.model.CertificateRecord;

import java.util.List;

public interface CertificateStore extends DataStore {

    CertificateRecord load(String certId);

    List<CertificateRecord> loadForIssuer(String issuerId);

    void save(BlockCert cert);

    boolean delete(String certId);
}
