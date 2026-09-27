# Documentación de Calidad y QA - PréstamoCTMA

## 1. Matriz de Trazabilidad (Requerimientos vs Casos de Prueba)

| ID REQ | Descripción del Requerimiento | ID Caso Prueba | Resultado (PASS/FAIL) |
|--------|--------------------------------|----------------|-----------------------|
| RN-01  | Propósito: 10-180 caracteres   | TC-01, TC-02, TC-03 | PASS |
| RN-02  | Duración: 1-8 horas            | TC-04, TC-05, TC-06 | PASS |
| RN-03  | Ambiente/Destino Obligatorio   | TC-07, TC-08        | PASS |
| RN-04  | Equipo Disponible              | TC-09, TC-10        | PASS |
| RN-05  | Prevención de Duplicados       | TC-11 (Lógica VM)   | PASS |
| RN-06  | Transición SOLICITADA -> CANCELADA | TC-12, TC-13    | PASS |

## 2. Bitácora de Ejecución de Pruebas (Ciclo 01)

*   **Fecha:** 26 Agosto 2026
*   **Ambiente:** Android Emulator API 35
*   **Versión:** 1.0.0-MVP

| ID | Resumen | Estado | Observaciones |
|----|---------|--------|---------------|
| TC-01 | Propósito < 10 chars | PASS | Bloquea el botón "Guardar". |
| TC-04 | Duración < 1 hora | PASS | Muestra error de validación. |
| TC-09 | Seleccionar equipo disponible | PASS | Navega al formulario. |
| TC-10 | Seleccionar equipo prestado | PASS | Botón deshabilitado en UI. |
| BUG-03| Error visual en badge | FAIL | Corregido en commit actual. |

## 3. Reporte de Defecto (Ejemplo BUG-03)

*   **ID:** BUG-03
*   **Título:** Color de Badge incorrecto en estado RESERVADO.
*   **Severidad:** Menor
*   **Prioridad:** Media
*   **Pasos para reproducir:**
    1. Abrir Catálogo.
    2. Identificar el equipo "iPad Air".
    3. Observar color del badge.
*   **Resultado esperado:** Color Naranja (Amber).
*   **Resultado obtenido:** Color Azul.
*   **Estado:** CORREGIDO.

## 4. Estrategia de Pruebas

### Pruebas de Confirmación
Se ejecutan inmediatamente después de cada corrección de bug (como BUG-03) para verificar que el defecto específico ha sido eliminado.

### Pruebas de Regresión
Integradas en el workflow de **GitHub Actions**. Cada vez que se realiza un *Push* o *Pull Request*, se ejecutan todos los tests unitarios (`./gradlew testDebugUnitTest`) para asegurar que las nuevas funciones no rompan las validaciones de negocio existentes.
