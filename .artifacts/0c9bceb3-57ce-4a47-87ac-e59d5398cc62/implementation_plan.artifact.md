# Plan de Implementación: Módulo de Autenticación, Perfil y Filtros (Thomas Isaza)

Este plan aborda la implementación de las Historias de Usuario HU-05, HU-06, HU-07 y HU-08, integrando seguridad básica, personalización y mejoras en la búsqueda de equipos.

## User Review Required

> [!IMPORTANT]
> **Autenticación Simulada:** Dado que no hay un backend real, la autenticación será local y persistente solo durante la sesión de la app. Se validará que el correo pertenezca al dominio `@sena.edu.co` o `@misena.edu.co`.

> [!WARNING]
> **Cambios en Modelos:** Se añadirá el campo `isFavorite` al modelo `Equipo`. Esto podría requerir actualizaciones en los tests existentes si estos dependen de la estructura exacta del constructor.

## Proposed Changes

### 1. Modelos y Datos

#### [MODIFY] [Equipo.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/model/Equipo.kt)
- Añadir `val isFavorite: Boolean = false`.

#### [NEW] [Usuario.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/model/Usuario.kt)
- Crear data class `Usuario` con `nombre`, `correo` y `rol`.

#### [MODIFY] [PrestamoRepository.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/data/repository/PrestamoRepository.kt)
- Añadir métodos: `login(correo: String, pass: String)`, `toggleFavorite(equipoId: Int)`, `obtenerUsuarioLogueado()`.

---

### 2. ViewModel y Estado

#### [MODIFY] [PrestamoUiState.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/viewmodel/PrestamoUiState.kt)
- Añadir `usuarioLogueado: Usuario?`, `searchQuery: String`, `categoriaSeleccionada: CategoriaEquipo?`.

#### [MODIFY] [PrestamoViewModel.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/viewmodel/PrestamoViewModel.kt)
- Implementar lógica de login, búsqueda, filtrado y favoritos.

---

### 3. Interfaz de Usuario (UI)

#### [NEW] [LoginScreen.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/ui/auth/LoginScreen.kt)
- Formulario con validación de dominio institucional y manejo de errores.

#### [NEW] [ProfileScreen.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/ui/profile/ProfileScreen.kt)
- Visualización de datos del usuario y botón de cerrar sesión.

#### [MODIFY] [CatalogoScreen.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/ui/catalogo/CatalogoScreen.kt)
- Integrar `SearchBar` de Material 3 y `FilterChips` por categoría.
- Añadir icono de corazón en `EquipoItem` para favoritos.

---

### 4. Navegación

#### [MODIFY] [AppNavigation.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/navigation/AppNavigation.kt)
- Añadir rutas `Login` y `Perfil`.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Sena/AndroidStudioProjects/PrestamoLabCTMA/app/src/main/java/com/example/prestamolabctma/MainActivity.kt)
- Configurar `Login` como destino inicial si no hay usuario.

## Verification Plan

### Automated Tests
- `PrestamoViewModelTest`: Añadir casos para validación de correo institucional y filtrado de equipos.

### Manual Verification
1. Abrir la app y verificar que redirige a Login.
2. Intentar loguearse con un correo no institucional (debe fallar).
3. Loguearse con `@sena.edu.co` (debe entrar al catálogo).
4. Usar la barra de búsqueda y los filtros de categoría.
5. Marcar un equipo como favorito y verificar que persiste visualmente.
6. Ir al perfil y cerrar sesión.
