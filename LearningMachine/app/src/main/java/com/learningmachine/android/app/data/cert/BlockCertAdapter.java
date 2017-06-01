package com.learningmachine.android.app.data.cert;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.learningmachine.android.app.data.cert.v11.BlockCertV11;
import com.learningmachine.android.app.data.cert.v12.BlockCertV12;
import com.learningmachine.android.app.data.cert.v20.BlockCertV20;

import java.lang.reflect.Type;

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
        if (isV20(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV20.class);
            jsonObject.remove("signature");
            blockCert.setDocumentNode(jsonObject);
        } else if (isV12(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV12.class);
            blockCert.setDocumentNode(jsonObject.getAsJsonObject("document"));
        } else if (isV11(jsonObject)) {
            blockCert = context.deserialize(json, BlockCertV11.class);
            blockCert.setDocumentNode(jsonObject);
        }
        return blockCert;
    }

    private boolean isV11(JsonObject jsonObject) {
        if (jsonObject.get("certificate") != null
                && jsonObject.get("assertion") != null
                && jsonObject.get("verify") != null
                && jsonObject.get("recipient") != null
                && jsonObject.get("signature") != null
                && jsonObject.get("extension") != null) {
            return true;
        }
        return false;
    }

    private boolean isV12(JsonObject jsonObject) {
        if (jsonObject.get("@context") != null
                && jsonObject.get("type") != null
                && jsonObject.get("document") != null
                && jsonObject.get("receipt") != null) {
            return true;
        }
        return false;
    }

    private boolean isV20(JsonObject jsonObject) {
        if (jsonObject.get("type") != null
                && jsonObject.get("badge") != null
                && jsonObject.get("signature") != null
                && jsonObject.get("recipient") != null) {
            return true;
        }
        return false;
    }
}
