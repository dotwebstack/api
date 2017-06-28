package org.dotwebstack.data.repository;

import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */
public interface TripleStoreRepository {

    void performQuery(Consumer<RepositoryConnection> performQuery);

    <T> T performQuery(Function<RepositoryConnection, T> performQuery);
}
