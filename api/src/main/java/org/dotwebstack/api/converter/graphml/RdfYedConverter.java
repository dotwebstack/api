package org.dotwebstack.api.converter.graphml;

import org.springframework.http.MediaType;
import org.xembly.Directives;


/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfYedConverter extends RdfGraphmlConverterBase {

  //todo: enrich
  public RdfYedConverter() {
    super(MediaType.valueOf("application/x.elmo.yed"));
  }

  @Override
  protected void handleKey(Directives key, String name, String forString) {
    if ("node".equals(forString) && "label".equals(name)) {
      key.attr("yfiles.type", "nodegraphics");
    } else if ("edge".equals(forString) && "label".equals(name)) {
      key.attr("yfiles.type", "edgegraphics");
    }
  }

  @Override
  protected void handleNode(Directives node, String label) {
    node.add("y:ShapeNode")
        .add("y:NodeLabel")
        .set(label)
        .up()
        .up();

  }

  @Override
  protected void handleEdge(Directives edge, String label) {
    edge.add("y:PolyLineEdge")
        .add("y:EdgeLabel")
        .set(label)
        .up()
        .up();
  }

  @Override
  protected void enrichGraphNode(Directives node) {
    node.attr("xmlns:y", "http://www.yworks.com/xml/graphml")
        .attr("xsi:schemaLocation",
            "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd");

  }
}
