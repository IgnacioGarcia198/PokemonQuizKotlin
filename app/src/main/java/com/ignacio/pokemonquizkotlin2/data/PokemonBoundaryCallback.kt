package com.ignacio.pokemonquizkotlin2.data

import androidx.paging.PagedList

import com.ignacio.pokemonquizkotlin2.db.DatabasePokemon
import com.ignacio.pokemonquizkotlin2.ui.home.HomeViewModel
import com.ignacio.pokemonquizkotlin2.utils.*
import com.ignacio.pokemonquizkotlin2.utils.sharedPreferences
import com.ignacio.pokemonquizkotlin2.OpenClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

import java.util.Calendar

/**
 * PagedList.BoundaryCallback class to know when to trigger the Network request for more data
 */
@OpenClass
class PokemonBoundaryCallback
//private int dbupdated;

internal constructor(private val repository: PokemonRepositoryInterface,
                     private val dispatchers: DispatcherProvider = DefaultDispatcherProvider()) :
    PagedList.BoundaryCallback<DatabasePokemon>() {
    // Avoid triggering multiple requests in the same time
    private var isRequestInProgress = false
    private val coroutineScope = CoroutineScope(dispatchers.default())
    // LiveData of network errors.


    companion object {
        // Constant for the Number of items in a page to be requested from the Github API
        private val NETWORK_PAGE_SIZE = 40
        // Keep the last requested page. When the request is successful, increment the page number.

        internal var lastOffset: Int = 0
        private val DB_EMPTY = 0
        private val OUTDATED = 1
        private val UP_TO_DATE = 2
        //private val FRESH_TIMEOUT_IN_MINUTES = 2//43200
    }

    init {
        if (isdbUpToDate() == OUTDATED) {
            Timber.i("db is outdated, retrieve data from network")
            requestAndSaveData()
        }
    }

    /**
     * Method to request data from Pokemon API for the given search query
     * and save the results.
     *
     *
     */
    private fun requestAndSaveData(howMany : Int = NETWORK_PAGE_SIZE, dbEmpty : Boolean = false) {
        //Exiting if the request is in progress
        Timber.i("lastOffset: $lastOffset")
        if (isRequestInProgress) return

        //Set to true as we are starting the network request
        isRequestInProgress = true

        //Calling the client API to retrieve the Repos for the given search query
        coroutineScope.launch {
            //withContext(dispatchers.io()) {
                try {
                    repository.changeResponseState(PokemonResponseState.LOADING)
                    Timber.i("requestandsave try calling refresh")
                    repository.refreshPokemonPlay(lastOffset, howMany) {
                        repository.changeResponseState(PokemonResponseState.DONE)
                        Timber.i("response in boundarycallback is good")
                        //Updating the last requested page number when the request was successful
                        //and the results were inserted successfully
                        lastOffset += NETWORK_PAGE_SIZE
                        Timber.i("last Offset: $lastOffset; page size: $NETWORK_PAGE_SIZE")
                        // update last refresh date
                        updateLastRefresh()
                    }
                }
                catch (e:Exception) {
                    if(repository.pokemons.value!!.isEmpty()) {
                        repository.changeResponseState(PokemonResponseState.NETWORK_ERROR.setFatal(dbEmpty))
                    }
                }
                finally {
                    //Mark the request progress as completed
                    isRequestInProgress = false
                }
            //}

        }
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        Timber.i("onZeroItemsLoaded: Started")
        requestAndSaveData(dbEmpty = true)
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within [PagedList.Config.prefetchDistance] of it.
     *
     *
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    override fun onItemAtEndLoaded(itemAtEnd: DatabasePokemon) {
        Timber.i("onItemAtEndLoaded: Started")
        //if(itemAtEnd)
        if(itemAtEnd.id < HomeViewModel.DOWNLOAD_SIZE - NETWORK_PAGE_SIZE) {
            Timber.i("Getting ${NETWORK_PAGE_SIZE}")
            requestAndSaveData()
            sharedPreferences.edit().putInt(LAST_PAGING_POKEMON_ID_KEY,itemAtEnd.id).apply()
        }
        else if(itemAtEnd.id < HomeViewModel.DOWNLOAD_SIZE) {
            Timber.i("Getting ${HomeViewModel.DOWNLOAD_SIZE - lastOffset}")
            requestAndSaveData(HomeViewModel.DOWNLOAD_SIZE - lastOffset)
            sharedPreferences.edit().putInt(LAST_PAGING_POKEMON_ID_KEY,itemAtEnd.id).apply()
        }

        // TODO THIS COULD NEED TO BE TRUE INSTEAD...
        // TODO CHECK THAT THE ERROR VALUES ARE UPDATING PROPERLY...
    }




    private fun isdbUpToDate(): Int {

        val lastSavedMinutes = sharedPreferences.getLong(LAST_DB_REFRESH, 0)
        if (lastSavedMinutes == 0L) {
            Timber.i("database empty")
            return DB_EMPTY
        }
        val calendar = Calendar.getInstance()
        val nowMinutes = calendar.timeInMillis / 60000
        Timber.i("time difference: ${nowMinutes - lastSavedMinutes}")
        return if (!dateIsFresh(nowMinutes)) {
            Timber.i("database outdated")
            OUTDATED // OR EMPTY
        } else {
            Timber.i("database up to date")
            UP_TO_DATE
        }
    }

    private fun updateLastRefresh() {
        val editor = sharedPreferences.edit()
        editor.putLong(LAST_DB_REFRESH, Calendar.getInstance().timeInMillis / 60000)
        editor.apply()
    }



}