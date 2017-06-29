package org.dotwebstack.unit.data.client;

import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

public class TripleStoreClientAddTest extends TripleStoreClientTest {

  @Test
  public void testAddCallsMethods() {
    //arrange
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    initConnectionFunction();

    Model picasso = createArtist("Picasso").build();

    //act
    client.add(picasso);

    //assert
    verify(connection).add(picasso);

    ArgumentCaptor<IRI> getSubject = ArgumentCaptor.forClass(IRI.class);
    verify(connection).getStatements(getSubject.capture(), eq(null), eq(null));
    assertEquals("IRI should be http://example.org/Picasso", "http://example.org/Picasso",
        getSubject.getValue().toString());
  }

  @Test
  public void testUpdateWithExistingData() {
    //arrange
    Model existingPicasso = createArtist("Picasso")
        .add(FOAF.LAST_NAME, "OtherName")
        .build();
    Model picasso = createArtist("Picasso").build();

    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<>(new CollectionIteration<>(existingPicasso)))
        .thenReturn(new RepositoryResult<>(new CollectionIteration<>(picasso)));

    //First, ex:Artist, then ex:Lastname, the rest
    when(connection.hasStatement(any(), any(), any(), eq(true))).thenReturn(false, true, false);
    initConnectionFunction();

    //act
    client.update(picasso);

    //assert
    ArgumentCaptor<IRI> removeSubject = ArgumentCaptor.forClass(IRI.class);
    ArgumentCaptor<IRI> removePredicate = ArgumentCaptor.forClass(IRI.class);
    ArgumentCaptor<IRI> removeValue = ArgumentCaptor.forClass(IRI.class);
    verify(connection)
        .remove(removeSubject.capture(), removePredicate.capture(), removeValue.capture());
    assertEquals("Subject should be http://example.org/Picasso", "http://example.org/Picasso",
        removeSubject.getValue().toString());
    assertEquals("Predicate should be type http://xmlns.com/foaf/0.1/lastName",
        "http://xmlns.com/foaf/0.1/lastName", removePredicate.getValue().toString());
    assertNull("Object should be null", removeValue.getValue());

    verify(connection).add(picasso);

    ArgumentCaptor<IRI> getSubject = ArgumentCaptor.forClass(IRI.class);
    verify(connection).getStatements(getSubject.capture(), eq(null), eq(null));
    assertEquals("IRI should be http://example.org/Picasso", "http://example.org/Picasso",
        getSubject.getValue().toString());
  }
}
