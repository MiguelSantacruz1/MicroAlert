# Arquitectura del Sistema MICROALERT

## 1. Visión General

MICROALERT se basa en una arquitectura **Clean Architecture + MVVM (Model-View-ViewModel)** adaptada para Wear OS. La aplicación garantiza un desacoplamiento estricto entre el hardware de los sensores, el procesamiento de señales, el modelo de clasificación Machine Learning y la capa de presentación Compose.

```text
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
- **Gestión de Ventanas Temporales:** Implementa `BufferWindowManager` para ventanas deslizantes de 60 segundos con paso/solape de 30 segundos (50% overlap).
- **Algoritmos HRV:** `HrvCalculator` evalúa $SDNN$ y $RMSSD$ únicamente cuando existen intervalos $RR/IBI$ válidos:
  - $SDNN = \sqrt{\frac{1}{N-1}\sum_{i=1}^N (RR_i - \overline{RR})^2}$
  - $RMSSD = \sqrt{\frac{1}{N-1}\sum_{i=1}^{N-1} (RR_{i+1} - RR_i)^2}$
- **Filtro y Detección de Inactividad:** `InactivityDetector` contabiliza tiempo continuo sin variación significativa de gravedad ($< 0.35 m/s^2$).
- **Agregación de Características:** `FeatureAggregator` calcula medias y consolidaciones dentro de cada ventana sin mezclar datos entre ventanas distintas.

### `ml`
- **Carga de Modelo:** `AssetModelLoader` deserializa `model.json` y `metadata.json` desde assets empaquetados en la aplicación o desde flujos de entrada en pruebas.
- **Modelos de Ensamble:** `RandomForestModel`, `DecisionTree` y `DecisionTreeNode` representan los árboles y nodos con mapeo eficiente por `nodeId`.
- **Inferencia Nativa Iterativa:** `RandomForestEvaluator` y `DecisionTreeEvaluator` ejecutan el descenso en cada árbol mediante un bucle iterativo sin recursión, eliminando el riesgo de desbordamiento de pila en Wear OS y promediando las probabilidades $P \in [0, 1]$.
- **Estrategia de Imputación:** `ImputationStrategy` sustituye variables no disponibles (HRV, Reacción, BPM) por las medianas calculadas en el entrenamiento.
- **Equivalencia Matemática:** Validación estricta con tolerancia $< 10^{-6}$ contra las probabilidades de referencia de `scikit-learn` registradas en `test_vectors.json`.

### `domain`
- **Modelos de Negocio:** `RiskLevel` (4 niveles), `RiskClassificationConfig` (umbrales configurables), `WindowDataQuality` (transparencia de señales) y `RiskAssessmentResult`.
- **Política de Clasificación:** `RiskPolicyManager` implementa el mapeo de probabilidades con filtro de persistencia temporal ($N$ ventanas consecutivas para elevar a `HIGH`/`CRITICAL`) e histéresis anti-rebote para evitar oscilaciones rápidas entre niveles adyacentes al descender de riesgo.
- **Casos de Uso:**
  - `EvaluateRiskUseCase`: Orquesta la extracción de calidad de señales, la inferencia local del modelo y la política de riesgo.
  - `PredictDrowsinessProbabilityUseCase`: Invoca al evaluador del ensamble ML.
  - `ExtractFeatureVectorUseCase`: Ensambla el vector de 8 variables en orden estricto.
  - `ProcessSignalWindowUseCase`: Expone las ventanas agregadas por el pipeline de señales.
  - `ObserveSensorDataUseCase` & `CheckSensorAvailabilityUseCase`: Diagnóstico y flujos de sensores.

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
