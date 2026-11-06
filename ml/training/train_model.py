#!/usr/bin/env python3
"""
Entrenamiento y Validación de Random Forest para MICROALERT
===========================================================
- 8 variables en orden estricto.
- Imputación por mediana basada en datos de entrenamiento.
- Validación cruzada agrupada por participante (GroupKFold) para evitar fuga de información.
- Evaluación exhaustiva: Accuracy, Precision, Recall, F1, Matriz de Confusión, Sensibilidad y Especificidad.
"""

import os
import json
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import GroupKFold
from sklearn.metrics import (
    accuracy_score, precision_score, recall_score, f1_score,
    confusion_matrix, classification_report
)
import joblib

FEATURE_NAMES = [
    "heartRate",
    "accelerometerMagnitude",
    "gyroscopeMagnitude",
    "inactivityDurationSeconds",
    "reactionTimeMs",
    "sessionDurationSeconds",
    "hrvSdnnMs",
    "hrvRmssdMs"
]

def train_and_validate():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    dataset_path = os.path.join(base_dir, "data", "synthetic_drowsiness_dataset.csv")

    if not os.path.exists(dataset_path):
        raise FileNotFoundError(f"No se encontró el dataset en {dataset_path}")

    df = pd.read_csv(dataset_path)
    print(f"Cargado dataset con {len(df)} registros y {df['participant_id'].nunique()} participantes.")

    # 1. Calcular medianas de entrenamiento para imputación
    medians = {}
    for col in FEATURE_NAMES:
        median_val = float(df[col].median(skipna=True))
        medians[col] = round(median_val, 4)

    medians_list = [medians[f] for f in FEATURE_NAMES]
    print(f"Medianas de imputación calculadas: {medians_list}")

    # Imputar valores faltantes
    X = df[FEATURE_NAMES].copy()
    for col in FEATURE_NAMES:
        X[col] = X[col].fillna(medians[col])

    X = X.values
    y = df["drowsy"].values
    groups = df["participant_id"].values

    # 2. Validación Cruzada con GroupKFold (5 splits por participante)
    gkf = GroupKFold(n_splits=5)
    oof_preds = np.zeros(len(y))
    oof_probs = np.zeros(len(y))

    rf_params = {
        "n_estimators": 25,
        "max_depth": 6,
        "min_samples_split": 5,
        "min_samples_leaf": 2,
        "random_state": 42
    }

    print("\n--- Ejecutando GroupKFold Cross-Validation ---")
    for fold, (train_idx, val_idx) in enumerate(gkf.split(X, y, groups=groups), 1):
        X_train, y_train = X[train_idx], y[train_idx]
        X_val, y_val = X[val_idx], y[val_idx]

        fold_rf = RandomForestClassifier(**rf_params)
        fold_rf.fit(X_train, y_train)

        probs = fold_rf.predict_proba(X_val)[:, 1]
        preds = fold_rf.predict(X_val)

        oof_probs[val_idx] = probs
        oof_preds[val_idx] = preds

        fold_acc = accuracy_score(y_val, preds)
        fold_f1 = f1_score(y_val, preds)
        print(f"Fold {fold}: Accuracy = {fold_acc:.4f}, F1 = {fold_f1:.4f}")

    # Métricas Globales Out-Of-Fold
    acc = accuracy_score(y, oof_preds)
    prec = precision_score(y, oof_preds)
    rec = recall_score(y, oof_preds)
    f1 = f1_score(y, oof_preds)
    cm = confusion_matrix(y, oof_preds)
    tn, fp, fn, tp = cm.ravel()
    sensitivity = tp / (tp + fn) if (tp + fn) > 0 else 0.0
    specificity = tn / (tn + fp) if (tn + fp) > 0 else 0.0

    print("\n================ RESULTADOS GLOBALES (OOF) ================")
    print(f"Accuracy:    {acc:.4f}")
    print(f"Precision:   {prec:.4f}")
    print(f"Recall:      {rec:.4f}")
    print(f"F1-Score:    {f1:.4f}")
    print(f"Sensibilidad:{sensitivity:.4f}")
    print(f"Especificidad:{specificity:.4f}")
    print(f"Matriz de Confusión:\n{cm}")
    print(f"TN: {tn}, FP: {fp}, FN: {fn}, TP: {tp}")

    metrics = {
        "dataset": "Sintético (synthetic_drowsiness_dataset.csv)",
        "samples_count": int(len(df)),
        "participants_count": int(df["participant_id"].nunique()),
        "rf_parameters": rf_params,
        "feature_names": FEATURE_NAMES,
        "feature_medians": medians_list,
        "metrics_oof": {
            "accuracy": round(float(acc), 4),
            "precision": round(float(prec), 4),
            "recall": round(float(rec), 4),
            "f1_score": round(float(f1), 4),
            "sensitivity": round(float(sensitivity), 4),
            "specificity": round(float(specificity), 4),
            "confusion_matrix": {
                "true_negative": int(tn),
                "false_positive": int(fp),
                "false_negative": int(fn),
                "true_positive": int(tp)
            }
        }
    }

    # 3. Entrenar modelo final con todo el dataset
    final_rf = RandomForestClassifier(**rf_params)
    final_rf.fit(X, y)

    # 4. Guardar artefactos
    output_dir = os.path.dirname(os.path.abspath(__file__))
    joblib_path = os.path.join(output_dir, "model.joblib")
    metrics_path = os.path.join(output_dir, "training_metrics.json")

    joblib.dump(final_rf, joblib_path)
    with open(metrics_path, "w", encoding="utf-8") as f:
        json.dump(metrics, f, indent=2)

    print(f"\nModelo final guardado en: {joblib_path}")
    print(f"Métricas guardadas en: {metrics_path}")

    return final_rf, metrics

if __name__ == "__main__":
    train_and_validate()
