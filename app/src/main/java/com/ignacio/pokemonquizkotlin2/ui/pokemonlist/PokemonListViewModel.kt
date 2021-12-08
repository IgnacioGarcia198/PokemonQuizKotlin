package com.ignacio.pokemonquizkotlin2.ui.pokemonlist

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ignacio.pokemonquizkotlin2.data.PokemonBoundaryCallback
import com.ignacio.pokemonquizkotlin2.data.PokemonRepositoryInterface
import com.ignacio.pokemonquizkotlin2.ui.BaseViewModel
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    app:Application,
    repository: PokemonRepositoryInterface,
    dispatchers : DispatcherProvider,
    sharedPref : SharedPreferences
    ) : BaseViewModel(app,repository) {
    //lateinit var pokemonList : LiveData<PagedList<DatabasePokemon>>

    private val _searchText = MutableLiveData<String>("")
    //val searchText : LiveData<String> = _searchText

    fun changeText(newText : String) {
        _searchText.value = newText
        Timber.i("Text is $newText")
    }

    val pokemonList = Transformations.switchMap(_searchText) {repository.searchPokemons(it,boundaryCallback = PokemonBoundaryCallback(repository,dispatchers,sharedPref))}

}