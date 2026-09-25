# PréstamoLab CTMA 📱🔬

![Android CI](https://github.com/Ficha-3169892/PrestamoCTMA/actions/workflows/android.yml/badge.svg)

**PréstamoLab CTMA** es una solución móvil integral desarrollada en Android para la gestión eficiente, segura y trazable del préstamo de equipos y herramientas en los laboratorios del Centro de Tecnología de la Manufactura y el Desarrollo de la Automatización (CTMA).

---

## 🚀 Propósito del Proyecto
Optimizar el flujo de consulta, solicitud, entrega, seguimiento y devolución de herramientas técnicas, garantizando información en tiempo real sobre disponibilidad, estados operativos e historial de trazabilidad.

---

## 📋 Mapeo de Historias de Usuario (GitHub Issues) y Distribución del Equipo

El desarrollo del proyecto está organizado en 16 Historias de Usuario oficiales registradas como Issues en el repositorio de GitHub:

| Issue | Historia de Usuario (Título Oficial) | Integrante Responsable | Estado |
| :---: | :--- | :--- | :---: |
| **#1** | **HU-01: Consulta de Catálogo de Equipos** | Steven123si | Completed |
| **#2** | **HU-02: Registrar Solicitud de Préstamo** | Steven123si | Completed |
| **#3** | **HU-03: Gestión y Cancelación de Solicitudes** | Steven123si | Completed |
| **#4** | **HU-04: Consultar Detalle y Ficha Técnica de un Equipo** | Steven123si | Completed |
| **#6** | **HU-05: Inicio de Sesión con Correo Institucional** | ThomasIsaza04 | Completed |
| **#7** | **HU-06: Gestión de Perfil de Usuario** | ThomasIsaza04 | Completed |
| **#8** | **HU-07: Búsqueda y Filtro Avanzado de Equipos** | ThomasIsaza04 | Completed |
| **#9** | **HU-08: Marcado de Equipos Frecuentes** | ThomasIsaza04 | Completed |
| **#10** | **HU-09: Registro de Nuevos Equipos en Inventario** | liney042-alt | Completed |
| **#11** | **HU-10: Actualización del Estado de Equipos** | liney042-alt | Completed |
| **#12** | **HU-11: Aprobación y Rechazo de Solicitudes** | liney042-alt | Completed |
| **#13** | **HU-12: Confirmación y Recepción de Devoluciones** | liney042-alt | Completed |
| **#14** | **HU-13: Registro de Novedades y Daños** | Durman-V | Completed |
| **#15** | **HU-14: Notificaciones y Alertas de Vencimiento** | Durman-V | Completed |
| **#16** | **HU-15: Generación de Reportes de Uso del Laboratorio** | Durman-V | Completed |
| **#17** | **HU-16: Historial de Trazabilidad y Auditoría de Equipos** | Durman-V | Completed |

---

## ✨ Funcionalidades Principales

### 🎓 Para Aprendices e Instructores (Módulo de Usuario)
- **Consulta de Catálogo (HU-01, HU-07 - Issue #1, #8):** Búsqueda en tiempo real y filtrado por categorías (Electrónica, Redes, Herramientas, Medición, Cómputo, Audiovisual).
- **Ficha Técnica Multimedia (HU-04 - Issue #4):** Visualización detallada de cada equipo con fotos reales, marca, número de serie, especificaciones y accesorios.
- **Registro de Solicitud (HU-02 - Issue #2):** Formulario dinámico con validaciones de negocio (Ambiente, Propósito y Duración de 1 a 8 horas).
- **Equipos Frecuentes / Favoritos (HU-08 - Issue #9):** Sistema de favoritos para un acceso rápido a las herramientas más utilizadas.
- **Gestión de Perfil (HU-06 - Issue #7):** Visualización de datos institucionales y actualización de información de contacto.
- **Alertas Inteligentes (HU-14 - Issue #15):** Notificaciones automáticas 15 minutos antes del vencimiento y avisos visuales críticos para préstamos vencidos.

### 🛠️ Para el Administrador (Módulo de Gestión)
- **Dashboard Administrativo:** Panel centralizado para el control total del laboratorio.
- **Gestión de Inventario (HU-09, HU-10 - Issue #10, #11):** Registro de nuevos equipos con serie única y control de estados operativos (Mantenimiento / Baja).
- **Aprobación de Solicitudes (HU-11 - Issue #12):** Interfaz para aprobar o rechazar solicitudes con justificación obligatoria.
- **Control de Devoluciones y Novedades (HU-12, HU-13 - Issue #13, #14):** Registro de retorno de equipos con reporte de novedades y clasificación de daños (Leve, Moderado, Grave).
- **Estadísticas y Reportes (HU-15 - Issue #16):** Métricas sobre equipos más solicitados y demanda por categorías.
- **Trazabilidad y Auditoría (HU-16 - Issue #17):** Historial cronológico completo de cada equipo, asociando usuarios, fechas y novedades.

---

## 🛠️ Stack Tecnológico
- **Lenguaje:** [Kotlin 2.0](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarativo y reactivo)
- **Arquitectura:** MVVM (Model-View-ViewModel) con Flujo Unidireccional de Datos (UDF)
- **Gestión de Estado:** `StateFlow` y `MutableStateFlow`
- **Navegación:** `Navigation Compose` con rutas de tipo seguro
- **Multimedia:** [Coil](https://coil-kt.github.io/coil/) para imágenes asíncronas
- **Pruebas Automatizadas:** JUnit 4 y `kotlinx-coroutines-test`
- **Integración Continua:** GitHub Actions (`android.yml` con Gradle Build, Test & Lint)
- **SDK Objetivo:** 37 (Android 15) / **Mínimo:** 24 (Android 7.0)

---

## 🔐 Seguridad y Acceso
- **Autenticación Institucional (HU-05 - Issue #6):** Dominios permitidos `@soy.sena.edu.co` y `@sena.edu.co`.
- **Control de Roles:** Detección automática de permisos de Aprendiz o Administrador.

---

## 📊 Cuestionario de Sustentación del Proyecto Integrador (Semana 10)

### 1. Trazabilidad de una Historia de Usuario
* **Historia:** `HU-05: Inicio de Sesión con Correo Institucional` (#6).
* **Criterio de Aceptación:** Solo permite acceso si el correo termina en `@sena.edu.co` o `@soy.sena.edu.co`.
* **Código:** Método `login(correo)` en [`PrestamoViewModel.kt`](file:///C:/Users/Thomas/StudioProjects/PrestamoCTMA/app/src/main/java/com/example/prestamolabctma/viewmodel/PrestamoViewModel.kt) con expresión regular `^[A-Za-z0-9._%+-]+@(soy\.)?sena\.edu\.co$`.
* **Prueba:** Test unitario `` `HU-05 Inicio de Sesion con Correo Institucional`() `` en [`PrestamoViewModelTest.kt`](file:///C:/Users/Thomas/StudioProjects/PrestamoCTMA/app/src/test/java/com/example/prestamolabctma/viewmodel/PrestamoViewModelTest.kt).

### 2. Fuente Local Canónica (Single Source of Truth - SSOT)
Room / Repositorio actúa como la única fuente de verdad. La capa de interfaz (Compose) observa emisiones de `Flow`/`StateFlow` desde el ViewModel y nunca modifica directamente la base de datos o el repositorio en memoria.

### 3. Diferencia entre Flow y StateFlow en ViewModel
- **Flow:** Flujo frío no suspendible de datos asíncronos que solo emite cuando hay un colector activo.
- **StateFlow:** Flujo caliente que siempre almacena y expone el estado más reciente de la UI (`UiState`), garantizando la preservación del estado ante cambios de configuración (como la rotación de pantalla).

### 4. Representación de Errores de Red en UiState
Se maneja una jerarquía o campo desacoplado en `PrestamoUiState` (ej. `errorFormulario` o `mensaje`) sin exponer excepciones crudas de red (ej. `IOException` o `401 Unauthorized`), mostrando en su lugar mensajes claros y accionables para el usuario (ej. *"Sin conexión a internet. Mostrando datos locales"*).

### 5. Patrón AAA en Pruebas Unitarias
- **Arrange (Preparar):** Instanciar el Repositorio y el ViewModel.
- **Act (Ejecutar):** Invocar `viewModel.registrarSolicitud(equipoId, ambiente, proposito, duracion)`.
- **Assert (Verificar):** `assertEquals("El ambiente o destino es obligatorio", viewModel.uiState.value.errorFormulario)`.

### 6. Aplicación de TDD (Test-Driven Development)
Se aplicó el ciclo **Red-Green-Refactor**:
1. **Red:** Escribir la prueba para la regla de negocio RN-03 (propósito entre 10 y 180 caracteres) antes de la validación.
2. **Green:** Implementar la condición `if (proposito.length !in 10..180)` en el ViewModel hasta pasar el test.
3. **Refactor:** Limpiar y estructurar el código manteniendo los tests en verde.

### 7. Confirmación y Regresión de Defectos
Al corregir el error de doble guardado (BUG-03), primero se ejecutó la prueba de confirmación para validar que la doble pulsación fuera rechazada, y luego se ejecutó la suite completa de 17 tests unitarios en [`PrestamoViewModelTest.kt`](file:///C:/Users/Thomas/StudioProjects/PrestamoCTMA/app/src/test/java/com/example/prestamolabctma/viewmodel/PrestamoViewModelTest.kt) para asegurar que la solicitud normal de préstamo siguiera funcionando sin regresiones.

### 8. Principio de Mínimo Privilegio en Permisos
Se utiliza el **Photo Picker** nativo (`PickVisualMedia`) para la selección de imágenes de perfil o evidencias, evitando solicitar permisos globales e invasivos de almacenamiento como `READ_MEDIA_IMAGES`.

### 9. Quality Gates en el Pipeline de CI/CD
El flujo de GitHub Actions (`android.yml`) exige que todo Pull Request cumpla obligatoriamente con:
1. Compilación Gradle limpia (`./gradlew assembleDebug`).
2. Ejecución exitosa de la suite completa de pruebas unitarias (`./gradlew testDebugUnitTest`).
3. Verificación de reglas de calidad con Android Lint (`./gradlew lintDebug`).

### 10. Riesgos Residuales e Identificados
- **Riesgo:** Pérdida de conectividad prolongada durante el envío de una devolución.
- **Mitigación:** La aplicación conserva la operación registrada en el Repositorio local de forma idempotente hasta restablecer la comunicación con el servidor.

---

## 📦 Instalación y Ejecución
1. Clonar el repositorio: `git clone https://github.com/Ficha-3169892/PrestamoCTMA.git`
2. Abrir en **Android Studio Ladybug (2024.2.1)** o superior.
3. Configurar **JDK 17** y ejecutar Gradle Sync.
4. Para ejecutar las pruebas unitarias:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 👨‍🏫 Equipo de Desarrollo y Supervisión
- **Instructor:** Wilson Castro Gil
- **Rol Responsable:** Scrum Master
- **Integrantes del Equipo:**
  - `ThomasIsaza04` (Thomas Isaza Chalarca) - HU-05, HU-06, HU-07, HU-08
  - `Steven123si` - HU-01, HU-02, HU-03, HU-04
  - `liney042-alt` - HU-09, HU-10, HU-11, HU-12
  - `Durman-V` - HU-13, HU-14, HU-15, HU-16

---
*Este proyecto cumple al 100% con la Definition of Done (DoD), los Criterios de Aceptación oficiales de GitHub Issues (#1 al #17) y los lineamientos de la Guía de Aprendizaje de la Semana 10.*
