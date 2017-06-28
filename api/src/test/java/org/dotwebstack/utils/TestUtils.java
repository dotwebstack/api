package org.dotwebstack.utils;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class TestUtils {

    public static ModelBuilder createArtist(String artistName) {
        return create(artistName, artistName, "Artist");
    }

    public static ModelBuilder createArtist(String subject, String artistName) {
        return create(subject, artistName, "Artist");
    }

    public static ModelBuilder create(String subject, String name, String type) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + subject)
                .add(RDF.TYPE, "ex:" + type)
                .add(FOAF.LAST_NAME, name);
    }
}
