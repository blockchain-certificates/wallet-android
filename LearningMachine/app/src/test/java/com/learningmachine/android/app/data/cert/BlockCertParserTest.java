package com.learningmachine.android.app.data.cert;

import com.learningmachine.android.app.data.cert.BlockCertParser;
import com.learningmachine.android.app.data.cert.BlockCert;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;

public class BlockCertParserTest {
    private BlockCertParser instance;

    @Before
    public void setup() {
        instance = new BlockCertParser();
    }

    @Test
    public void parseV20SetsDocumentNodeTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV2String = readFileAsString(dir + "/src/test/resources/mainnet-valid-2.0.json");
            final JSONObject expectedOutput = new JSONObject(jsonV2String);
            expectedOutput.remove("signature");
            final BlockCert output = instance.fromJson(jsonV2String);
            final JsonObject outputDocument = output.getDocumentNode();
            assertThat(expectedOutput.toString(), jsonEquals(outputDocument));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readFileAsString (String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}