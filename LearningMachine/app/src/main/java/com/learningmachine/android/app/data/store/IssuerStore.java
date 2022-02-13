package com.learningmachine.android.app.data.store;

import androidx.annotation.VisibleForTesting;

import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;

import java.util.List;

import javax.inject.Singleton;

@Singleton
public interface IssuerStore extends DataStore {

    void saveIssuerResponse(IssuerResponse issuerResponse, String recipientPubKey);

    void saveIssuer(IssuerRecord issuer, String recipientPubKey);

    List<IssuerRecord> loadIssuers();

    IssuerRecord loadIssuer(String uuid);

    IssuerRecord loadIssuerForCertificate(String certUuid);

    @VisibleForTesting
    void saveKeyRotation(KeyRotation keyRotation, String issuerUuid, String tableName);

    @VisibleForTesting
    List<KeyRotation> loadKeyRotations(String issuerUuid, String tableName);

    @Override
    void reset();
}
