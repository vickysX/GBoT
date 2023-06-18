package com.example.gbot

import android.app.Application
import com.example.gbot.data.AppContainer
import com.example.gbot.data.GBoTAppContainer

class GBoTApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = GBoTAppContainer(this)
    }

}