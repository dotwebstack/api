package org.dotwebstack.api.converter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.dotwebstack.data.utils.ModelUtils;
import org.dotwebstack.data.utils.helper.Subject;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;


/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfGraphmlConverter extends WriteOnlyRdfConverter {

  public RdfGraphmlConverter() {
    super(MediaType.valueOf("application/graphml+xml"));
  }

  @Override
  protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {

    Directives directives = buildXml(statements);

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

  private Directives buildXml(Model statements) {
    Directives directives = new Directives();

    directives.add("graphml");
    addKey(directives, "uri", "node");
    addKey(directives, "label", "node");
    addKey(directives, "uri", "edge");
    addKey(directives, "label", "edge");

    directives.add("graph")
        .attr("id", "G")
        .attr("edgedefault", "directed");

    List<Subject> subjects = ModelUtils.extractHelpers(statements);
    for (Subject model : subjects) {
      Directives node = directives.add("node");

      Optional<String> about = model.getValue("rdf:about");
      Optional<String> nodeId = model.getValue("rdf:nodeID");
      String id = nodeId.orElse("") + about.orElse("");
      node.attr("id", id);

      if (about.isPresent()) {
        node.add("data")
            .attr("key", "uri")
            .set(about.get())
            .up();
      }

      String label = model.getValue("rdfs:label")
          .orElse(about.map(s -> s.substring(s.lastIndexOf("#"))).orElse(""));

      node.add("data")
          .attr("key", "label")
          .set(label)
          .up();

      node.up();
    }

    return directives;
  }

  private void addKey(Directives directives, String name, String forString) {
    directives.add("key")
        .attr("id", "uri")
        .attr("for", forString)
        .attr("attr.name", name)
        .attr("attr.type", "string").up();
  }
}
