package com.ignacio.pokemonquizkotlin2.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository

class HomeViewModelFactory(
    private val app : Application,
    private val repository: PokemonRepository
) : ViewModelProvider.AndroidViewModelFactory(app) {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}