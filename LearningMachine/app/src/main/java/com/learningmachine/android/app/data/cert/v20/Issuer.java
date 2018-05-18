
package com.learningmachine.android.app.data.cert.v20;

import java.net.URI;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Defined by https://w3id.org/openbadges#Profile. This type is used in certificates, and in the issuer-hosted identification page. The minimal set of properties required in the certificate are `id` and `type`. In this case, additional issuer-identification properties are assumed to be available at the issuer-hosted identification page.
 * 
 */
public class Issuer {

    /**
     * Defined by `id` property of https://w3id.org/openbadges#Profile
     * (Required)
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Defined by `name` property of https://w3id.org/openbadges#Profile
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Defined by `url` property of https://w3id.org/openbadges#Profile
     * 
     */
    @SerializedName("url")
    @Expose
    private URI url;
    /**
     * Defined by `telephone` property of https://w3id.org/openbadges#Profile
     * 
     */
    @SerializedName("telephone")
    @Expose
    private String telephone;
    /**
     * Defined by `description` property of https://w3id.org/openbadges#Profile
     * 
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Defined by `email` property of https://w3id.org/openbadges#Profile
     * 
     */
    @SerializedName("email")
    @Expose
    private String email;
    /**
     * Defined by `revocationList` property of https://w3id.org/openbadges#Profile. If embedded in a Blockcert and the issuer-hosted Profile, the value in the Blockcert should take preference.
     * 
     */
    @SerializedName("revocationList")
    @Expose
    private URI revocationList;

    /**
     * Defined by `id` property of https://w3id.org/openbadges#Profile
     * (Required)
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * Defined by `id` property of https://w3id.org/openbadges#Profile
     * (Required)
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * (Required)
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Defined by `name` property of https://w3id.org/openbadges#Profile
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Defined by `name` property of https://w3id.org/openbadges#Profile
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Defined by `url` property of https://w3id.org/openbadges#Profile
     * 
     */
    public URI getUrl() {
        return url;
    }

    /**
     * Defined by `url` property of https://w3id.org/openbadges#Profile
     * 
     */
    public void setUrl(URI url) {
        this.url = url;
    }

    /**
     * Defined by `telephone` property of https://w3id.org/openbadges#Profile
     * 
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Defined by `telephone` property of https://w3id.org/openbadges#Profile
     * 
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Defined by `description` property of https://w3id.org/openbadges#Profile
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Defined by `description` property of https://w3id.org/openbadges#Profile
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Defined by `email` property of https://w3id.org/openbadges#Profile
     * 
     */
    public String getEmail() {
        return email;
    }

    /**
     * Defined by `email` property of https://w3id.org/openbadges#Profile
     * 
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Defined by `revocationList` property of https://w3id.org/openbadges#Profile. If embedded in a Blockcert and the issuer-hosted Profile, the value in the Blockcert should take preference.
     * 
     */
    public URI getRevocationList() {
        return revocationList;
    }

    /**
     * Defined by `revocationList` property of https://w3id.org/openbadges#Profile. If embedded in a Blockcert and the issuer-hosted Profile, the value in the Blockcert should take preference.
     * 
     */
    public void setRevocationList(URI revocationList) {
        this.revocationList = revocationList;
    }

}
