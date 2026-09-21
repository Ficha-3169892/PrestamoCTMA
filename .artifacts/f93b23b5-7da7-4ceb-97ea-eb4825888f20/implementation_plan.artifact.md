# Integración de Supabase como Backend Remoto

Integración del SDK de Supabase para persistencia remota, manteniendo a Room como la Fuente Única de Verdad (SSoT) y garantizando una arquitectura offline-first.

## User Review Required

> [!IMPORTANT]
> Se requiere que proporciones la `SUPABASE_URL` y la `SUPABASE_ANON_KEY`. Por ahora, se dejarán marcadores de posición en el código para que los rellenes o se usarán constantes configurables.
> Se utilizará `kotlinx-serialization` para el mapeo de datos remotos.

## Proposed Changes

### Gradle Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/gradle/libs.versions.toml)
- Agregar versiones para Supabase (Postgrest, GoTrue), Ktor y Kotlinx Serialization.
- Definir las librerías necesarias.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/build.gradle.kts)
- Aplicar plugin `plugin.serialization`.
- Agregar dependencias de Supabase y Ktor.

---

### Remote Data Layer (Supabase)

#### [NEW] [ArticuloDto.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/remote/dto/ArticuloDto.kt)
- DTO serializable para la tabla de equipos en Supabase.
- Funciones de conversión a `ArticuloEntity`.

#### [NEW] [PrestamoDto.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/remote/dto/PrestamoDto.kt)
- DTO serializable para la tabla de préstamos en Supabase.
- Funciones de conversión a `PrestamoEntity`.

#### [NEW] [SupabaseClient.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/remote/SupabaseClient.kt)
- Configuración y creación del cliente de Supabase usando `createSupabaseClient`.

---

### Repository Layer (Sync Logic)

#### [MODIFY] [PrestamoRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/repository/PrestamoRepository.kt)
- Agregar función `refresh()` a la interfaz.

#### [MODIFY] [OfflinePrestamoRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/repository/OfflinePrestamoRepository.kt)
- Implementar `refresh()`: consultar Supabase, mapear a entidades y guardar en Room en una transacción.
- Manejo de errores de red y timeouts.

---

### UI & ViewModel (Refresh State)

#### [MODIFY] [PrestamoUiState.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/ui/viewmodel/PrestamoUiState.kt)
- Agregar `isRefreshing` al estado de la UI.

#### [MODIFY] [PrestamoViewModel.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/ui/viewmodel/PrestamoViewModel.kt)
- Implementar función `refreshData()` que invoque al repositorio.
- Llamar a `refreshData()` al inicializar el ViewModel.

## Verification Plan

### Automated Tests
- Ejecutar `./gradlew build` para verificar dependencias y serialización.

### Manual Verification
- Verificar que la UI muestre un indicador de carga (SwipeRefresh o similar) cuando se está sincronizando con Supabase.
- Confirmar que los datos locales (Room) se actualicen tras un `refresh()` exitoso.
