package org.dotwebstack.api.converter.office;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dotwebstack.api.converter.WriteOnlyRdfConverter;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.MediaType;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public abstract class RdfExcelConverterBase extends WriteOnlyRdfConverter {

  public RdfExcelConverterBase(MediaType mediaType) {
    super(mediaType);
  }

  protected Workbook createWorkbook(Model statements, Supplier<Workbook> getBook)
      throws IOException {
    Workbook workbook = getBook.get();
    Sheet sheet = workbook.createSheet("Data");

    List<Statement> list = statements.stream().collect(toList());
    for (int i = 0; i < statements.size(); i++) {
      Row row = sheet.createRow(i);
      Statement statement = list.get(i);
      row.createCell(0).setCellValue(statement.getSubject().toString());
      row.createCell(1).setCellValue(statement.getPredicate().toString());

      if (statement.getObject() instanceof Literal) {
        Literal literal = (Literal) statement.getObject();
        row.createCell(2).setCellValue(literal.stringValue());
        row.createCell(3).setCellValue(literal.getDatatype().toString());
        row.createCell(4).setCellValue(literal.getLanguage().orElse(""));
      } else {
        row.createCell(2).setCellValue(statement.getObject().toString());
      }
    }

    return workbook;
  }
}
