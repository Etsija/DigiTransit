package com.etsija.digitransit.utils

import android.app.Application
import android.content.res.Resources

val prefs: Prefs by lazy {
    App.prefs!!
}

// Variables defined here can be used throughout the application.
// Resources and user preferences are good examples of the kind of info which should
// be referenced here.
class App: Application() {

    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
            private set
        lateinit var res: Resources
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        res = instance.resources
        prefs = Prefs(applicationContext)
    }
}