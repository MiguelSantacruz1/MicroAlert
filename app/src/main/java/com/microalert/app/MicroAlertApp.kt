package com.microalert.app

import android.app.Application
import com.microalert.app.di.AppContainer

class MicroAlertApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
