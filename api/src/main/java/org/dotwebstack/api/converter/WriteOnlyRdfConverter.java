package org.dotwebstack.api.converter;

import java.io.IOException;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public abstract class WriteOnlyRdfConverter extends AbstractHttpMessageConverter<Model> {

  public WriteOnlyRdfConverter(MediaType... types) {
    super(types);
  }

  @Override
  protected boolean supports(Class<?> aClass) {
    return Model.class.isAssignableFrom(aClass);
  }

  @Override
  protected Model readInternal(Class<? extends Model> aClass, HttpInputMessage httpInputMessage)
      throws IOException, HttpMessageNotReadableException {
    throw new UnsupportedOperationException("This converter does not support parsing of html");
  }

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }
}
