package org.dotwebstack.data.utils.helper;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.dotwebstack.data.utils.QueryUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;

/**
 * Created by Rick Fleuren on 6/29/2017.
 */
public class Subject {

  private String subject;
  private Map<String, List<Value>> values;

  public Subject(Model model) {
    assert model.subjects().size() == 1; //size should be 1, otherwise we cant work with this object
    subject = model.subjects().stream().findFirst().get().stringValue();

    values = model.stream()
        .collect(groupingBy(o -> o.getPredicate().stringValue(),
            mapping(s -> s.getObject(), toList())));
  }

  public boolean containsValue(String predicate) {
    return getValues(predicate).size() != 0;
  }

  public Optional<Value> getValue(String predicate) {
    return getValues(predicate).stream().findFirst();
  }

  public Set<String> getPredicates() {
    return values.keySet();
  }

  public boolean isIRI(String predicate) {

    Optional<Value> value = getValue(predicate);
    if (!value.isPresent()) {
      return false;
    }

    return value.get() instanceof IRI;
  }

  public Optional<String> getStringValue(String predicate) {
    return getStringValues(predicate).stream().findFirst();
  }

  public List<String> getStringValues(String predicate) {
    return getValues(predicate).stream().map(v -> v.stringValue()).collect(toList());
  }

  public List<Value> getValues(String predicate) {
    if (predicate == null) {
      return new ArrayList<>();
    }
    //Expand all known namespaces, ie rdf:about becomes http://www.w3.org/1999/02/22-rdf-syntax-ns#about
    predicate = QueryUtils.expand(predicate);

    if (!values.containsKey(predicate)) {
      return new ArrayList<>();
    }

    return values.get(predicate);
  }

  public String getSubject() {
    return subject;
  }
}
