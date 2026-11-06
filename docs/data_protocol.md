# Protocolo de Recolección de Datos Fisiológicos y Somnolencia – MICROALERT

## 1. Justificación y Objetivos

El presente documento define el protocolo estandarizado y los principios bioéticos para la adquisición de datos reales de somnolencia en sujetos humanos.

> ⚠️ **Declaración de Estado Actual:** Todos los conjuntos de datos incluidos en esta versión del repositorio corresponden a **datos sintéticos** generados algorítmicamente para verificar la viabilidad técnica del pipeline de procesamiento y de la inferencia local. No constituyen evidencia de validación clínica ni de rendimiento en entornos reales de conducción.

---

## 2. Marco Bioético y Consentimiento Informado

1. **Aprobación Ética:** Todo estudio con participantes humanos debe contar con la aprobación previa de un Comité de Ética en Investigación institucional.
2. **Consentimiento Informado:** Cada participante debe firmar un consentimiento por escrito donde se exponga:
   - Naturaleza no invasiva del registro (smartwatch comercial con PPG y acelerometría).
   - Ausencia de riesgos físicos o psicológicos significativos.
   - Posibilidad de revocar su participación en cualquier instante sin consecuencias.
3. **Anonimización:** Los datos personales quedan estrictamente disociados. Cada voluntario recibe un identificador alfanumérico único (e.g. `P_001`, `P_002`) sin almacenar nombres, documentos de identidad ni direcciones IP.

---

## 3. Escala de Referencia de Somnolencia (Ground Truth)

Para etiquetar el nivel subjetivo de somnolencia se establece la **Escala de Somnolencia de Karolinska (KSS - Karolinska Sleepiness Scale)**:

| Nivel KSS | Descripción Subjetiva | Clasificación Binaria MICROALERT |
|---|---|---|
| 1 | Extremadamente alerta | 0 (Alerta) |
| 2 | Muy alerta | 0 (Alerta) |
| 3 | Alerta | 0 (Alerta) |
| 4 | Bastante alerta | 0 (Alerta) |
| 5 | Ni alerta ni somnoliento | 0 (Alerta) |
| 6 | Algunas señales de somnolencia | 0 (Alerta / Pre-fatiga) |
| 7 | Somnoliento, pero sin esfuerzo para permanecer despierto | 1 (Somnolencia Detectada) |
| 8 | Somnoliento, requiere esfuerzo mantenerse despierto | 1 (Somnolencia Detectada) |
| 9 | Extremadamente somnoliento, gran esfuerzo para no dormir | 1 (Somnolencia Detectada) |

*Criterio de Etiquetado:* Las ventanas temporales asociadas a evaluaciones con $KSS \geq 7$ se etiquetan como clase positiva (`drowsy = 1`). Las ventanas con $KSS \leq 5$ se etiquetan como clase negativa (`drowsy = 0`). Las ventanas asociadas a $KSS = 6$ se documentan como periodo de transición.

---

## 4. Procedimiento de Registro Experimental

1. **Entorno Seguro:** Las pruebas se realizan exclusivamente en simuladores de conducción o cabinas de vigilancia estáticas controladas. **Nunca en vías públicas ni en vehículos en movimiento real.**
2. **Duración de Sesiones:** Sesiones continuas de 45 a 90 minutos en horarios de mayor presión homeostática de sueño (madrugada 01:00 - 05:00 o periodo postprandial 14:00 - 16:00).
3. **Colocación del Dispositivo:** Reloj en la muñeca no dominante, ajustado firmemente a 1-2 cm del pliegue de la muñeca para asegurar un contacto óptimo del sensor óptico de pulso (PPG).
4. **Prueba de Reacción (PVT reducida):** Se aplica una prueba de tiempo de reacción simple cada 15 minutos para contrastar la latencia psicomotora con la autoevaluación KSS.

---

## 5. Control de Calidad y Criterios de Exclusión

- Exclusión de ventanas con más del 30% de muestras de frecuencia cardíaca fuera de rango fisiológico ($<30$ o $>220$ BPM).
- Exclusión de sesiones donde el participante reporte consumo reciente de estimulantes (cafeína, taurina) o fármacos sedantes no declarados.
