package com.learningmachine.android.app.data.store;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.List;

public interface IssuerStore extends DataStore {

    void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey);

    void saveIssuer(IssuerRecord issuer, String recipientPubKey);

    List<IssuerRecord> loadIssuers();

    IssuerRecord loadIssuer(String issuerId);

    IssuerRecord loadIssuerForCertificate(String certId);

    void saveKeyRotation(KeyRotation keyRotation, String issuerId, String tableName);

    List<KeyRotation> loadKeyRotations(String issuerId, String tableName);
}
