package com.learningmachine.android.app.data.cert;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.learningmachine.android.app.data.cert.v11.BlockCertV11;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;
import com.learningmachine.android.app.data.cert.v30.BlockCertV30;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.*;

import timber.log.Timber;

public class BlockCertAdapter implements JsonSerializer<BlockCert>, JsonDeserializer<BlockCert> {
    @Override
    public JsonElement serialize(BlockCert src, Type typeOfSrc, JsonSerializationContext context) {
        if (src instanceof BlockCertV12) {
            return context.serialize(src, BlockCertV12.class);
        } else if (src instanceof BlockCertV11) {
            return context.serialize(src, BlockCertV11.class);
        }
        return null;
    }

    @Override
    public BlockCert deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BlockCert blockCert = null;
        JsonObject jsonObject = json.getAsJsonObject();
        blockCert = context.deserialize(json, getAdapterClass(jsonObject));
        if (blockCert != null) {
            blockCert.setDocumentNode(jsonObject);
        }
        return blockCert;
    }

    private Class<?> getAdapterClass (JsonObject jsonObject) {
        final String version = getBlockcertsVersion(jsonObject);
        switch (version) {
            case "v1.1":
                return BlockCertV11.class;

            case "v1":
            case "v1.2":
                return BlockCertV12.class;

            case "v2":
            case "v2.0":
            case "v2.1":
                return BlockCertV20.class;

            case "v3":
            case "v3.0":
            case "v3.1":
                return BlockCertV30.class;

            default:
                Timber.e(String.format("Unrecognized blockcerts version: %s", version));
                return null;
        }
    }

    private String getBlockcertsVersion (JsonObject jsonObject) {
        if (jsonObject.get("@context") == null && isV11(jsonObject)) {
            return "v1.1";
        }
        ArrayList contextArray = new ArrayList<String>();
        final JsonElement context = jsonObject.get("@context");
        if (context.isJsonArray()) {
            final JsonArray contextObjectAsArray = jsonObject.getAsJsonArray("@context");
            getStringsFromContextArray(contextObjectAsArray, contextArray);
        } else {
            contextArray.add(context.toString());
        }

        final String blockcertsContextUrl = filterBlockcertsContext(contextArray);
        if (blockcertsContextUrl != "") {
            final String version = getVersionNumber(blockcertsContextUrl);
            if (version.startsWith("v")) {
                return version;
            }
            return "v" + version;
        }
        return "invalid blockcerts version";
    }

    private void getStringsFromContextArray (JsonArray array, ArrayList targetArray) {
        for (int i = 0; i < array.size(); i++) {
            if (!array.get(i).isJsonObject()) {
                targetArray.add(array.get(i).toString());
            }
        }
    }

    private String filterBlockcertsContext(ArrayList<String> contextArray) {
        Object[] result = contextArray.stream().filter(s -> s.contains("blockcerts")).toArray();
        if (result.length != 0) {
            return result[0].toString();
        }
        return "";
    }

    private String getVersionNumber (String blockcertsContextUrl) {
        Pattern versionRegexPattern = Pattern.compile("/(v?\\d{1}.?\\d?\\b)");
        Matcher matcher = versionRegexPattern.matcher(blockcertsContextUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private boolean isV11(JsonObject jsonObject) {
        if (jsonObject.get("certificate") != null
                && jsonObject.get("assertion") != null
                && jsonObject.get("verify") != null
                && jsonObject.get("recipient") != null
                && jsonObject.get("signature") != null
                && jsonObject.get("@context") == null) {
            return true;
        }
        return false;
    }
}
