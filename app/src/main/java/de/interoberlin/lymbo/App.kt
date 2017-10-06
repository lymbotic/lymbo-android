package de.interoberlin.lymbo

import android.app.Application

class App : Application() {
    private object Holder {
        val INSTANCE = App()
    }

    companion object {
        val instance: App by lazy { Holder.INSTANCE }
    }
}
