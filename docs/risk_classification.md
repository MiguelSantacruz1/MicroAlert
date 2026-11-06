# Clasificación de Riesgo y Políticas de Decisión en MICROALERT

## 1. Definición de Niveles de Riesgo

MICROALERT clasifica el estado de somnolencia del usuario en cuatro niveles jerárquicos:

1. **NORMAL (Verde #4CAF50):** Probabilidad $< 0.35$.
   - Frecuencia cardíaca estable.
   - Movimiento motriz presente o variabilidad fisiológica coherente con vigilia activa.
   - Sin emisión de alertas ni vibración háptica.

2. **PRECAUCIÓN (Amarillo #FFEB3B):** $0.35 \le$ Probabilidad $< 0.60$.
   - Disminución leve en la motricidad o aumento gradual en variabilidad RR.
   - Indicador visual en pantalla. Alerta háptica suave con enfriamiento prolongado (90s).

3. **ALTO (Naranja #FF9800):** $0.60 \le$ Probabilidad $< 0.80$.
   - Patrón sostenido de inactividad física combinado con descenso o alteración cardíaca.
   - Requiere persistencia temporal de $N=2$ ventanas consecutivas para confirmación.
   - Notificación visual prominente y patrón de doble vibración háptica (enfriamiento 45s).

4. **CRÍTICO (Rojo #F44336):** Probabilidad $\ge 0.80$.
   - Evidencia multifactorial de microsueño inminente.
   - Vibración háptica intensa y continua en ráfagas triples hasta confirmación de usuario.
   - Enfriamiento estricto de 20s para re-evaluación inmediata.

---

## 2. Mecanismos de Estabilidad Temporal

### Persistencia Temporal ($N=2$ Ventanas)
Para evitar falsas alarmas provocadas por artefactos transitorios de movimiento o lectura puntual:
- El paso de un nivel a otro superior (`NORMAL` $\to$ `ALTO` o `CRÍTICO`) exige que **2 ventanas consecutivas de 30s** confirmen una probabilidad por encima del umbral.
- La primera ventana eleva el estado a `PRECAUCIÓN` provisional.

### Histéresis de Retorno ($\Delta = 0.05$)
Para descender de nivel de riesgo, la probabilidad debe caer por debajo del umbral de activación menos un margen delta:
$$\text{Umbral de descenso} = \text{Umbral de activación} - 0.05$$

Esto previene el fenómeno de "chattering" (oscilación rápida entre dos estados) en los límites de decisión.
