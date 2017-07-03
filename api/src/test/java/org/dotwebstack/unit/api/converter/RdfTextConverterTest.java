package org.dotwebstack.unit.api.converter;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.dotwebstack.api.converter.RdfTextConverter;
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

/**
 * Created by PJuergensen on 7/3/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfTextConverterTest {

  @Mock
  HttpOutputMessage message;

  @Mock
  HttpHeaders headers;

  @Test
  public void testCantReadOtherMediaType() {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to read other media types", canRead);
  }

  @Test
  public void testCantRead() {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.valueOf("text/plain"));

    //assert
    assertFalse("It shouldn't be able to read models", canRead);
  }

  @Test
  public void testCanWrite() {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.valueOf("text/plain"));

    //assert
    assertTrue("It should be able to write models", canWrite);
  }

  @Test
  public void testCanWriteOtherMediaType() {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to write other media types", canWrite);
  }

  @Test
  public void testWrite() throws IOException {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();
    Model model = new LinkedHashModel();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, MediaType.valueOf("text/plain"), message);

    //assert
    verify(message, times(2)).getBody();
  }

  @Test
  public void testCsvTags() throws IOException {
    //arrange
    RdfTextConverter converter = new RdfTextConverter();
    Model model = createArtist("Picasso").build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, MediaType.valueOf("text/plain"), message);

    //assert
    String csv = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    assertTrue("Should contain Picasso data", csv.contains("Picasso"));
    assertTrue("Should contain Artist data", csv.contains("Artist"));
  }

}
