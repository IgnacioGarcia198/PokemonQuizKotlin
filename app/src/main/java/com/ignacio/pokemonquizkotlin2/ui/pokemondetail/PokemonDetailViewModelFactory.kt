package com.ignacio.pokemonquizkotlin2.ui.pokemondetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PokemonDetailViewModelFactory(
    private val app : Application,
    private val id : Int
) : ViewModelProvider.AndroidViewModelFactory(app) {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PokemonDetailViewModel::class.java)) {
            return PokemonDetailViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}