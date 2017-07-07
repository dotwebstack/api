package org.dotwebstack.api.converter.graphml;

import org.dotwebstack.data.utils.helper.Subject;
import org.springframework.http.MediaType;
import org.xembly.Directives;


/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfGraphmlConverter extends RdfGraphmlConverterBase {

  public RdfGraphmlConverter() {
    super(MediaType.valueOf("application/graphml+xml"));
  }

  @Override
  protected void enrichGraphNode(Directives node) {
    node.attr("xsi:schemaLocation",
        "http://graphml.graphdrawing.org/xmlns");

  }

  @Override
  protected void handleKey(Directives key, String name, String forString) {
    //nothing
  }

  @Override
  protected void handleNode(Directives node, Subject model,
      String label) {
    node.set(label);
  }

  @Override
  protected void handleEdge(Directives edge, String label, String source, String target) {
    edge.set(label);
  }


}
