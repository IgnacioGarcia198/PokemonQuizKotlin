package com.ignacio.pokemonquizkotlin2

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)
    fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
        }
    }
}