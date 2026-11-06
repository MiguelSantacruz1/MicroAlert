# Arquitectura del Sistema MICROALERT

## 1. Visión General

MICROALERT se basa en una arquitectura **Clean Architecture + MVVM (Model-View-ViewModel)** adaptada para Wear OS. La aplicación garantiza un desacoplamiento estricto entre el hardware de los sensores, el procesamiento de señales, el modelo de clasificación Machine Learning y la capa de presentación Compose.

```
[ Sensores (Hardware / SensorManager) ]
                │
                ▼
  [ Módulo de Procesamiento de Señales ] ──► (Ventanas de 60s / Paso 30s)
                │
                ▼
  [ Vector de 8 Características ] ──► (Imputación por mediana si falta HRV/Reacción)
                │
                ▼
  [ Inferencia ML (Random Forest Evaluator) ]
                │
                ▼
    [ Clasificador de Riesgo & Políticas ]
                │
                ▼
[ ViewModel & Jetpack Compose UI / Haptic Alerts ]
```

---

## 2. Descripción de Módulos

### `sensors`
- Encargado de la comunicación directa con `SensorManager` y `Health Services`.
- Proporciona flujos asíncronos (`Flow<SensorSample>`) para Acelerómetro, Giroscopio y Frecuencia Cardíaca (BPM / RR-IBI).

### `signalprocessing`
- Procesa las señales crudas mediante ventanas temporales deslizantes (60 segundos con solape/paso de 30 segundos).
- Aplica filtros de ruido y calcula agregados (magnitud de aceleración, magnitud angular, variabilidad HRV SDNN/RMSSD).

### `ml`
- Implementa la carga del modelo Random Forest desde formato JSON.
- Ejecuta la inferencia nativa en Kotlin sin dependencias de frameworks externos pesados.
- Realiza la imputación de valores faltantes basada en las medianas guardadas en los metadatos del modelo.

### `domain`
- Contiene los casos de uso (`StartMonitoringUseCase`, `EvaluateDrowsinessUseCase`, `ManageAlertsUseCase`).
- Define los modelos de dominio independientes de la plataforma.

### `data`
- Coordina los datos entre los sensores, la base de datos local y la configuración del usuario.

### `storage`
- Implementa la persistencia local con Room Database para registrar el historial de sesiones y eventos de alerta.

### `services`
- Maneja el `Foreground Service` para mantener el monitoreo continuo en Wear OS y mostrar una `Ongoing Activity`.

### `presentation`
- Interfaz gráfica construida 100% en Jetpack Compose for Wear OS con componentes de Material 3 for Wear OS.

---

## 3. Principios de Diseño

1. **Eficiencia Energética:** Muestreo controlado de sensores y cálculo por ventanas para maximizar la batería.
2. **Autonomía:** Monitoreo e inferencia 100% locales sin requerir conectividad a Internet ni teléfono inteligente emparejado para la lógica principal.
3. **Resiliencia de Datos:** Manejo transparente de sensores no disponibles mediante imputación basada en metadatos de entrenamiento.
