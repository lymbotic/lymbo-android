package de.interoberlin.lymbo

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson

class App : Application() {
    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}
