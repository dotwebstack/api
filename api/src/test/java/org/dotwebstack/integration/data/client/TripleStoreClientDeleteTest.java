package org.dotwebstack.integration.data.client;

import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;

import org.eclipse.rdf4j.model.Model;
import org.junit.Test;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class TripleStoreClientDeleteTest extends TripleStoreIntegrationTest {

  @Test
  public void testDelete() {
    //arrange
    Model pablo = createArtist("Picasso").build();
    addModelToStore(pablo);
    Model model = getStatementsFromStore();
    assertEquals("There should be 2 statements, all should be stored first", 2, model.size());

    //act
    tripleStore.deleteBySubject("http://example.org/Picasso");

    //assert
    Model result = getStatementsFromStore();
    assertEquals("There should be 0 statements, all should be removed", 0, result.size());
  }
}
