package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.app.Application
import androidx.lifecycle.*
import com.ignacio.pokemonquizkotlin2.MyApplication
import com.ignacio.pokemonquizkotlin2.data.PokemonBoundaryCallback
import com.ignacio.pokemonquizkotlin2.data.PokemonRepository
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.data.ServiceLocator
import com.ignacio.pokemonquizkotlin2.db.getDatabase
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import timber.log.Timber

class PokemonListViewModel(
    app:Application,
    repository: PokemonRepositoryInterface = (app as MyApplication).repository
    ) : BaseViewModel(app,repository) {
    //lateinit var pokemonList : LiveData<PagedList<DatabasePokemon>>

    private val _searchText = MutableLiveData<String>("")
    //val searchText : LiveData<String> = _searchText

    fun changeText(newText : String) {
        _searchText.value = newText
        Timber.i("Text is $newText")
    }

    val pokemonList = Transformations.switchMap(_searchText) {repository.searchPokemons(it,boundaryCallback = PokemonBoundaryCallback(repository,dispatchers))}

}