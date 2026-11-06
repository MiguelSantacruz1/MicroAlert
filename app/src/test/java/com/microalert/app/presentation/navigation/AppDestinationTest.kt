package com.microalert.app.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppDestinationTest {

    @Test
    fun `destinations should have matching routes and titles`() {
        assertEquals(Screen.HOME, AppDestination.Home.route)
        assertEquals("Inicio", AppDestination.Home.title)
        assertEquals(Screen.MONITORING, AppDestination.Monitoring.route)
        assertEquals(Screen.ALERT, AppDestination.Alert.route)
    }
}
