package com.learningmachine.android.app.data.webservice;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;

import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import timber.log.Timber;

final class LMGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final TypeAdapter<T> adapter;
    private final Gson gson;

    LMGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        Buffer buffer = new Buffer();
        value.source().readAll(buffer);
        byte[] a = buffer.peek().readByteArray();
        value = ResponseBody.create(value.contentType(), value.contentLength(), buffer);
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        try {
            T result = adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("json not fully consumed.");
            }
            return result;
        } catch (MalformedJsonException e) {
            Timber.d(String.format("response body on error: %s", new String(a)));
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
