# ADR-001: Clean Architecture para el Backend

**Estado**: Aceptado  
**Fecha**: 2026-04-17  
**Autor**: Hans · Sofka Technologies · AI Center

---

## Contexto

El cotizador de seguros de daños tiene reglas de negocio complejas: cálculo de primas con múltiples factores (zona de riesgo, tipo de construcción, coberturas opcionales), estados de folio con transiciones controladas, e integración con un sistema core externo (stub). Se necesita una arquitectura que permita:

1. Probar la lógica de negocio de forma aislada (sin levantar Spring ni Postgres)
2. Cambiar el proveedor de persistencia sin tocar el dominio
3. Agregar nuevas coberturas o factores sin afectar las capas de infraestructura
4. Facilitar la comprensión del código a nuevos miembros del equipo

## Decisión

Se adopta **Clean Architecture** en 4 capas con la siguiente asignación de responsabilidades:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Domain | `domain/model`, `domain/port`, `domain/exception` | Entidades, reglas de negocio, contratos (interfaces) |
| Application | `application/usecase` | Orquestación de casos de uso, coordinación de puertos |
| Infrastructure | `infrastructure/persistence`, `infrastructure/http`, `infrastructure/config` | JPA, clientes HTTP, beans de Spring |
| Interfaces | `interfaces/rest` | Controllers REST, DTOs, mappers MapStruct |

**Regla de dependencia**: el código de una capa solo puede importar código de las capas internas, nunca de las externas.

## Alternativas consideradas

### Opción A: Arquitectura en capas tradicional (MVC/DAO)
- **Pros**: Familiar, menos archivos, menos interfaces
- **Contras**: La lógica de negocio tiende a migrar a los services de Spring, haciendo las pruebas unitarias dependientes del contexto de Spring. Difícil escalar sin acoplamiento.

### Opción B: Arquitectura Hexagonal (Ports & Adapters)
- **Pros**: Muy similar a Clean Architecture, igualmente válida
- **Contras**: La nomenclatura "puerto de entrada / puerto de salida" agrega confusión para desarrolladores acostumbrados a MVC. Clean Architecture con 4 capas nominadas es más pedagógica para un reto de evaluación.

### Opción C: Clean Architecture (elegida)
- **Pros**: Trazabilidad directa entre capas y responsabilidades, dominio completamente testeable sin Spring, cambio de ORM posible sin tocar negocio
- **Contras**: Más archivos y interfaces que MVC. Para un reto de 2 semanas, este overhead es aceptable dado que es un criterio de evaluación explícito.

## Consecuencias

**Positivas**:
- Pruebas unitarias del dominio sin `@SpringBootTest` (más rápidas)
- Posible migrar de PostgreSQL a otro motor sin cambiar `domain/` ni `application/`
- Lógica de negocio concentrada y documentada en entidades de dominio

**Negativas**:
- Mayor número de clases e interfaces (aprox. 30% más archivos que MVC)
- MapStruct agrega un paso de compilación adicional
- Los desarrolladores nuevos necesitan entender la regla de dependencias antes de contribuir

## Referencias

- Robert C. Martin, "Clean Architecture" (2017)
- `specs/guidelines/architecture-standards.md`
- `specs/guidelines/dev-guidelines.md`
