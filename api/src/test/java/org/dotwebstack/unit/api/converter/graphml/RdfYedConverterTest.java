package org.dotwebstack.unit.api.converter.graphml;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.dotwebstack.api.converter.graphml.RdfYedConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

/**
 * Created by Rick Fleuren on 6/26/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfYedConverterTest {

  MediaType mediaType = MediaType.valueOf("application/x.elmo.yed");

  @Mock
  HttpOutputMessage message;

  @Mock
  HttpHeaders headers;

  @Test
  public void testCantReadOtherMediaType() {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to read other media types", canRead);
  }

  @Test
  public void testCantRead() {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();

    //act
    boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

    //assert
    assertFalse("It shouldn't be able to read models", canRead);
  }

  @Test
  public void testCanWrite() {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

    //assert
    assertTrue("It should be able to write models", canWrite);
  }

  @Test
  public void testCanWriteOtherMediaType() {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();

    //act
    boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

    //assert
    assertFalse("It shouldn't be able to write other media types", canWrite);
  }

  @Test
  public void testWrite() throws IOException {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();
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
  public void testNodes() throws IOException {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();
    Model model = createArtist("Picasso").build();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    String xml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    assertTrue("Should contain GraphML data",
        xml.contains("<graph edgedefault=\"directed\" id=\"G\">"));

    assertTrue("Should contain one node", xml.contains("<node id=\"http://example.org/Picasso\""));
    assertTrue("Should contain foaf-lastName key", xml.contains("foaf-lastName"));
    assertFalse("Shouldnt contain edges (there are none)", xml.contains("<edge"));
  }

  @Test
  public void testEdges() throws IOException {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();
    Model model = createArtist("Picasso").build();
    Model model2 = createArtist("Broer_van_Picasso")
        .add(FOAF.KNOWS, SimpleValueFactory.getInstance().createIRI("http://example.org/Picasso"))
        .build();

    model.addAll(model2);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    String xml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    assertTrue("Should contain one edge)", xml.contains(
        "<edge source=\"http://example.org/Broer_van_Picasso\" target=\"http://example.org/Picasso\""));
  }

  @Test
  public void testYedMetadata() throws IOException {
    //arrange
    RdfYedConverter converter = new RdfYedConverter();
    Model model = createArtist("Picasso").build();
    Model model2 = createArtist("Broer_van_Picasso")
        .add(FOAF.KNOWS, SimpleValueFactory.getInstance().createIRI("http://example.org/Picasso"))
        .build();

    model.addAll(model2);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, mediaType, message);

    //assert
    String xml = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    assertTrue("Should contain nodegraphics in the keys",
        xml.contains("yfiles.type=\"nodegraphics"));
    assertTrue("Should contain edgegraphics in the keys",
        xml.contains("yfiles.type=\"edgegraphics"));

    assertTrue("Should contain node enriched data", xml.contains("<y:GenericNode"));
    assertTrue("Should contain edge enriched data", xml.contains("<y:PolyLineEdge"));
  }

}
