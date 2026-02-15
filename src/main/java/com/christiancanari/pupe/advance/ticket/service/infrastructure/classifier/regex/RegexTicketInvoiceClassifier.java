package com.christiancanari.pupe.advance.ticket.service.infrastructure.classifier.regex;

import com.christiancanari.pupe.advance.ticket.service.domain.classifier.ClassifiedTicketValues;
import com.christiancanari.pupe.advance.ticket.service.domain.classifier.TicketInvoiceClassifier;
import com.christiancanari.pupe.advance.ticket.service.infrastructure.classifier.config.TicketInvoiceClassifierProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Clasificador de tickets basado en expresiones regulares.
 *
 * <p>
 * Implementa {@link TicketInvoiceClassifier} y utiliza patrones configurables para identificar facturas
 * y comprobantes dentro del texto extraído de un PDF.
 * </p>
 *
 * @author Christian Rodriguez
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class RegexTicketInvoiceClassifier implements TicketInvoiceClassifier {

    private final Pattern invoicePattern;
    private final Pattern receiptPattern;

    /**
     * Constructor que inicializa los patrones de clasificación
     * a partir de la configuración.
     */
    public RegexTicketInvoiceClassifier(TicketInvoiceClassifierProperties properties) {
        this.invoicePattern = Pattern.compile(properties.invoiceRegex());
        this.receiptPattern = Pattern.compile(properties.receiptRegex());
    }

    /**
     * Clasifica el texto recibido identificando facturas
     * y comprobantes mediante expresiones regulares.
     *
     * @param text texto extraído del ticket
     * @return valores clasificados; vacío si el texto es nulo o vacío
     */
    @Override
    public ClassifiedTicketValues classify(String text) {

        return Optional.ofNullable(text)
                .filter(t -> !t.isBlank())
                .map(this::classifyText)
                .orElse(ClassifiedTicketValues.empty());
    }

    /**
     * Ejecuta la clasificación del texto usando los patrones definidos.
     */
    private ClassifiedTicketValues classifyText(String text) {

        Set<String> invoices = extract(invoicePattern, text);
        Set<String> receipts = extract(receiptPattern, text);

        return new ClassifiedTicketValues(
                String.join(",", invoices),
                String.join(",", receipts)
        );
    }

    /**
     * Extrae coincidencias únicas de un patrón dentro del texto.
     */
    private Set<String> extract(Pattern pattern, String text) {
        return pattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }

}
