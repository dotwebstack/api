package org.dotwebstack.unit.api.converter;

import org.dotwebstack.api.converter.RdfRioMessageConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfRioMessageConverterReadTest {

    @Mock
    HttpInputMessage message;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCanReadModels() throws IOException {
        //arrange
        RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

        Model picasso = create("Picasso", "Pablo", "Artist").build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Rio.write(picasso, output, RDFFormat.JSONLD);

        when(message.getBody()).thenReturn(new ByteArrayInputStream(output.toByteArray()));
        when(message.getHeaders()).thenReturn(headers);
        when(headers.get("namespace")).thenReturn(new ArrayList<>());

        //act
        Model model = converter.read(Model.class, message);

        //assert
        assertEquals("Model should have 2 statements", 2, model.size());
    }

    @Test
    public void testUsesInputstream() throws IOException {
        //arrange
        RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

        Model picasso = create("Picasso", "Pablo", "Artist").build();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Rio.write(picasso, output, RDFFormat.JSONLD);

        when(message.getBody()).thenReturn(new ByteArrayInputStream(output.toByteArray()));
        when(message.getHeaders()).thenReturn(headers);
        when(headers.get("namespace")).thenReturn(new ArrayList<>());

        //act
        converter.read(Model.class, message);

        //assert
        verify(message).getBody();
    }

    protected ModelBuilder create(String subject, String name, String type) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + subject)
                .add(RDF.TYPE, "ex:" + type)
                .add(FOAF.LAST_NAME, name);
    }

}
