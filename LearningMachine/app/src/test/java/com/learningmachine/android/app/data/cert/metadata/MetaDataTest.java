package com.learningmachine.android.app.data.cert.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.NumberFormat;

import static org.junit.Assert.assertEquals;

public class MetaDataTest {

    private MetaData subject;

    @Before
    public void setup() {
        NumberFormat numberFormat = NumberFormat.getInstance();
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        MetaDataTypeAdapter typeAdapter = new MetaDataTypeAdapter(numberFormat, integerFormat, "True", "False");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(MetaData.class, typeAdapter)
                .create();
        Reader reader = getResourceAsReader("cert-meta-data-01.json");

        subject = gson.fromJson(reader, MetaData.class);
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
