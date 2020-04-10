package com.ignacio.pokemonquizkotlin2

import android.app.Application
import com.ignacio.pokemonquizkotlin2.di.AppInjector
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@OpenForTesting
class MyApplication(dispatchers: DispatcherProvider = DefaultDispatcherProvider()) : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    //val repository : PokemonRepositoryInterface
    //get() = ServiceLocator.provideRepository(this)

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            delayedInit()
        }
        AppInjector.init(this)
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