package com.ignacio.pokemonquizkotlin2

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

const val PREFERENCE_FILE_NAME = "customPrefs.pref"

class MyApplication(private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : Application() {
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }

    private val applicationScope = CoroutineScope(dispatchers.default())
    fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            sharedPreferences = getSharedPreferences(
                PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

}