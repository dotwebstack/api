package org.dotwebstack.api.converter.graphml;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.function.Consumer;
import org.dotwebstack.data.utils.helper.Subject;
import org.springframework.http.MediaType;
import org.xembly.Directives;


/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfYedConverter extends RdfGraphmlConverterBase {

  //todo: elmo background color from config
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
  protected void handleNode(Directives node, Subject model, String label) {
    node.add("y:GenericNode")
        .attr("configuration", "com.yworks.entityRelationship.big_entity");

    node.add("y:Geometry").attr("height", 100).attr("width", 200).attr("x", .5).attr("y", .5).up();
    node.add("y:Fill").attr("color", "#E8EEF7").attr("color2", "#B7C9E3").attr("transparent", false)
        .up();
    node.add("y:BorderStyle").attr("color", "#000000").attr("type", "line").attr("width", "1.0")
        .up();

    addLabel(node, "internal", n -> n.set(label));
    addComments(node, model);

    node.add("y:StyleProperties").add("y:Property")
        .attr("class", "java.lang.Boolean")
        .attr("name", "y.view.ShadowNodePainter.SHADOW_PAINTING")
        .attr("value", true)
        .up().up();
    node.up();

  }

  private void addComments(Directives node, Subject model) {
    List<String> comments = model.getStringValues("rdfs:comment");
    if (comments.size() == 0) {
      return;
    }
    addLabel(node, "custom", n -> {
      n.set(comments.stream().collect(joining("\n")));
      n.add("y:LabelModel").add("y:ErdAttributesNodeLabelModel").up().up();
      n.add("y:ModelParameter").add("y:ErdAttributesNodeLabelModelParameter").up().up();
    });
  }

  private void addLabel(Directives node, String modelName, Consumer<Directives> apply) {
    node.add("y:NodeLabel")
        .attr("alignment", "center")
        .attr("autoSizePolicy", "node_width")
        .attr("configuration", "CroppingLabel")
        .attr("fontFamily", "Dialog")
        .attr("fontSize", 12)
        .attr("fontStyle", "plain")
        .attr("hasLineColor", false)
        .attr("modelName", modelName)
        .attr("modelPosition", "t")
        .attr("textColor", "#000000")
        .attr("visible", true)
        .attr("hasBackgroundColor", false);

    apply.accept(node);

    node.up();
  }

  @Override
  protected void handleEdge(Directives edge, String label, String source, String target) {
    edge.add("y:PolyLineEdge");

    edge.add("y:LineStyle").attr("color", "#000000").attr("width", 1).up();
    edge.add("y:Arrows").attr("source", source).attr("target", target).up();

    edge.add("y:EdgeLabel")
        .attr("alignment", "center")
        .attr("configuration", "AutoFlippingLabel")
        .attr("distance", 2)
        .attr("fontFamily", "Dialog")
        .attr("fontSize", 12)
        .attr("fontStyle", "plain")
        .attr("hasBackgroundColor", false)
        .attr("hasLineColor", false)
        .attr("modelName", "custom")
        .attr("preferredPlacement", "anywhere")
        .attr("ratio", 0.5)
        .attr("textColor", "#000000")
        .attr("visible", true)

        .set(label)
        .add("y:LabelModel")
        .add("y:SmartEdgeLabelModel").attr("autoRotationEnabled", false).attr("defaultAngle", 0)
        .attr("defaultDistance", 10)
        .up()
        .up()

        .add("y:ModelParameter")
        .add("y:SmartEdgeLabelModelParameter").attr("angle", 0).attr("distance", 30)
        .attr("distanceToCenter", true).attr("position", "center").attr("ratio", .5)
        .attr("segment", 0)
        .up()
        .up()

        .add("y:PreferredPlacementDescriptor").attr("angle", 0).attr("angleOffsetOnRightSide", 0)
        .attr("angleReference", "absolute").attr("angleRotationOnRightSide", "co")
        .attr("distance", -1)
        .attr("frozen", true)
        .attr("placement", "anywhere")
        .attr("side", "anywhere")
        .attr("sideReference", "relative_to_edge_flow")
        .up()

        .up()
        .add("y:BendStyle").attr("smoothed", false).up()
        .up();
  }

  @Override
  protected void enrichGraphNode(Directives node) {
    node.attr("xmlns:y", "http://www.yworks.com/xml/graphml")
        .attr("xsi:schemaLocation",
            "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd");

  }
}
