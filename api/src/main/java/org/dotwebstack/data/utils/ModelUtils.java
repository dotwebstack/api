package org.dotwebstack.data.utils;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dotwebstack.data.utils.helper.Subject;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;

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

  public static List<Subject> extractHelpers(Collection<Statement> result) {
    List<Model> models = filterBySubject(result);
    return models.stream().map(m -> new Subject(m)).collect(toList());
  }
}
