package com.learningmachine.android.app.data.inject;

import com.learningmachine.android.app.LMApplication;
import com.learningmachine.android.app.ui.home.HomeFragment;
import com.learningmachine.android.app.ui.issuer.AddIssuerFragment;
import com.learningmachine.android.app.ui.settings.passphrase.ReplacePassphraseFragment;
import com.learningmachine.android.app.ui.settings.passphrase.RevealPassphraseFragment;

public interface LMGraph {
    void inject(LMApplication application);

    // fragments
    void inject(HomeFragment fragment);
    void inject(RevealPassphraseFragment fragment);
    void inject(ReplacePassphraseFragment fragment);
    void inject(AddIssuerFragment fragment);
}
