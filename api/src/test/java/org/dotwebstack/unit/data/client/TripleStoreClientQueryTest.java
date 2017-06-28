package org.dotwebstack.unit.data.client;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientQueryTest extends TripleStoreClientTest {

    @Test
    public void testQueryEmptyResult() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        initConnectionFunction();

        //act
        Model model = client.query();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model size should not be 0", model.size());
    }

    @Test
    public void testQueryReturnsData() {
        //arrange
        Model picasso = createArtist("Picasso").build();
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new CollectionIteration(picasso)));
        initConnectionFunction();

        //act
        Model model = client.query();

        //assert
        assertNotNull("Model should not be null", model);
        assertNotNull("Model should not be 2", model.size());
    }

    @Test
    public void testQueryCallsMethods() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        initConnectionFunction();

        //act
        client.query();

        //assert
        verify(connection).getStatements(null, null, null);
    }
}
