package org.dotwebstack.data.utils.helper;

import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;
import org.dotwebstack.data.utils.QueryUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;

/**
 * Created by Rick Fleuren on 6/29/2017.
 */
public class Subject {

  private String subject;
  private Map<String, Value> values;

  public Subject(Model model) {
    assert model.subjects().size() == 1; //size should be 1, otherwise we cant work with this object
    subject = model.subjects().stream().findFirst().get().stringValue();
    values = model.stream()
        .collect(toMap(s -> s.getPredicate().stringValue(), Statement::getObject));
  }

  public boolean containsValue(String predicate) {
    return getValue(predicate).isPresent();
  }

  public Optional<String> getValue(String predicate) {
    if (predicate == null) {
      return Optional.empty();
    }
    //Expand all known namespaces, ie rdf:about becomes http://www.w3.org/1999/02/22-rdf-syntax-ns#about
    predicate = QueryUtils.expand(predicate);

    if (!values.containsKey(predicate)) {
      return Optional.empty();
    }

    return Optional.of(values.get(predicate).stringValue());
  }

  public String getSubject() {
    return subject;
  }
}
