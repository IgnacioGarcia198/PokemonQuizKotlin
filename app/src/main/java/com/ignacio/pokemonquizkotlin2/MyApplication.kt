package com.ignacio.pokemonquizkotlin2

import android.app.Application
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
@OpenForTesting
class MyApplication(dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            delayedInit()
        }
    }

    private val applicationScope = CoroutineScope(dispatchers.default())
    fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            //sharedPreferences = getSharedPreferences(
            //   ServiceLocator.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        }
    }
}
