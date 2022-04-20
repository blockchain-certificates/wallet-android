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
import static org.junit.Assert.assertEquals;

public class BlockCertParserTest {
    private BlockCertParser instance;

    @Before
    public void setup() {
        instance = new BlockCertParser();
    }

    @Test
    public void parseV30SetsCorrectVersionTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV3String = readFileAsString(dir + "/src/test/resources/v3-testnet-valid.json");
            final BlockCert output = instance.fromJson(jsonV3String);
            assertEquals(output.version(), "v3");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseV30SetsDocumentNodeTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV3String = readFileAsString(dir + "/src/test/resources/v3-testnet-valid.json");
            final JSONObject expectedOutput = new JSONObject(jsonV3String);
            final BlockCert output = instance.fromJson(jsonV3String);
            final JsonObject outputDocument = output.getDocumentNode();
            assertThat(expectedOutput.toString(), jsonEquals(outputDocument));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseV20SetsCorrectVersionTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV2String = readFileAsString(dir + "/src/test/resources/mainnet-valid-2.0.json");
            final BlockCert output = instance.fromJson(jsonV2String);
            assertEquals(output.version(), "v2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Test
    public void parseV12SetsCurrectVersionTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV12String = readFileAsString(dir + "/src/test/resources/common/Certificates/v1.2/sample_signed_cert-valid-1.2.0.json");
            final BlockCert output = instance.fromJson(jsonV12String);
            assertEquals(output.version(), "v1.2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseV12SetsDocumentNodeTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV12String = readFileAsString(dir + "/src/test/resources/common/Certificates/v1.2/sample_signed_cert-valid-1.2.0.json");
            final String AssertionV12DocumentNodeString = readFileAsString(dir + "/src/test/resources/assertions/v1.2-document-node.json");
            final BlockCert output = instance.fromJson(jsonV12String);
            final JsonObject outputDocument = output.getDocumentNode();
            assertThat(AssertionV12DocumentNodeString, jsonEquals(outputDocument));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseV11SetsCorrectVersionTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV11String = readFileAsString(dir + "/src/test/resources/common/Certificates/v1.1/sample_signed_cert-valid-1.1.0.json");
            final BlockCert output = instance.fromJson(jsonV11String);
            assertEquals(output.version(), "v1.1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseV11SetsDocumentNodeTest () {
        try {
            final String dir = System.getProperty("user.dir");
            final String jsonV11String = readFileAsString(dir + "/src/test/resources/common/Certificates/v1.1/sample_signed_cert-valid-1.1.0.json");
            final String AssertionV11DocumentNodeString = readFileAsString(dir + "/src/test/resources/assertions/v1.1-document-node.json");
            final BlockCert output = instance.fromJson(jsonV11String);
            final JsonObject outputDocument = output.getDocumentNode();
            assertThat(AssertionV11DocumentNodeString, jsonEquals(outputDocument));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readFileAsString (String filePath) throws Exception {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}