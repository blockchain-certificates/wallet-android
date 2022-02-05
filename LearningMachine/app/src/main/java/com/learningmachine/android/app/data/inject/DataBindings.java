package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.data.store.CertificateStore;
import com.learningmachine.android.app.data.store.SQLiteCertificateStore;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
interface DataBindings {

    @Binds
    @Singleton
    CertificateStore bindCertificateStore(SQLiteCertificateStore certStore);
}
