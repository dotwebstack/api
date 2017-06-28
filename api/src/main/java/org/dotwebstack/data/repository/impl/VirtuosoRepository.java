package org.dotwebstack.data.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@Repository("Virtuoso")
//@Primary
public class VirtuosoRepository extends Rdf4JRepository {

    private final virtuoso.rdf4j.driver.VirtuosoRepository repository;

    @Autowired
    public VirtuosoRepository(@Value("${virtuoso.connection.url}") String url,
                              @Value("${virtuoso.connection.user}") String user,
                              @Value("${virtuoso.connection.password}") String password,
                              @Value("${virtuoso.default.graph}") String graph) {
        repository = new virtuoso.rdf4j.driver.VirtuosoRepository(url, user, password, graph);
        repository.initialize();
    }

    @Override
    public org.eclipse.rdf4j.repository.Repository getRepository() {
        return repository;
    }
}
