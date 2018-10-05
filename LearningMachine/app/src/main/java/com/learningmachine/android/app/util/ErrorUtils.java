package com.learningmachine.android.app.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.learningmachine.android.app.data.error.LMApiError;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ErrorUtils {
    public static String getErrorFromThrowable(Throwable throwable) {

        String throwableBody = "";

        if (throwable instanceof HttpException) {
            ResponseBody body = ((HttpException) throwable).response().errorBody();
            Gson gson = new Gson();
            TypeAdapter<LMApiError> adapter = gson.getAdapter
                    (LMApiError
                            .class);
            try {
                LMApiError errorParser =
                        adapter.fromJson(body.string());

                throwableBody = "Code: " + errorParser.getCode() + ", Message: " + errorParser.getMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return throwableBody;
    }
}
