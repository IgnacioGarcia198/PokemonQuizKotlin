package com.ignacio.pokemonquizkotlin2.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.ignacio.pokemonquizkotlin2.data.api.*
import com.ignacio.pokemonquizkotlin2.data.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.data.db.PokemonDatabase
import com.ignacio.pokemonquizkotlin2.data.db.asDomainModel
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class PokemonRepository (private val database: PokemonDatabase) {
    //=======================================================
    // PART FOR HOMEVIEWMODEL
    //==========================================================

    var startup = true


    suspend fun getSpecieFlavorText(pokid : Int, language: String = "en", version : String = "red",
                                    callback: (versions: List<String>) -> Unit = {}) {
        withContext(Dispatchers.IO) {
            Timber.i("doing getSpecieFlavor")
            val specieDetail = PokemonNetwork.pokemonApiService.getSpecieDetail(pokid).await()
            val newVersionList = specieDetail.extractAvailableVersions(language)
            if(startup) {
                Timber.i("versions: startup is true, sending versions to viewmodel")
                Timber.i("after startup versions are ${newVersionList}")
                // get the real version list
                withContext(Dispatchers.Main) {
                    callback(specieDetail.extractAvailableVersions(language))
                }
                _flavorTextAndName.postValue(specieDetail.extractFlavorText(language,newVersionList.first()))
                //_versionList.value = newVesionList
                startup = false
            }
            else {
                _flavorTextAndName.postValue(specieDetail.extractFlavorText(language, version))
            }
        }
    }

    suspend fun getVersionList(callback: (versions : List<String>) -> Unit) {
        withContext(Dispatchers.IO) {
            val versionsContainer = PokemonNetwork.pokemonApiService.getVersionList(0,-1).await()
            Timber.i("Versions are ${versionsContainer.extractVersionList()}")

            withContext(Dispatchers.Main) {
                callback(versionsContainer.extractVersionList())
            }
        }
    }

    /*private val _versionList = MutableLiveData<List<String>>()
    val versionList : LiveData<List<String>>
    get() = _versionList*/

    private val _flavorTextAndName = MutableLiveData<Pair<String,String>>()
    val flavorTextAndName : LiveData<Pair<String,String>>
    get() = _flavorTextAndName

    //=======================================================
    // PART FOR PLAYVIEWMODEL
    //==========================================================

    suspend fun refreshPokemon(callback: ()->Unit = {}) {
        withContext(Dispatchers.IO) {
            val pokemonContainer = PokemonNetwork.pokemonApiService.getPokemonList(0,-1).await()
            database.pokemonDao.insertAll(pokemonContainer.asDatabaseModel())
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    val pokemons : LiveData<List<Pokemon>> =
        Transformations.map(database.pokemonDao.getAllPokemon()) {it.asDomainModel()}

    suspend fun getNextRoundQuestionPokemon() : Deferred<DatabasePokemon> {
        return withContext(Dispatchers.IO) {
            async { database.pokemonDao.getNextRoundQuestionPokemon() }
        }

    }

    suspend fun getNextRoundAnswers(id : Int, limit : Int) : Deferred<MutableList<String>> {
        return withContext(Dispatchers.IO) {
            async { database.pokemonDao.getNextRoundAnswerPokemonNames(id, limit) }
        }
    }

}