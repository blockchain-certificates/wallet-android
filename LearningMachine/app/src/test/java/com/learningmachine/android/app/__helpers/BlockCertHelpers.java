package com.learningmachine.android.test.helpers;

import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.cert.BlockCert;

import com.learningmachine.android.test.helpers.FileHelpers;

public class BlockCertHelpers {
    public static BlockCert fileToBlockCertInstance (String pathToFile) {
        try {
            final String jsonString = FileHelpers.readFileAsString(pathToFile);
            final BlockCertParser blockCertParser = new BlockCertParser();
            final BlockCert blockCert = blockCertParser.fromJson(jsonString);
            return blockCert;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
