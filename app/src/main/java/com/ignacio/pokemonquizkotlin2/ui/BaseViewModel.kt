package com.ignacio.pokemonquizkotlin2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState
import com.ignacio.pokemonquizkotlin2.data.db.getDatabase

open class BaseViewModel(val app : Application) : AndroidViewModel(app) {
    val repository = PokemonRepository(getDatabase(app))
    fun getResponseState() : LiveData<PokemonResponseState> {
        return repository.responseState
    }
}