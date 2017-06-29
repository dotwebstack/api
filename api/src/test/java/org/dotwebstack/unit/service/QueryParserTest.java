package org.dotwebstack.unit.service;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.dotwebstack.data.service.QueryParser;
import org.dotwebstack.test.categories.Categories;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */

@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class QueryParserTest {

  @Test
  public void testEmptyQuery() {
    //act
    List<String> result = QueryParser.getParametersFromQuery("");

    //assert
    assertEquals("Size should be 0", 0, result.size());
  }

  @Test
  public void testOneParameter() {
    //act
    List<String> result = QueryParser.getParametersFromQuery("this is my @PARAMETER@ parameter");

    //assert
    assertEquals("Size should be 1", 1, result.size());
    assertEquals("Should be PARAMETER", "PARAMETER", result.get(0));
  }

  @Test
  public void testTwoParameters() {
    //act
    List<String> result = QueryParser
        .getParametersFromQuery("this is my @PARAMETER@ parameter but also this one @PARAMETER2@");

    //assert
    assertEquals("Size should be 2", 2, result.size());
    assertEquals("Should be PARAMETER", "PARAMETER", result.get(0));
    assertEquals("Should be PARAMETER2", "PARAMETER2", result.get(1));
  }

  @Test
  public void testDistinctParameters() {
    //act
    List<String> result = QueryParser.getParametersFromQuery("this is my @PARAMETER@ parameter \n" +
        "but also this one @PARAMETER2@\n" +
        "Lets not forget this one @PARAMETER\n");

    //assert
    assertEquals("Size should be 2", 2, result.size());
    assertEquals("Should be PARAMETER", "PARAMETER", result.get(0));
    assertEquals("Should be PARAMETER2", "PARAMETER2", result.get(1));
  }
}
