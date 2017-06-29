package org.dotwebstack.unit.data.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.query.BooleanQuery;
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
public class TripleStoreClientAskQueryTest extends TripleStoreClientTest {

  @Mock
  BooleanQuery booleanQuery;

  @Test
  public void testQueryCallsMethods() {
    //arrange
    when(connection.prepareBooleanQuery(eq("myQuery"))).thenReturn(booleanQuery);
    when(booleanQuery.evaluate()).thenReturn(true);
    initConnectionFunction();

    //act
    boolean result = client.ask("myQuery");

    //assert
    verify(booleanQuery).evaluate();
    assertTrue(result);
  }
}
