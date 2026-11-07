# MICROALERT

**MICROALERT** es una aplicación nativa para relojes inteligentes (Wear OS) diseñada para identificar posibles patrones fisiológicos y conductuales relacionados con la somnolencia mediante el análisis en tiempo real de sensores, procesamiento de señales e inferencia local de Machine Learning (Random Forest).

---

## Objetivo del Proyecto

Monitorear variables fisiológicas y de movimiento durante sesiones activas en un smartwatch para clasificar el nivel de riesgo de somnolencia en 4 categorías (`NORMAL`, `PRECAUCIÓN`, `ALTO`, `CRÍTICO`) y emitir alertas visuales y hápticas personalizadas.

> **Exención de responsabilidad médica:** MICROALERT es un prototipo experimental de asistencia. No es un dispositivo médico, no realiza diagnósticos clínicos ni reemplaza el descanso adecuado o las medidas de seguridad del usuario.

---

## Arquitectura y Tecnologías

- **Plataforma:** Wear OS (Android API 30+ / minSdk 30, targetSdk 35)
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose for Wear OS & Material 3 for Wear OS (`androidx.wear.compose.material3`)
- **Navegación:** Navigation Compose for Wear OS
- **Inferencia ML:** Evaluador nativo de Random Forest en Kotlin (in-memory JSON deserializer)
- **Sensores:** SensorManager de Android & Wear OS Health Services
- **Almacenamiento:** Room Database local
- **Asincronía:** Kotlin Coroutines & StateFlow
- **Inyección de Dependencias:** Inyección Manual de Dependencias (AppContainer / Service Locator)
- **CI/CD:** GitHub Actions

---

## Vector de Características (Machine Learning)

El modelo Random Forest evalúa ventanas temporales continuas utilizando un vector estricto de 8 variables:

1. `heartRate`: Frecuencia cardíaca (BPM)
2. `accelerometerMagnitude`: Magnitud agregada del acelerómetro (m/s²)
3. `gyroscopeMagnitude`: Magnitud angular agregada del giroscopio (rad/s)
4. `inactivityDurationSeconds`: Tiempo de inactividad física (s)
5. `reactionTimeMs`: Tiempo de respuesta en prueba de reacción (ms)
6. `sessionDurationSeconds`: Duración total acumulada de la sesión (s)
7. `hrvSdnnMs`: Variabilidad de frecuencia cardíaca SDNN (ms)
8. `hrvRmssdMs`: Variabilidad de frecuencia cardíaca RMSSD (ms)

---

## Niveles de Riesgo y Alertas

- **NORMAL:** Sin indicadores significativos de somnolencia.
- **PRECAUCIÓN:** Patrones iniciales que requieren atención.
- **ALTO:** Múltiples indicadores coincidentes de fatiga/somnolencia.
- **CRÍTICO:** Patrón claro de alto riesgo; requiere alerta inmediata visual y vibración háptica continua con enfriamiento.

---

## Modos de Funcionamiento

- **Modo REAL:** Captura directa de sensores de hardware y procesamiento en vivo.
- **Modo DEMO:** Simulación interactiva de patrones fisiológicos y prueba de niveles de riesgo.

---

## Estructura del Proyecto

```text
com.microalert.app/
├── data/                 # Repositorios y fuentes de datos
├── di/                   # Contenedor manual de inyección de dependencias (AppContainer)
├── domain/               # Casos de uso, clasificadores y modelos de negocio
├── ml/                   # Evaluador de Random Forest e imputación
├── presentation/         # Pantallas Wear OS en Jetpack Compose y ViewModels
├── sensors/              # Listeners de SensorManager y Health Services
├── services/             # Foreground Service para monitoreo continuo
├── signalprocessing/     # Filtros y cálculo de ventanas temporales
├── storage/              # Base de datos Room e historial de sesiones
└── utils/                # Utilidades de vibración, optimización y formateo
```

---

## Verificación y Pruebas Unitarias

El proyecto cuenta con una suite completa de pruebas unitarias y de integración que certifican la equivalencia numérica del modelo Random Forest con respecto a Python scikit-learn ($< 10^{-6}$), la estabilidad temporal ante artefactos y la resiliencia en almacenamiento:

- **Total de pruebas automatizadas:** 62 pruebas unitarias e integración en JVM.
- **Tasa de éxito:** 100% pasando (`gradle testDebugUnitTest`).
- **Historial de Commits:** Desarrollo granular riguroso siguiendo Conventional Commits en español.

---

## Licencia

Distribuido bajo la Licencia MIT. Consulta `LICENSE` para más información.
