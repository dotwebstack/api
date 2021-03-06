package org.dotwebstack.integration.api.converter.office;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.dotwebstack.api.converter.office.RdfWordConverter;
import org.dotwebstack.integration.data.client.configuration.SailMemoryTestConfiguration;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(Categories.IntegrationTests.class)
@ContextConfiguration(classes = SailMemoryTestConfiguration.class)
public class RdfWordConverterTest {

  final MediaType mediaType = MediaType.valueOf("application/msword");

  @Autowired
  ResourceLoader resourceLoader;


  @Test
  public void testCantReadOtherMediaType() {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to read other media types", canRead);
  }

  @Test
  public void testCantRead() {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

    //assert
    assertFalse("It shouldn't be able to read models", canRead);
  }

  @Test
  public void testCanWrite() {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

    //assert
    assertTrue("It should be able to write models", canWrite);
  }

  @Test
  public void testCanWriteOtherMediaType() {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to write other media types", canWrite);
  }

  @Test
  public void testWrite() throws IOException {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();
    converter.setResourceLoader(resourceLoader);
    Model model = new LinkedHashModel();

    HttpOutputMessage message = Mockito.mock(HttpOutputMessage.class);
    HttpHeaders headers = Mockito.mock(HttpHeaders.class);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    verify(message, times(2)).getBody();
  }

  @Test
  public void testAddsHeader() throws IOException {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();
    converter.setResourceLoader(resourceLoader);
    Model model = new LinkedHashModel();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    HttpOutputMessage message = Mockito.mock(HttpOutputMessage.class);
    HttpHeaders headers = Mockito.mock(HttpHeaders.class);
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    verify(headers).add("Content-Disposition", "attachment; filename=document.doc");
  }

  @Test
  public void testWordTags() throws IOException {
    //arrange
    RdfWordConverter converter = new RdfWordConverter();
    converter.setResourceLoader(resourceLoader);

    Model model = createArtist("Picasso").build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    HttpOutputMessage message = Mockito.mock(HttpOutputMessage.class);
    HttpHeaders headers = Mockito.mock(HttpHeaders.class);
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    HWPFDocument document = new HWPFDocument(new ByteArrayInputStream(outputStream.toByteArray()));
    WordExtractor extractor = new WordExtractor(document); //cant extract tables?

    String word = extractor.getText();
    assertTrue("Should contain Linked data results", word.contains("Linked data"));
  }

}
