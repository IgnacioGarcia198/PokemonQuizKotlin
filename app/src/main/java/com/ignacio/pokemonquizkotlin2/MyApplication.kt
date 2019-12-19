package com.ignacio.pokemonquizkotlin2

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


@OpenForTesting
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
                ServiceLocator.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

}