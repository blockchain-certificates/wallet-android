package com.learningmachine.android.app.data.web;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Issuer {

    @SerializedName("email")
    private String email;
    @SerializedName("id")
    private String id;
    @SerializedName("image")
    private String image;
    @SerializedName("introductionURL")
    private String introductionURL;
    @SerializedName("issuerKeys")
    private List<KeyRotation> issuerKeys;
    @SerializedName("name")
    private String name;
    @SerializedName("revocationKeys")
    private List<KeyRotation> revocationKeys;
    @SerializedName("url")
    private String url;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntroductionURL() {
        return introductionURL;
    }

    public void setIntroductionURL(String introductionURL) {
        this.introductionURL = introductionURL;
    }

    public List<KeyRotation> getIssuerKeys() {
        return issuerKeys;
    }

    public void setIssuerKeys(List<KeyRotation> issuerKeys) {
        this.issuerKeys = issuerKeys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KeyRotation> getRevocationKeys() {
        return revocationKeys;
    }

    public void setRevocationKeys(List<KeyRotation> revocationKeys) {
        this.revocationKeys = revocationKeys;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
