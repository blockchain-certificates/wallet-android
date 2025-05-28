package com.hyland.android.app.data.cert.metadata;

import android.content.Context;

import com.hyland.android.app.R;
import com.hyland.android.app.data.cert.BlockCert;
import com.learningmachine.android.test.helpers.BlockCertHelpers;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MetadataTest {

    private Metadata subject;

    @Before
    public void setup() {
        Reader reader = getResourceAsReader("cert-meta-data-01.json");
        Context context = mock(Context.class);
        when(context.getString(R.string.cert_metadata_boolean_true)).thenReturn("True");
        when(context.getString(R.string.cert_metadata_boolean_false)).thenReturn("False");
        MetadataParser metadataParser = new MetadataParser(context);

        subject = metadataParser.fromJson(reader);
    }

    @Test
    public void hasCorrectDisplayOrder() {
        assertEquals(7, subject.getDisplayOrder().size());
        assertEquals("group.date", subject.getDisplayOrder().get(1));
    }

    @Test
    public void hasCorrectNumberOfGroups() {
        assertEquals(1, subject.getGroupDefinitions().size());
        assertEquals(2, subject.getGroups().size());
    }

    @Test
    public void hasCorrectStringField() {
        Field field = subject.getFields().get(0);
        assertEquals("Text title", field.getTitle());
        assertEquals("string", field.getValue());
    }

    @Test
    public void hasCorrectNumberField() {
        Field field = subject.getFields().get(4);
        assertEquals("GPA", field.getTitle());
        assertEquals("3.7", field.getValue());
    }

    @Test
    public void hasCorrectBooleanField() {
        Field field = subject.getFields().get(6);
        assertEquals("is this a boolean?", field.getTitle());
        assertEquals("True", field.getValue());
    }

    @Test
    public void malformedMetadataObjectDoesNotBreak() {
        final Context context = mock(Context.class);
        final MetadataParser metadataParser = new MetadataParser(context);

        final List<String> expectedEmptyListOutput = new ArrayList<>();
        final Metadata output = metadataParser.fromJson("{\"classOf\":\"2022\"}");
        assertEquals(expectedEmptyListOutput, output.getFields());
    }

    private Reader getResourceAsReader(String name) {
        InputStream inputStream = getResourceAsStream(name);
        Reader reader = new InputStreamReader(inputStream);
        return reader;
    }

    private InputStream getResourceAsStream(String name) {
        ClassLoader classLoader = getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(name);
        return inputStream;
    }
}
