package com.christiancanari.pupe.advance.ticket.service.infrastructure.file.adapter;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketFileWriterPort;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Adapter de infraestructura responsable de generar un archivo Excel
 * con el resultado del procesamiento de tickets.
 *
 * <p>
 * Implementa el puerto {@link TicketFileWriterPort} y utiliza Apache POI
 * para crear un archivo Excel en formato XLSX.
 * </p>
 *
 * <p>
 * Estructura del Excel generado:
 * <ul>
 *     <li>Hoja: RESULTADO</li>
 *     <li>Columnas:
 *         <ul>
 *             <li>file</li>
 *             <li>filePR</li>
 *             <li>facturas</li>
 *             <li>comprobantes</li>
 *         </ul>
 *     </li>
 * </ul>
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class ExcelTicketWriterAdapter implements TicketFileWriterPort {

    /**
     * Genera un archivo Excel con la información de los tickets procesados.
     *
     * <p>
     * Cada elemento de la lista {@link TicketInvoice} se representa
     * como una fila dentro del archivo Excel.
     * </p>
     *
     * @param invoices lista de tickets procesados
     * @return arreglo de bytes correspondiente al archivo Excel generado
     * @throws CoreTechnicalException si ocurre un error durante la generación del archivo
     */
    @Override
    public byte[] export(List<TicketInvoice> invoices) {

        log.info("📊 [ExcelWriter] Iniciando generación de archivo Excel. Registros: {}",
                invoices != null ? invoices.size() : 0);

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("RESULTADO");
            createHeader(sheet);

            IntStream.range(0, invoices.size())
                    .forEach(index ->
                            writeRow(sheet, index + 1, invoices.get(index))
                    );

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                workbook.write(bos);
                log.info("[ExcelWriter] Archivo Excel generado correctamente");
                return bos.toByteArray();
            }

        } catch (Exception ex) {
            log.error("[ExcelWriter] Error generando el archivo Excel de resultados", ex);
            throw new CoreTechnicalException("Error generando el archivo Excel de resultados",
                    ErrorType.FILE_GENERATION_ERROR, ex);
        }
    }

    /**
     * Escribe una fila del Excel con los datos de un ticket.
     *
     * @param sheet   hoja de Excel donde se escribirá la fila
     * @param rowIndex índice de la fila
     * @param invoice información del ticket
     */
    private void writeRow(Sheet sheet, int rowIndex, TicketInvoice invoice) {

        Row row = sheet.createRow(rowIndex);

        row.createCell(0).setCellValue(invoice.sourceFileName());
        row.createCell(1).setCellValue(invoice.processedFileName());
        row.createCell(2).setCellValue(invoice.facturaContent());
        row.createCell(3).setCellValue(invoice.comprobanteContent());
    }

    /**
     * Crea la fila de encabezado del archivo Excel.
     *
     * @param sheet hoja de Excel donde se escribirá el encabezado
     */
    private void createHeader(Sheet sheet) {

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("file");
        header.createCell(1).setCellValue("filePR");
        header.createCell(2).setCellValue("facturas");
        header.createCell(3).setCellValue("comprobantes");
    }

}
