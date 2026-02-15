package com.christiancanari.pupe.advance.ticket.service.infrastructure.web.handler;

import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.dto.response.ErrorResponse;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.error.ErrorType;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.web.exception.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    // ------------------------------------------------------------------
    // handleCore
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Debe manejar CoreException y construir la respuesta correcta")
    void shouldHandleCoreException() {

        CoreException coreException = mock(CoreException.class);

        when(coreException.getType()).thenReturn(ErrorType.EXCEL_INVALID);
        when(coreException.getMessage()).thenReturn("Excel inválido");

        ResponseEntity<ErrorResponse> response =
                handler.handleCore(coreException);

        assertNotNull(response);
        assertEquals(
                ErrorType.EXCEL_INVALID.status(),
                response.getStatusCode()
        );

        ErrorResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(
                ErrorType.EXCEL_INVALID.status().value(),
                body.status()
        );
        assertEquals(
                ErrorType.EXCEL_INVALID.status().getReasonPhrase(),
                body.error()
        );
        assertEquals("Excel inválido", body.message());
        assertEquals(ErrorType.EXCEL_INVALID.name(), body.type());
        assertNotNull(body.timestamp());
    }

    // ------------------------------------------------------------------
    // handleUnexpected
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Debe manejar Exception genérica como UNEXPECTED_ERROR")
    void shouldHandleUnexpectedException() {

        Exception ex = new RuntimeException("Boom inesperado");

        ResponseEntity<ErrorResponse> response =
                handler.handleUnexpected(ex);

        assertNotNull(response);
        assertEquals(
                ErrorType.UNEXPECTED_ERROR.status(),
                response.getStatusCode()
        );

        ErrorResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(
                ErrorType.UNEXPECTED_ERROR.status().value(),
                body.status()
        );
        assertEquals(
                ErrorType.UNEXPECTED_ERROR.status().getReasonPhrase(),
                body.error()
        );
        assertEquals("Boom inesperado", body.message());
        assertEquals(ErrorType.UNEXPECTED_ERROR.name(), body.type());
        assertNotNull(body.timestamp());
    }

}
