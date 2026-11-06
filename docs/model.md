# Especificación del Modelo Machine Learning – Random Forest

## 1. Definición del Problema

MICROALERT utiliza un clasificador **Random Forest** entrenado en Python mediante `scikit-learn` y exportado a formato JSON ejecutable de forma nativa en Kotlin dentro del smartwatch Wear OS.

El modelo calcula la probabilidad continua de la clase positiva **Somnolencia Detectada** ($P \in [0, 1]$). La capa de clasificación de riesgo (sección 7) traduce esta probabilidad en cuatro estados (`NORMAL`, `PRECAUCIÓN`, `ALTO`, `CRÍTICO`) mediante umbrales e histéresis temporal.

---

## 2. Vector de Características (Feature Vector)

El vector mantiene de forma estricta el siguiente orden de 8 variables tanto en entrenamiento, exportación e inferencia en Wear OS:

| Índice | Nombre de Variable | Unidad | Estadístico de Agregación | Mediana Imputación |
|---|---|---|---|---|
| 0 | `heartRate` | BPM | Media aritmética en ventana de 60s | 70.45 |
| 1 | `accelerometerMagnitude` | m/s² | Media de magnitud vectorial $(\sqrt{x^2+y^2+z^2})$ | 9.81 |
| 2 | `gyroscopeMagnitude` | rad/s | Media de magnitud angular $(\sqrt{wx^2+wy^2+wz^2})$ | 0.04 |
| 3 | `inactivityDurationSeconds` | s | Tiempo acumulado con variación $< 0.35 m/s^2$ | 17.80 |
| 4 | `reactionTimeMs` | ms | Latencia válida en prueba táctil (últimos 15 min) | 357.55 |
| 5 | `sessionDurationSeconds` | s | Tiempo total transcurrido en la sesión activa | 915.00 |
| 6 | `hrvSdnnMs` | ms | Desviación estándar de intervalos IBI/RR ($SDNN$) | 49.46 |
| 7 | `hrvRmssdMs` | ms | Raíz de la media de diferencias al cuadrado ($RMSSD$) | 44.60 |

---

## 3. Parámetros del Modelo y Arquitectura

El modelo está optimizado para garantizar una inferencia rápida y bajo consumo de batería en Wear OS:
- **Algoritmo:** `RandomForestClassifier` (`scikit-learn`).
- **Número de estimadores (`n_estimators`):** 25 árboles de decisión.
- **Profundidad máxima (`max_depth`):** 6 niveles.
- **Criterio de división:** Gini.
- **Mínimo de muestras por división (`min_samples_split`):** 5.
- **Mínimo de muestras por hoja (`min_samples_leaf`):** 2.
- **Semilla fija (`random_state`):** 42 (100% reproducible).

---

## 4. Métricas de Validación (GroupKFold por Participante)

La validación se realizó agrupando por participante (`GroupKFold`, $k=5$, 25 participantes sintéticos, 3000 ventanas) para certificar que ningún participante estuviese simultáneamente en entrenamiento y prueba:

| Métrica | Valor Obtenido (Out-Of-Fold) |
|---|---|
| **Accuracy** | 0.9997 |
| **Precision** | 1.0000 |
| **Recall / Sensibilidad** | 0.9992 |
| **F1-Score** | 0.9996 |
| **Especificidad** | 1.0000 |
| **Verdaderos Negativos (TN)** | 1732 |
| **Falsos Positivos (FP)** | 0 |
| **Falsos Negativos (FN)** | 1 |
| **Verdaderos Positivos (TP)** | 1267 |

> ⚠️ **Advertencia Metodológica y Ética:** Las métricas superiores al 99% reflejan la coherencia matemática y el ajuste del clasificador sobre el dataset sintético generado para verificación de software. **No representan la precisión clínica real**, la cual debe determinarse mediante ensayos clínicos y protocolos formales con sujetos humanos ([docs/data_protocol.md](file:///C:/Users/Albeiro/StudioProjects/MicroAlert/docs/data_protocol.md)).

---

## 5. Formato de Exportación JSON (`model.json` y `metadata.json`)

El bosque se exporta a formato JSON ligero dentro de `app/src/main/assets/model.json`:
- **Nodos internos:** `node_id`, `feature_index` (0 a 7), `threshold`, `left_child`, `right_child`, `value = null`.
- **Nodos hoja:** `node_id`, `feature_index = -1`, `threshold = null`, `left_child = -1`, `right_child = -1`, `value = float` (probabilidad $P \in [0, 1]$).

---

## 6. Criterios de Evaluación y Tolerancia de Equivalencia

- Se dispone del archivo [test_vectors.json](file:///C:/Users/Albeiro/StudioProjects/MicroAlert/app/src/main/assets/test_vectors.json) con vectores de referencia cubriendo alerta, somnolencia, estados límites y variables faltantes.
- En la prueba cruzada Python-Kotlin, la diferencia absoluta entre la predicción de `scikit-learn` y el evaluador Kotlin debe ser estrictamente menor a **$10^{-6}$**.
