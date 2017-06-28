package org.dotwebstack.api.converter;

import com.opencsv.CSVWriter;
import org.eclipse.rdf4j.model.Model;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static java.util.stream.Collectors.toList;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
public class RdfCsvConverter extends WriteOnlyRdfConverter {

    public RdfCsvConverter() {
        super(MediaType.valueOf("text/csv"));
    }

    @Override
    protected void writeInternal(Model statements, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpOutputMessage.getBody(), StandardCharsets.UTF_8)) {
            try (CSVWriter writer = new CSVWriter(outputStreamWriter)) {
                writer.writeAll(statements.stream().map(s ->
                        new String[]{
                                s.getSubject().toString(),
                                s.getPredicate().toString(),
                                s.getObject().toString()
                        }).collect(toList()));
            }
        }
    }
}
