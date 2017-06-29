package org.dotwebstack.api.converter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

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

    try {

      XMLStreamWriter out = XMLOutputFactory.newInstance()
          .createXMLStreamWriter(new OutputStreamWriter(httpOutputMessage.getBody(), "utf-8"));

      out.writeStartDocument();
      out.writeStartElement("graphml");

      for (Statement s : statements) {
        writeKey(out, s.getPredicate().stringValue(), "node");
      }

      writeKey(out, "uri", "node");
      writeKey(out, "label", "node");
      writeKey(out, "uri", "edge");
      writeKey(out, "label", "edge");

      out.writeStartElement("graph");
      out.writeAttribute("id", "G");
      out.writeAttribute("edgedefault", "directed");

      out.writeEndElement();
      out.writeEndElement();

      out.writeEndDocument();

      out.flush();
      out.close();


    } catch (XMLStreamException e) {
      logger.error(e.getMessage(), e);
      throw new HttpMessageNotWritableException("Message not writable", e);
    }
  }

  private void writeKey(XMLStreamWriter out, String name, String forString)
      throws XMLStreamException {
    out.writeStartElement("key");
    out.writeAttribute("id", "uri");
    out.writeAttribute("for", forString);
    out.writeAttribute("attr.name", name);
    out.writeAttribute("attr.type", "string");
    out.writeEndElement();
  }
}
