package com.learningmachine.android.app.data.inject;

import android.content.Context;

import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.bitcoin.BitcoinManager;
import com.learningmachine.android.app.data.model.Certificate;
import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.ImageStore;
import com.learningmachine.android.app.data.store.IssuerStore;
import com.learningmachine.android.app.data.store.LMDatabaseHelper;
import com.learningmachine.android.app.data.webservice.CertificateService;
import com.learningmachine.android.app.data.webservice.IssuerService;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    BitcoinManager providesBitcoinManager(Context context, NetworkParameters networkParameters, IssuerManager issuerManager) {
        return new BitcoinManager(context, networkParameters, issuerManager);
    }

    @Provides
    @Singleton
    LMDatabaseHelper provideLmDatabase(Context context) {
        return new LMDatabaseHelper(context);
    }

    @Provides
    @Singleton
    ImageStore providesImageStore(Context context) {
        return new ImageStore(context);
    }

    @Provides
    @Singleton
    IssuerStore providesIssuerStore(Context context, LMDatabaseHelper databaseHelper, ImageStore imageStore) {
        return new IssuerStore(context, databaseHelper, imageStore);
    }

    @Provides
    @Singleton
    IssuerManager providesIssuerManager(IssuerStore issuerStore, IssuerService issuerService) {
        return new IssuerManager(issuerStore, issuerService);
    }

    @Provides
    @Singleton
    CertificateManager providesCertificateManager(Context context, CertificateStore certificateStore, CertificateService certificateService) {
        return new CertificateManager(context, certificateStore, certificateService);
    }

    @Provides
    @Singleton
    CertificateStore providesCertificateStore(LMDatabaseHelper databaseHelper, ImageStore imageStore) {
        return new CertificateStore(databaseHelper, imageStore);
    }
}
