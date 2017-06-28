package org.dotwebstack.unit.data.client;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientUpdateTest extends TripleStoreClientTest {

    @Test
    public void testUpdateCallsMethods() {
        //arrange
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));

        Model picasso = createArtist("Picasso").build();
        initConnectionFunction();

        //act
        client.update(picasso);

        //assert
        verify(connection).add(picasso);
    }

    @Test
    public void testUpdateWithExistingData() {
        //arrange
        Model result = createArtist("ExistingPicasso").build();
        when(connection.getStatements(any(), any(), any())).thenReturn(new RepositoryResult<>(new CollectionIteration<>(result)));

        Model picasso = createArtist("Picasso").build();
        initConnectionFunction();

        //act
        client.update(picasso);

        //assert
        verify(connection).add(picasso);
    }

    private ModelBuilder createArtist(String artistName) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + artistName)
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, artistName);
    }
}
