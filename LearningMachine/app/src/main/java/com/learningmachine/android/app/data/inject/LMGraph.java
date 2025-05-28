package com.hyland.android.app.data.inject;

import com.hyland.android.app.LMApplication;
import com.hyland.android.app.ui.LMActivity;
import com.hyland.android.app.ui.LMFragment;
import com.hyland.android.app.ui.LMIssuerBaseFragment;
import com.hyland.android.app.ui.cert.AddCertificateFileFragment;
import com.hyland.android.app.ui.cert.AddCertificateURLFragment;
import com.hyland.android.app.ui.cert.CertificateFragment;
import com.hyland.android.app.ui.cert.CertificateInfoFragment;
import com.hyland.android.app.ui.cert.VerifyCertificateFragment;
import com.hyland.android.app.ui.home.HomeFragment;
import com.hyland.android.app.ui.issuer.AddIssuerFragment;
import com.hyland.android.app.ui.issuer.IssuerActivity;
import com.hyland.android.app.ui.issuer.IssuerFragment;
import com.hyland.android.app.ui.issuer.IssuerInfoActivity;
import com.hyland.android.app.ui.issuer.IssuerInfoFragment;
import com.hyland.android.app.ui.onboarding.BackupPassphraseFragment;
import com.hyland.android.app.ui.onboarding.OnboardingActivity;
import com.hyland.android.app.ui.onboarding.OnboardingFragment;
import com.hyland.android.app.ui.onboarding.PastePassphraseFragment;
import com.hyland.android.app.ui.onboarding.ViewPassphraseFragment;
import com.hyland.android.app.ui.settings.SettingsFragment;
import com.hyland.android.app.ui.settings.passphrase.RevealPassphraseFragment;
import com.hyland.android.app.ui.splash.SplashActivity;

public interface LMGraph {
    void inject(LMApplication application);

    // Activities
    void inject(SplashActivity activity);
    void inject(IssuerActivity activity);
    void inject(IssuerInfoActivity activity);
    void inject(OnboardingActivity activity);
    void inject(LMActivity activity);

    // Fragments
    void inject(ViewPassphraseFragment fragment);
    void inject(BackupPassphraseFragment fragment);
    void inject(PastePassphraseFragment fragment);
    void inject(HomeFragment fragment);
    void inject(RevealPassphraseFragment fragment);
    void inject(SettingsFragment fragment);
    void inject(AddIssuerFragment fragment);
    void inject(LMIssuerBaseFragment fragment);
    void inject(IssuerFragment fragment);
    void inject(OnboardingFragment fragment);
    void inject(CertificateFragment fragment);
    void inject(LMFragment fragment);
    void inject(VerifyCertificateFragment fragment);
    void inject(AddCertificateURLFragment fragment);
    void inject(AddCertificateFileFragment fragment);
    void inject(IssuerInfoFragment fragment);
    void inject(CertificateInfoFragment fragment);
}
