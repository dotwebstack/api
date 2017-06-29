package org.dotwebstack.data.client.impl;

import org.dotwebstack.data.repository.impl.ConfigurationRepository;
import org.springframework.stereotype.Service;

/**
 * Created by Rick Fleuren on 6/15/2017.
 */
@Service
public class ConfigurationTripleStoreClient extends TripleStoreClientImpl<ConfigurationRepository> {

  public ConfigurationTripleStoreClient() {
    super("Configuration");
  }
}
