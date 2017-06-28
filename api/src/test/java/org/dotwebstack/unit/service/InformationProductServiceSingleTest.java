package org.dotwebstack.unit.service;

import org.dotwebstack.data.client.impl.TripleStoreClientImpl;
import org.dotwebstack.data.repository.impl.ConfigurationRepository;
import org.dotwebstack.data.service.InformationProduct;
import org.dotwebstack.data.service.InformationProductService;
import org.dotwebstack.data.service.impl.InformationProductServiceImpl;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.ELMO_INFORMATION_PRODUCT;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.SELECT_ONE_QUERY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class InformationProductServiceSingleTest extends InformationProductServiceBase {

    @Mock
    TripleStoreClientImpl<ConfigurationRepository> client;

    @Test
    public void getInformationProduct() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        LinkedHashModel model = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));

        //act
        Optional<InformationProduct> result = service.getInformationProduct("name");

        //assert
        assertFalse("Information model should not be present", result.isPresent());
    }

    @Test
    public void usesQuery() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);

        String query = String.format(SELECT_ONE_QUERY, "name", ELMO_INFORMATION_PRODUCT);
        when(client.select(eq(query))).thenReturn(convertModel(new LinkedHashModel()));

        //act
        service.getInformationProduct("name");

        //assert
        verify(client).select(query);
    }

    @Test
    public void extractsInformationProduct() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model informationProduct = createInformationProduct("name", "query").build();
        when(client.select(any())).thenReturn(convertModel(informationProduct));

        //act
        Optional<InformationProduct> result = service.getInformationProduct("http://example.org/name");

        //assert
        assertTrue("Should be present", result.isPresent());

        InformationProduct product = result.get();
        assertEquals("Name should be name", "http://example.org/name", product.getName());
        assertEquals("Query should be query", "query", product.getQuery());
        assertEquals("No parameters", 0, product.getParameters().size());
    }

}
