# Definition of Done (DoD) - PréstamoLab CTMA

Este documento define los criterios de calidad que debe cumplir el incremento para ser considerado "Hecho" (Done) según la metodología Scrum del proyecto.

## 1. Criterios de Producto y Funcionalidad
- [x] **Compilación:** El proyecto compila correctamente utilizando SDK 37 y JDK 17.
- [x] **Historias de Usuario:** Los criterios de aceptación de HU-01, HU-02 y HU-03 están implementados al 100%.
- [x] **Reglas de Negocio:** Las 9 reglas de negocio (**RN-01 a RN-09**) están codificadas y verificadas.
- [x] **Navegación:** El flujo entre pantallas es funcional y maneja IDs inexistentes sin errores (**RN-08**).

## 2. Calidad Técnica y Arquitectura
- [x] **Arquitectura:** Se respeta el patrón **MVVM** con separación clara entre UI (Compose), ViewModel (StateFlow) y Data (Repository).
- [x] **Estado:** El ViewModel expone el estado mediante `StateFlow` de solo lectura, garantizando el flujo unidireccional de datos (UDF).
- [x] **Código Limpio:** Se han eliminado advertencias del linter y dependencias redundantes en Gradle.
- [x] **Accesibilidad:** La disponibilidad de equipos se comunica mediante texto e iconografía, no dependiendo exclusivamente del color.

## 3. Pruebas y Evidencia
- [x] **Unit Tests:** Ejecución exitosa de `testDebugUnitTest` con cobertura en las validaciones del ViewModel.
- [x] **Trazabilidad:** Existe una matriz que vincula HU -> Requisito -> Riesgo -> Caso de Prueba.
- [x] **Caja Negra:** Se ejecutaron casos representativos de valores límite, partición y transición de estados.
- [x] **Integración Continua:** Configuración de GitHub Actions para verificar builds y pruebas automáticamente.

## 4. Documentación y Entrega
- [x] **README:** Documento actualizado con el rol de Scrum Master y supervisión del instructor Wilson Castro Gil.
- [x] **Git:** Repositorio organizado con mensajes de commit claros y vinculados a Issues.
- [x] **Sustentación:** El incremento es demostrable y el equipo puede justificar cada decisión técnica.

---
**Rol responsable:** Scrum Master  
**Instructor:** Wilson Castro Gil  
**Estado final:** VERIFICADO  
