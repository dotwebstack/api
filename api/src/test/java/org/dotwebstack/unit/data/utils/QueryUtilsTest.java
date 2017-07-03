package org.dotwebstack.unit.data.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.dotwebstack.data.utils.QueryUtils;
import org.dotwebstack.test.categories.Categories;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Rick Fleuren on 6/19/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class QueryUtilsTest {

  @Test
  public void testNull() {
    //act
    String result = QueryUtils.addDefaultPrefixes(null);

    //assert
    assertNull("No result should be added", result);
  }

  @Test
  public void testNothingAdded() {
    //act
    String result = QueryUtils.addDefaultPrefixes("");

    //assert
    assertEquals("No result should be added", "", result);
  }

  @Test
  public void testNamespaceAdded() {
    //act
    String result = QueryUtils.addDefaultPrefixes("SELECT {?s rdf:type ?o}");

    //assert
    assertEquals("Results should be the same",
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nSELECT {?s rdf:type ?o}",
        result);
  }

  @Test
  public void testNamespaceIgnored() {
    //act
    String result = QueryUtils
        .addDefaultPrefixes("PREFIX rdf: <http://mynamespace.org/>\nSELECT {?s rdf:type ?o}");

    //assert
    assertEquals("Results should be the same",
        "PREFIX rdf: <http://mynamespace.org/>\nSELECT {?s rdf:type ?o}", result);
  }

  @Test
  public void testURIs() {
    //act
    String result = QueryUtils.addDefaultPrefixes("http://www.example.org/");

    //assert
    assertEquals("No result should be added", "http://www.example.org/", result);
  }

  @Test
  public void testExpandNull() {
    //act
    String result = QueryUtils.expand(null);

    //assert
    assertNull("No prefix should be added", result);
  }

  @Test
  public void testExpandNoPrefix() {
    //act
    String result = QueryUtils.expand("String");

    //assert
    assertEquals("No prefix should be added", "String", result);
  }

  @Test
  public void testExpandAddPrefix() {
    //act
    String result = QueryUtils.expand("elmo:String");

    //assert
    assertEquals("Prefix should be added", "http://bp4mc2.org/elmo/def#String", result);
  }

  @Test
  public void testExpandIgnoreUris() {
    //act
    String result = QueryUtils.expand("http://example.org/");

    //assert
    assertEquals("Should be ignored", "http://example.org/", result);
  }

  @Test
  public void testFlattenNull() {
    //act
    String result = QueryUtils.flatten(null);

    //assert
    assertNull("No prefix should be added", result);
  }

  @Test
  public void testFlattenNoUri() {
    //act
    String result = QueryUtils.flatten("String");

    //assert
    assertEquals("No prefix should be added", "String", result);
  }

  @Test
  public void testFlattenReplacePrefix() {
    //act
    String result = QueryUtils.flatten("http://bp4mc2.org/elmo/def#String");

    //assert
    assertEquals("Prefix should be replaced", "elmo:String", result);
  }

  @Test
  public void testGetNamespaceNull() {
    //act
    String namespace = QueryUtils.getNamespace(null);

    //assert
    assertNull("No namespace should be added", namespace);
  }

  @Test
  public void testGetNamespaceNoUri() {
    //act
    String namespace = QueryUtils.getNamespace("myString");

    //assert
    assertEquals("No namespace should be added", "myString", namespace);
  }

  @Test
  public void testGetNamespaceHash() {
    //act
    String namespace = QueryUtils.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#about");

    //assert
    assertEquals("Namespace should be found", "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        namespace);
  }

  @Test
  public void testGetNamespaceSlash() {
    //act
    String namespace = QueryUtils.getNamespace("http://id.loc.gov/vocabulary/iso639-1/about");

    //assert
    assertEquals("Namespace should be found", "http://id.loc.gov/vocabulary/iso639-1/",
        namespace);
  }

  @Test
  public void testGetNamespaceHashNSlash() {
    //act
    String namespace = QueryUtils.getNamespace("http://id.loc.gov/vocabulary/iso639-1/#about");

    //assert
    assertEquals("Namespace should be found", "http://id.loc.gov/vocabulary/iso639-1/#",
        namespace);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testExpandUnknownNamespace() {
    //act
    QueryUtils.expand("ex:String");
  }
}
