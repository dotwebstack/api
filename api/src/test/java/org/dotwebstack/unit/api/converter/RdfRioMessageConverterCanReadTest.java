package org.dotwebstack.unit.api.converter;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import org.dotwebstack.api.converter.RdfRioMessageConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfRioMessageConverterCanReadTest {

  @Test
  public void testCanReadModels() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canRead = converter
        .canRead(LinkedHashModel.class, MediaType.valueOf("application/ld+json"));

    //assert
    assertTrue("It should be able to read models", canRead);
  }

  @Test
  public void testCantReadUnknownMediatypes() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canRead = converter
        .canRead(LinkedHashModel.class, MediaType.valueOf("application/xml"));

    //assert
    assertFalse("It should be able to read models with xml", canRead);
  }

  @Test
  public void testCantReadUnknownClass() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canRead = converter.canRead(Object.class, MediaType.valueOf("application/ld+json"));

    //assert
    assertFalse("It should be able to read object", canRead);
  }

}
