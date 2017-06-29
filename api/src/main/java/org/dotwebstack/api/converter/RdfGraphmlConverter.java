package org.dotwebstack.api.converter;

import org.dotwebstack.data.utils.ModelUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfGraphmlConverter extends WriteOnlyRdfConverter {
    public RdfGraphmlConverter() {
        super(MediaType.valueOf("application/graphml+xml"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        Directives directives = buildXml(statements);

        try {
            String xml = new Xembler(directives).xml();
            try (OutputStreamWriter writer = new OutputStreamWriter(httpOutputMessage.getBody(), StandardCharsets.UTF_8)) {
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


        List<Model> models = ModelUtils.filterBySubject(statements);
        for (Model model : models) {
            Directives node = directives.add("node");

            

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
