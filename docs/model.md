# Especificación del Modelo Machine Learning – Random Forest

## 1. Definición del Problema

MICROALERT utiliza un clasificador **Random Forest** entrenado en Python mediante `scikit-learn` y exportado a formato JSON ejecutable de forma nativa en Kotlin.

El modelo calcula la probabilidad de la clase **Somnolencia Detectada** ($P \in [0, 1]$).

---

## 2. Vector de Características (Feature Vector)

El vector debe mantener de forma estricta el siguiente orden de 8 variables tanto en entrenamiento como en inferencia:

| Índice | Nombre de Variable | Unidad | Descripción / Estadística de Agregación |
|---|---|---|---|
| 0 | `heartRate` | BPM | Frecuencia cardíaca media de la ventana |
| 1 | `accelerometerMagnitude` | m/s² | Magnitud vectorial agregada $(\sqrt{x^2+y^2+z^2})$ |
| 2 | `gyroscopeMagnitude` | rad/s | Magnitud angular agregada $(\sqrt{wx^2+wy^2+wz^2})$ |
| 3 | `inactivityDurationSeconds` | s | Tiempo transcurrido sin movimiento significativo |
| 4 | `reactionTimeMs` | ms | Latencia registrada en prueba de reacción táctil/visual |
| 5 | `sessionDurationSeconds` | s | Tiempo total transcurrido en la sesión activa |
| 6 | `hrvSdnnMs` | ms | Desviación estándar de intervalos IBI/RR ($SDNN$) |
| 7 | `hrvRmssdMs` | ms | Raíz cuadrada de la media de diferencias al cuadrado ($RMSSD$) |

---

## 3. Formato de Exportación del Modelo (JSON)

El modelo se serializa en un archivo `model.json` con la siguiente estructura:

```json
{
  "version": "1.0.0",
  "created_at": "2025-03-01T00:00:00Z",
  "feature_names": [
    "heartRate",
    "accelerometerMagnitude",
    "gyroscopeMagnitude",
    "inactivityDurationSeconds",
    "reactionTimeMs",
    "sessionDurationSeconds",
    "hrvSdnnMs",
    "hrvRmssdMs"
  ],
  "feature_medians": [72.0, 9.81, 0.05, 10.0, 350.0, 600.0, 45.0, 40.0],
  "trees": [
    {
      "node_id": 0,
      "feature_index": 0,
      "threshold": 65.5,
      "left_child": 1,
      "right_child": 2,
      "value": null
    }
  ]
}
```

---

## 4. Inferencia en Kotlin

1. **Lectura de Entrada:** Se construye la ventana temporal de 60 segundos.
2. **Imputación de Valores Faltantes:** Si una variable (por ejemplo HRV o tiempo de reacción) no está disponible, se imputa la mediana correspondiente de `feature_medians`.
3. **Recorrido del Árbol:** Para cada árbol en el bosque, se desciende desde el nodo raíz hasta la hoja evaluando las condiciones $X_i \leq \text{threshold}$.
4. **Votación Promedio:** La probabilidad final es el promedio de las probabilidades predichas por cada árbol.

---

## 5. Criterios de Evaluación y Tolerancia

- **Pruebas de Equivalencia:** Se generan vectores de prueba en Python y se verifica que la implementación en Kotlin devuelva probabilidades idénticas con una tolerancia máxima de $10^{-6}$.
