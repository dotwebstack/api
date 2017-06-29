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
public class RdfRioMessageConverterCanWriteTest {

  @Test
  public void testCanWriteModels() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canWrite = converter
        .canWrite(LinkedHashModel.class, MediaType.valueOf("application/ld+json"));

    //assert
    assertTrue("It should be able to write models", canWrite);
  }

  @Test
  public void testCantWriteUnknownMediatypes() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canWrite = converter
        .canWrite(LinkedHashModel.class, MediaType.valueOf("application/xml"));

    //assert
    assertFalse("It should be able to write models with xml", canWrite);
  }

  @Test
  public void testCantWriteUnknownClass() {
    //arrange
    RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

    //act
    boolean canWrite = converter.canWrite(Object.class, MediaType.valueOf("application/ld+json"));

    //assert
    assertFalse("It should be able to write object", canWrite);
  }

}
