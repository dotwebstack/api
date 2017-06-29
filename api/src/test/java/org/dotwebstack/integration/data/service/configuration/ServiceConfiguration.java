package org.dotwebstack.integration.data.service.configuration;

import java.io.IOException;
import org.dotwebstack.data.client.impl.ConfigurationTripleStoreClient;
import org.dotwebstack.data.repository.impl.ConfigurationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
@Configuration
public class ServiceConfiguration {

  @Bean
  public ConfigurationRepository getConfigurationRepository(ResourceLoader loader)
      throws IOException {
    return new ConfigurationRepository(loader, "http://example.org", "testconfig");
  }

  @Bean
  public ConfigurationTripleStoreClient getConfigurationTripleStoreClient() {
    return new ConfigurationTripleStoreClient();
  }
}
