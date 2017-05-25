
package com.learningmachine.android.app.data.cert.v11;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;


/**
 * Details about the issuer of the certificate.
 * 
 */
public class Issuer {

    /**
     * A base-64 encoded png image of the issuer's logo.
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * URI of the issuer's homepage
     * (Required)
     * 
     */
    @SerializedName("url")
    @Expose
    private URI url;
    /**
     * Name of the issuer.
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Email address of the issuer.
     * (Required)
     * 
     */
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * A base-64 encoded png image of the issuer's logo.
     * (Required)
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * A base-64 encoded png image of the issuer's logo.
     * (Required)
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * Link to a JSON that details the issuer's issuing and recovation keys. Default is https://[domain]/issuer/[org_abbr]-issuer.json. Included for (near) backward compatibility with open badges specification 1.1
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * URI of the issuer's homepage
     * (Required)
     * 
     */
    public URI getUrl() {
        return url;
    }

    /**
     * URI of the issuer's homepage
     * (Required)
     * 
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * Name of the issuer.
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Name of the issuer.
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Email address of the issuer.
     * (Required)
     * 
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email address of the issuer.
     * (Required)
     * 
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
