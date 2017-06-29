package org.dotwebstack.api.converter;

import static org.springframework.http.MediaType.APPLICATION_PDF;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * Created by Rick Fleuren on 6/26/2017.
 */
public class RdfPdfConverter extends WriteOnlyRdfConverter {

  public RdfPdfConverter() {
    super(APPLICATION_PDF);
  }

  @Override
  protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage)
      throws IOException, HttpMessageNotWritableException {
    httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=document.pdf");

    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(PDRectangle.A4);
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        createTitle(page, contentStream);
        document.addPage(page);

        float margin = 50;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

        boolean drawContent = true;
        float bottomMargin = 70;

        BaseTable table = new BaseTable(page.getMediaBox().getHeight() - 50, yStartNewPage,
            bottomMargin, tableWidth, margin, document, page, true, drawContent);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(33, "Subject");
        headerRow.createCell(33, "Predicate");
        headerRow.createCell(33, "Object");
        table.addHeaderRow(headerRow);

        for (Statement statement : statements) {
          Row<PDPage> row = table.createRow(12);
          row.createCell(33, statement.getSubject().toString());
          row.createCell(33, statement.getPredicate().toString());
          row.createCell(33, statement.getObject().toString());
        }

        table.draw();
      }

      document.save(httpOutputMessage.getBody());
    }
  }

  private void createTitle(PDPage page, PDPageContentStream contentStream) {
    PDFont font = PDType1Font.HELVETICA;
    float leftMargin = 50;
    float yPosition = page.getMediaBox().getHeight() - 20;
    float titleFontSize = 18;

    PDStreamUtils
        .write(contentStream, "Linked data results", font, titleFontSize, leftMargin, yPosition,
            Color.BLACK);
  }
}
