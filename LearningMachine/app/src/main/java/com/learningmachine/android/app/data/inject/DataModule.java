package com.hyland.android.app.data.inject;

import android.content.Context;

import com.hyland.android.app.data.CertificateManager;
import com.hyland.android.app.data.IssuerManager;
import com.hyland.android.app.data.bitcoin.BitcoinManager;
import com.hyland.android.app.data.passphrase.PassphraseManager;
import com.hyland.android.app.data.preferences.SharedPreferencesManager;
import com.hyland.android.app.data.store.CertificateStore;
import com.hyland.android.app.data.store.ImageStore;
import com.hyland.android.app.data.store.IssuerStore;
import com.hyland.android.app.data.store.LMDatabaseHelper;
import com.hyland.android.app.data.webservice.CertificateService;
import com.hyland.android.app.data.webservice.IssuerService;

import org.bitcoinj.core.NetworkParameters;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    BitcoinManager providesBitcoinManager(Context context, NetworkParameters networkParameters,
                                          IssuerStore issuerStore, CertificateStore certificateStore,
                                          SharedPreferencesManager sharedPreferencesManager,
                                          PassphraseManager passphraseManager) {
        return new BitcoinManager(context, networkParameters, issuerStore,
                certificateStore, sharedPreferencesManager, passphraseManager);
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
    IssuerStore providesIssuerStore(LMDatabaseHelper databaseHelper, ImageStore imageStore) {
        return new IssuerStore(databaseHelper, imageStore);
    }

    @Provides
    @Singleton
    IssuerManager providesIssuerManager(IssuerStore issuerStore, IssuerService issuerService) {
        return new IssuerManager(issuerStore, issuerService);
    }

    @Provides
    @Singleton
    CertificateManager providesCertificateManager(Context context, CertificateStore certificateStore, CertificateService certificateService, BitcoinManager bitcoinManager, IssuerManager issuerManager) {
        return new CertificateManager(context, certificateStore, certificateService, bitcoinManager, issuerManager);
    }

    @Provides
    @Singleton
    CertificateStore providesCertificateStore(LMDatabaseHelper databaseHelper) {
        return new CertificateStore(databaseHelper);
    }

    @Provides
    @Singleton
    SharedPreferencesManager providesSharedPreferencesManager(Context context) {
        return new SharedPreferencesManager(context);
    }

    @Provides
    @Singleton
    PassphraseManager providesPassphraseManager(Context context) {
        return new PassphraseManager(context);
    }
}
