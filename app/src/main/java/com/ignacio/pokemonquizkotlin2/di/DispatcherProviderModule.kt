package com.ignacio.pokemonquizkotlin2.di

import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import dagger.Binds
import dagger.Module

@Module
abstract class DispatcherProviderModule {
    // Makes Dagger provide PokemonRepository when a PokemonRepositoryInterface type is requested
    @Binds
    abstract fun provideDispatcherProvider(dispatcherProvider : DefaultDispatcherProvider): DispatcherProvider
}