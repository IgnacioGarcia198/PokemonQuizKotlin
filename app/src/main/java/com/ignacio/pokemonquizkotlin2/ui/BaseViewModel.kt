package com.ignacio.pokemonquizkotlin2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ignacio.pokemonquizkotlin2.data.PokemonResponseState

open class BaseViewModel(val app : Application) : AndroidViewModel(app) {
    protected val _responseState = MutableLiveData<PokemonResponseState>()
    val responseState : LiveData<PokemonResponseState>
        get() = _responseState

    fun changeResponseState(newState : PokemonResponseState) {
        _responseState.value = newState
    }
}