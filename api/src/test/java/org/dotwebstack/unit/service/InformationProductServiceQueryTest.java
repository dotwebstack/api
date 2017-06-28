package org.dotwebstack.unit.service;

import org.dotwebstack.data.client.TripleStoreClient;
import org.dotwebstack.data.client.impl.TripleStoreClientImpl;
import org.dotwebstack.data.repository.impl.ConfigurationRepository;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class InformationProductServiceQueryTest extends InformationProductServiceBase {

    @Mock
    TripleStoreClientImpl<ConfigurationRepository> client;

    @Mock
    TripleStoreClient firstClient;

    @Mock
    TripleStoreClient secondClient;

    @Mock
    TripleStoreClient thirdClient;

    @Test(expected = IllegalStateException.class)
    public void productNotFound() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        LinkedHashModel model = new LinkedHashModel();
        when(client.construct(any())).thenReturn(model);

        //act
        service.queryInformationProduct("myQuery", null);
    }

    @Test
    public void returnsModelDefaultClient() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { ?s ?p ?o }").build();
        Model constructedResult = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(constructedResult);

        //act
        Model result = service.queryInformationProduct("name", null);

        //assert
        assertEquals("Result should be the same", constructedResult, result);
    }


    @Test
    public void returnsModelSpecificAdapter() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client, Arrays.asList(new TripleStoreClient[]{firstClient, secondClient, thirdClient}));

        Model model = createInformationProduct("name", "SELECT * WHERE { ?s ?p ?o }", "secondAdapter").build();

        Model constructedResult = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));

        when(firstClient.getAdapterName()).thenReturn("firstAdapter");
        when(secondClient.getAdapterName()).thenReturn("secondAdapter");
        when(thirdClient.getAdapterName()).thenReturn("thirdAdapter");
        when(secondClient.construct(any())).thenReturn(constructedResult);

        //act
        service.queryInformationProduct("name", null);

        //assert
        verify(secondClient).construct("SELECT * WHERE { ?s ?p ?o }");
    }

    @Test
    public void noArguments() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { ?s ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        service.queryInformationProduct("name", null);

        //assert
        verify(client).construct("SELECT * WHERE { ?s ?p ?o }");
    }

    @Test
    public void convertOneArgument() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT * WHERE { <@PARAMETER@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER", "myParameter");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT * WHERE { <myParameter> ?p ?o }");
    }

    @Test
    public void convertOneArgumentMultipleTimes() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER@ WHERE { <@PARAMETER@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER", "myParameter");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT myParameter WHERE { <myParameter> ?p ?o }");
    }

    @Test
    public void convertTwoArguments() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER1@ WHERE { <@PARAMETER2@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER1", "myParameter1");
        parameters.put("PARAMETER2", "myParameter2");
        service.queryInformationProduct("name", parameters);

        //assert
        verify(client).construct("SELECT myParameter1 WHERE { <myParameter2> ?p ?o }");
    }

    @Test(expected = IllegalStateException.class)
    public void missingParameters() {
        //arrange
        InformationProductService service = new InformationProductServiceImpl(client);
        Model model = createInformationProduct("name", "SELECT @PARAMETER1@ WHERE { <@PARAMETER2@> ?p ?o }").build();
        Model result = new LinkedHashModel();
        when(client.select(any())).thenReturn(convertModel(model));
        when(client.construct(any())).thenReturn(result);

        //act
        Map<String, String> parameters = new HashMap<>();
        parameters.put("PARAMETER1", "myParameter1");
        service.queryInformationProduct("name", parameters);
    }
}
