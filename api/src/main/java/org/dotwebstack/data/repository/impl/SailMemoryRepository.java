package org.dotwebstack.data.repository.impl;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;

import java.io.File;

/**
 * Created by Rick Fleuren on 6/9/2017.
 */

@org.springframework.stereotype.Repository("Memory")
@Primary
public class SailMemoryRepository extends Rdf4JRepository {
    private Repository repository;

    public SailMemoryRepository() {
        this(false, null);
    }

    @Autowired
    public SailMemoryRepository(@Value("${sail.init.clearData}") boolean clearData, @Value("${sail.init.filepath}") String filePath) {
        MemoryStore memoryStore = filePath != null && !"".equals(filePath)
                ? new MemoryStore(new File(filePath))
                : new MemoryStore();

        memoryStore.setPersist(true);

        repository = new org.eclipse.rdf4j.repository.sail.SailRepository(memoryStore);

        if (clearData) {
            clearAllData();
        }
    }

    private void clearAllData() {
        performQuery(connection -> {
            connection.clear();
            connection.clearNamespaces();
        });
    }

    @Override
    public Repository getRepository() {
        return repository;
    }
}
