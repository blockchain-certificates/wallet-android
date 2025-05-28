package com.hyland.android.app.ui.issuer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hyland.android.app.data.model.CertificateRecord;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class CertificateListItemViewModel extends BaseObservable {

    private CertificateRecord mCertificate;

    @Bindable
    public String getTitle() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getName();
    }

    @Bindable
    public String getDescription() {
        if (mCertificate == null) {
            return null;
        }
        return mCertificate.getDescription();
    }

    @Bindable
    public String getDateIssued() {
        if (mCertificate == null) {
            return null;
        }

        // 2018-02-01T23:00:00.0000+00.00
        DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        DateTime issuedDate = parser.parseDateTime(mCertificate.getIssuedOn());
        return issuedDate.toString("MMM dd, yyyy");
    }

    public void bindCertificate(CertificateRecord certificate) {
        mCertificate = certificate;
        notifyChange();
    }

    public CertificateRecord getCertificate() {
        return mCertificate;
    }
}
