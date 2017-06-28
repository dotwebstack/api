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

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
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
public class TripleStoreClientQueryGroupBySubjectTest extends TripleStoreClientTest {

    @Test
    public void testQueryGroupedBySubjectEmptyResult() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        initConnectionFunction();

        //act
        List<Model> models = client.queryGroupedBySubject();

        //assert
        assertNotNull("Models size should not be 0", models.size());
    }

    @Test
    public void testQueryGroupedBySubjectCallsMethods() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
        initConnectionFunction();

        //act
        client.queryGroupedBySubject();

        //assert
        verify(connection).getStatements(null, null, null);
    }

    @Test
    public void testQueryGroupedBySubjectReturnsData() {
        //arrange
        Model picasso = createArtist("Picasso").build();
        Model ross = createArtist("Ross").build();

        List<Statement> statements = Stream.concat(picasso.stream(), ross.stream()).collect(toList());
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<>(new CollectionIteration<>(statements)));
        initConnectionFunction();

        //act
        client.queryGroupedBySubject();

        //assert
        verify(connection).getStatements(null, null, null);
    }
}
