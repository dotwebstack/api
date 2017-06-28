package org.dotwebstack.api.converter.office;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfWordOpenXmlConverter extends RdfExcelConverterBase {

    public RdfWordOpenXmlConverter() {
        super(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=document.docx");

        XWPFDocument document = new XWPFDocument();
        addRdfData(document, statements);
        document.write(httpOutputMessage.getBody());
    }

    private void addRdfData(XWPFDocument document, Model statements) {
        XWPFRun run = document.createParagraph().createRun();
        run.setText("Linked data results");
        run.setFontSize(20);
        XWPFTable table = document.createTable(statements.size(), 3);

        List<Statement> list = statements.stream().collect(toList());
        for (int i = 0; i < statements.size(); i++) {
            Statement statement = list.get(i);
            XWPFTableRow row = table.getRow(i);
            row.getCell(0).addParagraph().createRun().setText(statement.getSubject().toString());
            row.getCell(1).addParagraph().createRun().setText(statement.getPredicate().toString());
            row.getCell(2).addParagraph().createRun().setText(statement.getObject().toString());
        }
        ;
    }
}
