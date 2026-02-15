package com.christiancanari.pupe.advance.ticket.service.infrastructure.file.adapter;

import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelTicketWriterAdapterTest {

    private ExcelTicketWriterAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ExcelTicketWriterAdapter();
    }

    // ----------------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe generar un archivo Excel válido con los tickets procesados")
    void shouldGenerateExcelSuccessfully() throws Exception {

        List<TicketInvoice> invoices = List.of(
                new TicketInvoice(
                        "origen-1.pdf",
                        "procesado-1.pdf",
                        "FAC-001",
                        "COMP-001"
                ),
                new TicketInvoice(
                        "origen-2.pdf",
                        "procesado-2.pdf",
                        "FAC-002",
                        "COMP-002"
                )
        );

        byte[] result = adapter.export(invoices);

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Validamos el contenido del Excel generado
        try (InputStream is = new ByteArrayInputStream(result);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("RESULTADO");
            assertNotNull(sheet);

            // Header
            Row header = sheet.getRow(0);
            assertEquals("file", header.getCell(0).getStringCellValue());
            assertEquals("filePR", header.getCell(1).getStringCellValue());
            assertEquals("facturas", header.getCell(2).getStringCellValue());
            assertEquals("comprobantes", header.getCell(3).getStringCellValue());

            // Primera fila de datos
            Row row1 = sheet.getRow(1);
            assertEquals("origen-1.pdf", row1.getCell(0).getStringCellValue());
            assertEquals("procesado-1.pdf", row1.getCell(1).getStringCellValue());
            assertEquals("FAC-001", row1.getCell(2).getStringCellValue());
            assertEquals("COMP-001", row1.getCell(3).getStringCellValue());

            // Segunda fila de datos
            Row row2 = sheet.getRow(2);
            assertEquals("origen-2.pdf", row2.getCell(0).getStringCellValue());
            assertEquals("procesado-2.pdf", row2.getCell(1).getStringCellValue());
            assertEquals("FAC-002", row2.getCell(2).getStringCellValue());
            assertEquals("COMP-002", row2.getCell(3).getStringCellValue());
        }
    }

    // ----------------------------------------------------------------------
    // Edge cases
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe generar un Excel solo con encabezado cuando la lista está vacía")
    void shouldGenerateExcelWithOnlyHeaderWhenInvoicesEmpty() throws Exception {

        byte[] result = adapter.export(List.of());

        assertNotNull(result);

        try (InputStream is = new ByteArrayInputStream(result);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("RESULTADO");
            assertNotNull(sheet);

            // Solo header
            assertEquals(0, sheet.getFirstRowNum());
            assertEquals(0, sheet.getLastRowNum());

            Row header = sheet.getRow(0);
            assertEquals("file", header.getCell(0).getStringCellValue());
            assertEquals("filePR", header.getCell(1).getStringCellValue());
            assertEquals("facturas", header.getCell(2).getStringCellValue());
            assertEquals("comprobantes", header.getCell(3).getStringCellValue());
        }
    }

    // ----------------------------------------------------------------------
    // Error técnico
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException cuando ocurre un error al generar el Excel")
    void shouldThrowExceptionWhenExcelGenerationFails() {

        // Forzamos un error pasando una lista que provocará NPE al iterar
        List<TicketInvoice> invalidList = null;

        CoreTechnicalException exception = assertThrows(
                CoreTechnicalException.class,
                () -> adapter.export(invalidList)
        );

        assertTrue(exception.getMessage().contains("Excel"));
    }
}
