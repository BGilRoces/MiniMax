# Stack Tecnológico - MiniMax

## Frontend / UI
- **Lenguaje:** Kotlin (Nativo)
- **UI Framework:** Jetpack Compose
- **Design System:** Material Design 3
- **Arquitectura UI:** MVVM con Flujo Unidireccional de Datos (UDF)
    - `ViewModels` exponen estado vía `StateFlow`.
    - `Screens`: Manejan lógica de navegación y ViewModel.
    - `Content`: Componentes stateless y reutilizables.

## Capa de Datos (Repository Pattern)
- **DataStore:** Preferencias de usuario y tokens de sesión.
- **Room (SQLite):** Persistencia local y soporte offline (cache de oportunidades).
- **Retrofit:** Comunicación con API REST.
- **Mappers:** Conversión entre DTOs y modelos de dominio.

## Servicios Externos
- **Firebase Cloud Messaging (FCM):** Notificaciones push para progreso de grupos.

## Inyección de Dependencias
- **Koin:** Configuración ligera de dependencias para Repositorios, DAOs y Retrofit.

## Navegación
- **Navigation Compose:** NavHost con bifurcación de rutas basada en el rol del usuario (Comprador/Proveedor).
