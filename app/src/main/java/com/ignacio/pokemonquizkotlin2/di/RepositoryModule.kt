package com.ignacio.pokemonquizkotlin2.di

import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    // Makes Dagger provide PokemonRepository when a PokemonRepositoryInterface type is requested
    @Binds
    abstract fun provideRepository(repository: PokemonRepository): PokemonRepositoryInterface
}