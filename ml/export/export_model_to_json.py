#!/usr/bin/env python3
"""
Exportador de Modelo Random Forest a JSON Nativo para MICROALERT
==============================================================
Serializa el bosque de árboles entrenado con scikit-learn a un formato JSON
ligero que puede ser deserializado y ejecutado directamente en Kotlin
dentro del smartwatch sin librerías pesadas.
"""

import os
import json
from datetime import datetime, timezone
import joblib
import numpy as np

def tree_to_dict_list(tree):
    nodes = []
    n_nodes = tree.node_count

    for i in range(n_nodes):
        is_leaf = tree.children_left[i] == -1 and tree.children_right[i] == -1

        if is_leaf:
            # Calcular probabilidad de la clase positiva (drowsy = 1)
            counts = tree.value[i][0]
            prob_drowsy = float(counts[1] / np.sum(counts)) if np.sum(counts) > 0 else 0.0

            nodes.append({
                "node_id": int(i),
                "feature_index": -1,
                "threshold": None,
                "left_child": -1,
                "right_child": -1,
                "value": round(prob_drowsy, 6)
            })
        else:
            nodes.append({
                "node_id": int(i),
                "feature_index": int(tree.feature[i]),
                "threshold": round(float(tree.threshold[i]), 6),
                "left_child": int(tree.children_left[i]),
                "right_child": int(tree.children_right[i]),
                "value": None
            })
    return nodes

def export_model():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    model_path = os.path.join(base_dir, "training", "model.joblib")
    metrics_path = os.path.join(base_dir, "training", "training_metrics.json")

    if not os.path.exists(model_path):
        raise FileNotFoundError(f"No se encontró el modelo entrenado en {model_path}")

    model = joblib.load(model_path)
    with open(metrics_path, "r", encoding="utf-8") as f:
        metrics = json.load(f)

    trees_json = []
    for estimator in model.estimators_:
        tree_nodes = tree_to_dict_list(estimator.tree_)
        trees_json.append(tree_nodes)

    created_at = datetime.now(timezone.utc).isoformat()

    model_data = {
        "version": "1.0.0",
        "created_at": created_at,
        "model_type": "RandomForestClassifier",
        "n_estimators": len(model.estimators_),
        "feature_names": metrics["feature_names"],
        "feature_medians": metrics["feature_medians"],
        "trees": trees_json
    }

    metadata_data = {
        "version": "1.0.0",
        "created_at": created_at,
        "feature_names": metrics["feature_names"],
        "feature_medians": metrics["feature_medians"],
        "rf_parameters": metrics["rf_parameters"],
        "metrics_summary": metrics["metrics_oof"],
        "equivalence_tolerance": 1e-6
    }

    # Guardar en ml/export y en app/src/main/assets
    export_dir = os.path.dirname(os.path.abspath(__file__))
    assets_dir = os.path.join(base_dir, "..", "app", "src", "main", "assets")
    os.makedirs(assets_dir, exist_ok=True)

    # 1. model.json en export y assets
    model_export_path = os.path.join(export_dir, "model.json")
    model_assets_path = os.path.join(assets_dir, "model.json")
    with open(model_export_path, "w", encoding="utf-8") as f:
        json.dump(model_data, f, indent=2)
    with open(model_assets_path, "w", encoding="utf-8") as f:
        json.dump(model_data, f, indent=2)

    # 2. metadata.json en export y assets
    meta_export_path = os.path.join(export_dir, "metadata.json")
    meta_assets_path = os.path.join(assets_dir, "metadata.json")
    with open(meta_export_path, "w", encoding="utf-8") as f:
        json.dump(metadata_data, f, indent=2)
    with open(meta_assets_path, "w", encoding="utf-8") as f:
        json.dump(metadata_data, f, indent=2)

    print(f"Modelo JSON exportado a:")
    print(f"  - {model_export_path}")
    print(f"  - {model_assets_path}")
    print(f"Metadatos JSON exportados a:")
    print(f"  - {meta_export_path}")
    print(f"  - {meta_assets_path}")
    print(f"Total árboles exportados: {len(trees_json)}")

if __name__ == "__main__":
    export_model()
