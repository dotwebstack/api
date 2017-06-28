package org.dotwebstack.data.utils;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;

import java.util.*;

/**
 * Created by Rick Fleuren on 6/16/2017.
 */
public class ModelUtils {
    public static List<Model> filterBySubject(Collection<Statement> result) {
        Map<Resource, Model> modelMap = new HashMap<>();
        for (Statement statement : result) {
            Resource subject = statement.getSubject();

            if (!modelMap.containsKey(subject)) {
                modelMap.put(subject, new LinkedHashModel());
            }

            modelMap.get(subject).add(statement);
        }
        return new ArrayList<>(modelMap.values());
    }
}
