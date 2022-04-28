package com.learningmachine.android.test.stubs;

import com.learningmachine.android.test.helpers.FileHelpers;

import com.learningmachine.android.app.data.webservice.request.IssuerAnalytic;
import com.learningmachine.android.app.data.webservice.request.IssuerIntroductionRequest;
import com.learningmachine.android.app.data.webservice.response.DidResponse;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.data.webservice.IssuerService;

import com.google.gson.Gson;
import rx.Observable;

public class StubIssuerService implements IssuerService {
    public Observable<DidResponse> getIssuerDID(String url) {
        DidResponse parsedResponse = null;
        try {
            if (url.contains("did:ion:EiA_Z6LQILbB2zj_eVrqfQ2xDm4HNqeJUw5Kj2Z7bFOOeQ")) {
                final String didResponseFixtureString = FileHelpers.readFileAsString("/src/test/resources/did/didUniversalResolverResponse.json");
                Gson gson = new Gson();
                parsedResponse = gson.fromJson(didResponseFixtureString, DidResponse.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Observable.just(parsedResponse);
    };

    public Observable<IssuerResponse> getIssuer(String url) {
        IssuerResponse parsedResponse = null;
        try {
            if (url.equals("https://www.blockcerts.org/samples/3.0/issuer-blockcerts.json")) {
                final String issuerResponseString = FileHelpers.readFileAsString("/src/test/resources/issuer/issuer-blockcerts.json");
                Gson gson = new Gson();
                parsedResponse = gson.fromJson(issuerResponseString, IssuerResponse.class);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Observable.just(parsedResponse);
    };

    public Observable<Void> postIntroduction(String url, IssuerIntroductionRequest request) {
        return Observable.just(getVoid());
    };

    public Observable<Void> postIssuerAnalytics(String url, IssuerAnalytic issuerAnalytic) {
        return Observable.just(getVoid());
    };

    private Void getVoid () {
        return null;
    }
}
