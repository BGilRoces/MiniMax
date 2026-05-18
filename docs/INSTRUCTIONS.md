# Guía de Implementación Paso a Paso - MiniMax

Este documento sirve como hoja de ruta para el desarrollo de la aplicación, siguiendo la arquitectura y tecnologías definidas.

## Fase 1: Configuración Inicial
1. **Dependencias:** Configurar `build.gradle` con Jetpack Compose, Koin, Room, Retrofit, DataStore, Navigation y Firebase.
2. **Estructura de Carpetas:** Definir paquetes: `data`, `domain`, `ui`, `di`, `util`.
3. **DI con Koin:** Configurar el `MainApplication` y los módulos iniciales.

## Fase 2: Capa de Datos y Dominio
1. **Modelos de Dominio:** Definir `Oportunidad`, `Usuario`, `Pedido`.
2. **DataStore:** Implementar `UserPreferencesDataSource` para el rol y token.
3. **Room:** Definir la base de datos local y DAOs para caching de oportunidades.
4. **Retrofit:** Definir interfaces de la API y DTOs.
5. **Mappers:** Crear funciones de extensión para convertir entre DTOs, Entities y Modelos de Dominio.
6. **Repositorios:** Implementar `AuthRepository` y `OportunidadRepository` con estrategia de "Single Source of Truth".

## Fase 3: Navegación y Perfil de Usuario
1. **Navigation:** Configurar `NavHost` y rutas (Auth, Buyer, Provider).
2. **Login/Selector de Rol:** Pantalla para elegir si se es Comprador o Proveedor y persistir en DataStore.

## Fase 4: Flujo del Comprador
1. **Lista de Oportunidades:** ViewModel + Screen para explorar compras grupales activas.
2. **Detalle de Oportunidad:** Información ampliada y opción de "Sumarse".
3. **Mis Pedidos:** Seguimiento de las compras en las que participa.

## Fase 5: Flujo del Proveedor
1. **Mis Publicaciones:** Lista de oportunidades creadas por el proveedor.
2. **Nueva Publicación:** Formulario para cargar producto, precio, mínimo y fecha límite.
3. **Gestión de Publicación:** Ver progreso de la cantidad acumulada.

## Fase 6: Notificaciones y Extras
1. **Firebase Cloud Messaging:** Integrar el servicio para recibir actualizaciones.
2. **UI Polish:** Aplicar Material Design 3 consistentemente y manejar estados de carga/error.
