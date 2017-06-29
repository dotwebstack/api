package org.dotwebstack.unit.data.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
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
public class TripleStoreClientTupleQueryTest extends TripleStoreClientTest {

  @Mock
  TupleQuery tupleQuery;

  @Mock
  TupleQueryResult tupleQueryResult;

  @Test
  public void testQueryCallsMethods() {
    //arrange
    when(connection.prepareTupleQuery(eq("myQuery"))).thenReturn(tupleQuery);
    when(tupleQuery.evaluate()).thenReturn(tupleQueryResult);
    initConnectionFunction();

    //act
    List<Map<String, Value>> result = client.select("myQuery");

    //assert
    verify(tupleQuery).evaluate();
    assertNotNull("Model should have been returned", result);
  }
}
