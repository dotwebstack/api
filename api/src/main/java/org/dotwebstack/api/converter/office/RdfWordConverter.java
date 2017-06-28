package org.dotwebstack.api.converter.office;

import org.dotwebstack.api.converter.ResourceConverter;
import org.dotwebstack.api.converter.WriteOnlyRdfConverter;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import org.eclipse.rdf4j.model.Model;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfWordConverter extends WriteOnlyRdfConverter implements ResourceConverter {

    private ResourceLoader loader;

    public RdfWordConverter() {
        super(MediaType.valueOf("application/msword"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        assert loader != null;

        httpOutputMessage.getHeaders().add("Content-Disposition", "attachment; filename=document.doc");
        Resource resource = loader.getResource("classpath:templates/template.doc");
        HWPFDocument document = new HWPFDocument(resource.getInputStream());
        addRdfData(document, statements);

        document.write(httpOutputMessage.getBody());
    }

    private void addRdfData(HWPFDocument document, Model statements) {
        Range range = document.getRange();

        statements.forEach(statement -> {
            String line = statement.getSubject().toString() + " " + statement.getPredicate().toString() + " " + statement.getObject() + "\r";
            CharacterRun charRun = range.insertAfter(line);
            charRun.setFontSize(18);
        });
    }

    @Override
    public void setResourceLoader(ResourceLoader loader) {
        this.loader = loader;
    }
}
