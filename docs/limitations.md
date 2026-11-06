# Limitaciones Técnicas y Exención de Responsabilidad – MICROALERT

## 1. Exención de Responsabilidad Médica y de Seguridad

- **No es un Dispositivo Médico:** MICROALERT es un software experimental de asistencia y prototipado tecnológico. No cuenta con certificación médica, no diagnostica trastornos del sueño (como apnea o narcolepsia) ni sustituye el criterio médico.
- **No Garantiza la Conducción Segura:** El uso de MICROALERT no garantiza que el usuario permanezca despierto ni previene accidentes durante la conducción u operaciones de maquinaria pesada. El usuario es el único responsable de detener el vehículo y descansar ante cualquier signo de fatiga.

---

## 2. Limitaciones del Hardware y Sensores

1. **Variabilidad de Sensores por Fabricante:** La disponibilidad y precisión de la frecuencia cardíaca, acelerómetro y giroscopio varía ampliamente entre modelos de smartwatches y versiones de Wear OS.
2. **Disponibilidad de Datos HRV:** La extracción de variabilidad cardíaca (SDNN y RMSSD) requiere el acceso a intervalos entre latidos (RR/IBI). En varios modelos de Wear OS estos datos no son expuestos por las APIs estándar o presentan artefactos por movimiento.
3. **Consumo de Batería:** El monitoreo continuo de sensores y servicios en primer plano incrementa el consumo energético del dispositivo portátil. Se recomienda utilizar el monitoreo solo en periodos específicos de interés.

---

## 3. Limitaciones del Modelo Machine Learning

- **Entrenamiento Sintético vs. Real:** Los modelos entrenados con datos sintéticos demuestran la viabilidad técnica del flujo de procesamiento, pero sus métricas no garantizan una precisión clínica real.
- **Condiciones Ambientales:** El modelo no detecta factores externos como iluminación, temperatura ambiente ni ruido auditivo.
