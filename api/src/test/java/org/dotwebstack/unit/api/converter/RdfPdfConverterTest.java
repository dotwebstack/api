package org.dotwebstack.unit.api.converter;

import org.dotwebstack.api.converter.RdfPdfConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Rick Fleuren on 6/26/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfPdfConverterTest {

    MediaType mediaType = MediaType.APPLICATION_PDF;

    @Mock
    HttpOutputMessage message;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCantReadOtherMediaType() {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to read other media types", canRead);
    }

    @Test
    public void testCantRead() {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

        //assert
        assertFalse("It shouldn't be able to read models", canRead);
    }

    @Test
    public void testCanWrite() {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

        //assert
        assertTrue("It should be able to write models", canWrite);
    }

    @Test
    public void testCanWriteOtherMediaType() {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to write other media types", canWrite);
    }

    @Test
    public void testWrite() throws IOException {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        verify(message, times(2)).getBody();
    }

    @Test
    public void testTags() throws IOException {
        //arrange
        RdfPdfConverter converter = new RdfPdfConverter();
        Model model = createArtist("Picasso").build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        String pdf = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertTrue("Should contain PDF data", pdf.contains("PDF"));
        assertTrue("Should contain Pages data", pdf.contains("Pages"));
    }
}
