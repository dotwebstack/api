package org.dotwebstack.api.converter;

import org.springframework.core.io.ResourceLoader;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public interface ResourceConverter {
    void setResourceLoader(ResourceLoader loader);
}
