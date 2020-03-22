package com.ignacio.pokemonquizkotlin2.data

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ignacio.pokemonquizkotlin2.data.api.*
import com.ignacio.pokemonquizkotlin2.data.model.Pokemon
import com.ignacio.pokemonquizkotlin2.db.*
import com.ignacio.pokemonquizkotlin2.testing.OpenForTesting
import com.ignacio.pokemonquizkotlin2.ui.gamerecords.RecordItem
import com.ignacio.pokemonquizkotlin2.utils.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.Delayed
import javax.inject.Inject

//==============================================================================================

//@OpenForTesting
class PokemonRepository @Inject constructor(
    private val database: MyDatabase,
    private val service: PokemonService = PokemonNetwork.pokemonApiService,
    private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()
) :
    PokemonRepositoryInterface {
    //=======================================================
    // PART FOR HOMEVIEWMODEL
    //==========================================================

    override val _responseState = MutableLiveData<PokemonResponseState>(PokemonResponseState.DONE)
    override val responseState : LiveData<PokemonResponseState> = _responseState
    override fun changeResponseState(responseState: PokemonResponseState) {
        _responseState.postValue(responseState)
    }
    companion object {
        const val DATABASE_PAGE_SIZE = 20
        /*@Volatile
        private var defaultRepository : PokemonRepository? = null
        fun getDefaultRepository(context : Context) : PokemonRepository {
            if(defaultRepository == null) {
                defaultRepository = PokemonRepository(getDatabase(context))
            }
            return defaultRepository!!
        }*/
    }

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

    override suspend fun getFlavorTextAndNameFirstTimeReturns(pokid : Int, language: String)
    : Pair<Map<String, String>, String> {
        return withContext(dispatchers.io()) {
            wrapEspressoIdlingResource {
                writeLine()
                Timber.i("getflavortextandname called")
                Timber.i("doing getSpecieFlavor")
                print("doing getSpecieFlavor")

                val specieDetail = service.getSpecieDetail(pokid).await()
                val flavors = specieDetail.extractAllVersionsAndFlavors(language)
                val name = specieDetail.extractName(language)
                Pair(flavors,name)
            }
        }
    }


    //=======================================================
    // PART FOR PLAYVIEWMODEL
    //==========================================================

    override suspend fun refreshPokemonPlay(offset : Int, limit: Int, callback: ()->Unit) {
        Timber.i("refreshpokemon is called")
        withContext(dispatchers.io()) {
            val pokemonContainer = service.getPokemonList(offset,limit).await()
            Timber.e("==== pokemoncontainer: $pokemonContainer")
            database.pokemonDao.insertAll(pokemonContainer.asDatabaseModel(offset,limit))
            withContext(dispatchers.main()) {
                callback()
            }
        }
    }

    override val pokemons : LiveData<List<Pokemon>> =
        Transformations.map(database.pokemonDao.getAllPokemon()) {it.asDomainModel()}

    override suspend fun getNextRoundQuestionPokemon() : DatabasePokemon? {
        return withContext(dispatchers.io()) {
            database.pokemonDao.getNextRoundQuestionPokemon()
        }

    }

    override suspend fun getNextRoundAnswers(id : Int, limit : Int) : MutableList<String> {
        return withContext(dispatchers.io()) {
            database.pokemonDao.getNextRoundAnswerPokemonNames(id, limit)
        }
    }

    override suspend fun updateUsedAsQuestion(id : Int, value : Boolean) {
        withContext(dispatchers.io()) {
            database.pokemonDao.updateUsedAsQuestion(id,value)
        }
    }

    override suspend fun resetUsedAsQuestionPlain() {
        withContext(dispatchers.io()) {
            database.pokemonDao.resetUsedAsQuestion()
        }
    }

    override suspend fun deletePokemon(pokemon: DatabasePokemon) {
        withContext(dispatchers.io()) {
            database.pokemonDao.delete(pokemon)
        }
    }

    override suspend fun deleteAllPokemon() {
        withContext(dispatchers.io()) {
            database.pokemonDao.deleteAllPokemon()
        }
    }


    //============================================================
    // PART FOR POKEMONLISTVIEWMODEL
    //============================================================

    /**
     * Fetching pokemons from database
     * @param name
     * @return
     */
    override fun fetchPokemonsFromDb(name: String?): DataSource.Factory<Int, DatabasePokemon> {

        return if (name == null || name == "") {
            // get by id
            database.pokemonDao.getAllPokemonsFromRoom()
        } else {
            // get by name and id
            database.pokemonDao.findPokemonsByNameInRoom("%$name%")
        }

    }

    /**
     * Search photos.
     */
    override fun searchPokemons(
        name: String,
        boundaryCallback: PokemonBoundaryCallback
    )
            : LiveData<PagedList<DatabasePokemon>> {
        Timber.i("search: New query: name: $name")

        // Get data source factory from the local cache
        val pokemonsResult : DataSource.Factory<Int, DatabasePokemon> = fetchPokemonsFromDb(name)

        // Construct the boundary callback
        //val boundaryCallback = PokemonBoundaryCallback(name, this)
        //networkErrors = boundaryCallback.getResponseState()

        // Set the Page size for the Paged list
        val pagedConfig = PagedList.Config.Builder()
            .setPageSize(DATABASE_PAGE_SIZE)
            .setInitialLoadSizeHint(DATABASE_PAGE_SIZE * 3)
            .setEnablePlaceholders(true)
            .build()

        // Get the Live Paged list
        val data = LivePagedListBuilder(pokemonsResult, pagedConfig)
            .setBoundaryCallback(boundaryCallback)
            .build()

        // Get the Search result with the network errors exposed by the boundary callback
        return data
    }

    //============================================
    // PART FOR GAMERECORDVIEWMODEL
    //============================================
    override suspend fun saveRecord(gameRecord: GameRecord) {
        withContext(dispatchers.io()) {
            database.gameRecordDao.save(gameRecord)
        }
    }

    override fun getAllRecords() : LiveData<List<GameRecord>> {
        return database.gameRecordDao.allGameRecordsLiveData
    }

    override suspend fun getAllRecordsPlain() : List<GameRecord> {
        return withContext(dispatchers.io()) {
            database.gameRecordDao.allGameRecords
        }
    }

    override suspend fun deleteRecord(gameRecord: GameRecord) {
        withContext(dispatchers.io()) {
            database.gameRecordDao.delete(gameRecord)
        }
    }

    override suspend fun deleteAllRecords() {
        withContext(dispatchers.io()) {
            database.gameRecordDao.deleteAllGameRecords()
        }
    }

    override suspend fun getFixedListForAdapter(lastRecord : GameRecord) : List<RecordItem> {
        val outputList : MutableList<RecordItem> = mutableListOf(RecordItem.Header)
        return withContext(dispatchers.io()) {
            val allRecords = getAllRecordsPlain()


            val averagesRow = GameRecord(questionsPerSecond = database.gameRecordDao.averageSpeed.roundTo(3),
                hitRate = database.gameRecordDao.averageHitRate.roundTo(3))
            outputList.add(RecordItem.GameRecordItem(lastRecord))
            outputList.add(RecordItem.GameRecordItem(averagesRow))
            outputList.addAll(allRecords.map { RecordItem.GameRecordItem(it) })
            //if(list.isNotEmpty()) {

            //}
            outputList
        }
    }



}