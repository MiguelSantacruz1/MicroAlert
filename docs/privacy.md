# Política de Privacidad y Tratamiento de Datos Fisiológicos – MICROALERT

## 1. Declaración de Privacidad

MICROALERT está diseñada bajo el principio de **Privacidad por Diseño (Privacy by Design)** y **Minimización de Datos**. Reconocemos que los datos recolectados por sensores de smartwatch (frecuencia cardíaca, movimiento, variabilidad cardíaca) son sensibles y pertenecen exclusivamente al usuario.

---

## 2. Principios Fundamentales de Datos

1. **Inferencia Local Exclusiva:** Todo el procesamiento de señales y la inferencia del modelo Machine Learning se realizan localmente en el dispositivo Wear OS.
2. **Sin Envío a Servidores Externos:** No se transmiten datos fisiológicos, registros de sesiones ni información personal a ningún servicio en la nube o servidor externo.
3. **Almacenamiento Local Controlado:** El historial de sesiones y eventos de alerta se almacena únicamente en la base de datos cifrada local Room dentro del almacenamiento privado de la aplicación.
4. **Derecho a la Eliminación de Datos:** El usuario puede borrar en cualquier momento todo el historial almacenado mediante la pantalla de Configuración.

---

## 3. Permisos Requeridos

- `BODY_SENSORS`: Requerido exclusivamente para acceder a la frecuencia cardíaca y datos de intervalos IBI durante las sesiones activas de monitoreo.
- `ACTIVITY_RECOGNITION`: Utilizado para la acelerometría y detección de periodos de inactividad.
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_HEALTH`: Requerido para mantener la ejecución transparente del monitoreo en segundo plano notificando activamente al usuario.
- `VIBRATE`: Utilizado únicamente para emitir alertas hápticas de somnolencia.

---

## 4. Uso de Datos Sintéticos

Cualquier conjunto de datos de prueba utilizado durante la etapa de desarrollo es de carácter puramente sintético y no proviene de individuos reales sin consentimiento previo en un protocolo de investigación aprobado.
