
package com.learningmachine.android.app.data.cert.v12;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class IssuerId {

    /**
     * list of issuer keys, listed in descending date order (most recent first). V1.2 change: renamed from issuer_key, added optional invalidated field.
     * (Required)
     * 
     */
    @SerializedName("issuerKeys")
    @Expose
    private List<IssuerKey> issuerKeys = new ArrayList<IssuerKey>();
    /**
     * list of revocation keys, listed in descending date order (most recent first). V1.2 changes: renamed from revocation_key, added optional invalidated field.
     * (Required)
     * 
     */
    @SerializedName("revocationKeys")
    @Expose
    private List<RevocationKey> revocationKeys = new ArrayList<RevocationKey>();
    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Contact address for the individual or organization.
     * (Required)
     * 
     */
    @SerializedName("email")
    @Expose
    private String email;
    /**
     * The URL where the issuer's certificates can be found
     * (Required)
     * 
     */
    @SerializedName("url")
    @Expose
    private URI url;
    /**
     * The URL hosting the issuer's introduction endpoint
     * (Required)
     * 
     */
    @SerializedName("introductionURL")
    @Expose
    private URI introductionURL;
    /**
     * Data URI; a base-64 encoded png image of the issuer's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;

    /**
     * list of issuer keys, listed in descending date order (most recent first). V1.2 change: renamed from issuer_key, added optional invalidated field.
     * (Required)
     * 
     */
    public List<IssuerKey> getIssuerKeys() {
        return issuerKeys;
    }

    /**
     * list of issuer keys, listed in descending date order (most recent first). V1.2 change: renamed from issuer_key, added optional invalidated field.
     * (Required)
     * 
     */
    public void setIssuerKeys(List<IssuerKey> issuerKeys) {
        this.issuerKeys = issuerKeys;
    }

    /**
     * list of revocation keys, listed in descending date order (most recent first). V1.2 changes: renamed from revocation_key, added optional invalidated field.
     * (Required)
     * 
     */
    public List<RevocationKey> getRevocationKeys() {
        return revocationKeys;
    }

    /**
     * list of revocation keys, listed in descending date order (most recent first). V1.2 changes: renamed from revocation_key, added optional invalidated field.
     * (Required)
     * 
     */
    public void setRevocationKeys(List<RevocationKey> revocationKeys) {
        this.revocationKeys = revocationKeys;
    }

    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * The URL of the issuer's website or homepage
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Human-readable name of the issuing entity
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Contact address for the individual or organization.
     * (Required)
     * 
     */
    public String getEmail() {
        return email;
    }

    /**
     * Contact address for the individual or organization.
     * (Required)
     * 
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * The URL where the issuer's certificates can be found
     * (Required)
     * 
     */
    public URI getUrl() {
        return url;
    }

    /**
     * The URL where the issuer's certificates can be found
     * (Required)
     * 
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * The URL hosting the issuer's introduction endpoint
     * (Required)
     * 
     */
    public URI getIntroductionURL() {
        return introductionURL;
    }

    /**
     * The URL hosting the issuer's introduction endpoint
     * (Required)
     * 
     */
    public void setIntroductionURL(URI introductionURL) {
        this.introductionURL = introductionURL;
    }

    /**
     * Data URI; a base-64 encoded png image of the issuer's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * Data URI; a base-64 encoded png image of the issuer's image. https://en.wikipedia.org/wiki/Data_URI_scheme
     * (Required)
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

}
