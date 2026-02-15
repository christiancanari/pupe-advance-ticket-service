package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.controller;

import com.christiancanari.pupe.advance.ticket.service.domain.port.in.ProcessTicketUseCase;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.request.ProcessTicketRequest;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreRequestException;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreTechnicalException;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdvanceTicketControllerTest {

    private MockMvc mockMvc;
    private ProcessTicketUseCase processTicketUseCase;
    private AdvanceTicketController controller;

    @BeforeEach
    void setUp() {

        processTicketUseCase = mock(ProcessTicketUseCase.class);
        controller = new AdvanceTicketController(processTicketUseCase);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------------
    // OK - request válido
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar 200 y el Excel generado cuando el request es válido")
    void shouldReturnExcelWhenRequestIsValid() throws Exception {

        byte[] excelResult = "excel-result".getBytes();

        when(processTicketUseCase.process(any()))
                .thenReturn(excelResult);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "input.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido".getBytes()
        );

        mockMvc.perform(
                        multipart("/advances/process-ticket")
                                .file(file)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Disposition",
                        org.hamcrest.Matchers.containsString("attachment; filename=\"resultado_")
                ))
                .andExpect(content().bytes(excelResult));

        verify(processTicketUseCase).process(any());
    }

    // ---------------------------------------------------------------------
    // Archivo NO enviado (binding multipart falla antes del controller)
    // → Exception genérica → UNEXPECTED_ERROR → 500
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar 500 cuando no se envía archivo (error de binding multipart)")
    void shouldReturnErrorWhenFileIsMissing_MockMvc() throws Exception {

        mockMvc.perform(
                        multipart("/advances/process-ticket")
                )
                .andExpect(status().isInternalServerError());
    }

    // ---------------------------------------------------------------------
    // Error leyendo InputStream → CoreRequestException → EXCEL_INVALID → 400
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar 400 cuando falla la lectura del archivo")
    void shouldReturnBadRequestWhenFileCannotBeRead() throws Exception {

        MockMultipartFile brokenFile =
                new MockMultipartFile(
                        "file",
                        "input.xlsx",
                        MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        "contenido".getBytes()
                ) {
                    @Override
                    public java.io.InputStream getInputStream() throws IOException {
                        throw new IOException("Error leyendo archivo");
                    }
                };

        mockMvc.perform(
                        multipart("/advances/process-ticket")
                                .file(brokenFile)
                )
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------------
    // Error en el UseCase → RuntimeException → UNEXPECTED_ERROR → 500
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe retornar 500 cuando el UseCase lanza una excepción")
    void shouldReturnInternalServerErrorWhenUseCaseFails() throws Exception {

        when(processTicketUseCase.process(any()))
                .thenThrow(new RuntimeException("Boom"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "input.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido".getBytes()
        );

        mockMvc.perform(
                        multipart("/advances/process-ticket")
                                .file(file)
                )
                .andExpect(status().isInternalServerError());
    }

    // ---------------------------------------------------------------------
    // TEST UNITARIO DIRECTO
    // Cubre: file == null
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe lanzar CoreRequestException cuando el archivo es null")
    void shouldThrowCoreRequestExceptionWhenFileIsNull_DirectCall() {

        ProcessTicketRequest request =
                new ProcessTicketRequest(null);

        CoreRequestException ex = assertThrows(
                CoreRequestException.class,
                () -> controller.processTicket(request)
        );

        assertEquals(ErrorType.EXCEL_INVALID, ex.getType());
    }

    // ---------------------------------------------------------------------
    // TEST UNITARIO DIRECTO
    // Cubre: file != null && file.isEmpty()
    // (último branch que JaCoCo marca)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("Debe lanzar CoreRequestException cuando el archivo está vacío (llamada directa)")
    void shouldThrowCoreRequestExceptionWhenFileIsEmpty_DirectCall() {

        MockMultipartFile emptyFile =
                new MockMultipartFile(
                        "file",
                        "input.xlsx",
                        MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new byte[0]
                );

        ProcessTicketRequest request =
                new ProcessTicketRequest(emptyFile);

        CoreRequestException ex = assertThrows(
                CoreRequestException.class,
                () -> controller.processTicket(request)
        );

        assertEquals(ErrorType.EXCEL_INVALID, ex.getType());
    }

    @Test
    @DisplayName("Debe cubrir el constructor CoreTechnicalException sin causa")
    void shouldCreateCoreTechnicalExceptionWithoutCause() {

        CoreTechnicalException exception =
                new CoreTechnicalException(
                        "Error técnico sin causa",
                        ErrorType.DRIVE_ACCESS_ERROR
                );

        assertEquals("Error técnico sin causa", exception.getMessage());
        assertEquals(ErrorType.DRIVE_ACCESS_ERROR, exception.getType());
        assertNull(exception.getCause());
    }

}
