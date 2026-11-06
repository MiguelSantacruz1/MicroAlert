# Especificación de Patrones Hápticos y Retroalimentación Táctil

## 1. Principios de Diseño Háptico en Wear OS

La retroalimentación táctil en MICROALERT está diseñada para ser inequívoca, permitiendo al usuario identificar el grado de riesgo sin necesidad de mirar la pantalla:

- **Diferenciabilidad:** Cada nivel posee una cadencia, duración y amplitud claramente distinguibles.
- **Eficiencia de Batería:** Uso de pausas entre impulsos y formas de onda nativas (`VibrationEffect.createWaveform`) para no sobrecargar el motor háptico.
- **Control de Sobrestimulación:** Períodos de enfriamiento obligatorios para evitar la desensibilización sensorial del conductor u operario.

---

## 2. Tabla de Patrones por Nivel

| Nivel de Riesgo | Patrón Temporal (ms) | Amplitud (0-255) | Enfriamiento (s) | Descripción |
|---|---|---|---|---|
| **PRECAUCIÓN** | `[0, 200, 100, 200]` | `[0, 128, 0, 128]` | 90s | Pulso suave doble. |
| **ALTO** | `[0, 350, 150, 350]` | `[0, 200, 0, 200]` | 45s | Pulso firme prolongado doble. |
| **CRÍTICO** | `[0, 500, 100, 500, 100, 500]` | `[0, 255, 0, 255, 0, 255]` | 20s | Ráfaga triple máxima intensidad. |
| **RECONOCIMIENTO** | `[0, 100]` | `[0, 100]` | N/A | Pulso corto táctil de confirmación. |

---

## 3. Compatibilidad con Hardware Antiguo
En dispositivos Wear OS con API < 26 sin soporte de amplitudes variables, se emplea la interfaz de legado `vibrator.vibrate(pattern, -1)` con tiempos temporales idénticos para garantizar coherencia en la percepción.
