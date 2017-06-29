package org.dotwebstack.unit.data.client;

import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.BiConsumer;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
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
public class TripleStoreClientQueryByTest extends TripleStoreClientTest {

  @Mock
  ValueFactory valueFactory;

  @Mock
  IRI subjectMock;

  @Mock
  IRI predicateMock;

  @Mock
  Literal objectMock;

  @Test
  public void testQueryByEmptyResult() {
    //arrange
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    initConnectionFunction();

    //act
    Model model = client.queryBy(null, null, null);

    //assert
    assertNotNull("Model should not be null", model);
    assertNotNull("Model size should not be 0", model.size());
  }

  @Test
  public void testQueryByReturnsData() {
    //arrange
    Model picasso = createArtist("Picasso").build();
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new CollectionIteration(picasso)));
    initConnectionFunction();

    //act
    Model model = client.queryBy(null, null, null);

    //assert
    assertNotNull("Model should not be null", model);
    assertNotNull("Model should not be 2", model.size());
  }

  @Test
  public void testQueryByCallsMethods() {
    //arrange
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    initConnectionFunction();

    //act
    client.queryBy(null, null, null);

    //assert
    verify(connection).getStatements(null, null, null);
  }

  @Test
  public void convertsParameters() {
    //arrange
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI("subjectMock")).thenReturn(subjectMock);
    when(valueFactory.createIRI("predicateMock")).thenReturn(predicateMock);
    when(valueFactory.createLiteral("objectMock")).thenReturn(objectMock);
    initConnectionFunction();

    //act
    client.queryBy("subjectMock", "predicateMock", "objectMock");

    //assert
    verify(connection).getStatements(subjectMock, predicateMock, objectMock);
  }

  @Test
  public void convertsObjectParametersToLiteralString() {
    runConversionTest("myString", (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralBoolean() {
    runConversionTest(true, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralInteger() {
    runConversionTest(42, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralDouble() {
    runConversionTest(42d, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralFloat() {
    runConversionTest(42f, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralLong() {
    runConversionTest(42l, (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralBigDecimal() {
    runConversionTest(new BigDecimal(42),
        (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  @Test
  public void convertsObjectParametersToLiteralDate() {
    runConversionTest(new Date(42), (vf, v) -> when(vf.createLiteral(v)).thenReturn(objectMock));
  }

  private <T> void runConversionTest(T objectToConvert, BiConsumer<ValueFactory, T> consumer) {
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    when(connection.getValueFactory()).thenReturn(valueFactory);
    consumer.accept(valueFactory, objectToConvert);
    initConnectionFunction();

    //act
    client.queryBy(null, null, objectToConvert);

    //assert
    verify(connection).getStatements(null, null, objectMock);
  }

  @Test
  public void convertsObjectParametersToIRI() {
    when(connection.getStatements(any(), any(), any()))
        .thenReturn(new RepositoryResult<Statement>(new EmptyIteration()));
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI("http://example.org")).thenReturn(subjectMock);
    initConnectionFunction();

    //act
    client.queryBy(null, null, "http://example.org");

    //assert
    verify(connection).getStatements(null, null, subjectMock);
  }
}
