package org.dotwebstack.unit.data.client;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.IRI;
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
public class TripleStoreClientDeleteTest extends TripleStoreClientTest {

  @Mock
  IRI subjectIri;

  @Test
  public void testDeleteCallsMethods() {
    //arrange
    when(connection.getValueFactory()).thenReturn(valueFactory);
    when(valueFactory.createIRI(eq("subjectMock"))).thenReturn(subjectIri);
    initConnectionConsumer();

    //act
    client.deleteBySubject("subjectMock");

    //assert
    verify(connection).remove(subjectIri, null, null);
  }
}
