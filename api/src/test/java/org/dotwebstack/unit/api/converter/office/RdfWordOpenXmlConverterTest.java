package org.dotwebstack.unit.api.converter.office;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.dotwebstack.api.converter.office.RdfWordOpenXmlConverter;
import org.dotwebstack.test.categories.Categories;
import org.dotwebstack.utils.TestUtils;
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
 * Created by Rick Fleuren on 6/23/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfWordOpenXmlConverterTest {

  final MediaType mediaType = MediaType
      .valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

  @Mock
  HttpOutputMessage message;

  @Mock
  HttpHeaders headers;

  @Test
  public void testCantReadOtherMediaType() {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to read other media types", canRead);
  }

  @Test
  public void testCantRead() {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

    //assert
    assertFalse("It shouldn't be able to read models", canRead);
  }

  @Test
  public void testCanWrite() {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

    //assert
    assertTrue("It should be able to write models", canWrite);
  }

  @Test
  public void testCanWriteOtherMediaType() {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to write other media types", canWrite);
  }

  @Test
  public void testWrite() throws IOException {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();
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
  public void testAddsHeader() throws IOException {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();
    Model model = new LinkedHashModel();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    verify(headers).add("Content-Disposition", "attachment; filename=document.docx");
  }

  @Test
  public void testWordTags() throws IOException {
    //arrange
    RdfWordOpenXmlConverter converter = new RdfWordOpenXmlConverter();
    Model model = TestUtils.createArtist("Picasso").build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(outputStream.toByteArray()));
    XWPFWordExtractor extractor = new XWPFWordExtractor(document);

    String word = extractor.getText();
    assertTrue("Should contain Picasso data", word.contains("Picasso"));
    assertTrue("Should contain Artist data", word.contains("Artist"));
  }

}
