# PréstamoLab CTMA 📱🔬

**PréstamoLab CTMA** es una solución móvil integral desarrollada en Android para la gestión eficiente, segura y trazable del préstamo de equipos en los laboratorios del Centro de Tecnología de la Manufactura y el Desarrollo de la Automatización (CTMA).

## 🚀 Propósito del Proyecto
Optimizar el flujo de solicitud y entrega de herramientas técnicas, garantizando que tanto aprendices como instructores cuenten con información en tiempo real sobre la disponibilidad y el estado físico del inventario.

---

## ✨ Funcionalidades Principales

### 🎓 Para el Aprendiz (Módulo de Usuario)
- **Consulta de Catálogo (HU-01, HU-07):** Búsqueda en tiempo real y filtrado por categorías (Electrónica, Redes, Herramientas, Medición, Cómputo, Audiovisual).
- **Ficha Técnica Multimedia (HU-04):** Visualización detallada de cada equipo con fotos reales, marca, número de serie, especificaciones y accesorios.
- **Registro de Solicitud (HU-02):** Formulario dinámico con validaciones de negocio (Ambiente, Propósito y Duración de 1 a 8 horas).
- **Equipos Frecuentes (HU-08):** Sistema de favoritos para un acceso rápido a las herramientas más utilizadas.
- **Gestión de Perfil (HU-06):** Visualización de datos institucionales y actualización de información de contacto.
- **Alertas Inteligentes (HU-14):** Notificaciones automáticas 15 minutos antes del vencimiento y avisos visuales críticos para préstamos vencidos.

### 🛠️ Para el Administrador (Módulo de Gestión)
- **Dashboard Administrativo:** Panel centralizado para el control total del laboratorio.
- **Gestión de Inventario (HU-09, HU-10):** Registro de nuevos equipos con validación de serie única y control de estados operativos (Mantenimiento/Baja).
- **Aprobación de Solicitudes (HU-11):** Interfaz para aprobar o rechazar solicitudes con justificación obligatoria.
- **Control de Devoluciones (HU-12, HU-13):** Registro de retorno de equipos con reporte de novedades y clasificación de daños (Leve, Moderado, Grave).
- **Estadísticas y Reportes (HU-15):** Métricas sobre equipos más solicitados y demanda por categorías.
- **Trazabilidad y Auditoría (HU-16):** Historial cronológico completo de cada equipo, asociando usuarios, fechas y novedades.

---

## 🛠️ Stack Tecnológico
- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Moderno, declarativo y reactivo).
- **Arquitectura:** MVVM (Model-View-ViewModel) con Flujo Unidireccional de Datos (UDF).
- **Gestión de Estado:** `StateFlow` y `MutableStateFlow`.
- **Navegación:** `Navigation Compose` con rutas seguras y manejo de argumentos.
- **Inyección de Dependencias:** Factory Pattern para ViewModels.
- **Multimedia:** [Coil](https://coil-kt.github.io/coil/) para la carga asíncrona de imágenes desde la web.
- **Pruebas:** JUnit 4 y `kotlinx-coroutines-test`.
- **SDK Objetivo:** 37 (Android 15).

---

## 🔐 Seguridad y Acceso
El sistema implementa un acceso restringido mediante **Correo Institucional (HU-05)**:
- **Dominios permitidos:** `@soy.sena.edu.co` y `@sena.edu.co`.
- **Roles:** El sistema detecta automáticamente si el usuario es Aprendiz o Administrador según las credenciales.

---

## 📦 Instalación y Ejecución
1. Clonar el repositorio.
2. Abrir el proyecto en **Android Studio Ladybug (2024.2.1)** o superior.
3. Asegurarse de tener configurado el **JDK 17**.
4. Ejecutar el Gradle Sync.
5. Correr la aplicación en un emulador o dispositivo físico con acceso a internet.

---

## 👨‍🏫 Equipo y Supervisión
- **Instructor:** Wilson Castro Gil
- **Rol Responsable:** Scrum Master
- **Metodología:** Ágil (Scrum) - Sprint Final verificado.

---
*Este proyecto cumple satisfactoriamente con el Definition of Done (DoD) y los criterios de aceptación de las 16 Historias de Usuario oficiales.*
