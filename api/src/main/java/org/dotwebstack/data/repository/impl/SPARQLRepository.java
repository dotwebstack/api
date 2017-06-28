package org.dotwebstack.data.repository.impl;

import org.eclipse.rdf4j.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@org.springframework.stereotype.Repository("SPARQL")
public class SPARQLRepository extends Rdf4JRepository {

    private Repository repository;

    @Autowired
    public SPARQLRepository(@Value("${sparql.init.queryEndpoint}") String queryEndpoint,
                            @Value("${sparql.init.updateEndpoint}") String updateEndpoint) {

        repository = updateEndpoint != null && !"".equals(updateEndpoint) ?
                new org.eclipse.rdf4j.repository.sparql.SPARQLRepository(queryEndpoint, updateEndpoint) :
                new org.eclipse.rdf4j.repository.sparql.SPARQLRepository(queryEndpoint);
    }

    @Override
    public Repository getRepository() {
        return repository;
    }
}
