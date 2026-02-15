package com.christiancanari.pupe.advance.ticket.service.infrastructure.file.adapter;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketFolderReaderPort;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Adapter de infraestructura responsable de leer un archivo Excel y extraer los nombres de carpetas desde su contenido.
 *
 * <p>
 * Este adapter implementa el puerto {@link TicketFolderReaderPort} y utiliza
 * la librería Apache POI para procesar archivos Excel en formato XLSX.
 * </p>
 *
 * <p>
 * Convención de lectura:
 * <ul>
 *     <li>Se utiliza la primera hoja del archivo</li>
 *     <li>La primera fila (índice 0) se considera encabezado</li>
 *     <li>Los nombres de carpetas se leen desde la primera columna</li>
 * </ul>
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
@Component
public class ExcelTicketFolderReaderAdapter implements TicketFolderReaderPort {

    /**
     * Lee los nombres de carpetas desde un archivo Excel.
     *
     * <p>
     * Recorre todas las filas de la primera hoja del archivo,
     * ignorando la fila de encabezado y extrayendo el valor textual
     * de la primera columna.
     * </p>
     *
     * <p>
     * Las celdas vacías, nulas o con contenido en blanco son ignoradas.
     * </p>
     *
     * @param inputStream archivo Excel de entrada
     * @return lista de nombres de carpetas válidos encontrados en el archivo
     * @throws CoreTechnicalException si ocurre un error durante la lectura del archivo
     */
    @Override
    public List<String> readFolderNames(InputStream inputStream) {

        log.info("[ExcelFolderReader] Iniciando lectura de nombres de carpetas desde Excel");

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            List<String> folderNames = IntStream
                    .rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .filter(Objects::nonNull)
                    .map(row -> row.getCell(0))
                    .filter(Objects::nonNull)
                    .map(Cell::getStringCellValue)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .toList();

            log.info("[ExcelFolderReader] Lectura completada. Carpetas encontradas: {}", folderNames.size());
            return folderNames;

        } catch (Exception ex) {
            log.error("[ExcelFolderReader] Error técnico leyendo el archivo Excel de carpetas", ex);
            throw new CoreTechnicalException("Error leyendo el archivo Excel de carpetas", ErrorType.EXCEL_INVALID, ex);
        }
    }
}
