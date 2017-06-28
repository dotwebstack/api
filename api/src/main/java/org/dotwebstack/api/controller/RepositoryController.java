package org.dotwebstack.api.controller;

import org.dotwebstack.data.client.TripleStoreClient;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@RestController
@RequestMapping("/api/v1/repositories")
public class RepositoryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryController.class);

    private final TripleStoreClient client;

    @Autowired
    public RepositoryController(TripleStoreClient client) {
        this.client = client;
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Model> deleteAllData() {
        client.clearAllTriples();
        return ResponseEntity.ok(client.query());
    }

    @RequestMapping("/insert-testdata")
    public ResponseEntity<String> insertTest() {

        ModelBuilder builder = new ModelBuilder();

        Model pablo = builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + "Picasso")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, "Picasso")
                .add(FOAF.FIRST_NAME, "Pablo").build();

        client.add(pablo);

        Model bob = builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + "Ross")
                .add(RDF.TYPE, "ex:Artist")
                .add(FOAF.LAST_NAME, "Ross")
                .add(FOAF.FIRST_NAME, "Bob").build();

        client.add(bob);

        return ResponseEntity.ok("done!");
    }
}
