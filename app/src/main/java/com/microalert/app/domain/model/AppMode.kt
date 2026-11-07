package com.microalert.app.domain.model

enum class AppMode(
    val title: String,
    val description: String
) {
    REAL(
        title = "REAL",
        description = "Monitoreo en tiempo real utilizando sensores físicos del smartwatch."
    ),
    DEMO(
        title = "DEMO",
        description = "Modo de simulación interactiva sin depender de sensores reales."
    )
}
