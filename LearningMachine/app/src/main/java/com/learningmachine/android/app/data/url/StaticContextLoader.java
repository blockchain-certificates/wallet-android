package com.learningmachine.android.app.data.url.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import android.content.res.Resources;
import android.content.Context;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.apicatalog.vc.VcVocab;

import com.learningmachine.android.app.LMApplication;

import timber.log.Timber;

public class StaticContextLoader implements DocumentLoader {

    protected static final Map<String, Document> mStaticCache = defaultValues();

    public StaticContextLoader() {
        super();
    }

    @Override
    public Document loadDocument(final URI url, final DocumentLoaderOptions options) throws JsonLdError {
        Timber.i("Loading document %s", url);
        final Document document = mStaticCache.get(url.toString());

        if (document != null) {
            return document;
        }

        return null;
    }

    public static Map<String, Document> defaultValues() {

        Map<String, Document> staticCache = new LinkedHashMap<>();

        staticCache.put("https://www.w3.org/2018/credentials/v1", get("credentials_v1"));
        staticCache.put("https://www.w3.org/ns/credentials/v2", get("credentials_v2"));
        staticCache.put("https://www.w3.org/ns/did/v1", get("did_v1"));
        staticCache.put("https://w3id.org/security/data-integrity/v1", get("data_integrity_v1"));
        staticCache.put("https://w3id.org/security/data-integrity/v2", get("data_integrity_v2"));
        staticCache.put("https://w3id.org/security/multikey/v1", get("multikey_v1"));
        staticCache.put("https://w3id.org/blockcerts/v3", get("blockcerts_v3"));

        return Collections.unmodifiableMap(staticCache);
    }

    protected static JsonDocument get(final String name) {
        try {
            Context context = LMApplication.getContext();
            InputStream inputStream = context.getResources().openRawResource(
                    context.getResources().getIdentifier(name,
                            "raw", context.getPackageName()));
//            String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
            return JsonDocument.of(inputStream);

        } catch (JsonLdError e) {
            e.printStackTrace();
        }
        return null;
    }
}
