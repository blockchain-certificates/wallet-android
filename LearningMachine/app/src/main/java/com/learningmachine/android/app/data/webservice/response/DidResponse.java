package com.learningmachine.android.app.data.webservice.response;

import com.learningmachine.android.app.data.model.DidDocument;

import com.google.gson.annotations.SerializedName;

public class DidResponse {
    /** Structure of the JSONLD response document
     * @context (currently https://w3id.org/did-resolution/v1)
     * didDocument
     * didResolutionMetadata
     * didDocumentMetadata
     *
     * We are only interested in didDocument at this moment
     */

    @SerializedName("didDocument")
    private DidDocument mDidDocument;

    public String getIssuerProfile () {
        return mDidDocument.getIssuerProfile();
    }
}
