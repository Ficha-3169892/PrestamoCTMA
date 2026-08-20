# PréstamoLab CTMA - Incremento Funcional

## 1. Información del Proyecto
**Institución:** SENA  
**Instructor a cargo:** Wilson Castro Gil  
**Rol en el equipo:** Scrum Master  
**Estado del Incremento:** Rama de trabajo personal - Finalizado / Verificado  

---

## 2. Descripción del Producto
**PréstamoLab CTMA** es un prototipo educativo desarrollado en Android diseñado para optimizar la trazabilidad, consulta y registro de préstamos de recursos de formación (equipos y herramientas). El objetivo es reemplazar procesos manuales por una experiencia móvil ágil, accesible y coherente.

### Product Goal
Mejorar la trazabilidad y consulta de préstamos de recursos de formación mediante una experiencia móvil.

---

## 3. Arquitectura y Tecnologías
El proyecto se ha construido siguiendo las mejores prácticas de la arquitectura moderna de Android:

- **Lenguaje:** Kotlin (JVM Target 11 / SDK 37).
- **Interfaz:** Jetpack Compose con Material Design 3.
- **Arquitectura:** MVVM (Model-View-ViewModel).
- **Gestión de Estado:** UiState con StateFlow observable.
- **Capa de Datos:** Patrón Repository con implementación `InMemoryRepository` compartida para simular persistencia durante el ciclo de vida de la aplicación.
- **Navegación:** Navigation Compose utilizando paso de identificadores (`equipoId`, `solicitudId`).

---

## 4. Alcance Funcional (Sprint Goal)
El incremento actual cumple con el objetivo de permitir que un usuario consulte un equipo disponible y registre una solicitud válida, manteniendo la consistencia de datos.

### Funcionalidades Implementadas:
1. **Catálogo de Equipos:** Visualización de nombre, categoría y disponibilidad (accesible mediante texto e iconos).
2. **Detalle del Equipo:** Consulta de información técnica y validación de estado (**RN-01**).
3. **Registro de Solicitud:** Formulario dinámico con captura de ambiente, propósito y duración.
4. **Validaciones Automáticas:** Implementación de reglas de negocio en tiempo real.
5. **Mis Solicitudes:** Seguimiento del historial de préstamos y capacidad de cancelación (**RN-07**).
6. **Manejo de Errores:** Control de identificadores inexistentes para evitar cierres abruptos (**RN-08**).

---

## 5. Reglas de Negocio (RN)
Se han implementado y verificado las siguientes reglas:
- **RN-01:** Solo equipos en estado `DISPONIBLE` pueden ser solicitados.
- **RN-02:** El campo Ambiente/Destino es obligatorio.
- **RN-03:** El Propósito debe tener entre 10 y 180 caracteres.
- **RN-04:** La duración estimada debe estar en el rango de 1 a 8 horas.
- **RN-05:** Prevención de duplicados mediante control de estado en el ViewModel.
- **RN-06:** Reserva automática: al solicitar, el equipo pasa de `DISPONIBLE` a `RESERVADO`.
- **RN-09:** Uso exclusivo de datos sintéticos reproducibles.

---

## 6. Gestión de Calidad y Pruebas
Como **Scrum Master**, he velado por el cumplimiento de la **Definition of Done (DoD)** y la trazabilidad del proceso:

- **Matriz de Trazabilidad:** Vinculación directa entre Historias de Usuario, Riesgos y Casos de Prueba.
- **Suite de Pruebas:** Diseño y ejecución de 16 casos de prueba utilizando técnicas de **Caja Negra** (Partición de Equivalencia, Valores Límite y Transición de Estados).
- **Pruebas Unitarias:** Verificación automatizada de las reglas de validación en el `PrestamoViewModel` (5/5 PASS).
- **Accesibilidad:** Cumplimiento de estándares de contraste y comunicación multi-modal (no solo color).

---

## 7. Estructura de Paquetes
```text
com.example.prestamolabctma/
├── model/         # Entidades del dominio y Enums de estado.
├── data/          # Repositorios (Interfaz e Implementación InMemory).
├── viewmodel/     # PrestamoViewModel y PrestamoUiState.
├── ui/            # Pantallas Compose organizadas por funcionalidad.
└── navigation/    # Configuración de rutas y grafos de navegación.
```

---

## 8. Evidencias de Calidad
Los informes detallados se encuentran en la carpeta de artefactos del proyecto:
- [Matriz de Trazabilidad](file:///C:/Users/Sena/AppData/Local/Google/AndroidStudio2026.1.3/projects/prestamolabctma.7c382d98/.artifacts/f202e8d3-70e2-4650-9ee5-32dc749b8535/trazabilidad.artifact.md)
- [Suite de Pruebas](file:///C:/Users/Sena/AppData/Local/Google/AndroidStudio2026.1.3/projects/prestamolabctma.7c382d98/.artifacts/f202e8d3-70e2-4650-9ee5-32dc749b8535/suite_pruebas.artifact.md)
- [Informe Ejecutivo de Calidad](file:///C:/Users/Sena/AppData/Local/Google/AndroidStudio2026.1.3/projects/prestamolabctma.7c382d98/.artifacts/f202e8d3-70e2-4650-9ee5-32dc749b8535/informe_calidad.artifact.md)

---
*Este proyecto es un incremento funcional entregado como parte de la formación profesional integral del SENA.*
