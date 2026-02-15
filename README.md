# 🧾 PUPE – Advance Ticket Service

Servicio backend desarrollado en **Java + Spring Boot** para el procesamiento automatizado de **tickets PDF almacenados en Google Drive**, a partir de un archivo **Excel de entrada**, generando como resultado un **Excel consolidado** con la información extraída.

---

## 📌 Descripción General

El **Advance Ticket Service** recibe un archivo Excel que contiene una lista de nombres de carpetas.
Para cada carpeta:

1. Busca la carpeta correspondiente en **Google Drive**
2. Localiza la subcarpeta de **Tickets**
3. Filtra archivos PDF válidos según reglas configurables
4. Extrae información relevante desde los PDFs
5. Genera un archivo Excel consolidado como respuesta

El proyecto sigue una **Arquitectura Hexagonal (Ports & Adapters)**, separando claramente **dominio**, **aplicación** e **infraestructura**.

---

## 🏗️ Arquitectura

Arquitectura Hexagonal / Clean Architecture:

```
┌───────────────────────────────┐
│        Web / REST API         │
│   (Controllers, DTOs)         │
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│        Application Layer      │
│   (Use Cases, Services)       │
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│           Domain              │
│   (Ports, Models, Rules)      │
└───────────────┬───────────────┘
                │
┌───────────────▼───────────────┐
│        Infrastructure         │
│ (Google Drive, PDF, Excel)    │
└───────────────────────────────┘
```

### Principios aplicados

- Inversión de dependencias
- Dominio independiente de frameworks
- Infraestructura desacoplada mediante puertos
- Manejo explícito de errores
- Programación funcional con Streams y Optionals
- Código testeable por diseño

---

## 🚀 Tecnologías Utilizadas

- Java 21
- Spring Boot
- Spring Web / Validation
- Springdoc OpenAPI (Swagger)
- Google Drive API
- Apache PDFBox
- Apache POI
- Maven
- Lombok
- SLF4J + Logback
- JUnit 5
- Mockito
- SonarQube

---

## 🧪 Testing

El proyecto incluye **tests unitarios** enfocados en:

- Casos de uso (Application Layer)
- Servicios de dominio
- Policies y clasificadores
- Manejo de excepciones

### Ejecutar tests

    mvn clean test

### Ejecutar con verificación y cobertura

    mvn clean verify

---

## 📊 Análisis de Calidad – SonarQube

El proyecto está integrado con **SonarQube** para:

- Análisis estático de código
- Detección de code smells
- Vulnerabilidades
- Bugs
- Coverage de tests
- Cumplimiento de buenas prácticas

### Ejecutar análisis local

    mvn clean verify sonar:sonar -Dsonar.projectKey=pupe-advance-ticket -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_TOKEN

### Métricas monitoreadas

- Coverage mínima requerida
- Maintainability Rating
- Reliability Rating
- Technical Debt
- Duplicación de código

---

## 📂 Estructura del Proyecto

```
com.christiancanari.pupe.advance.ticket.service
│
├── domain
│   ├── model
│   ├── port
│   ├── service
│   └── classifier
│
├── application
│   ├── service
│   └── usecase
│
├── infrastructure
│   ├── client
│   ├── classifier
│   ├── extractor
│   ├── policy
│   ├── config
│   └── web
│       ├── controller
│       ├── dto
│       ├── exception
│       ├── handler
│       └── filter
│
└── AdvanceTicketApplication
```

---

## 📥 Endpoint Principal

### Procesar Tickets

**POST** `/advances/process-ticket`

**Consumes:**  
`multipart/form-data`

**Produces:**  
`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

**Request:**  
Archivo Excel (`file`) con nombres de carpetas

**Response:**  
Archivo Excel consolidado con los tickets procesados

---

## 📖 Swagger / OpenAPI

Documentación interactiva disponible en:

```
http://localhost:8080/swagger
```

---

## ⚙️ Configuración

Ejemplo `application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB

ticket:
  invoice:
    policy:
      keywords:
        - "pr"
        - "ir"
        - "perurail"
        - "incarail"

google:
  drive:
    application-name: pupe-dev
    credentials:
      location: classpath:credentials-dev.json
    scopes:
      - https://www.googleapis.com/auth/drive.readonly
```

---

## ❗ Manejo de Errores

Excepciones personalizadas:

- `CoreRequestException` → errores de entrada HTTP
- `CoreBusinessException` → reglas de negocio
- `CoreTechnicalException` → fallos técnicos / infraestructura

Respuesta estándar:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El archivo Excel es obligatorio",
  "type": "EXCEL_INVALID",
  "timestamp": "2026-02-06T11:06:44"
}
```

---

## 🪵 Logging y Trazabilidad

- Logging implementado con SLF4J
- Configuración mediante Logback
- Logs estructurados por capas (Controller, Application, Domain, Infrastructure)

---

## 🧪 Ejemplo con curl

```bash
curl --location 'http://localhost:8080/advances/process-ticket' \
  --form 'file=@folders.xlsx'
```

---

## 🔒 Buenas Prácticas

- Separación estricta de capas
- Validaciones declarativas
- No exposición de detalles técnicos al cliente
- Configuración externa por perfiles
- Manejo correcto de recursos
- Alta cohesión / bajo acoplamiento
- Código cubierto por tests
- Quality Gate validado en Sonar

---

## 📈 Mejoras Futuras

- Micrometer + Prometheus
- OpenTelemetry
- Logs estructurados (JSON)
- Paralelización del procesamiento
- Retry / Circuit Breaker
- Cache de carpetas procesadas

---

## 👨‍💻 Autor

**Christian Rodriguez** – Arquitectura Hexagonal · Java Backend · Clean Code · Testing · Code · Quality
