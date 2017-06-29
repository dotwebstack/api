package org.dotwebstack.integration.data.client;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Test;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class TripleStoreClientAddTest extends TripleStoreIntegrationTest {

  @Test
  public void testSingleAdd() {
    //arrange
    Model pablo = createArtist("Picasso").build();

    //act
    tripleStore.add(pablo);

    //assert
    Model model = getStatementsFromStore();

    assertEquals("There should be 2 statements", 2, model.size());

    List<String> names = model.filter(null, FOAF.LAST_NAME, null).
        stream().map(m -> m.getObject().stringValue()).collect(toList());

    assertEquals("There should be 1 last name", 1, names.size());
    assertEquals("It should be Picasso", "Picasso", names.get(0));

    List<String> types = model.filter(null, RDF.TYPE, null).
        stream().map(m -> m.getObject().stringValue()).collect(toList());

    assertEquals("There should be a type", 1, types.size());
    assertEquals("It should be an Artist", "http://example.org/Artist", types.get(0));
  }

  @Test
  public void testDoubleAddLastname() {
    //arrange
    Model pablo = createArtist("Picasso").build();
    Model pablo2 = createArtist("Picasso", "YetAnotherPicasso").build();

    //act
    tripleStore.add(pablo);
    tripleStore.add(pablo2);

    //assert
    Model model = getStatementsFromStore();

    assertEquals("There should be 3 statements", 3, model.size());

    String[] names = model.filter(null, FOAF.LAST_NAME, null).
        stream().map(m -> m.getObject().stringValue()).toArray(String[]::new);

    assertEquals("There should be 2 last name", 2, names.length);
    String[] expectedNames = {"Picasso", "YetAnotherPicasso"};
    assertArrayEquals("It should be Picasso and YetAnotherPicasso", expectedNames, names);

    List<String> types = model.filter(null, RDF.TYPE, null).
        stream().map(m -> m.getObject().stringValue()).collect(toList());

    assertEquals("There should be one type", 1, types.size());
    assertEquals("It should be one Artist object", "http://example.org/Artist", types.get(0));
  }
}
