package org.dotwebstack.integration.data.client.configuration;

import org.dotwebstack.data.client.impl.SailMemoryTripleStoreClient;
import org.dotwebstack.data.repository.impl.SailMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Testcase configuration with default beans
 * <p>
 * Created by Rick Fleuren on 6/9/2017.
 */
@Configuration
public class SailMemoryTestConfiguration {

    @Bean
    public SailMemoryRepository getTripleStoreRepository() {
        return new SailMemoryRepository();
    }

    @Bean
    public SailMemoryTripleStoreClient getTripleStoreClient() {
        return new SailMemoryTripleStoreClient();
    }

}
