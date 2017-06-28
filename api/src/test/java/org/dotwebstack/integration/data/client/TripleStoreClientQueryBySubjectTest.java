package org.dotwebstack.integration.data.client;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.assertEquals;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public class TripleStoreClientQueryBySubjectTest extends TripleStoreIntegrationTest {

    @Test
    public void testQueryBySubject() {
        //arrange
        Model pablo = createArtist("Picasso").build();
        addModelToStore(pablo);

        //act
        Model model = tripleStore.queryBySubject("http://example.org/Picasso");

        //assert
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
}
