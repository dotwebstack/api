package org.dotwebstack.data.client.impl;

import org.dotwebstack.data.repository.impl.SPARQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class SPARQLTripleStoreClient extends TripleStoreClientImpl<SPARQLRepository> {

    @Autowired
    public SPARQLTripleStoreClient(@Value("${sparql.init.adapterName}") String adapterName) {
        super(adapterName);
    }
}
