package org.dotwebstack.data.service;

import org.eclipse.rdf4j.model.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Rick Fleuren on 6/19/2017.
 */
public interface InformationProductService {
    /**
     * Finds all inforamtion products with its meta data
     *
     * @return
     */
    List<InformationProduct> getInformationProducts();

    /**
     * Finds one information product with its meta data
     *
     * @return
     */
    Optional<InformationProduct> getInformationProduct(String name);

    /**
     * Queries the information product against the datasource
     *
     * @return
     */
    Model queryInformationProduct(String name, Map<String, String> parameters);
}
