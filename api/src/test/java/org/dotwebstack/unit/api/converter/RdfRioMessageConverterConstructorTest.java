package org.dotwebstack.unit.api.converter;

import org.dotwebstack.api.converter.RdfRioMessageConverter;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Rick Fleuren on 6/22/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfRioMessageConverterConstructorTest {

    @Test
    public void testAddsMediaTypes() {
        //arrange
        RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.JSONLD);

        //act
        List<MediaType> mediaTypes = converter.getSupportedMediaTypes();

        //assert
        assertEquals("It should be contain one media type", 1, mediaTypes.size());
        assertEquals("It should be application/ld+json", "application/ld+json", mediaTypes.get(0).toString());
    }

    @Test
    public void testIgnoresNamespaces() {
        //arrange
        RdfRioMessageConverter converter = new RdfRioMessageConverter(RDFFormat.RDFXML);

        //act
        List<MediaType> mediaTypes = converter.getSupportedMediaTypes();

        //assert
        assertEquals("It should be contain one media type", 1, mediaTypes.size());
        assertEquals("It should be application/rdf+xml", "application/rdf+xml", mediaTypes.get(0).toString());
    }

}
