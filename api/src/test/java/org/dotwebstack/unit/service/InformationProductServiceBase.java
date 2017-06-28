package org.dotwebstack.unit.service;

import org.dotwebstack.data.utils.QueryUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.data.service.impl.InformationProductServiceImpl.*;

/**
 * Created by Rick Fleuren on 6/19/2017.
 */
public class InformationProductServiceBase {

    protected List<Map<String, Value>> convertModel(Model model) {
        return model.stream().map(s -> {
            Map<String, Value> result = new HashMap<>();
            result.put("s", s.getSubject());
            result.put("p", s.getPredicate());
            result.put("o", s.getObject());
            return result;
        }).collect(toList());
    }

    protected ModelBuilder createInformationProduct(String name, String query) {
        return createInformationProduct(name, query, "adapter");
    }

    protected ModelBuilder createInformationProduct(String name, String query, String adapter) {
        ModelBuilder builder = new ModelBuilder();

        return builder
                .setNamespace("ex", "http://example.org/")
                .subject("ex:" + name)
                .add(RDF.TYPE, QueryUtils.expand(ELMO_INFORMATION_PRODUCT))
                .add(QueryUtils.expand(ELMO_ADAPTER), adapter)
                .add(QueryUtils.expand(ELMO_QUERY), query);
    }
}
