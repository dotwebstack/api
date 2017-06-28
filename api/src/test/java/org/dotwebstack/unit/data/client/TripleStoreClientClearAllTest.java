package org.dotwebstack.unit.data.client;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.IRI;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientClearAllTest extends TripleStoreClientTest {

    @Mock
    IRI subjectIri;

    @Test
    public void testClearAllCallsMethods() {
        //arrange
        when(connection.getValueFactory()).thenReturn(valueFactory);
        initConnectionConsumer();

        //act
        client.clearAllTriples();


        //assert
        verify(connection).clear();
        verify(connection).clearNamespaces();
    }
}
