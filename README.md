# PréstamoCTMA - Sistema de Gestión de Préstamos de Equipos

## 1. Visión del Proyecto (Scrum)
*   **Product Goal:** Desarrollar una solución móvil ágil para el Centro de Tecnologías para el Mantenimiento y la Manufactura (CTMA) que digitalice el proceso de préstamo de equipos, eliminando el papeleo y garantizando la trazabilidad.
*   **Sprint Goal (MVP):** Implementar el flujo básico de solicitud, catálogo de equipos y seguimiento de solicitudes del aprendiz con validaciones de negocio integradas.
*   **Definition of Done (DoD):**
    *   Código fuente compilable sin errores.
    *   Pruebas unitarias al 100% de cobertura en lógica de validación.
    *   Análisis estático de código (Lint) sin errores críticos.
    *   Validaciones de negocio RN-01 a RN-09 operativas.
    *   Documentación de calidad y matriz de trazabilidad actualizada.

## 2. Arquitectura y Navegación
La aplicación utiliza una arquitectura **MVVM (Model-View-ViewModel)** con el patrón **Repository**:
*   **UI:** Jetpack Compose para interfaces declarativas y reactivas.
*   **State Management:** StateFlow y UiState inmutable.
*   **Navigation:** Navigation Compose mediante rutas tipadas (`equipoId`, `solicitudId`).
*   **Data:** Repositorio desacoplado para facilitar la migración futura a base de datos local (Room) o remota (Retrofit).

### Mapa de Navegación
1.  **Catálogo (`catalogo`):** Lista de equipos con estados visuales.
2.  **Formulario de Solicitud (`solicitar/{id}`):** Captura de datos con validación en tiempo real.
3.  **Mis Solicitudes (`mis_solicitudes`):** Historial de trámites y gestión de cancelaciones.

## 3. Guía de Ejecución y Compilación
### Requisitos
*   Android Studio Ladybug (o superior).
*   JDK 17.
*   Gradle 9.5.0.

### Pasos
1.  Clonar el repositorio.
2.  Sincronizar Gradle (`Sync Project with Gradle Files`).
3.  Ejecutar el Build: `./gradlew assembleDebug`.
4.  Ejecutar Pruebas: `./gradlew test`.
5.  Desplegar en emulador o dispositivo físico.

## 4. Registro de Uso Responsable de IA
En este proyecto se utilizó IA para:
*   Generación de estructura de clases de datos y enums.
*   Creación de esqueletos de UI en Compose siguiendo estándares de Material 3.
*   Automatización de casos de prueba JUnit basados en reglas de negocio.
*   *Revisión Humana:* Toda la lógica de navegación y la integración del repositorio fue supervisada y ajustada manualmente por el Desarrollador Senior para garantizar la estabilidad.
