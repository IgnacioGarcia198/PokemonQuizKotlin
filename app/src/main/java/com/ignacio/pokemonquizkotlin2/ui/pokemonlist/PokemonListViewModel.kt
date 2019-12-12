package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import timber.log.Timber

class PokemonListViewModel(app:Application) : BaseViewModel(app) {
    //lateinit var pokemonList : LiveData<PagedList<DatabasePokemon>>

    private val _searchText = MutableLiveData<String>("")
    //val searchText : LiveData<String> = _searchText

    fun changeText(newText : String) {
        _searchText.value = newText
        Timber.i("Text is $newText")
    }

    val pokemonList = Transformations.switchMap(_searchText) {repository.searchPokemons(it)}

}