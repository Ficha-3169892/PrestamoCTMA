# PréstamoLab CTMA 📱🧪

**PréstamoLab CTMA** es una aplicación móvil nativa para Android desarrollada con Kotlin y Jetpack Compose. Su objetivo es gestionar el catálogo, las solicitudes y las reservas de equipos de laboratorio en el Centro de Tecnología de la Manufactura Avanzada (CTMA), garantizando el cumplimiento estricto de las reglas de negocio del sistema.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose con Material Design 3
- **Navegación:** Jetpack Navigation Compose
- **Arquitectura:** MVVM (Model-View-ViewModel) + Pattern Repository
- **Gestión de Estado:** `StateFlow` y `MutableStateFlow` (Reactive State)

---

## 📐 Estructura del Proyecto

```text
com.ctma.prestamolab/
├── data/
│   └── repository/
│       ├── PrestamoRepository.kt        # Interfaz de datos
│       └── InMemoryPrestamoRepository.kt# Implementación en memoria y lógica de negocio
├── model/
│   ├── Equipo.kt                        # Data class Equipo
│   ├── Estados.kt                       # Enums (CategoriaEquipo, EstadoEquipo, EstadoSolicitud)
│   └── SolicitudPrestamo.kt             # Data class SolicitudPrestamo
├── ui/
│   ├── navigation/
│   │   └── AppNavigation.kt             # Grafo de navegación de la App
│   ├── screens/
│   │   ├── CatalogoScreen.kt            # Catálogo de equipos disponibles
│   │   ├── SolicitarScreen.kt           # Formulario de préstamo
│   │   └── MisSolicitudesScreen.kt      # Historial y cancelación
│   └── viewmodel/
│       ├── PrestamoUiState.kt           # Estado inmutable de la interfaz
│       └── PrestamoViewModel.kt         # Controlador de lógica de presentación
└── util/
    └── Validaciones.kt                  # Reglas de validación aisladas
```

---

## ⚙️ Reglas de Negocio Implementadas

- **RN-01 (Disponibilidad):** Solo se permite iniciar el proceso de solicitud en equipos con estado `DISPONIBLE`.
- **RN-02 (Campo Obligatorio):** El ambiente de destino es un campo obligatorio y no puede estar vacío.
- **RN-03 (Longitud del Propósito):** El propósito del préstamo debe contener entre **10 y 180 caracteres**.
- **RN-04 (Límite de Tiempo):** La duración del préstamo debe configurarse entre **1 y 8 horas**.
- **RN-05 (Control de Envío Duplicado):** Se deshabilita el botón de envío durante el procesamiento para evitar múltiples registros por doble clic.
- **RN-06 (Reserva Automática):** Al confirmar una solicitud exitosa, el estado del equipo cambia automáticamente a `RESERVADO`.
- **RN-07 (Cancelación y Liberación):** Solo se pueden cancelar solicitudes en estado `SOLICITADA`. Al cancelar, la solicitud cambia a `CANCELADA` y el equipo se libera volviendo a estado `DISPONIBLE`.

---

## 🚀 Requisitos de Ejecución

- **Android Studio:** Ladybug (2024.2.1) o superior.
- **JDK:** Java 17 o superior.
- **Android SDK:** `compileSdk 34` / `minSdk 24` (Android 7.0+).

---

## 👤 Autor

- **Nombre:** Stiven Tobon T.
- **Programa:** Análisis y Desarrollo de Software (ADSO)
- **Centro:** Centro de Tecnología de la Manufactura Avanzada (CTMA) - SENA