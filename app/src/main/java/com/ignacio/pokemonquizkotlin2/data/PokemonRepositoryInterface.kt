package com.ignacio.pokemonquizkotlin2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.db.GameRecord
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.RecordItem

interface PokemonRepositoryInterface {
    val _responseState: MutableLiveData<PokemonResponseState>
    val responseState: LiveData<PokemonResponseState>
    val pokemons: LiveData<List<Pokemon>>
    fun changeResponseState(responseState: PokemonResponseState)

    /**
     * Gets both flavor text for current language and version and also the pokemon's name, we do it together
     * in order to save in calls to the api.
     */
/*override suspend fun getFlavorTextAndNameFirstTime(pokid : Int, language: String, version : String,
                                                   newPokemonCallback: (versions: List<String>, flavorAndName : Pair<String,String>) -> Unit
) {
    withContext(dispatchers.io()) {
        Timber.i("doing getSpecieFlavor")
        print("doing getSpecieFlavor")

        val specieDetail = service.getSpecieDetail(pokid).await()
        val newVersionList = specieDetail.extractAvailableVersions(language)
        val flavorAndName = specieDetail.extractFlavorTextAndName(language,newVersionList.first())
        Timber.i("versions: homeStartup is true, sending versions to viewmodel")
        print("after homeStartup versions are ${newVersionList}")
        Timber.i("after homeStartup versions are ${newVersionList}")
        Timber.i("and flavor and name are $flavorAndName")
        Timber.i("AllversionsFlavors = ${specieDetail.printAllVersionFlavors(language)}")
        // get the real version list
        withContext(dispatchers.main()) {
           newPokemonCallback(newVersionList, flavorAndName)
        }
        //return@withContext Pair(newVersionList,flavorAndName)
    }
}*/

//val flavorMap = MutableLiveData<Map<String,String>>()

    suspend fun getFlavorTextAndNameFirstTimeReturns(pokid: Int, language: String)
            : Pair<Map<String, String>, String>

    suspend fun refreshPokemonPlay(offset: Int, limit: Int, callback: () -> Unit)

    suspend fun getNextRoundQuestionPokemon(): DatabasePokemon?

    suspend fun getNextRoundAnswers(id: Int, limit: Int): MutableList<String>

    suspend fun updateUsedAsQuestion(id: Int, value: Boolean)

    suspend fun resetUsedAsQuestionPlain()

    suspend fun deletePokemon(pokemon: DatabasePokemon)

    suspend fun deleteAllPokemon()

    /**
     * Fetching pokemons from database
     * @param name
     * @return
     */
    fun fetchPokemonsFromDb(name: String?): DataSource.Factory<Int, DatabasePokemon>

    /**
     * Search photos.
     */
    fun searchPokemons(
        name: String,
        boundaryCallback: PokemonBoundaryCallback
    )
            : LiveData<PagedList<DatabasePokemon>>

    //============================================
// PART FOR GAMERECORDVIEWMODEL
//============================================
    suspend fun saveRecord(gameRecord: GameRecord)

    fun getAllRecords(): LiveData<List<GameRecord>>

    suspend fun getAllRecordsPlain(): List<GameRecord>

    suspend fun deleteRecord(gameRecord: GameRecord)

    suspend fun deleteAllRecords()

    suspend fun getFixedListForAdapter(lastRecord: GameRecord): List<RecordItem>
}