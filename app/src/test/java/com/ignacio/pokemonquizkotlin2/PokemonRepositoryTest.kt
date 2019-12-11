package com.ignacio.pokemonquizkotlin2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.db.DatabasePokemon
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

class PokemonRepositoryTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private lateinit var repository: PokemonRepository
    private val mockDatabasePokemon : DatabasePokemon =

    @Before
    fun createRepository() {

    }
}