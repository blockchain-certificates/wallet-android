package com.learningmachine.android.app.ui.cert;

public class CertificateInfoItemViewModel {

    @FunctionalInterface
    public interface Callback <R> {
        public R apply ();
    }

    private String mTitle;
    private String mValue;
    private Callback deleteButtonCallback;

    public CertificateInfoItemViewModel(String title, String value) {
        mTitle = title;
        mValue = value;
        deleteButtonCallback = null;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getValue() {
        return mValue;
    }

    public void pressDeleteButton() {
        deleteButtonCallback.apply();
    }

    public boolean isDeleteButton() {
        return deleteButtonCallback != null;
    }

    public void setIsDeleteButton(Callback deleteButtonCallback) {
        this.deleteButtonCallback = deleteButtonCallback;
    }
}
