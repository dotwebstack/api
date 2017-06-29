package org.dotwebstack.unit.data.client;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.algebra.evaluation.iterator.CollectionIteration;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class TripleStoreClientQueryBySubjectTest extends TripleStoreClientTest {

  @Mock
  IRI subjectIri;

  @Test(expected = AssertionError.class)
  public void testQueryByInvalidSubject() {
    //arrange
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI(anyString())).thenReturn(subjectIri);
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    initConnectionFunction();

    //act & assert
    client.queryBySubject(null);
  }

  @Test
  public void testQueryBySubjectEmptyResult() {
    //arrange
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI(eq("subjectMock"))).thenReturn(subjectIri);
    when(connection.getStatements(eq(subjectIri), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    initConnectionFunction();

    //act
    Model model = client.queryBySubject("subjectMock");

    //assert
    assertNotNull("Model should not be null", model);
    assertNotNull("Model size should not be 0", model.size());
  }

  @Test
  public void testQueryBySubjectReturnsData() {
    //arrange
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI(eq("subjectMock"))).thenReturn(subjectIri);

    Model picasso = createArtist("Picasso").build();
    Model ross = createArtist("Ross").build();

    List<Statement> statements = Stream.concat(picasso.stream(), ross.stream()).collect(toList());
    when(connection.getStatements(eq(subjectIri), any(), any()))
        .thenReturn(new RepositoryResult<>(new CollectionIteration<>(statements)));
    initConnectionFunction();

    //act
    client.queryBySubject("subjectMock");

    //assert
    verify(connection).getStatements(subjectIri, null, null);
  }

}
