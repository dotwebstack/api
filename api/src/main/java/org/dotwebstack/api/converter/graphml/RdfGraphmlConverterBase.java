package org.dotwebstack.api.converter.graphml;

import static java.util.stream.Collectors.toList;
import static org.dotwebstack.data.utils.ModelUtils.extractHelpers;
import static org.dotwebstack.data.utils.QueryUtils.flatten;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.dotwebstack.api.converter.WriteOnlyRdfConverter;
import org.dotwebstack.data.utils.helper.Subject;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Created by Rick Fleuren on 6/30/2017.
 */
public abstract class RdfGraphmlConverterBase extends WriteOnlyRdfConverter {

  protected RdfGraphmlConverterBase(MediaType mediaType) {
    super(mediaType);
  }

  protected abstract void handleKey(Directives key, String name, String forString);

  protected abstract void handleNode(Directives node,
      Subject model, String label);

  protected abstract void handleEdge(Directives edge, String label, String source, String target);

  protected abstract void enrichGraphNode(Directives directives);

  @Override
  protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    httpOutputMessage.getHeaders()
        .add("Content-Disposition", "attachment; filename=diagram.graphml");

    List<Subject> subjects = extractHelpers(statements);
    Directives directives = buildXml(subjects);

    try {
      String xml = new Xembler(directives).xml();
      try (OutputStreamWriter writer = new OutputStreamWriter(httpOutputMessage.getBody(),
          StandardCharsets.UTF_8)) {
        writer.write(xml);
      }

    } catch (ImpossibleModificationException e) {

      logger.error(e.getMessage(), e);
      throw new HttpMessageNotWritableException("Message not writable", e);
    }
  }

  private Directives buildXml(List<Subject> subjects) {

    Directives directives = new Directives();
    directives.add("graphml")
        .attr("xmlns", "http://graphml.graphdrawing.org/xmlns")
        .attr("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

    enrichGraphNode(directives);

    addKeysToXml(subjects, directives);

    directives.add("graph").attr("id", "G").attr("edgedefault", "directed");

    addNodes(subjects, directives);
    addEdges(subjects, directives);

    return directives;
  }

  private void addNodes(List<Subject> subjects, Directives directives) {
    for (Subject model : subjects) {
      Directives node = directives.add("node");

      String about = getAboutString(model, node);

      node.add("data").attr("key", "uri").set(about).up();

      for (String predicate : model.getPredicates()) {
        String flatPredicate = flattenAndMakeXmlReady(predicate);

        List<Value> stringValues = model.getValues(predicate);
        stringValues.stream().filter(s -> !(s instanceof IRI)).forEach(s -> node.add("data")
            .attr("key", flatPredicate).set(s.stringValue()).up());
      }

      int index = about.lastIndexOf("#");
      String label = model.getStringValue("rdfs:label")
          .orElse(index == -1 ? about : about.substring(index));

      node.add("data").attr("key", "label");
      handleNode(node, model, label);
      node.up();

      node.up();
    }
  }

  private String getAboutString(Subject model, Directives node) {
    String about = model.getSubject();
    Optional<String> nodeId = model.getStringValue("rdf:nodeID");
    String id = nodeId.orElse("") + about;
    node.attr("id", id);
    return about;
  }

  private void addEdges(List<Subject> subjects, Directives directives) {
    List<String> allSubjects = subjects.stream().map(s -> s.getSubject()).collect(toList());
    for (Subject model : subjects) {

      for (String predicate : model.getPredicates()) {

        String flatPredicate = flattenAndMakeXmlReady(predicate);

        List<Value> stringValues = model.getValues(predicate);
        stringValues.stream().filter(s -> s instanceof IRI && allSubjects.contains(s.stringValue()))
            .forEach(s -> {
                  directives.add("edge")
                      .attr("source", model.getSubject())
                      .attr("target", s.stringValue())
                      .add("data")
                      .attr("key", "uri")
                      .set(predicate)
                      .up()
                      .add("data")
                      .attr("key", "label");

                  handleEdge(directives, flatPredicate, model.getSubject(), s.stringValue());

                  directives
                      .up()
                      .up();
                }
            );
      }
    }
  }

  private void addKeysToXml(List<Subject> subjects, Directives directives) {
    addKey(directives, "uri", "node");
    addKey(directives, "label", "node");
    addKey(directives, "uri", "edge");
    addKey(directives, "label", "edge");

    List<String> distinctPredicates = subjects.stream()
        .flatMap(s -> s.getPredicates().stream().filter(p -> !s.isIRI(p)))
        .distinct()
        .collect(toList());

    for (String predicate : distinctPredicates) {
      String key = flattenAndMakeXmlReady(predicate);
      directives.add("key")
          .attr("id", key)
          .attr("for", "node")
          .attr("attr.name", key)
          .attr("attr.type", "string").up();
    }
  }

  private String flattenAndMakeXmlReady(String uri) {
    String flatUri = flatten(uri);
    return flatUri.replace(':', '-');
  }

  private void addKey(Directives directives, String name, String forString) {
    directives.add("key")
        .attr("id", name)
        .attr("for", forString)
        .attr("attr.name", name)
        .attr("attr.type", "string");

    handleKey(directives, name, forString);
    directives.up();
  }

}
