package com.learningmachine.android.app;

public class LMConstants {

    public static final String INJECTOR_SERVICE = "com.learningmachine.android.app.data.inject.Injector";

    public static final String WALLET_FILE = "learningmachine.wallet";
    public static final int WALLET_SEED_BYTE_SIZE = 32;
    public static final String WALLET_PASSPHRASE = "";
    public static final int WALLET_CREATION_TIME_SECONDS = 0;

    public static final String BASE_URL = "https://certificates.learningmachine.com";

    public static final String VERSION_BASE_URL = "https://www.blockcerts.org";

    public static final String BLOCKCHAIN_SERVICE_URL = "https://blockchain.info/";

    public static final String ECDSA_KOBLITZ_PUBKEY_PREFIX = "ecdsa-koblitz-pubkey:";

    public static final boolean SHOULD_PERFORM_OWNERSHIP_CHECK = false;
    public static final String PLAY_STORE_URL = "market://details?id=com.learningmachine.android.app";
    public static final String PLAY_STORE_EXTERNAL_URL = "https://play.google.com/store/apps/details?id=com.learningmachine.android.app";
}
