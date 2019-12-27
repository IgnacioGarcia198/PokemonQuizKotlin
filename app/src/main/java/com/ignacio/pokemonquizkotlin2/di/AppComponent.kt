package com.ignacio.pokemonquizkotlin2.di

import android.app.Application
import android.content.SharedPreferences
import com.ignacio.pokemonquizkotlin2.MyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        RepositoryModule::class,
        DispatcherProviderModule::class,
        MainActivityModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(myApp: MyApplication)
    fun sharedPreferences() : SharedPreferences
}