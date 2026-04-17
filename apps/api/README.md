# plataforma-danos-back

Backend del Cotizador de Seguros de Daños · Spring Boot 3.2 + Java 21 + PostgreSQL 16.

## Arquitectura

Clean Architecture en 4 capas: `domain`, `application`, `infrastructure`, `interfaces`.

## Comandos

```bash
# Compilar
./mvnw clean compile

# Tests con cobertura
./mvnw test jacoco:report

# Arrancar en local
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Construir imagen Docker
docker build -t plataforma-danos-back .
```

## Estructura de paquetes

```
com.sofka.cotizador/
├── CotizadorApplication.java
├── domain/
│   ├── model/       ← Entidades y value objects
│   ├── port/        ← Interfaces de repositorio y servicios
│   └── exception/   ← Excepciones del dominio
├── application/
│   └── usecase/     ← Casos de uso (orquestación)
├── infrastructure/
│   ├── persistence/ ← JPA adapters
│   ├── http/        ← Clientes HTTP al core-stub
│   └── config/      ← Beans de Spring
└── interfaces/
    └── rest/        ← Controllers, DTOs, MapStruct mappers
```
