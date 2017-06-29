package org.dotwebstack.unit.api.converter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;
import org.dotwebstack.api.converter.RdfRioMessageConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfRioMessageConverterWriteTest {

  @Mock
  HttpOutputMessage message;

  @Mock
  OutputStream outputStream;

  @Mock
  HttpHeaders headers;

  @Test
  public void testCanWriteModels() throws IOException {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);
    Model model = new LinkedHashModel();
    when(message.getBody()).thenReturn(outputStream);
    when(message.getHeaders()).thenReturn(headers);

    //act
    converter.write(model, MediaType.valueOf("application/ld+json"), message);

    //assert
    verify(message, times(2)).getBody();
  }
}
