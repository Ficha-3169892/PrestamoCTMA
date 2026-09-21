# Implementación de Persistencia Local y Fuente Única de Verdad

Implementación de Room 3.0 (KSP), DataStore y Repositorios para el proyecto PrestamoCTMA, siguiendo patrones offline-first.

## User Review Required

> [!IMPORTANT]
> Se configurará Room con KSP. Asegúrate de que el plugin de KSP sea compatible con la versión de Kotlin instalada (2.0.21). Se utilizará la versión estable más reciente compatible.

## Proposed Changes

### Gradle Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/gradle/libs.versions.toml)
- Agregar versiones para Room, KSP y DataStore.
- Definir las librerías necesarias.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/build.gradle.kts)
- Aplicar plugin KSP.
- Agregar dependencias de Room y DataStore.
- Configurar la exportación de esquemas de Room.

---

### Room Persistence Layer

#### [NEW] [ArticuloEntity.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/local/entities/ArticuloEntity.kt)
- Entidad que representa un artículo (Equipo).

#### [NEW] [PrestamoEntity.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/local/entities/PrestamoEntity.kt)
- Entidad que representa un préstamo (SolicitudPrestamo), con relación a ArticuloEntity.

#### [NEW] [AppDatabase.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/local/AppDatabase.kt)
- Clase abstracta RoomDatabase y TypeConverters para fechas y enums.

#### [NEW] [PrestamoDao.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/local/dao/PrestamoDao.kt)
- Interfaz DAO para operaciones CRUD y consultas reactivas con Flow.

---

### DataStore (Preferences)

#### [NEW] [PreferenciasRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/local/datastore/PreferenciasRepository.kt)
- Implementación de DataStore para persistir filtros de UI (estado de préstamo).

---

### Repository Layer (Single Source of Truth)

#### [MODIFY] [PrestamoRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/repository/PrestamoRepository.kt)
- Actualizar la interfaz para incluir los flujos reactivos.

#### [NEW] [OfflinePrestamoRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoCTMA/app/src/main/java/com/ctma/prestamoctma/data/repository/OfflinePrestamoRepository.kt)
- Implementación del repositorio que media entre Room/DataStore y la UI.

## Verification Plan

### Automated Tests
- Ejecutar `./gradlew build` para verificar la configuración de KSP y Room.
- Se recomienda crear pruebas unitarias para el DAO en el futuro.

### Manual Verification
- Verificar que los archivos se generen correctamente en la estructura de paquetes definida.
