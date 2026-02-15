package com.christiancanari.pupe.advance.ticket.service.application.usecase;

import com.christiancanari.pupe.advance.ticket.service.application.service.FolderTicketProcessor;
import com.christiancanari.pupe.advance.ticket.service.domain.model.TicketInvoice;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketFileWriterPort;
import com.christiancanari.pupe.advance.ticket.service.domain.port.out.TicketFolderReaderPort;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreBusinessException;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessTicketUseCaseImplTest {

    private TicketFolderReaderPort folderReaderPort;
    private TicketFileWriterPort fileWriterPort;
    private FolderTicketProcessor folderTicketProcessor;

    private ProcessTicketUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        folderReaderPort = mock(TicketFolderReaderPort.class);
        fileWriterPort = mock(TicketFileWriterPort.class);
        folderTicketProcessor = mock(FolderTicketProcessor.class);

        useCase = new ProcessTicketUseCaseImpl(
                folderReaderPort,
                fileWriterPort,
                folderTicketProcessor
        );
    }

    @Test
    @DisplayName("Debe procesar correctamente cuando existen carpetas")
    void shouldProcessTicketsSuccessfully() {

        InputStream excelInput = new ByteArrayInputStream("excel".getBytes());

        when(folderReaderPort.readFolderNames(any()))
                .thenReturn(List.of("CARPETA_1", "CARPETA_2"));

        TicketInvoice invoice1 = mock(TicketInvoice.class);
        TicketInvoice invoice2 = mock(TicketInvoice.class);

        when(folderTicketProcessor.process("CARPETA_1"))
                .thenReturn(List.of(invoice1));
        when(folderTicketProcessor.process("CARPETA_2"))
                .thenReturn(List.of(invoice2));

        byte[] output = "resultado".getBytes();
        when(fileWriterPort.export(List.of(invoice1, invoice2)))
                .thenReturn(output);

        byte[] result = useCase.process(excelInput);

        assertNotNull(result);
        assertArrayEquals(output, result);

        verify(folderReaderPort).readFolderNames(any());
        verify(folderTicketProcessor).process("CARPETA_1");
        verify(folderTicketProcessor).process("CARPETA_2");
        verify(fileWriterPort).export(List.of(invoice1, invoice2));
    }

    @Test
    @DisplayName("Debe lanzar CoreBusinessException si el Excel no contiene carpetas")
    void shouldThrowBusinessExceptionWhenNoFoldersFound() {

        InputStream excelInput = new ByteArrayInputStream("excel".getBytes());

        when(folderReaderPort.readFolderNames(any()))
                .thenReturn(List.of());

        assertThrows(CoreBusinessException.class,
                () -> useCase.process(excelInput));

        verify(fileWriterPort, never()).export(any());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException si ocurre un error leyendo el Excel")
    void shouldThrowTechnicalExceptionWhenExcelIsInvalid() {

        InputStream excelInput = new ByteArrayInputStream("excel".getBytes());

        when(folderReaderPort.readFolderNames(any()))
                .thenThrow(new RuntimeException("Excel corrupto"));

        assertThrows(CoreTechnicalException.class,
                () -> useCase.process(excelInput));

        verify(folderTicketProcessor, never()).process(any());
    }

    @Test
    @DisplayName("Debe lanzar CoreTechnicalException si ocurre un error generando el archivo de salida")
    void shouldThrowTechnicalExceptionWhenFileGenerationFails() {

        InputStream excelInput = new ByteArrayInputStream("excel".getBytes());

        when(folderReaderPort.readFolderNames(any()))
                .thenReturn(List.of("CARPETA_1"));

        when(folderTicketProcessor.process("CARPETA_1"))
                .thenReturn(List.of(mock(TicketInvoice.class)));

        when(fileWriterPort.export(any()))
                .thenThrow(new RuntimeException("Error escribiendo Excel"));

        assertThrows(CoreTechnicalException.class,
                () -> useCase.process(excelInput));
    }

}
