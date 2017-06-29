package org.dotwebstack.integration.data.client;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class TripleStoreClientQueryTest extends TripleStoreIntegrationTest {

  @Test
  public void testQuery() {
    //arrange
    Model pablo = createArtist("Picasso").build();
    Model ross = createArtist("Ross").build();

    addModelToStore(pablo);
    addModelToStore(ross);

    //act
    Model model = tripleStore.query();

    //assert
    Rio.write(model, System.out, RDFFormat.TURTLE);
    assertEquals("Size of statements should be four after saving ", 4, model.size());

    Model filterOnNames = model.filter(null, FOAF.LAST_NAME, null);

    assertEquals("There should be two last names", 2, filterOnNames.size());

    List<String> names = filterOnNames.objects().stream().map(Value::stringValue).collect(toList());

    assertTrue("Names should contain Ross", names.contains("Ross"));
    assertTrue("Names should contain Picasso", names.contains("Picasso"));
  }

  @Test
  public void testQueryGroupedBySubject() {
    //arrange
    Model pablo = createArtist("Picasso").add(FOAF.FIRST_NAME, "Pablo").build();
    Model ross = createArtist("Ross").add(FOAF.FIRST_NAME, "Bob").build();

    addModelToStore(pablo);
    addModelToStore(ross);

    //act
    List<Model> models = tripleStore.queryGroupedBySubject();

    //assert
    models.forEach(model -> Rio.write(model, System.out, RDFFormat.TURTLE));

    assertEquals("Size should be two after saving ", 2, models.size());

    List<String> names = models.stream()
        .map(m -> m.filter(null, FOAF.LAST_NAME, null))
        .flatMap(m -> m.objects().stream())
        .map(Value::stringValue).collect(toList());

    assertTrue("Names should be Ross", names.contains("Ross"));
    assertTrue("Names should be Picasso", names.contains("Picasso"));
  }

}
