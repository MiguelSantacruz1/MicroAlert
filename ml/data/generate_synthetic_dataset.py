#!/usr/bin/env python3
"""
Generador de Dataset Sintético para MICROALERT
==============================================
ADVERTENCIA OBLIGATORIA:
Este dataset es 100% SINTÉTICO y se genera exclusivamente con fines de
verificación técnica del pipeline de Machine Learning (entrenamiento,
exportación a JSON, inferencia en Kotlin y pruebas de equivalencia).
NO debe utilizarse para certificar precisión médica ni eficacia clínica.
"""

import numpy as np
import pandas as pd
import os

def generate_synthetic_data(num_participants=20, sessions_per_participant=2, windows_per_session=60, random_seed=42):
    np.random.seed(random_seed)

    records = []

    for p in range(1, num_participants + 1):
        p_id = f"P_{p:03d}"
        # Variabilidad fisiológica inter-sujeto
        base_hr = np.random.normal(73.0, 5.0)
        base_reaction = np.random.normal(320.0, 30.0)

        for s in range(1, sessions_per_participant + 1):
            s_id = f"{p_id}_S{s:02d}"

            # Simular progresión en el tiempo (inicio alerta, transición a fatiga en fases avanzadas)
            drowsiness_onset_window = np.random.randint(25, 45)

            for w in range(windows_per_session):
                session_duration_sec = float((w + 1) * 30.0) # Paso de 30s
                is_drowsy = 1 if w >= drowsiness_onset_window else 0

                if is_drowsy:
                    # Perfil somnoliento: HR desciende, alta inactividad, reacción lenta, HRV elevada (parasimpático)
                    hr = np.random.normal(base_hr - 8.0, 3.5)
                    accel_mag = 9.81 + np.random.normal(0.0, 0.08)
                    gyro_mag = np.clip(np.random.exponential(0.02), 0.005, 0.08)
                    inactivity_sec = np.clip(np.random.normal(48.0, 6.0), 20.0, 60.0)
                    reaction_ms = np.clip(np.random.normal(base_reaction + 180.0, 60.0), 380.0, 950.0)
                    sdnn_ms = np.clip(np.random.normal(68.0, 10.0), 35.0, 110.0)
                    rmssd_ms = np.clip(np.random.normal(62.0, 9.0), 30.0, 100.0)
                else:
                    # Perfil alerta: HR activo, movimiento regular, reacción rápida, inactividad baja
                    hr = np.random.normal(base_hr + 4.0, 4.5)
                    accel_mag = 9.81 + np.random.normal(0.0, 0.35)
                    gyro_mag = np.clip(np.random.exponential(0.12), 0.02, 0.45)
                    inactivity_sec = np.clip(np.random.normal(12.0, 5.0), 0.0, 25.0)
                    reaction_ms = np.clip(np.random.normal(base_reaction, 35.0), 220.0, 410.0)
                    sdnn_ms = np.clip(np.random.normal(42.0, 8.0), 20.0, 65.0)
                    rmssd_ms = np.clip(np.random.normal(38.0, 7.0), 18.0, 60.0)

                # Simulación de disponibilidad realista de sensores:
                # La HRV falta en ~50% de las ventanas por limitaciones de hardware Wear OS
                hrv_available = np.random.rand() > 0.50
                if not hrv_available:
                    sdnn_ms = np.nan
                    rmssd_ms = np.nan

                # La prueba de reacción solo se realiza ocasionalmente (~25% de ventanas)
                reaction_available = np.random.rand() > 0.75
                if not reaction_available:
                    reaction_ms = np.nan

                records.append({
                    "participant_id": p_id,
                    "session_id": s_id,
                    "window_index": w,
                    "heartRate": round(float(hr), 2),
                    "accelerometerMagnitude": round(float(accel_mag), 3),
                    "gyroscopeMagnitude": round(float(gyro_mag), 4),
                    "inactivityDurationSeconds": round(float(inactivity_sec), 1),
                    "reactionTimeMs": round(float(reaction_ms), 1) if not np.isnan(reaction_ms) else np.nan,
                    "sessionDurationSeconds": round(float(session_duration_sec), 1),
                    "hrvSdnnMs": round(float(sdnn_ms), 2) if not np.isnan(sdnn_ms) else np.nan,
                    "hrvRmssdMs": round(float(rmssd_ms), 2) if not np.isnan(rmssd_ms) else np.nan,
                    "drowsy": is_drowsy
                })

    df = pd.DataFrame(records)
    return df

if __name__ == "__main__":
    output_dir = os.path.dirname(os.path.abspath(__file__))
    output_path = os.path.join(output_dir, "synthetic_drowsiness_dataset.csv")

    df = generate_synthetic_data(num_participants=25, sessions_per_participant=2, windows_per_session=60)
    df.to_csv(output_path, index=False)
    print(f"Dataset sintético generado exitosamente: {output_path}")
    print(f"Total registros: {len(df)} | Participantes: {df['participant_id'].nunique()} | Balance clases: {df['drowsy'].value_counts().to_dict()}")
