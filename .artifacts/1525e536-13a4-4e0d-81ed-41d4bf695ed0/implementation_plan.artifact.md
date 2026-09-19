# Plan de Implementación: Semana 8 - Persistencia Cloud (Supabase)

Este plan establece la infraestructura necesaria para la sincronización en la nube de "PréstamoLab CTMA", utilizando Supabase como backend y Ktor como motor de red, siguiendo el Manifiesto Tecnológico 2026.

## User Review Required

> [!IMPORTANT]
> Se requiere la **URL de Supabase** y la **Anon Key** para la configuración final del cliente. Por ahora se usarán placeholders en `BuildConfig` o `ServiceLocator`.

> [!WARNING]
> La migración a Supabase Auth (S8) reemplazará el sistema de login actual. Se debe asegurar que los usuarios existentes en la base local tengan una correspondencia en la nube.

## Proposed Changes

### [Infraestructura de Red]

#### [MODIFY] [libs.versions.toml](file:///C:/Users/User/Desktop/prestamolab-ctma/gradle/libs.versions.toml)
*   Añadir dependencias de Supabase (Postgrest, Storage, Auth) y Ktor 3.x.
*   Añadir el plugin de Serialización de Kotlin.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/User/Desktop/prestamolab-ctma/app/build.gradle.kts)
*   Aplicar plugin `kotlinx-serialization`.
*   Implementar dependencias de red.
*   Configurar `network_security_config.xml` (Requerimiento S9 anticipado).

#### [NEW] [network_security_config.xml](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/res/xml/network_security_config.xml)
*   Prohibir tráfico de texto claro (HTTP).

### [Modelos de Datos (Red)]

#### [NEW] [EquipoDto.kt](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/java/data/remote/dto/EquipoDto.kt)
*   Clase serializable para la comunicación con Supabase.

#### [NEW] [SolicitudDto.kt](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/java/data/remote/dto/SolicitudDto.kt)
*   Manejo de estados y fechas para la API.

### [Servicios Supabase]

#### [NEW] [SupabaseClient.kt](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/java/data/remote/SupabaseClient.kt)
*   Configuración centralizada del cliente Supabase con Ktor.

#### [NEW] [RemotePrestamoDataSource.kt](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/java/data/remote/RemotePrestamoDataSource.kt)
*   Lógica CRUD en la nube (Postgrest).

### [Repositorio Sincronizado]

#### [MODIFY] [LocalPrestamoRepository.kt](file:///C:/Users/User/Desktop/prestamolab-ctma/app/src/main/java/data/repository/LocalPrestamoRepository.kt)
*   Implementar el patrón de persistencia híbrida:
    1.  Escritura en Room (Garantía local).
    2.  Actualización asíncrona en Supabase.
*   Implementar descarga inicial de datos desde la nube.

## Verification Plan

### Automated Tests
*   **Unit Tests:** Probar mappers DTO -> Domain -> Entity.
*   **Integration Tests:** Simular fallos de red y verificar que la base local (Room) mantiene la integridad.

### Manual Verification
*   Verificar en la consola de Supabase que los registros se crean correctamente al realizar una solicitud en la app.
*   Probar el modo offline: realizar cambios sin internet y verificar que la app sigue funcionando con Room.
