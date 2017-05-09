package com.learningmachine.android.app.data;

import android.content.Context;

import com.google.gson.Gson;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.response.AddCertificateResponse;
import com.learningmachine.android.app.util.FileUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;

public class CertificateManager {

    private Context mContext;
    private CertificateStore mCertificateStore;
    private CertificateService mCertificateService;

    public CertificateManager(Context context, CertificateStore certificateStore, CertificateService certificateService) {
        mContext = context;
        mCertificateStore = certificateStore;
        mCertificateService = certificateService;
    }

    /**
     * Currently returns a static filepath until saving of certificates is implemented
     *
     * @param uuid document.assertion.uid from the Certificate's json
     * @return filepath for the certificates json
     */
    public String getCertificateJsonFileUrl(String uuid) {
        return "file:///android_asset/sample-certificate.json";
    }

    public Observable<ResponseBody> addCertificate(String url) {
        return mCertificateService.getCertificate(url, "json")
                .map(responseBody -> {
                    saveCertificateResponse(responseBody);
                    return null;
                });
    }

    private void saveCertificateResponse(ResponseBody responseBody) {
        Gson gson = new Gson();
        try {
            AddCertificateResponse addCertificateResponse = gson.fromJson(responseBody.string(),
                    AddCertificateResponse.class);
            String uuid = addCertificateResponse.getDocument()
                    .getLMAssertion()
                    .getUuid();
            mCertificateStore.saveAddCertificateResponse(addCertificateResponse);

            FileUtils.saveCertificate(mContext, responseBody, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
