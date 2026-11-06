#!/usr/bin/env python3
"""
Generador de Vectores de Prueba para Equivalencia Python - Kotlin
================================================================
Genera un conjunto representativo de vectores de entrada (claros, límites,
con valores faltantes imputados) y registra las probabilidades calculadas por
scikit-learn para verificar que el evaluador nativo en Kotlin devuelva
exactamente los mismos resultados con tolerancia < 1e-6.
"""

import os
import json
import joblib
import numpy as np

def evaluate_json_trees(trees, vector):
    tree_probs = []
    for tree_nodes in trees:
        node = tree_nodes[0]
        while node["left_child"] != -1 and node["right_child"] != -1:
            feat_idx = node["feature_index"]
            val = vector[feat_idx]
            if val <= node["threshold"]:
                node = tree_nodes[node["left_child"]]
            else:
                node = tree_nodes[node["right_child"]]
        tree_probs.append(node["value"])
    return float(np.mean(tree_probs))

def generate_test_vectors():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    model_path = os.path.join(base_dir, "training", "model.joblib")
    json_model_path = os.path.join(base_dir, "export", "model.json")

    model = joblib.load(model_path)
    with open(json_model_path, "r", encoding="utf-8") as f:
        json_model = json.load(f)

    medians = json_model["feature_medians"]
    trees = json_model["trees"]

    # Definir vectores de prueba cubriendo distintos escenarios
    scenarios = [
        {
            "id": "vec_01_clear_alert",
            "description": "Patrón claro de alerta: HR alto, movimiento continuo, reacción rápida, baja inactividad",
            "vector": [82.0, 10.20, 0.25, 5.0, 260.0, 120.0, 36.0, 32.0]
        },
        {
            "id": "vec_02_clear_drowsy",
            "description": "Patrón claro de somnolencia: HR bajo, alta inactividad, reacción lenta, sesión avanzada",
            "vector": [58.0, 9.81, 0.01, 52.0, 680.0, 1500.0, 75.0, 68.0]
        },
        {
            "id": "vec_03_borderline_precaution",
            "description": "Patrón intermedio / umbral de precaución: parámetros en zona de transición",
            "vector": [66.0, 9.85, 0.04, 25.0, 380.0, 600.0, 48.0, 44.0]
        },
        {
            "id": "vec_04_missing_hrv_imputed",
            "description": "HRV no disponible (SDNN y RMSSD imputados con medianas)",
            "vector": [74.0, 9.90, 0.08, 12.0, 310.0, 300.0, medians[6], medians[7]]
        },
        {
            "id": "vec_05_missing_reaction_imputed",
            "description": "Tiempo de reacción no disponible (imputado con mediana)",
            "vector": [62.0, 9.82, 0.02, 45.0, medians[4], 900.0, 65.0, 58.0]
        },
        {
            "id": "vec_06_high_inactivity_drowsy",
            "description": "Inactividad extrema sostenida con descenso cardíaco",
            "vector": [56.0, 9.81, 0.008, 58.0, 720.0, 1800.0, 80.0, 72.0]
        },
        {
            "id": "vec_07_all_medians_baseline",
            "description": "Vector correspondiente exactamente a las medianas base",
            "vector": list(medians)
        }
    ]

    test_cases = []
    print("\n--- Evaluando Vectores de Prueba con scikit-learn y JSON Trees ---")
    for sc in scenarios:
        vec_arr = np.array(sc["vector"]).reshape(1, -1)
        sklearn_prob = float(model.predict_proba(vec_arr)[0][1])
        json_prob = evaluate_json_trees(trees, sc["vector"])

        diff = abs(sklearn_prob - json_prob)
        print(f"[{sc['id']}] Sklearn: {sklearn_prob:.6f} | JSON: {json_prob:.6f} | Diff: {diff:.2e}")
        assert diff < 1e-6, f"Discrepancia en {sc['id']}: {diff}"

        test_cases.append({
            "id": sc["id"],
            "description": sc["description"],
            "features": sc["vector"],
            "expected_probability": round(sklearn_prob, 6),
            "expected_drowsy_prediction": int(sklearn_prob >= 0.5)
        })

    payload = {
        "version": "1.0.0",
        "description": "Vectores de prueba de referencia para equivalencia Python-Kotlin",
        "tolerance": 1e-6,
        "feature_order": json_model["feature_names"],
        "test_cases": test_cases
    }

    # Guardar en ml/ y en app/src/main/assets/
    ml_output = os.path.join(base_dir, "test_vectors.json")
    assets_output = os.path.join(base_dir, "..", "app", "src", "main", "assets", "test_vectors.json")

    with open(ml_output, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)
    with open(assets_output, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2)

    print(f"\nVectores de prueba guardados en:")
    print(f"  - {ml_output}")
    print(f"  - {assets_output}")

if __name__ == "__main__":
    generate_test_vectors()
