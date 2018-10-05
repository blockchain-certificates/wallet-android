package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMApplication;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.LMIssuerBaseFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateFileFragment;
import com.learningmachine.android.app.ui.cert.AddCertificateURLFragment;
import com.learningmachine.android.app.ui.cert.CertificateFragment;
import com.learningmachine.android.app.ui.cert.CertificateInfoFragment;
import com.learningmachine.android.app.ui.cert.VerifyCertificateFragment;
import com.learningmachine.android.app.ui.home.HomeFragment;
import com.learningmachine.android.app.ui.issuer.AddIssuerFragment;
import com.learningmachine.android.app.ui.issuer.IssuerActivity;
import com.learningmachine.android.app.ui.issuer.IssuerFragment;
import com.learningmachine.android.app.ui.issuer.IssuerInfoActivity;
import com.learningmachine.android.app.ui.issuer.IssuerInfoFragment;
import com.learningmachine.android.app.ui.onboarding.OnboardingActivity;
import com.learningmachine.android.app.ui.onboarding.OnboardingFragment;
import com.learningmachine.android.app.ui.onboarding.PastePassphraseFragment;
import com.learningmachine.android.app.ui.onboarding.ViewPassphraseFragment;
import com.learningmachine.android.app.ui.onboarding.BackupPassphraseFragment;
import com.learningmachine.android.app.ui.settings.SettingsFragment;
import com.learningmachine.android.app.ui.settings.passphrase.RevealPassphraseFragment;
import com.learningmachine.android.app.ui.splash.SplashActivity;

public interface LMGraph {
    void inject(LMApplication application);

    // Activities
    void inject(SplashActivity activity);
    void inject(IssuerActivity activity);
    void inject(IssuerInfoActivity activity);
    void inject(OnboardingActivity activity);

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
