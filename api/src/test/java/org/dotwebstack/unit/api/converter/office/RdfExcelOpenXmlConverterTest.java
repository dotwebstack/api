package org.dotwebstack.unit.api.converter.office;

import org.dotwebstack.api.converter.office.RdfExcelOpenXmlConverter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dotwebstack.test.categories.Categories;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertFalse;
import static org.dotwebstack.utils.TestUtils.createArtist;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Rick Fleuren on 6/23/2017.
 */
@RunWith(MockitoJUnitRunner.class)
@Category(Categories.UnitTests.class)
public class RdfExcelOpenXmlConverterTest {

    final MediaType mediaType = MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    @Mock
    HttpOutputMessage message;

    @Mock
    HttpHeaders headers;

    @Test
    public void testCantReadOtherMediaType() {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to read other media types", canRead);
    }

    @Test
    public void testCantRead() {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();

        //act
        boolean canRead = converter.canRead(LinkedHashModel.class, mediaType);

        //assert
        assertFalse("It shouldn't be able to read models", canRead);
    }

    @Test
    public void testCanWrite() {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, mediaType);

        //assert
        assertTrue("It should be able to write models", canWrite);
    }

    @Test
    public void testCanWriteOtherMediaType() {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();

        //act
        boolean canWrite = converter.canWrite(LinkedHashModel.class, MediaType.APPLICATION_RSS_XML);

        //assert
        assertFalse("It shouldn't be able to write other media types", canWrite);
    }

    @Test
    public void testWrite() throws IOException {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        verify(message, times(2)).getBody();
    }

    @Test
    public void testAddsHeader() throws IOException {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();
        Model model = new LinkedHashModel();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        verify(headers).add("Content-Disposition", "attachment; filename=data.xlsx");
    }

    @Test
    public void testExcelTags() throws IOException {
        //arrange
        RdfExcelOpenXmlConverter converter = new RdfExcelOpenXmlConverter();
        Model model = createArtist("Picasso").build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(message.getBody()).thenReturn(outputStream);
        when(message.getHeaders()).thenReturn(headers);

        //act
        converter.write(model, mediaType, message);

        //assert
        XSSFWorkbook book = new XSSFWorkbook(new ByteArrayInputStream(outputStream.toByteArray()));
        assertEquals("1 sheet should be created", 1, book.getNumberOfSheets());
        Sheet data = book.getSheet("Data");
        assertNotNull("It should be called Data", data);
        assertEquals("Picasso should be in it", "http://example.org/Picasso", data.getRow(0).getCell(0).getStringCellValue());

        String excel = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertTrue("Should contain workbook.xml", excel.contains("workbook.xml"));
    }

}
