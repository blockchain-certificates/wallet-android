
package com.learningmachine.android.app.data.cert.v20;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * From https://w3id.org/openbadges#BadgeClass
 * 
 */
public class Badge {

    /**
     * Defined by `id` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges but currently optional in Blockcerts for compatibility. This may be an HTTP IRI, but only if the issuer plans to host the BadgeClass definitions on a long-term basis, or (at least) until expiration of certificates referencing this BadgeClass. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * 
     */
    @SerializedName("id")
    @Expose
    private URI id;
    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * 
     */
    @SerializedName("type")
    @Expose
    private Object type;
    /**
     * Defined by `name` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Blockcerts extension: optional subtitle of the certificate
     * 
     */
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    /**
     * Defined by `description` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * (Required)
     * 
     */
    @SerializedName("image")
    @Expose
    private String image;
    /**
     * Defined by `criteria` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges, currently optional in Blockcerts for compatibility. 
     * 
     */
    @SerializedName("criteria")
    @Expose
    private Criteria criteria;
    /**
     * Defined by https://w3id.org/openbadges#Profile. This type is used in certificates, and in the issuer-hosted identification page. The minimal set of properties required in the certificate are `id` and `type`. In this case, additional issuer-identification properties are assumed to be available at the issuer-hosted identification page.
     * (Required)
     * 
     */
    @SerializedName("issuer")
    @Expose
    private Issuer issuer;
    /**
     * List of objects describing which objectives or educational standards this badge aligns to, if any.
     * 
     */
    @SerializedName("alignment")
    @Expose
    private List<Alignment> alignment = new ArrayList<Alignment>();
    /**
     * List of tags that describe the type of achievement.
     * 
     */
    @SerializedName("tags")
    @Expose
    private Set<String> tags = new LinkedHashSet<String>();
    /**
     * Blockcerts extension: array of signature lines for display.
     * 
     */
    @SerializedName("signatureLines")
    @Expose
    private Object signatureLines;

    /**
     * Defined by `id` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges but currently optional in Blockcerts for compatibility. This may be an HTTP IRI, but only if the issuer plans to host the BadgeClass definitions on a long-term basis, or (at least) until expiration of certificates referencing this BadgeClass. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * 
     */
    public URI getId() {
        return id;
    }

    /**
     * Defined by `id` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges but currently optional in Blockcerts for compatibility. This may be an HTTP IRI, but only if the issuer plans to host the BadgeClass definitions on a long-term basis, or (at least) until expiration of certificates referencing this BadgeClass. Otherwise it is recommended to use a `urn:uuid:<UUID>`-formatted IRI.
     * 
     */
    public void setId(URI id) {
        this.id = id;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * 
     */
    public Object getType() {
        return type;
    }

    /**
     * A type or an array of types defined in a referenced JSON-LD context.
     * 
     */
    public void setType(Object type) {
        this.type = type;
    }

    /**
     * Defined by `name` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Defined by `name` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Blockcerts extension: optional subtitle of the certificate
     * 
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Blockcerts extension: optional subtitle of the certificate
     * 
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Defined by `description` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Defined by `description` property of https://w3id.org/openbadges#BadgeClass
     * (Required)
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * (Required)
     * 
     */
    public String getImage() {
        return image;
    }

    /**
     * An image representative of the entity. In Blockcerts this is typically a data URI (https://en.wikipedia.org/wiki/Data_URI_scheme) embedded as a base-64 encoded PNG image, but it may also be a URI where the image may be found.
     * (Required)
     * 
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Defined by `criteria` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges, currently optional in Blockcerts for compatibility. 
     * 
     */
    public Criteria getCriteria() {
        return criteria;
    }

    /**
     * Defined by `criteria` property of https://w3id.org/openbadges#BadgeClass. This field is required in Open Badges, currently optional in Blockcerts for compatibility. 
     * 
     */
    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Defined by https://w3id.org/openbadges#Profile. This type is used in certificates, and in the issuer-hosted identification page. The minimal set of properties required in the certificate are `id` and `type`. In this case, additional issuer-identification properties are assumed to be available at the issuer-hosted identification page.
     * (Required)
     * 
     */
    public Issuer getIssuer() {
        return issuer;
    }

    /**
     * Defined by https://w3id.org/openbadges#Profile. This type is used in certificates, and in the issuer-hosted identification page. The minimal set of properties required in the certificate are `id` and `type`. In this case, additional issuer-identification properties are assumed to be available at the issuer-hosted identification page.
     * (Required)
     * 
     */
    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    /**
     * List of objects describing which objectives or educational standards this badge aligns to, if any.
     * 
     */
    public List<Alignment> getAlignment() {
        return alignment;
    }

    /**
     * List of objects describing which objectives or educational standards this badge aligns to, if any.
     * 
     */
    public void setAlignment(List<Alignment> alignment) {
        this.alignment = alignment;
    }

    /**
     * List of tags that describe the type of achievement.
     * 
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * List of tags that describe the type of achievement.
     * 
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * Blockcerts extension: array of signature lines for display.
     * 
     */
    public Object getSignatureLines() {
        return signatureLines;
    }

    /**
     * Blockcerts extension: array of signature lines for display.
     * 
     */
    public void setSignatureLines(Object signatureLines) {
        this.signatureLines = signatureLines;
    }

}
