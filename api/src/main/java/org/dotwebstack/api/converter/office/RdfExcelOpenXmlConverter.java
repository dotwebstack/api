package org.dotwebstack.api.converter.office;

import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfExcelOpenXmlConverter extends RdfExcelConverterBase {

  public RdfExcelOpenXmlConverter() {
    super(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
  }

  @Override
  protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=data.xlsx");
    createWorkbook(statements, () -> new XSSFWorkbook()).write(httpOutputMessage.getBody());
  }
}
