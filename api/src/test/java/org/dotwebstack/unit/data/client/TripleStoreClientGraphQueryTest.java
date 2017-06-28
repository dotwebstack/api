package org.dotwebstack.unit.data.client;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientGraphQueryTest extends TripleStoreClientTest {

    @Mock
    GraphQuery graphQuery;

    @Mock
    GraphQueryResult graphQueryResult;

    @Test
    public void testQueryCallsMethods() {
        //arrange
        when(connection.prepareGraphQuery(eq("myQuery"))).thenReturn(graphQuery);
        when(graphQuery.evaluate()).thenReturn(graphQueryResult);
        initConnectionFunction();

        //act
        Model result = client.construct("myQuery");

        //assert
        verify(graphQuery).evaluate();
        assertNotNull("Model should have been returned", result);
        assertEquals("With no content", 0, result.size());
    }
}
