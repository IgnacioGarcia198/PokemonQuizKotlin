package com.ignacio.pokemonquizkotlin2.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ignacio.pokemonquizkotlin2.data.api.*
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.db.*
import com.ignacio.pokemonquizkotlin2.utils.DefaultDispatcherProvider
import com.ignacio.pokemonquizkotlin2.utils.DispatcherProvider
import kotlinx.coroutines.withContext
import timber.log.Timber

interface PokemonRepositoryContract {

    //private val _responseState = MutableLiveData<PokemonResponseState>(PokemonResponseState.DONE)
    val responseState : LiveData<PokemonResponseState>
    fun changeResponseState(responseState: PokemonResponseState)

    /**
     * Gets both flavor text for current language and version and also the pokemon's name, we do it together
     * in order to save in calls to the api.
     */
    suspend fun getFlavorTextAndNameFirstTime(pokid : Int, language: String = "en", version : String = "red",
                                              newPokemonCallback: (versions: List<String>, flavorAndName : Pair<String,String>) -> Unit)


    suspend fun getFlavorTextNormally(pokid : Int, language: String = "en", version : String = "red",
                                      normalCallback : (flavorAndName : String) -> Unit)





    //=======================================================
    // PART FOR PLAYVIEWMODEL
    //==========================================================

    suspend fun refreshPokemonPlay(offset : Int = 0, limit: Int = -1, callback: ()->Unit = {})

    suspend fun getNextRoundQuestionPokemon() : DatabasePokemon?

    suspend fun getNextRoundAnswers(id : Int, limit : Int) : MutableList<String>

    suspend fun updateUsedAsQuestion(id : Int, value : Boolean)

    suspend fun resetUsedAsQuestionPlain()


    //============================================================
    // PART FOR POKEMONLISTVIEWMODEL
    //============================================================

    /**
     * Fetching pokemons from database
     * @param name
     * @return
     */
    fun fetchPokemonsFromDb(name: String?): DataSource.Factory<Int, DatabasePokemon>


    /**
     * Search photos.
     */
    fun searchPokemons(name: String): LiveData<PagedList<DatabasePokemon>>

    //============================================
    // PART FOR GAMERECORDVIEWMODEL
    //============================================
    suspend fun saveRecord(gameRecord: GameRecord)

    fun getAllRecords() : LiveData<List<GameRecord>>
}