package com.learningmachine.android.app.data.cert;

import com.apicatalog.vc.holder.Holder;
import com.apicatalog.vc.processor.ExpandedVerifiable;
import com.apicatalog.ld.signature.SigningError;
import com.apicatalog.ld.DocumentError;
import com.apicatalog.vc.suite.SignatureSuite;
import com.apicatalog.jsonld.loader.DocumentLoader;
import jakarta.json.JsonObject;
import java.util.Collection;

public class SelectiveDisclosureHolder extends Holder {

    public SelectiveDisclosureHolder(final SignatureSuite... suites) {
        super(suites);
    }

    public ExpandedVerifiable deriveWithLoader(final JsonObject document, Collection<String> selectors, DocumentLoader loader) throws SigningError, DocumentError {
        return derive(document, selectors, loader);
    }
}