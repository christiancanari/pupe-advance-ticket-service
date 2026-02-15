package com.christiancanari.pupe.advance.ticket.service.infrastructure.file.adapter;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelTicketFolderReaderAdapterTest {

    private ExcelTicketFolderReaderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ExcelTicketFolderReaderAdapter();
    }

    // ----------------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe leer correctamente los nombres de carpetas desde un Excel válido")
    void shouldReadFolderNamesSuccessfully() throws Exception {

        InputStream excelStream = createExcel(
                "HEADER",
                "Carpeta A",
                "Carpeta B",
                "Carpeta C"
        );

        List<String> result = adapter.readFolderNames(excelStream);

        assertEquals(3, result.size());
        assertEquals("Carpeta A", result.get(0));
        assertEquals("Carpeta B", result.get(1));
        assertEquals("Carpeta C", result.get(2));
    }

    // ----------------------------------------------------------------------
    // Ignora valores inválidos
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe ignorar filas vacías, nulas o con texto en blanco")
    void shouldIgnoreBlankOrEmptyCells() throws Exception {

        InputStream excelStream = createExcel(
                "HEADER",
                "Carpeta A",
                "   ",
                "",
                null,
                "Carpeta B"
        );

        List<String> result = adapter.readFolderNames(excelStream);

        assertEquals(2, result.size());
        assertEquals("Carpeta A", result.get(0));
        assertEquals("Carpeta B", result.get(1));
    }

    // ----------------------------------------------------------------------
    // Error técnico
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando el archivo Excel es inválido")
    void shouldThrowExceptionWhenExcelIsInvalid() {

        InputStream invalidStream = new ByteArrayInputStream("no-es-excel".getBytes());

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.readFolderNames(invalidStream)
        );

        assertTrue(exception.getMessage().contains("Excel"));
    }

    // ----------------------------------------------------------------------
    // Utils
    // ----------------------------------------------------------------------

    private InputStream createExcel(String... values) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        for (int i = 0; i < values.length; i++) {
            Row row = sheet.createRow(i);
            if (values[i] != null) {
                row.createCell(0).setCellValue(values[i]);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
