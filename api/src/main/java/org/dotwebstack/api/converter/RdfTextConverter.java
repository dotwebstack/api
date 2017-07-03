package org.dotwebstack.api.converter;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Created by PJuergensen on 6/30/2017.
 */
public class RdfTextConverter extends WriteOnlyRdfConverter {

  public RdfTextConverter() {
    super(TEXT_PLAIN);
  }

  @Override
  protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpOutputMessage.getBody(),
        StandardCharsets.UTF_8)) {
      for (Statement statement : statements) {
        outputStreamWriter.write(
            statement.getSubject().stringValue() + " " + statement.getPredicate().stringValue()
                + " " + statement.getObject().stringValue() + "\n");
      }

    }
  }
}
